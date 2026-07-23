#!/usr/bin/env bash
# rename-template.sh
# Usage: bash scripts/rename-template.sh <new-package> <new-project-name>
#
# Example:
#   bash scripts/rename-template.sh com.yourcompany.yourproject your-project-name
#
# What it does:
#   1. Renames Java package directory tree  (com/example/harnessdemo → your/package/path)
#   2. Updates all file content (package declarations, pom.xml, yml, md files)
#   3. Prints a summary of changed files

set -euo pipefail

# ── Args ──────────────────────────────────────────────────────────────────────
NEW_PACKAGE="${1:-}"
NEW_PROJECT="${2:-}"

if [[ -z "$NEW_PACKAGE" || -z "$NEW_PROJECT" ]]; then
  echo "Usage: bash scripts/rename-template.sh <new-package> <new-project-name>"
  echo ""
  echo "  <new-package>      e.g. com.yourcompany.yourproject"
  echo "  <new-project-name> e.g. your-project-name"
  echo ""
  echo "Example:"
  echo "  bash scripts/rename-template.sh com.myteam.backend my-backend"
  exit 1
fi

# ── Derived values ─────────────────────────────────────────────────────────────
OLD_PACKAGE="com.example.harnessdemo"
OLD_GROUP="com.example"
OLD_ARTIFACT="harness-demo"
OLD_SHORT="harnessdemo"          # no-separator form
OLD_DESC="Demo project for Claude Code harness setup"

NEW_GROUP="${NEW_PACKAGE%.*}"                          # e.g. com.yourcompany
NEW_SHORT="${NEW_PACKAGE##*.}"                         # e.g. yourproject
NEW_PACKAGE_PATH="${NEW_PACKAGE//.//}"                 # e.g. com/yourcompany/yourproject
OLD_PACKAGE_PATH="${OLD_PACKAGE//.//}"                 # com/example/harnessdemo

# Project description (default = capitalize new project name)
NEW_DESC="$(echo "${NEW_PROJECT}" | sed 's/-/ /g' | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) tolower(substr($i,2)); print}')"

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "================================================================"
echo "  harness-demo rename-template"
echo "================================================================"
echo "  Old package : $OLD_PACKAGE"
echo "  New package : $NEW_PACKAGE"
echo "  Old project : $OLD_ARTIFACT"
echo "  New project : $NEW_PROJECT"
echo "----------------------------------------------------------------"

# ── Step 1: Rename Java source directories ────────────────────────────────────
echo ""
echo "[1/3] Renaming Java package directories..."

for BASE_DIR in \
  "$REPO_ROOT/src/main/java" \
  "$REPO_ROOT/src/test/java"; do

  OLD_DIR="$BASE_DIR/$OLD_PACKAGE_PATH"
  NEW_DIR="$BASE_DIR/$NEW_PACKAGE_PATH"

  if [[ -d "$OLD_DIR" ]]; then
    # Create parent dirs
    mkdir -p "$(dirname "$NEW_DIR")"
    # Move directory tree
    mv "$OLD_DIR" "$NEW_DIR"
    echo "    Moved: $OLD_DIR"
    echo "       → $NEW_DIR"

    # Also clean up now-empty parent dirs (e.g. com/example/)
    OLD_PARENT="$BASE_DIR/${OLD_GROUP//.//}"
    if [[ -d "$OLD_PARENT" ]] && [[ -z "$(ls -A "$OLD_PARENT")" ]]; then
      rm -rf "$BASE_DIR/$(echo "$OLD_GROUP" | cut -d. -f1)"
      echo "    Removed empty parent: $OLD_PARENT"
    fi
  fi
done

# ── Step 2: Update JaCoCo exclude path in pom.xml ─────────────────────────────
echo ""
echo "[2/3] Updating file contents..."

# Files to update content in
CONTENT_FILES=(
  "$REPO_ROOT/pom.xml"
  "$REPO_ROOT/CLAUDE.md"
  "$REPO_ROOT/README.md"
  "$REPO_ROOT/session-handoff.md"
  "$REPO_ROOT/docker-compose.yml"
  "$REPO_ROOT/Dockerfile"
  "$REPO_ROOT/.github/CODEOWNERS"
  "$REPO_ROOT/.github/dependabot.yml"
  "$REPO_ROOT/docs/architecture.md"
  "$REPO_ROOT/docs/harness-standards.md"
  "$REPO_ROOT/docs/PROJECT_STATUS.md"
  "$REPO_ROOT/.claude/README.md"
  "$REPO_ROOT/memory/topic_project_root.md"
  "$REPO_ROOT/memory/topic_tech_stack.md"
)

# Also find all Java files under new location
while IFS= read -r -d '' f; do
  CONTENT_FILES+=("$f")
done < <(find "$REPO_ROOT/src" -name "*.java" -print0 2>/dev/null)

# Also find all yml/yaml files
while IFS= read -r -d '' f; do
  CONTENT_FILES+=("$f")
done < <(find "$REPO_ROOT/src/main/resources" -name "*.yml" -o -name "*.yaml" -print0 2>/dev/null)

CHANGED=0
for FILE in "${CONTENT_FILES[@]}"; do
  [[ -f "$FILE" ]] || continue

  # Run all substitutions via sed
  if sed -i.bak \
      -e "s|${OLD_PACKAGE}|${NEW_PACKAGE}|g" \
      -e "s|${OLD_PACKAGE_PATH}|${NEW_PACKAGE_PATH}|g" \
      -e "s|${OLD_GROUP}|${NEW_GROUP}|g" \
      -e "s|${OLD_SHORT}|${NEW_SHORT}|g" \
      -e "s|${OLD_ARTIFACT}|${NEW_PROJECT}|g" \
      -e "s|${OLD_DESC}|${NEW_DESC}|g" \
      "$FILE" 2>/dev/null; then

    # Check if file actually changed (compare with backup)
    if ! diff -q "$FILE" "${FILE}.bak" > /dev/null 2>&1; then
      echo "    Updated: ${FILE#$REPO_ROOT/}"
      CHANGED=$((CHANGED + 1))
    fi
    rm -f "${FILE}.bak"
  fi
done

echo "    $CHANGED file(s) updated"

# ── Step 3: Update spring.application.name in application.yml ────────────────
APP_YML="$REPO_ROOT/src/main/resources/application.yml"
if [[ -f "$APP_YML" ]]; then
  sed -i.bak "s|name: ${OLD_ARTIFACT}|name: ${NEW_PROJECT}|g" "$APP_YML"
  rm -f "${APP_YML}.bak"
fi

# ── Done ──────────────────────────────────────────────────────────────────────
echo ""
echo "[3/3] Done!"
echo ""
echo "Next steps:"
echo "  1. Review the changes:  git diff --stat"
echo "  2. Run baseline:        mvn clean verify"
echo "  3. Commit:              git add -A && git commit -m 'chore: rename template to ${NEW_PROJECT}'"
echo ""
echo "Manual checks (search these in your IDE):"
echo "  - Any remaining 'harness-demo' or 'harnessdemo' literals"
echo "  - README.md project description"
echo "  - OpenAPI info in OpenApiConfig.java"
echo "  - logstash service name in logback-spring.xml (if exists)"
