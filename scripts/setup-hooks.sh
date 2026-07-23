#!/usr/bin/env bash
#
# setup-hooks.sh — Install pre-commit hooks for Java project
#

set -euo pipefail

HOOKS_DIR="$(git rev-parse --git-dir)/hooks"
PROJECT_DIR="$(git rev-parse --show-toplevel)"

# Pre-commit hook: run eval-gate checks (doc sync + checkstyle + test + coverage)
cat > "$HOOKS_DIR/pre-commit" << 'HOOK'
#!/usr/bin/env bash
set -euo pipefail

echo "========================================"
echo "  Eval Gate - Pre-commit Checks"
echo "========================================"

# 1. Doc Sync Check
echo ""
echo "[1/4] Running Doc Sync Check..."
if ! bash scripts/doc-sync-check.sh 2>/dev/null; then
    echo "ERROR: Doc sync check failed. Fix documentation drift before committing."
    exit 1
fi
echo "✓ Doc sync check passed"

# 2. Checkstyle
echo ""
echo "[2/4] Running Checkstyle..."
if ! mvn checkstyle:check -q 2>/dev/null; then
    echo "ERROR: Checkstyle failed. Fix code style issues before committing."
    exit 1
fi
echo "✓ Checkstyle passed"

# 3. PMD (Alibaba p3c)
echo ""
echo "[3/4] Running PMD (Alibaba p3c)..."
if ! mvn pmd:check -q 2>/dev/null; then
    echo "ERROR: PMD check failed. Fix coding standard issues before committing."
    exit 1
fi
echo "✓ PMD passed"

# 4. Tests + Coverage (quick mode - skip if too slow)
echo ""
echo "[4/4] Running Tests + Coverage..."
echo "      (This may take a minute, skip with --no-verify if urgent)"
if ! mvn verify -q 2>/dev/null; then
    echo "ERROR: Tests or coverage check failed."
    echo "       Run 'mvn verify' manually to see details."
    exit 1
fi
echo "✓ Tests + Coverage passed"

echo ""
echo "========================================"
echo "  ✅ All Eval Gate checks passed!"
echo "========================================"
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
