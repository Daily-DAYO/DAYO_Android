#!/usr/bin/env python3
import os
import re
import json
import subprocess
import time
from pathlib import Path
from urllib import error, request




SKIP_SCREEN_PREFIXES = {"main", "sub"}

ALLOWED_EDIT_PREFIXES = (
    "app/src/main/java/",
    "data/src/main/java/",
    "domain/src/main/java/",
    "presentation/src/main/java/",
)

DENIED_EDIT_CONTAINS = (
    "local.properties",
    ".keystore",
    ".jks",
    "keystore",
    "sentry.properties",
    ".github/",
)

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


def build_snippet_catalog(files: list[Path]) -> dict[str, dict[str, str]]:
    def is_interesting(line: str) -> bool:
        tokens = (
            "DayoTextField(",
            "DayoPasswordTextField(",
            "label =",
            "placeholder =",
            ".padding(",
            "Modifier.",
            "Text(",
            "fontSize =",
            "fontWeight =",
            "color =",
            "shape =",
            "border =",
            "keyboardOptions",
            "keyboardActions",
        )
        return any(t in line for t in tokens)

    catalog: dict[str, dict[str, str]] = {}
    for file_idx, f in enumerate(files):
        try:
            content = f.read_text(encoding="utf-8")
        except Exception:
            continue

        snippets: dict[str, str] = {}
        count = 0
        for line_idx, line in enumerate(content.splitlines(), start=1):
            if not is_interesting(line):
                continue
            text = line.rstrip("\n")
            if not text.strip():
                continue

            snippet_id = f"F{file_idx}S{count}L{line_idx}"
            snippets[snippet_id] = text
            count += 1
            if count >= 35:
                break

        if snippets:
            catalog[str(f)] = snippets

    return catalog


def is_allowed_edit_path(path: Path) -> bool:
    s = path.as_posix().lstrip("./")
    if any(x in s for x in DENIED_EDIT_CONTAINS):
        return False
    if not s.endswith(".kt"):
        return False
    return s.startswith(ALLOWED_EDIT_PREFIXES)


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
        p = f.as_posix().lower()
        stem = f.stem.lower()
        if "/screen/" in p:
            s += 25
        if "screen" in stem:
            s += 15
        if "screen" in p:
            s += 5
        if "/view/" in p or stem.endswith("view"):
            s += 4

        if "/model/" in p or "model" in stem:
            s -= 6
        for p in meaningful:
            if p.lower() in f.stem.lower():
                s += 5
            if p.lower() in f.as_posix().lower():
                s += 2
        return s

    return sorted(candidates, key=score, reverse=True)[:6]


def build_prompt(
    issue: dict,
    parsed: dict,
    files: list[Path],
    snippet_catalog: dict[str, dict[str, str]],
) -> str:
    file_sections = []
    catalog_sections: list[str] = []
    for f in files:
        try:
            content = f.read_text(encoding="utf-8")

            snippets = snippet_catalog.get(str(f), {})
            if snippets:
                lines = [
                    f"- [{sid}] {text}"
                    for sid, text in list(snippets.items())[:35]
                    if isinstance(text, str)
                ]
                catalog_sections.append(
                    "### "
                    + str(f)
                    + "\n"
                    + "Pick snippet_id(s) from this catalog:\n"
                    + "\n".join(lines)
                )

            if len(content) > 4000:
                pivot = content.find("DayoTextField(")
                if pivot == -1:
                    pivot = content.find("DayoPasswordTextField(")
                if pivot == -1:
                    pivot = content.find("@Composable")

                if pivot != -1:
                    start = max(pivot - 1800, 0)
                    end = min(start + 4000, len(content))
                    content = content[start:end] + "\n... (truncated)"
                else:
                    content = content[:4000] + "\n... (truncated)"
            file_sections.append(f"### {f}\n```kotlin\n{content}\n```")
        except Exception:
            pass

    files_str = "\n\n".join(file_sections) if file_sections else "No relevant files found."
    catalog_str = (
        "\n\n".join(catalog_sections)
        if catalog_sections
        else "(No snippet catalog available.)"
    )

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

## Snippet Catalog (Preferred)
To avoid mismatches, prefer returning snippet_id(s) from the catalog below instead of retyping "original":
{catalog_str}

## Rules
1. Make the smallest possible change that fixes the issue.
2. Only modify EXISTING files — never create new files.
 3. Prefer snippet_id. If you use "original", it must be an exact verbatim substring of the file content.
4. Use existing color/style constants from the codebase if they match the required value.
5. For color fills: prefer the closest existing color constant over hardcoding a new hex.
6. For fontWeight: use the Compose FontWeight constant shown in the hint above.
7. Do not retype resource identifiers (e.g., R.string.*). Copy from the provided source or snippets.

## Required Response Format
Respond with ONLY a JSON object — no markdown fences, no extra text:
{{
  "can_fix": true,
  "explanation": "One-line explanation of what was changed",
  "commit_message": "Imperative short commit message, no period",
  "changes": [
    {{
      "file": "relative/path/to/File.kt",
      "snippet_id": "F0S0L123",
      "original": "(optional) exact verbatim code snippet to replace",
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


def apply_changes(
    changes: list[dict],
    snippet_catalog: dict[str, dict[str, str]],
) -> tuple[list[str], list[str]]:
    repo_root = Path(".").resolve()
    applied: list[str] = []
    skipped: list[str] = []
    for change in changes:
        file_str = change.get("file") if isinstance(change, dict) else None
        if not isinstance(file_str, str) or not file_str.strip():
            skipped.append("missing file in change")
            print("SKIP: missing file in change")
            continue

        path = Path(file_str)
        if path.is_absolute():
            skipped.append(f"absolute path not allowed: {path}")
            print(f"SKIP: absolute path not allowed — {path}")
            continue

        try:
            resolved = path.resolve()
        except Exception:
            skipped.append(f"invalid path: {path}")
            print(f"SKIP: invalid path — {path}")
            continue

        try:
            if not resolved.is_relative_to(repo_root):
                skipped.append(f"path escapes repo: {path}")
                print(f"SKIP: path escapes repo — {path}")
                continue
        except AttributeError:
            if str(resolved).startswith(str(repo_root)) is False:
                skipped.append(f"path escapes repo: {path}")
                print(f"SKIP: path escapes repo — {path}")
                continue

        if not is_allowed_edit_path(path):
            skipped.append(f"path not allowed: {path}")
            print(f"SKIP: path not allowed — {path}")
            continue

        if not path.exists():
            print(f"SKIP: file not found — {path}")
            skipped.append(f"file not found: {path}")
            continue

        content = path.read_text(encoding="utf-8")

        original = ""
        snippet_id = change.get("snippet_id") if isinstance(change, dict) else None
        if isinstance(snippet_id, str) and snippet_id:
            file_snips = snippet_catalog.get(str(path)) or snippet_catalog.get(str(path.as_posix()))
            if isinstance(file_snips, dict) and isinstance(file_snips.get(snippet_id), str):
                original = file_snips[snippet_id]
            else:
                skipped.append(f"snippet_id not found for {path}: {snippet_id}")
                print(f"SKIP: snippet_id not found for {path} — {snippet_id}")
                continue
        else:
            original = change.get("original", "") if isinstance(change, dict) else ""

        replacement = change.get("replacement", "") if isinstance(change, dict) else ""
        if not isinstance(replacement, str) or not replacement:
            skipped.append(f"missing replacement for {path}")
            print(f"SKIP: missing replacement for {path}")
            continue

        if original not in content:
            print(f"SKIP: original snippet not found in {path}")
            print(f"  Looking for: {original[:80]!r}")
            skipped.append(f"snippet not found in {path}: {original[:80]!r}")
            continue

        replaced = False
        line_no = None
        if isinstance(snippet_id, str) and snippet_id:
            m = re.search(r"L(\d+)$", snippet_id)
            if m:
                try:
                    line_no = int(m.group(1))
                except ValueError:
                    line_no = None

        if isinstance(line_no, int) and line_no >= 1:
            lines = content.splitlines(True)
            idx = line_no - 1
            if idx < len(lines):
                current_line = lines[idx].rstrip("\r\n")
                if current_line == original:
                    suffix = ""
                    if lines[idx].endswith("\r\n") and not replacement.endswith("\r\n"):
                        suffix = "\r\n"
                    elif lines[idx].endswith("\n") and not replacement.endswith("\n"):
                        suffix = "\n"
                    lines[idx] = replacement + suffix
                    new_content = "".join(lines)
                    replaced = True

        if not replaced:
            new_content = content.replace(original, replacement, 1)
        path.write_text(new_content, encoding="utf-8")
        applied.append(str(path))
        print(f"CHANGED: {path}")

    return applied, skipped


def write_outputs(can_fix: bool, commit_msg: str, explanation: str, pr_body: str):
    out_dir = Path(".tmp_runner/qa-bot")
    out_dir.mkdir(parents=True, exist_ok=True)

    (out_dir / "qa_bot_commit_msg.txt").write_text(commit_msg, encoding="utf-8")
    (out_dir / "qa_bot_explanation.txt").write_text(explanation, encoding="utf-8")
    (out_dir / "pr_body.md").write_text(pr_body, encoding="utf-8")

    output_file = os.environ.get("GITHUB_OUTPUT", "")
    if output_file:
        with open(output_file, "a") as f:
            f.write(f"can_fix={'true' if can_fix else 'false'}\n")


def build_repair_prompt(
    issue: dict,
    parsed: dict,
    files: list[Path],
    snippet_catalog: dict[str, dict[str, str]],
    skipped: list[str],
) -> str:
    base = build_prompt(issue, parsed, files, snippet_catalog)
    details = "\n".join(f"- {s}" for s in skipped[:5]) if skipped else "- (none)"
    return (
        base
        + "\n\n## Apply Failure\n"
        + "The previous JSON could not be applied because the exact 'original' snippet was not found.\n"
        + "Fix this by returning snippet_id from the Snippet Catalog, or by copying 'original' from the provided files/snippets exactly.\n\n"
        + details
    )


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

    snippet_catalog = build_snippet_catalog(relevant_files)
    prompt = build_prompt(issue, parsed, relevant_files, snippet_catalog)
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

    changed, skipped = apply_changes(result["changes"], snippet_catalog)
    if skipped:
        repair_prompt = build_repair_prompt(issue, parsed, relevant_files, snippet_catalog, skipped)
        print("Retrying Gemini for exact snippet match...")
        repaired = call_gemini(repair_prompt)
        if repaired.get("can_fix") and repaired.get("changes"):
            changed2, skipped2 = apply_changes(repaired["changes"], snippet_catalog)
            changed = list(dict.fromkeys(changed + changed2))
            skipped = skipped2

    if not changed or skipped:
        reason = (
            "Code snippets could not be matched in file content. "
            + "; ".join(skipped[:3])
        )[:500]
        write_outputs(
            can_fix=False,
            commit_msg="",
            explanation=reason,
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
