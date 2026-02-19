#!/usr/bin/env python3
import os
import re
import json
import subprocess
from pathlib import Path

import google.generativeai as genai
from github import Github


SKIP_SCREEN_PREFIXES = {"main", "sub"}

FONT_WEIGHT_MAP = {
    100: "FontWeight.Thin",
    200: "FontWeight.ExtraLight",
    300: "FontWeight.Light",
    400: "FontWeight.Normal",
    500: "FontWeight.Medium",
    600: "FontWeight.SemiBold",
    700: "FontWeight.Bold",
    800: "FontWeight.ExtraBold",
    900: "FontWeight.Black",
}


def fetch_issue():
    token = os.environ["GITHUB_TOKEN"]
    repo_name = os.environ["REPO_FULL_NAME"]
    issue_number = int(os.environ["ISSUE_NUMBER"])

    g = Github(token)
    repo = g.get_repo(repo_name)
    issue = repo.get_issue(issue_number)

    return {
        "number": issue_number,
        "title": issue.title,
        "body": issue.body or "",
    }


def parse_issue(title, body):
    result = {}

    title_match = re.search(r"\[QA\]\s+Fix\s+(\S+)\s+in\s+(\S+)", title)
    if title_match:
        result["component"] = title_match.group(1)
        result["screen"] = title_match.group(2)

    screen_match = re.search(r"\*\*화면\*\*:\s*(.+)", body)
    if screen_match:
        result["screen"] = screen_match.group(1).strip()

    problem_match = re.search(r"## 문제 설명\n(.+?)(?:\n###|\n##)", body, re.DOTALL)
    if problem_match:
        result["problem"] = problem_match.group(1).strip()

    props_match = re.search(
        r"### Annotation에 함께 등록된 properties\n(.+?)(?:\n## |\Z)", body, re.DOTALL
    )
    if props_match:
        raw = props_match.group(1).strip()
        result["properties"] = raw if raw != "- 없음" else ""

    spec_match = re.search(r"## 상세 스펙\n(.+?)(?:\n##|\Z)", body, re.DOTALL)
    if spec_match:
        raw = spec_match.group(1).strip()
        result["specs"] = raw if raw != "- 없음" else ""

    return result


def find_relevant_files(screen: str, component: str) -> list[Path]:
    repo_root = Path(".")
    presentation = repo_root / "presentation"

    parts = screen.split("_")
    meaningful = [p for p in parts if p not in SKIP_SCREEN_PREFIXES]

    candidates: set[Path] = set()

    search_terms = []
    if meaningful:
        search_terms.append("".join(p.capitalize() for p in meaningful))
        search_terms.append(meaningful[-1].capitalize())
        if len(meaningful) > 1:
            search_terms.append(meaningful[0].capitalize())

    if component:
        search_terms.append(component)

    for term in search_terms:
        for kt_file in presentation.rglob("*.kt"):
            if term.lower() in kt_file.stem.lower():
                candidates.add(kt_file)

        result = subprocess.run(
            ["grep", "-rl", "--include=*.kt", term, str(presentation)],
            capture_output=True,
            text=True,
        )
        for line in result.stdout.strip().splitlines():
            if line:
                candidates.add(Path(line))

    def score(f: Path) -> int:
        s = 0
        if "screen" in str(f):
            s += 10
        for p in meaningful:
            if p.lower() in f.stem.lower():
                s += 5
        return s

    return sorted(candidates, key=score, reverse=True)[:6]


def build_prompt(issue: dict, parsed: dict, files: list[Path]) -> str:
    file_sections = []
    for f in files:
        try:
            content = f.read_text(encoding="utf-8")
            if len(content) > 4000:
                content = content[:4000] + "\n... (truncated)"
            file_sections.append(f"### {f}\n```kotlin\n{content}\n```")
        except Exception:
            pass

    files_str = "\n\n".join(file_sections) if file_sections else "No relevant files found."

    color_hint = ""
    props = parsed.get("properties", "")
    if props:
        r_match = re.search(r"r:\s*([\d.]+)", props)
        g_match = re.search(r"g:\s*([\d.]+)", props)
        b_match = re.search(r"b:\s*([\d.]+)", props)
        if r_match and g_match and b_match:
            r = int(float(r_match.group(1)) * 255)
            g = int(float(g_match.group(1)) * 255)
            b = int(float(b_match.group(1)) * 255)
            hex_color = f"#{r:02X}{g:02X}{b:02X}"
            color_hint = f"\n\n> Color hint: r/g/b values convert to hex **{hex_color}**"

    fw_hint = ""
    fw_match = re.search(r"fontWeight[\"']?:\s*(\d+)", props)
    if fw_match:
        fw_val = int(fw_match.group(1))
        compose_fw = FONT_WEIGHT_MAP.get(fw_val, f"FontWeight({fw_val})")
        fw_hint = f"\n\n> FontWeight hint: {fw_val} maps to **{compose_fw}** in Compose"

    return f"""You are an Android developer fixing a UI/design QA issue in a Kotlin Jetpack Compose Android app.

## Issue
- Number: #{issue["number"]}
- Title: {issue["title"]}
- Screen: {parsed.get("screen", "unknown")}
- Component: {parsed.get("component", "unknown")}

## Problem
{parsed.get("problem", "No description provided.")}

## Design Properties
{parsed.get("properties", "none") or "none"}{color_hint}{fw_hint}

## Detailed Specs
{parsed.get("specs", "none") or "none"}

## Relevant Source Files
{files_str}

## Rules
1. Make the smallest possible change that fixes the issue.
2. Only modify EXISTING files — never create new files.
3. The "original" field must be an exact verbatim substring of the file content.
4. Use existing color/style constants from the codebase if they match the required value.
5. For color fills: prefer the closest existing color constant over hardcoding a new hex.
6. For fontWeight: use the Compose FontWeight constant shown in the hint above.

## Required Response Format
Respond with ONLY a JSON object — no markdown fences, no extra text:
{{
  "can_fix": true,
  "explanation": "One-line explanation of what was changed",
  "commit_message": "Imperative short commit message, no period",
  "changes": [
    {{
      "file": "relative/path/to/File.kt",
      "original": "exact verbatim code snippet to replace",
      "replacement": "new code snippet"
    }}
  ]
}}

If auto-fix is not safe or the issue requires logic changes beyond styling, respond with:
{{
  "can_fix": false,
  "explanation": "Specific reason why this cannot be auto-fixed",
  "changes": []
}}"""


def call_gemini(prompt: str) -> dict:
    genai.configure(api_key=os.environ["GEMINI_API_KEY"])
    model = genai.GenerativeModel("gemini-1.5-flash")
    response = model.generate_content(prompt)
    text = response.text.strip()

    json_fence = re.search(r"```(?:json)?\n(.+?)\n```", text, re.DOTALL)
    if json_fence:
        text = json_fence.group(1).strip()

    try:
        return json.loads(text)
    except json.JSONDecodeError:
        return {
            "can_fix": False,
            "explanation": f"Failed to parse AI response: {text[:200]}",
            "changes": [],
        }


def apply_changes(changes: list[dict]) -> list[str]:
    applied = []
    for change in changes:
        path = Path(change["file"])
        if not path.exists():
            print(f"SKIP: file not found — {path}")
            continue

        content = path.read_text(encoding="utf-8")
        original = change["original"]

        if original not in content:
            print(f"SKIP: original snippet not found in {path}")
            print(f"  Looking for: {original[:80]!r}")
            continue

        new_content = content.replace(original, change["replacement"], 1)
        path.write_text(new_content, encoding="utf-8")
        applied.append(str(path))
        print(f"CHANGED: {path}")

    return applied


def write_outputs(can_fix: bool, commit_msg: str, explanation: str, pr_body: str):
    Path("qa_bot_commit_msg.txt").write_text(commit_msg, encoding="utf-8")
    Path("qa_bot_explanation.txt").write_text(explanation, encoding="utf-8")
    Path("pr_body.md").write_text(pr_body, encoding="utf-8")

    output_file = os.environ.get("GITHUB_OUTPUT", "")
    if output_file:
        with open(output_file, "a") as f:
            f.write(f"can_fix={'true' if can_fix else 'false'}\n")


def main():
    print("QA Bot starting...")

    issue = fetch_issue()
    print(f"Issue #{issue['number']}: {issue['title']}")

    parsed = parse_issue(issue["title"], issue["body"])
    parsed["number"] = issue["number"]
    print(f"Screen: {parsed.get('screen')}  Component: {parsed.get('component')}")

    screen = parsed.get("screen", "")
    component = parsed.get("component", "")
    relevant_files = find_relevant_files(screen, component)
    print(f"Found {len(relevant_files)} candidate file(s):")
    for f in relevant_files:
        print(f"  {f}")

    prompt = build_prompt(issue, parsed, relevant_files)
    print("Calling Gemini...")
    result = call_gemini(prompt)

    can_fix = result.get("can_fix", False)
    explanation = result.get("explanation", "No explanation provided.")
    print(f"can_fix={can_fix}  explanation={explanation}")

    if not can_fix or not result.get("changes"):
        write_outputs(
            can_fix=False,
            commit_msg="",
            explanation=explanation,
            pr_body="",
        )
        return

    changed = apply_changes(result["changes"])

    if not changed:
        write_outputs(
            can_fix=False,
            commit_msg="",
            explanation="Code snippets could not be matched in any file.",
            pr_body="",
        )
        return

    commit_msg = result.get("commit_message", f"Fix {component} in {screen}")
    pr_body = (
        f"Closes #{issue['number']}\n\n"
        f"**Screen**: `{screen}`  \n"
        f"**Component**: `{component}`\n\n"
        f"**Changes**:  \n{explanation}\n\n"
        f"**Files modified**:  \n"
        + "\n".join(f"- `{f}`" for f in changed)
        + "\n\n> 🤖 Auto-generated by QA Bot"
    )

    write_outputs(can_fix=True, commit_msg=commit_msg, explanation=explanation, pr_body=pr_body)
    print(f"Done — {len(changed)} file(s) modified.")


if __name__ == "__main__":
    main()
