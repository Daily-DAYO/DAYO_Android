#!/usr/bin/env python3
import os
import re
import json
import subprocess
import time
from pathlib import Path
from urllib import error, request




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
    from github import Auth, Github

    token = os.environ["GITHUB_TOKEN"]
    repo_name = os.environ["REPO_FULL_NAME"]
    issue_number = int(os.environ["ISSUE_NUMBER"])

    g = Github(auth=Auth.Token(token))
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
    api_key = os.environ.get("GEMINI_API_KEY", "").strip()
    if not api_key:
        return {
            "can_fix": False,
            "explanation": "Missing GEMINI_API_KEY",
            "changes": [],
        }

    def http_json(method: str, url: str, payload: dict | None = None) -> dict:
        data = None
        if payload is not None:
            data = json.dumps(payload).encode("utf-8")

        req = request.Request(
            url=url,
            method=method,
            data=data,
            headers={
                "Content-Type": "application/json",
            },
        )

        try:
            with request.urlopen(req, timeout=30) as resp:
                body = resp.read().decode("utf-8")
                return json.loads(body) if body else {}
        except error.HTTPError as e:
            raw = e.read().decode("utf-8", errors="replace")
            try:
                detail = json.loads(raw) if raw else {"error": {"code": e.code, "message": raw}}
            except json.JSONDecodeError:
                detail = {"error": {"code": e.code, "message": raw}}
            raise RuntimeError(json.dumps(detail, ensure_ascii=True)) from e

    def list_models(api_version: str) -> list[dict]:
        url = f"https://generativelanguage.googleapis.com/{api_version}/models?key={api_key}"
        data = http_json("GET", url)
        models = data.get("models")
        return models if isinstance(models, list) else []

    def extract_text(resp: dict) -> str:
        candidates = resp.get("candidates")
        if not isinstance(candidates, list) or not candidates:
            return ""
        content = candidates[0].get("content") if isinstance(candidates[0], dict) else None
        parts = content.get("parts") if isinstance(content, dict) else None
        if not isinstance(parts, list) or not parts:
            return ""
        texts: list[str] = []
        for p in parts:
            if isinstance(p, dict) and isinstance(p.get("text"), str):
                texts.append(p["text"])
        return "".join(texts).strip()

    preferred_tokens = [
        "gemini-2.0-flash-lite",
        "gemini-2.0-flash",
        "gemini-2.0",
        "gemini-1.5-flash",
        "gemini-1.5-pro",
        "gemini-1.0-pro",
        "gemini-pro",
    ]

    attempts: list[tuple[str, str]] = []
    errors_seen: list[str] = []

    for api_version in ("v1beta", "v1", "v1alpha"):
        try:
            models = list_models(api_version)
        except Exception as e:
            errors_seen.append(f"{api_version} list models failed: {e}")
            continue

        generate_models: list[str] = []
        for m in models:
            if not isinstance(m, dict):
                continue
            name = m.get("name")
            methods = m.get("supportedGenerationMethods")
            if not isinstance(name, str):
                continue
            if isinstance(methods, list) and "generateContent" in methods:
                generate_models.append(name)

        if not generate_models:
            errors_seen.append(f"{api_version}: no generateContent-capable models returned")
            continue

        print(f"{api_version}: {len(generate_models)} generateContent-capable model(s)")

        ordered: list[str] = []
        for token in preferred_tokens:
            for name in generate_models:
                if token in name and name not in ordered:
                    ordered.append(name)
        for name in generate_models:
            if name not in ordered:
                ordered.append(name)

        for model_name in ordered[:8]:
            attempts.append((api_version, model_name))

    if not attempts:
        return {
            "can_fix": False,
            "explanation": "Unable to list any usable Gemini models. " + "; ".join(errors_seen)[:500],
            "changes": [],
        }

    last_err = ""
    saw_limit_zero = False
    saw_not_found = False
    saw_other = False
    for api_version, model_name in attempts:
        print(f"Trying Gemini model: {model_name} (api: {api_version})")
        url = f"https://generativelanguage.googleapis.com/{api_version}/{model_name}:generateContent?key={api_key}"
        payload = {
            "contents": [
                {
                    "role": "user",
                    "parts": [
                        {
                            "text": prompt,
                        }
                    ],
                }
            ]
        }

        try:
            resp = http_json("POST", url, payload)
            text = extract_text(resp)
            if not text:
                return {
                    "can_fix": False,
                    "explanation": f"Gemini returned empty response for {model_name}",
                    "changes": [],
                }

            json_fence = re.search(r"```(?:json)?\n(.+?)\n```", text, re.DOTALL)
            if json_fence:
                text = json_fence.group(1).strip()

            try:
                return json.loads(text)
            except json.JSONDecodeError:
                return {
                    "can_fix": False,
                    "explanation": f"Failed to parse AI response from {model_name}: {text[:200]}",
                    "changes": [],
                }
        except Exception as e:
            err_str = str(e)
            last_err = err_str
            if "RESOURCE_EXHAUSTED" in err_str and "limit\": 0" in err_str:
                saw_limit_zero = True
                continue
            if "NOT_FOUND" in err_str:
                saw_not_found = True
                continue
            saw_other = True
            retry_delay = re.search(r"retryDelay\":\s*\"(\d+)s\"", err_str)
            if retry_delay:
                time.sleep(min(int(retry_delay.group(1)), 15))
                continue
            continue

    if saw_limit_zero and not saw_other:
        msg = "Gemini API free-tier quota appears to be 0 for all usable models in this project. Enable billing or use a different API key/project."
        if saw_not_found:
            msg += " Some model IDs were also not found."
        return {
            "can_fix": False,
            "explanation": msg,
            "changes": [],
        }

    return {
        "can_fix": False,
        "explanation": f"All Gemini models failed. Last error: {last_err}"[:500],
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
