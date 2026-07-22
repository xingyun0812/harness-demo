#!/usr/bin/env bash
# scripts/claude-pre-push-guard.sh
# Claude Code PreToolUse hook: block `git push` to main/master
#
# Registered in .claude/settings.json under hooks.PreToolUse (matcher: Bash).
# This is the Claude-side guard layer. The authoritative guards are:
#   1. .git/hooks/pre-push  (installed via scripts/setup-hooks.sh)
#   2. GitHub branch protection rules (enable in repo settings)
#
# Why: the deny list in settings.json can't distinguish `git push origin main`
# from `git push origin feat/xxx` with a single glob, so we inspect the command.
#
# Exit codes (per Claude Code hook convention):
#   0 = allow (stdout empty or '{"continue":true}')
#   2 = deny  (stderr shown to Claude as stopReason)

set -euo pipefail

PAYLOAD=$(cat)

# Extract tool_input.command and cwd — use jq if available, sed fallback otherwise
if command -v jq &>/dev/null; then
  COMMAND=$(printf '%s' "$PAYLOAD" | jq -r '.tool_input.command // ""')
  CWD=$(printf '%s' "$PAYLOAD" | jq -r '.cwd // ""')
else
  COMMAND=$(printf '%s' "$PAYLOAD" | sed -n 's/.*"command"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p')
  CWD=$(printf '%s' "$PAYLOAD" | sed -n 's/.*"cwd"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p')
fi

# Skip non-push commands quickly
case "$COMMAND" in
  *"git push"*) ;;
  *) exit 0 ;;
esac

# Block if main/master appears as a whole-word ref in the command.
# Matches:  "git push origin main", "git push origin main:main", "git push origin HEAD:main"
# Skips:    "git push origin feature/main" (preceded by "/", not space/start/colon)
#           "git push origin main-fix"      (followed by "-", not space/colon/end)
if printf '%s' "$COMMAND" | grep -qE '([[:space:]]|^|:)(main|master)([[:space:]]|:|$)'; then
  echo "ERROR: Direct push to main/master is not allowed. Use a pull request." >&2
  echo "Hint: git checkout -b feat/xxx && git push -u origin feat/xxx, then open a PR on GitHub." >&2
  exit 2
fi

# Edge case: "git push" or "git push origin" with no explicit refspec,
# while current branch is main/master. Only check when command has ≤3 tokens
# (git, push, [remote]) — anything longer has an explicit refspec handled above.
TOKEN_COUNT=$(printf '%s' "$COMMAND" | wc -w | tr -d ' ')
if [[ "$TOKEN_COUNT" -le 3 ]]; then
  CURRENT_BRANCH=""
  if [[ -n "$CWD" ]]; then
    CURRENT_BRANCH=$(git -C "$CWD" branch --show-current 2>/dev/null || echo "")
  fi
  if [[ "$CURRENT_BRANCH" == "main" || "$CURRENT_BRANCH" == "master" ]]; then
    echo "ERROR: Current branch is '$CURRENT_BRANCH'. Direct push to main/master is not allowed. Use a pull request." >&2
    exit 2
  fi
fi

exit 0
