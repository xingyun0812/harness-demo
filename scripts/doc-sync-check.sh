#!/usr/bin/env bash
# scripts/doc-sync-check.sh
# 检查文档与代码的一致性

set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
ERRORS=0

# 颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

echo "开始文档同步检查..."

# 检查 Spring Boot 版本
SB_VERSION=$(grep -A1 'spring-boot-starter-parent' "$PROJECT_DIR/pom.xml" | grep version | sed 's/.*<version>\(.*\)<\/version>.*/\1/')
echo "Spring Boot 版本: $SB_VERSION"

# 检查关键文件中的旧版本
for file in "$PROJECT_DIR/CLAUDE.md" "$PROJECT_DIR/docs/architecture.md" "$PROJECT_DIR/docs/harness-standards.md"; do
    if [[ -f "$file" ]]; then
        if grep -q 'Spring Boot 3\.2' "$file" 2>/dev/null; then
            echo -e "${RED}错误: $(basename $file) 包含旧版本 Spring Boot 3.2${NC}"
            ERRORS=$((ERRORS + 1))
        fi
    fi
done

# 检查 README 中的失效引用
if grep -q 'rename-template.sh\|dependabot.yml' "$PROJECT_DIR/README.md" 2>/dev/null; then
    echo -e "${RED}错误: README 包含已删除文件的引用${NC}"
    ERRORS=$((ERRORS + 1))
fi

if [[ $ERRORS -eq 0 ]]; then
    echo -e "${GREEN}✅ 检查通过${NC}"
    exit 0
else
    echo -e "${RED}❌ 发现 $ERRORS 个问题${NC}"
    exit 1
fi
