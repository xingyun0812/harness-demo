#!/usr/bin/env bash
#
# setup-hooks.sh — Install pre-commit hooks for Java project
#

set -euo pipefail

HOOKS_DIR="$(git rev-parse --git-dir)/hooks"
PROJECT_DIR="$(git rev-parse --show-toplevel)"

# Pre-commit hook: run checkstyle + doc sync check before each commit
cat > "$HOOKS_DIR/pre-commit" << 'HOOK'
#!/usr/bin/env bash
set -euo pipefail

echo "Running Doc Sync Check..."
if ! bash scripts/doc-sync-check.sh 2>/dev/null; then
    echo "Doc sync check failed. Fix documentation drift before committing."
    exit 1
fi
echo "Doc sync check passed."

echo "Running Checkstyle..."
if ! mvn checkstyle:check -q 2>/dev/null; then
    echo "Checkstyle failed. Fix issues before committing."
    exit 1
fi
echo "Checkstyle passed."
HOOK
chmod +x "$HOOKS_DIR/pre-commit"

# Pre-push hook: block direct pushes to main
echo "Installing pre-push hook..."
cat > "$HOOKS_DIR/pre-push" << 'HOOK'
#!/usr/bin/env bash
set -euo pipefail
while read -r local_ref local_sha remote_ref remote_sha; do
    if [[ "$remote_ref" == "refs/heads/main" || "$remote_ref" == "refs/heads/master" ]]; then
        echo "ERROR: Direct push to main/master is not allowed. Use a pull request."
        exit 1
    fi
done
HOOK
chmod +x "$HOOKS_DIR/pre-push"

echo "Hooks installed successfully."
