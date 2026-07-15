# harness-demo 工程规范全景

本文档一次性汇总所有规范及其生效机制，便于新成员或审计时快速理解全貌。

## 1. 代码规范

### 1.1 语言/框架

| 项目 | 值 |
|------|-----|
| 语言 | Java 17 |
| 框架 | Spring Boot 3.2.5 |
| 构建 | Maven |

### 1.2 代码风格 — Checkstyle + Google Style

**位置**：`pom.xml` line 101–125

**生效时机**：`mvn validate` 阶段（及任何包含 validate 的生命周期）

**强制方式**：`<failsOnError>true</failsOnError>`，风格不合格直接 BUILD FAILURE

**如何触发**：

```bash
mvn checkstyle:check        # 单独执行
mvn clean verify            # 连带执行（validate → check）
```

### 1.3 覆盖率门禁 — JaCoCo

**位置**：`pom.xml` line 54–89

**生效时机**：`mvn verify` 阶段，测试执行后

**规则**：

- BUNDLE 级别 LINE COVEREDRATIO ≥ 60%
- `com/example/harnessdemo/HarnessDemoApplication.class` 排除（启动类无业务逻辑，不参与统计）

**如何触发**：

```bash
mvn verify                  # 不达标则 BUILD FAILURE
mvn test                    # 不检查覆盖率（仅跑测试）
```

**如何绕过（不推荐）**：

```bash
mvn verify -Dcoverage.threshold=0
```

### 1.5 架构分层

### 1.5 架构分层

```
src/main/java/com/example/harnessdemo/
  controller/     # REST 控制器（对内暴露的 HTTP API）
  dto/            # 请求/响应 DTO（网络边界契约，与 model 独立演化）
  service/        # 业务逻辑接口 + 实现
  repository/     # 数据访问接口 + 实现
  model/          # 领域模型（含业务行为）
  client/         # 外部服务客户端（HTTP/RPC 出站 gateway）
  config/         # 配置类
```

非平凡架构决策需记录到 `docs/architecture/adr/`。

---

## 2. Git 规范

### 2.1 分支策略

- 默认分支：`main`
- 工作流：Issue → feature branch → PR → review → merge
- 禁止直推 `main`（双向防护：Git hook + CI 配置）

### 2.2 Pre-commit Hook — Checkstyle + Doc-Sync

**位置**：`.git/hooks/pre-commit`（由 `scripts/setup-hooks.sh` 安装）

**生效时机**：每次 `git commit` 前

两阶段检查：

1. **Checkstyle（阻断）**：代码风格不合格 → commit 被中断。

   ```bash
   if ! mvn checkstyle:check -q 2>/dev/null; then
       echo "Checkstyle failed. Fix issues before committing."
       exit 1
   fi
   ```

2. **Doc-Sync（非阻断）**：源文件变更但对应文档未暂存 → 打印警告（commit 仍继续）。

   ```bash
   bash "$PROJECT_DIR/scripts/check-doc-sync.sh"
   ```

   **映射规则**定义在 `.doc-sync.yml`，是该检查的唯一事实来源。核心逻辑：

   - **`always` 规则**：任意源文件变更（非纯文档变更）时，必须同时暂存 `progress.md`、`docs/PROJECT_STATUS.md`、`session-handoff.md`。
   - **`conditional` 规则**：特定源文件（如 `pom.xml`、controller、CI 配置等）变更时，要求对应文档同步暂存。

   > 详见 `.doc-sync.yml` 文件及 `CLAUDE.md` → Document Sync 章节。

**效果**：阻断性风格错误阻止 commit；文档遗漏仅提醒不阻塞。

### 2.3 Pre-push Hook — 保护 main

**位置**：`.git/hooks/pre-push`（由 `scripts/setup-hooks.sh` 安装）

**生效时机**：每次 `git push` 前

**逻辑**：目标 ref 为 `refs/heads/main` 或 `refs/heads/master` → 拒绝并提示用 PR。

### 2.4 Commit 规范

采用 conventional commits 格式：

```
feat:      新功能
fix:       缺陷修复
docs:      文档
refactor:  重构
test:      测试
chore:     构建/工具维护
```

### 2.5 GitHub 模板

| 模板 | 位置 |
|------|------|
| Bug report | `.github/ISSUE_TEMPLATE/bug.md` |
| Feature request | `.github/ISSUE_TEMPLATE/feature.md` |
| Pull Request | `.github/PULL_REQUEST_TEMPLATE.md` |

---

## 3. CI/CD

### 3.1 GitHub Actions 工作流

**位置**：`.github/workflows/ci.yml`

**触发条件**：push / pull_request 到 `main` 分支

**执行阶段**：

| 阶段 | 命令 | 作用 |
|------|------|------|
| 1. Checkstyle | `mvn checkstyle:check -q` | 风格检查 |
| 2. Build & Coverage | `mvn verify` | 编译 + 测试 + 覆盖率门禁 |
| 3. Upload artifact | coverage report → GitHub | 可下载 HTML 报告 |

**当前状态**：工作流文件已就绪。连接 remote 仓库后自动生效。

---

## 4. Claude Code 工程化

### 4.1 CLAUDE.md

**位置**：项目根目录 `CLAUDE.md`

**生效机制**：Claude Code 启动会话时自动读取，作为系统指令的一部分。

**内容覆盖**：

- 构建/测试/检查/安全扫描命令
- 代码标准（Java 17, Spring Boot 3.2, Checkstyle Google）
- 覆盖率门禁值（60%）
- 架构决策记录要求（`docs/architecture/adr/`）
- Process（Issue → branch → PR）
- 环境变量表
- 记忆系统索引入口

### 4.2 权限控制 — settings.json

**位置**：`.claude/settings.json`

**生效机制**：Claude Code 启动时加载，影响会话中所有工具调用。

**三级模型**：

| 级别 | 含义 | 当前规则 |
|------|------|----------|
| `allow` | 自动放行 | mvn, git, java, pwd, echo, ls, cat, chmod, which, type |
| `ask` | 询问用户 | curl |
| `deny` | 拒绝执行 | git push --force, git reset --hard, rm -rf |

注意：`deny` 仅对 Bash 工具生效，不影响 Read/Write 等文件工具。

### 4.3 Agent

| Agent | 位置 | 用途 |
|-------|------|------|
| code-review | `.claude/agents/code-review.md` | 代码审查代理，在 PR 阶段调用 |
| eval-gate | `.claude/agents/eval-gate.md` | 评估门禁代理，验收前检查 |

**生效时机**：在 Claude Code 中通过 `/agent` 或工作流引用时触发。

### 4.4 Workflow

| Workflow | 位置 | 用途 |
|----------|------|------|
| ci-monitor | `.claude/workflows/ci-monitor.md` | CI 流水线状态监控 |

### 4.5 Hooks（Claude 侧）

**位置**：`.claude/hooks/pre-push`

**生效机制**：Claude Code 执行 git push 前触发。与 `.git/hooks/pre-push` 不同，这是 Claude 客户端侧的额外防护。

### 4.6 Memory 系统

**位置**：`memory/MEMORY.md` + `memory/*.md`

**生效机制**：Claude Code 启动时，`CLAUDE.md` 中的引用指向 `memory/MEMORY.md`，该文件作为索引列出所有记忆文件。Claude 会在相关上下文中自动检索。

---

## 5. 文档治理

| 文档 | 位置 | 更新频率 | 过期后果 |
|------|------|----------|----------|
| Architecture | `docs/architecture/architecture.md` | 架构变动时 | 新成员理解偏差 |
| Roadmap | `docs/roadmap/roadmap.md` | 功能达成时 | 目标模糊 |
| Project Status | `docs/PROJECT_STATUS.md` | 每轮改动 | CI/Review 认知不一致 |
| ADR | `docs/architecture/adr/` | 每次非平凡决策 | 架构决策丢失上下文 |
| Feature List | `feature_list.json` | 功能完成时 | 无法追踪已交付内容 |
| Progress | `progress.md` | 每轮改动 | 下一步方向丢失 |
| Session Handoff | `session-handoff.md` | 会话结束 | 下一 Claude 会话冷启动 |

**同步原则**：修改代码后必须同步相关文档；文档与工程状态之间的漂移视为技术债。

---

## 快速参考

### 本地开发常用命令

```bash
mvn spring-boot:run                    # 启动开发服务器
mvn test                               # 跑单元测试
mvn verify                             # 全量验证（测试 + 覆盖率门禁）
mvn checkstyle:check                   # 风格检查
bash scripts/setup-hooks.sh            # 安装 git hooks
```

### 规范化开发流程

1. 从 `main` 创建 feature branch
2. 开发，频繁 `git commit`（pre-commit 自动检查风格 + doc-sync）
3. `mvn verify` 通过
4. 同步文档（progress / status / feature_list）
5. 提交 PR（pre-push 拦截直推 main）
6. CI 在 GitHub 上二次验证
7. Review → merge

---

## 附录：Doc-Sync 检查机制说明

### 完整链路

```
开发者编码 → git add（暂存文件）
                   ↓
            git commit
                   ↓
     .git/hooks/pre-commit（由 scripts/setup-hooks.sh 安装）
                   ↓
            ┌── 阶段 1：Checkstyle（阻断）
            │     失败 → exit 1，commit 中断
            │
            └── 阶段 2：Doc-Sync（非阻断，仅提醒）
                  scripts/check-doc-sync.sh
                        ↓
                  读取 .doc-sync.yml（规则配置文件）
                        ↓
                  ┌── always 规则
                  │   任意源文件变更（非纯文档变更）→ 需暂存 progress.md 等
                  │
                  └── conditional 规则
                       逐条匹配暂存文件：
                       → pom.xml 变更 → harness-standards.md + CLAUDE.md
                       → controller/*.java 变更 → README.md
                       → .github/workflows/ 变更 → harness-standards.md
                       → .claude/ 变更 → harness-standards.md
                       → GitHub 模板变更 → CONTRIBUTING.md
                       缺失则打印 [WARN]
                        ↓
                  exit 0（不阻断 commit）
```

### 规则配置：`.doc-sync.yml`

`.doc-sync.yml` 是规则配置的单一事实来源，`scripts/check-doc-sync.sh` 自动读取。

```yaml
always:
  - progress.md
  - docs/PROJECT_STATUS.md
  - session-handoff.md

conditional:
  - source: "pom.xml"
    docs:
      - docs/standards/harness-standards.md
      - CLAUDE.md
  ...
```

- `always` — 任何源文件变更都要更新的文档
- `conditional` — 特定源文件匹配时要求更新的文档
- `source` 支持单个 glob 或多个 glob 列表

### 推广到其他项目

1. 复制 `scripts/check-doc-sync.sh` 和 `scripts/setup-hooks.sh`
2. 在项目根目录创建 `.doc-sync.yml`，按自身文档结构编写规则
3. 执行 `bash scripts/setup-hooks.sh` 安装 pre-commit hook
4. **无需修改脚本**——`check-doc-sync.sh` 自动读取 `.doc-sync.yml`

### 与 `--no-verify` 的关系

开发者可通过 `git commit --no-verify` 绕过本地 hook，因此 doc-sync 的最终防线是 PR 流水线。
（当前 PR 阶段未配置，后续可加 `bash scripts/check-doc-sync.sh --strict` 步骤。）
