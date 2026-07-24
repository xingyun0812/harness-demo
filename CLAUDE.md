# harness-demo

## Role

harness-demo — 用于演示和测试 Claude Code Java 项目 harness 功能的 Spring Boot 项目。

## Build & Run

```bash
# Start dev server
mvn spring-boot:run

# Build without tests
mvn clean package -DskipTests

# Build with tests
mvn clean verify
```

## Test

```bash
# Unit tests
mvn test

# Full verification (tests + coverage gate)
mvn verify

# Lint (checkstyle)
mvn checkstyle:check

# Security scan (OWASP dependency check)
mvn org.owasp:dependency-check-maven:check
```

## Code Standards

- **Language**: Java 17
- **Framework**: Spring Boot 3.4.x
- **Build**: Maven
- **Style**: Checkstyle (Google style)
- **Coverage Gate**: 80% line coverage (JaCoCo)
- **Architecture Decisions**: `docs/adr/` — mandatory for non-trivial choices
- **Process**: Issue → feature branch → PR — No direct pushes to `main`

## Interaction Rules

### Clarify Before Acting

需求不明确时反问，不猜测。如果存在多种理解，列出选项让我确认。不确定就是不确定，不要替我做决定。

### Surgical Changes

只改必须改的，只清理自己造成的垃圾。

- 不改动无关代码、注释、格式
- 不重构没坏的东西
- 匹配现有风格，即使你更喜欢另一种写法
- 发现无关的死代码，提一句就行，不要删
- 你的改动造成的 unused import/变量必须清理，但不要清理本来就存在的

### Document Sync

修改代码后必须同步相关文档，漂移视为技术债。

**每次改动必更新：**
- `progress.md` — 当前进度、下一步
- `docs/PROJECT_STATUS.md` — 完成状态总览
- `session-handoff.md` — 会话结束时记录上下文

**视情况更新：**
- `docs/architecture.md` — 架构/分层变更时
- `docs/harness-standards.md` — 工程规范变更时
- `docs/roadmap.md` — 功能达成时
- `feature_list.json` — 功能完成时
- `memory/` — 需要持久化的上下文
- `docs/adr/` — 非平凡架构决策
- `README.md` — 功能或使用方式变更时
- `.github/ISSUE_TEMPLATE/` `.github/PULL_REQUEST_TEMPLATE.md` — 流程变更时
- `CHANGELOG.md` — 版本发布时

**验证文档同步：**
```bash
bash scripts/doc-sync-check.sh    # 本地检查
bash scripts/doc-sync-check.sh --ci  # CI 模式（严格）
```

**自动防护**：`git commit` 前会自动运行 doc-sync-check，版本漂移会阻断提交。

## Config & Deploy

| Purpose | Path |
|---------|------|
| Application config | `src/main/resources/application.yml` |

本地用 `mvn spring-boot:run` 即可。Docker / Compose **非必须**，本 demo 不维护；需要容器化部署时再补。

## Key Architecture Files

- `docs/architecture.md` — system overview
- `docs/roadmap.md` — future plans & gaps
- `docs/adr/` — architecture decision records
- `docs/PROJECT_STATUS.md` — current completion status

## Environment

| Variable | Default | Purpose |
|----------|---------|---------|
| `SERVER_PORT` | 8080 | HTTP server port |
| `SPRING_PROFILES_ACTIVE` | dev | Active Spring profile |

## Memory & Documentation

- `memory/MEMORY.md` — memory index (loaded on startup)
- `memory/` — per-topic memory files
- `feature_list.json` — all features with status + verification evidence
- `progress.md` — current active feature, milestones, next action
- `session-handoff.md` — cross-session context handoff
