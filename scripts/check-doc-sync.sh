#!/usr/bin/env bash
#
# check-doc-sync.sh — Verify source changes have matching doc updates staged.
#
# Reads mapping rules from .doc-sync.yml (project root).
#
# Usage:
#   scripts/check-doc-sync.sh            # non-blocking (exit 0 always)
#   scripts/check-doc-sync.sh --strict   # blocking (exit 1 if warnings)

set -euo pipefail

STRICT=false
for arg in "$@"; do
    case "$arg" in
        --strict) STRICT=true ;;
        *) echo "Unknown option: $arg" >&2; exit 1 ;;
    esac
done

# --- Config ---

CONFIG_FILE=".doc-sync.yml"
PROJECT_ROOT=$(git rev-parse --show-toplevel 2>/dev/null || echo ".")
CONFIG_PATH="$PROJECT_ROOT/$CONFIG_FILE"

if [[ ! -f "$CONFIG_PATH" ]]; then
    echo "[INFO] $CONFIG_FILE not found — skipping doc-sync check." >&2
    echo "[INFO] Create $CONFIG_FILE at project root to enable rules (see docs/standards/harness-standards.md)." >&2
    exit 0
fi

STAGED=$(git diff --cached --name-only || true)
WARNINGS=0
declare -a WARNED_DOCS=()

# --- Helpers ---

is_staged() {
    local target="$1"
    local f
    while IFS= read -r f; do
        [[ "$f" == "$target" ]] && return 0
    done <<< "$STAGED"
    return 1
}

warn() {
    echo "[WARN] $1" >&2
    WARNINGS=$((WARNINGS + 1))
}

# Warn about a doc only once
require_doc() {
    local doc="$1"
    local seen
    for seen in "${WARNED_DOCS[@]+${WARNED_DOCS[@]}}"; do
        [[ "$seen" == "$doc" ]] && return 0
    done
    WARNED_DOCS+=("$doc")
    is_staged "$doc" || warn "Source files changed but required doc not staged: $doc"
}

# Convert glob pattern to ERE for grep
# Supports: ** (recursive), * (single segment), ? (single char)
glob_to_regex() {
    local pattern="$1"
    local regex=""
    local i=0 char next

    # Anchor the pattern
    regex="^"

    while [[ $i -lt ${#pattern} ]]; do
        char="${pattern:$i:1}"
        next="${pattern:$((i+1)):1}"

        case "$char" in
            '.')
                regex+='\.'
                ;;
            '*')
                if [[ "$next" == '*' ]]; then
                    # ** — match across directories
                    # Skip the second *
                    i=$((i + 1))
                    # Check if this is **/ (zero or more dir levels)
                    if [[ $((i + 1)) -lt ${#pattern} && "${pattern:$((i+1)):1}" == '/' ]]; then
                        regex+='(.*/)?'
                        i=$((i + 1))
                    else
                        regex+='.*'
                    fi
                else
                    # * — single segment, no slash
                    regex+='[^/]*'
                fi
                ;;
            '?')
                regex+='.'
                ;;
            '/')
                regex+='/'
                ;;
            *)
                # Escape other special regex chars
                case "$char" in
                    '+'|'^'|'$'|'['|']'|'('|')'|'{'|'}'|'|'|'\\')
                        regex+="\\$char"
                        ;;
                    *)
                        regex+="$char"
                        ;;
                esac
                ;;
        esac
        i=$((i + 1))
    done

    regex+='$'
    echo "$regex"
}

# Check if any staged file matches a glob pattern
glob_matches() {
    local pattern="$1"
    local regex
    regex=$(glob_to_regex "$pattern")
    echo "$STAGED" | grep -qE "$regex"
}

# --- YAML Parsing (awk) ---

# Extract always-required docs: lines under "always:" matching "  - <value>"
parse_always_docs() {
    awk '
        /^always:/ { in_always = 1; next }
        /^[a-z]/ && !/^  / && !/^#/ { in_always = 0 }
        in_always && /^  - / {
            sub(/^  - /, "");
            sub(/[ \t]+#.*$/, "");  # strip trailing comments
            gsub(/^[ \t]+|[ \t]+$/, "");  # trim
            gsub(/\042/, "");  # strip double quotes (octal 042)
            if ($0 != "") print
        }
    ' "$CONFIG_PATH"
}

# Extract conditional rules: emit "source_glob|doc_path" lines, one per pair.
# Handles both single-string source and list-of-strings source.
parse_conditional_rules() {
    awk '
    BEGIN {
        in_conditional = 0
        in_entry = 0
        reading_source = 0
        reading_docs = 0
        current_source = ""
        doc_count = 0
        src_count = 0
    }

    # Enter conditional section
    /^conditional:/ { in_conditional = 1; next }

    # Exit conditional on next top-level key (no indent, not comment/blank)
    in_conditional && /^[a-z]/ && !/^[ \t]/ { in_conditional = 0; emit_current(); next }

    !in_conditional { next }

    # Start a new rule entry
    /^  - source:/ {
        emit_current()
        in_entry = 1
        reading_source = 1
        reading_docs = 0
        current_source = ""
        doc_count = 0
        src_count = 0
        delete doc_list
        delete sources

        # Check if value is on same line (single string form)
        val = $0
        sub(/^[ \t]*- source:[ \t]*/, "", val)
        sub(/[ \t]+#.*$/, "", val)
        gsub(/\042/, "", val)  # strip double quotes
        if (val != "" && val != $0) {
            current_source = val
            reading_source = 0
        }
        next
    }

    # Inside source list
    reading_source == 1 && /^[ \t]+- / {
        if (match($0, /^[ \t]+-[ \t]+/)) {
            src = substr($0, RSTART + RLENGTH)
            sub(/[ \t]+#.*$/, "", src)
            gsub(/\042/, "", src)  # strip double quotes
            if (src != "") {
                sources[src_count++] = src
                reading_source = 0
            }
        }
        next
    }

    # Detect docs section
    /^    docs:/ {
        reading_source = 0
        reading_docs = 1
        next
    }

    # Collect doc paths
    reading_docs == 1 && /^      - / {
        val = $0
        sub(/^[ \t]*-[ \t]*/, "", val)
        sub(/[ \t]+#.*$/, "", val)
        gsub(/\042/, "", val)  # strip double quotes
        if (val != "") {
            doc_list[doc_count++] = val
        }
        next
    }

    # Blank line resets current entry context but we hold the entry
    in_entry && /^$/ { next }

    END {
        emit_current()
    }

    function emit_current() {
        if (!in_entry || doc_count == 0) return

        # If we have explicit sources from a list, use them; otherwise use current_source
        if (src_count > 0) {
            for (s = 0; s < src_count; s++) {
                for (d = 0; d < doc_count; d++) {
                    print sources[s] "|" doc_list[d]
                }
            }
        } else if (current_source != "") {
            for (d = 0; d < doc_count; d++) {
                print current_source "|" doc_list[d]
            }
        }

        # Reset for next entry
        in_entry = 0
        reading_source = 0
        reading_docs = 0
        current_source = ""
        doc_count = 0
        src_count = 0
        delete doc_list
        delete sources
    }
    ' "$CONFIG_PATH"
}

# --- Main evaluation ---

# Check if staged changes include any actual source code (not just docs)
has_source_changes() {
    echo "$STAGED" | grep -qEv '^docs/|^memory/|^\.gitkeep|^$'
}

# No staged files → nothing to check
if [[ -z "$STAGED" ]]; then
    exit 0
fi

# Always-required docs: only enforce when source code (not just docs) is changed
if has_source_changes; then
    while IFS= read -r doc; do
        [[ -n "$doc" ]] && require_doc "$doc"
    done < <(parse_always_docs)
fi

# Conditional rules: source_glob|doc_path lines
while IFS='|' read -r source_glob doc_path; do
    [[ -z "$source_glob" || -z "$doc_path" ]] && continue
    if glob_matches "$source_glob"; then
        require_doc "$doc_path"
    fi
done < <(parse_conditional_rules)

# --- Exit ---
if [[ "$STRICT" == true && "$WARNINGS" -gt 0 ]]; then
    exit 1
fi
exit 0
