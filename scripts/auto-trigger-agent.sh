#!/usr/bin/env bash
# scripts/auto-trigger-agent.sh
# 自动触发 Claude Code agent 的辅助脚本
#
# 用法:
#   bash scripts/auto-trigger-agent.sh code-review    # 触发代码审查
#   bash scripts/auto-trigger-agent.sh eval-gate      # 触发质量门禁

set -euo pipefail

AGENT_NAME="${1:-}"
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if [[ -z "$AGENT_NAME" ]]; then
    echo "Usage: bash scripts/auto-trigger-agent.sh <agent-name>"
    echo ""
    echo "Available agents:"
    echo "  code-review    - 代码审查"
    echo "  eval-gate      - 质量门禁"
    exit 1
fi

case "$AGENT_NAME" in
    code-review)
        echo "🤖 自动触发代码审查..."
        echo ""
        echo "提示: 在 Claude Code 中运行:"
        echo "  /agent code-review"
        echo ""
        echo "或手动审查变更:"
        echo "  git diff main...HEAD"
        ;;
    eval-gate)
        echo "🛡️  自动触发质量门禁..."
        echo ""
        echo "运行检查:"
        echo "  1. mvn verify (测试 + 覆盖率)"
        echo "  2. mvn checkstyle:check"
        echo "  3. mvn pmd:check"
        echo ""
        
        # 自动运行检查
        echo "[1/3] Running mvn verify..."
        if mvn verify -q 2>/dev/null; then
            echo "✅ Tests + Coverage passed"
        else
            echo "❌ Tests or coverage failed"
            exit 1
        fi
        
        echo "[2/3] Running Checkstyle..."
        if mvn checkstyle:check -q 2>/dev/null; then
            echo "✅ Checkstyle passed"
        else
            echo "❌ Checkstyle failed"
            exit 1
        fi
        
        echo "[3/3] Running PMD..."
        if mvn pmd:check -q 2>/dev/null; then
            echo "✅ PMD passed"
        else
            echo "❌ PMD failed"
            exit 1
        fi
        
        echo ""
        echo "🎉 All eval gate checks passed!"
        ;;
    *)
        echo "Unknown agent: $AGENT_NAME"
        echo "Available: code-review, eval-gate"
        exit 1
        ;;
esac
