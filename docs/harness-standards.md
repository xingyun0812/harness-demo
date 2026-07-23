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

- BUNDLE 级别 LINE COVEREDRATIO ≥ 80%（2026-07-15 从 60% 提升至 80%）
- `com/example/harnessdemo/HarnessDemoApplication.class` 排除（启动类无业务逻辑，不参与统计）
- `com/example/harnessdemo/config/TraceIdFilter.class` 排除（工具类 Filter，无业务逻辑）

**如何触发**：

```bash
mvn verify                  # 不达标则 BUILD FAILURE
mvn test                    # 不检查覆盖率（仅跑测试）
```

**如何绕过（不推荐）**：

```bash
mvn verify -Dcoverage.threshold=0
```

### 1.4 依赖安全 — OWASP Dependency Check

**位置**：`pom.xml`（OWASP dependency-check-maven 插件配置）

**生效时机**：仅单独执行（不绑到 `mvn verify`，保持本地构建快速）。CI 在 verify 之后单独跑一步。

**规则**：CVSS ≥ 7 报出，但**不阻断构建**（`pom.xml` 设 `failBuildOnCVSS=11` 实际不阻断；CI 步骤 `continue-on-error: true` 双保险）。

**设计考量**：安全知悉优先于阻塞交付；CI 中可查看报告但不会打断流水线；本地开发不被 OWASP 网络/数据库拖慢。

**如何触发**：

```bash
mvn org.owasp:dependency-check-maven:check
```

### 1.5 架构分层

```
src/main/java/com/example/harnessdemo/
  controller/     # REST 控制器（对内暴露的 HTTP API）
  dto/            # 请求/响应 DTO（网络边界契约，与 model 独立演化）
  service/        # 业务逻辑接口 + 实现
  repository/     # 数据访问接口 + 实现
  model/          # 领域模型（含业务行为）
  exception/      # 领域异常（类型化错误处理，映射 HTTP 状态码）
  client/         # 外部服务客户端（HTTP/RPC 出站 gateway）
  config/         # 配置类
```

非平凡架构决策需记录到 `docs/adr/`。

### 1.6 事务管理 — `@Transactional`

**规则**：

| 操作类型 | 注解 | 说明 |
|----------|------|------|
| 写（insert/update/delete） | `@Transactional(rollbackFor = Exception.class)` | 显式指定 rollbackFor，按 Alibaba p3c 规范避免默认只回滚 RuntimeException 的陷阱 |
| 只读（select） | `@Transactional(readOnly = true)` | 走读优化路径，并防止误写 |

**位置**：`service/` 层方法上（不要放在 `controller/` 或 `repository/`）

**生效机制**：Spring AOP 代理，类加载时织入。方法被同类内部调用时不会走代理（self-invocation 陷阱）——跨方法调用须通过 Bean。

**示例**（见 `UserService.java`）：

```java
@Transactional(rollbackFor = Exception.class)
public UserResponse create(CreateUserRequest request) { ... }

@Transactional(readOnly = true)
public List<UserResponse> listAll() { ... }
```

**为什么强制 `rollbackFor`**：Spring `@Transactional` 默认只回滚 `RuntimeException` 和 `Error`，受检异常（如 `IOException`）不回滚。显式 `rollbackFor = Exception.class` 确保所有异常都触发回滚，避免漏回滚导致脏数据。Alibaba p3c 的 `TransactionMustHaveRollbackRule` 会强制此规则（`mvn pmd:check`）。

---

## 2. Git 规范

### 2.1 分支策略

- 默认分支：`main`
- 工作流：Issue → feature branch → PR → review → merge
- 禁止直推 `main`（三层防护：Claude PreToolUse hook + Git pre-push hook + GitHub branch protection，见 2.3 / 4.5 / 2.6）

### 2.2 Pre-commit Hook — Checkstyle

**位置**：`.git/hooks/pre-commit`（由 `scripts/setup-hooks.sh` 安装）

**生效时机**：每次 `git commit` 前

```bash
if ! mvn checkstyle:check -q 2>/dev/null; then
    echo "Checkstyle failed. Fix issues before committing."
    exit 1
fi
```

**效果**：代码风格不合格 → commit 被中断。

### 2.3 Pre-push Hook — 保护 main（Git 侧，权威）

**位置**：`.git/hooks/pre-push`（由 `scripts/setup-hooks.sh` 安装，需手动执行一次）

**生效时机**：每次 `git push` 前（由 git 本身触发，与是否使用 Claude Code 无关）

**逻辑**：目标 ref 为 `refs/heads/main` 或 `refs/heads/master` → 拒绝并提示用 PR。

**安装**：

```bash
bash scripts/setup-hooks.sh    # clone 仓库后必须执行一次
```

⚠️ 此 hook 不会被 git 跟踪，每个新 clone 都需要重新安装。GitHub branch protection 是最终兑底（见 2.6）。

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

### 2.6 GitHub Branch Protection（最终兑底）

**位置**：GitHub 仓库 Settings → Branches → Branch protection rules

**必配项**：

- Protect `main`：require pull request before merge
- Require status checks to pass：CI（`ci.yml`）必须通过
- Require approvals：至少 1 个 review

**为何必配**：本地 hook（`.git/hooks/pre-push`、Claude PreToolUse）都可被绕过（`--no-verify`、非 Claude 终端 push），只有仓库侧的 branch protection 是不可绕过的。clone 仓库后第一件事：在 GitHub 仓库设置里开启。

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
| 3. OWASP | `mvn org.owasp:...:check`（continue-on-error） | 依赖安全扫描 |
| 4. Upload artifact | coverage report → GitHub | 可下载 HTML 报告 |

**当前状态**：工作流文件已就绪。连接 remote 仓库后自动生效。

---

## 4. Claude Code 工程化

### 4.1 CLAUDE.md

**位置**：项目根目录 `CLAUDE.md`

**生效机制**：Claude Code 启动会话时自动读取，作为系统指令的一部分。

**内容覆盖**：

- 构建/测试/检查/安全扫描命令
- 代码标准（Java 17, Spring Boot 3.2, Checkstyle Google）
- 覆盖率门禁值（80%）
- 架构决策记录要求（`docs/adr/`）
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

### 4.5 Hooks（Claude 侧 PreToolUse）

**位置**：`.claude/settings.json` → `hooks.PreToolUse`（matcher: `Bash`），脚本在 `scripts/claude-pre-push-guard.sh`

**生效机制**：Claude Code 在调用 Bash 工具前触发，检查命令是否为 `git push ... main/master`，命中则拒绝（exit 2）。与 `.git/hooks/pre-push` 互补：Claude 侧在命令发出前拦截，git 侧在 push 执行时拦截。

**覆盖场景**：

- `git push origin main` / `git push origin master` → 拒绝
- `git push origin main:main` / `git push origin HEAD:main` → 拒绝
- `git push`（无 refspec，当前分支为 main）→ 拒绝（查当前分支）
- `git push origin feature/main` / `git push origin feat/xxx` → 放行

**局限**：仅对 Claude Code 发起的 push 生效；终端直接 `git push` 不经过此 hook，靠 `.git/hooks/pre-push` 兑底（见 2.3）。

### 4.6 Memory 系统

**位置**：`memory/MEMORY.md` + `memory/*.md`

**生效机制**：Claude Code 启动时，`CLAUDE.md` 中的引用指向 `memory/MEMORY.md`，该文件作为索引列出所有记忆文件。Claude 会在相关上下文中自动检索。

---

## 6. 安全规范

### 6.1 安全基线

| 要求 | 标准 | 例外 |
|------|------|------|
| 依赖漏洞扫描 | OWASP Dependency Check (CVSS ≥ 7 警告，不阻断) | Maven 本地开发可跳过 |
| SQL 注入防护 | 必须使用参数化查询或 ORM（MyBatis-Plus 默认满足） | 无 |
| 敏感信息泄露 | 日志中不得输出密码/Token/身份证/手机号等 PII | 开发环境 Debug 需手动确认 |
| API 输入校验 | `@Valid` / `@NotBlank` 等 Bean Validation | 无 |
| 统一异常处理 | `@RestControllerAdvice` 全局兜底，不泄漏内部堆栈 | 500 级错误可记录堆栈但不返回客户端 |
| 配置文件 | 密码/密钥等敏感配置通过环境变量注入，不硬编码 | 默认值必须安全（如空密码） |
| HTTP 安全头 | 生产环境需配置 `X-Content-Type-Options`、`X-Frame-Options` 等 | 本地开发不强制 |

### 6.2 CI 安全门禁

CI 流水线中的安全步骤（`ci.yml`）：

- OWASP Dependency Check（CVSS ≥ 7 告警，`continue-on-error: true`）
- Checkstyle 阻断（防止不规范代码合入）
- JaCoCo 覆盖率门禁（80%，确保新增代码有测试覆盖）

### 6.3 生产环境安全 Checklist

- [ ] Spring Security 已配置（认证 + 鉴权）
- [ ] Actuator 端点已限制暴露（仅 health, info）
- [ ] 数据库密码通过密钥管理服务注入，非环境变量明文
- [ ] HTTPS 已强制开启
- [ ] 日志无敏感信息输出
- [ ] Rate limiting 已配置

---

| 文档 | 位置 | 更新频率 | 过期后果 |
|------|------|----------|----------|
| Architecture | `docs/architecture.md` | 架构变动时 | 新成员理解偏差 |
| Roadmap | `docs/roadmap.md` | 功能达成时 | 目标模糊 |
| Project Status | `docs/PROJECT_STATUS.md` | 每轮改动 | CI/Review 认知不一致 |
| ADR | `docs/adr/` | 每次非平凡决策 | 架构决策丢失上下文 |
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
mvn org.owasp:dependency-check-maven:check  # 依赖安全扫描
bash scripts/setup-hooks.sh            # 安装 git hooks
```

### 规范化开发流程

0. 首次 clone：`bash scripts/setup-hooks.sh` 安装 git hooks；在 GitHub 仓库设置开启 `main` branch protection（见 2.6）
1. 从 `main` 创建 feature branch
2. 开发，频繁 `git commit`（pre-commit 自动检查风格）
3. `mvn verify` 通过
4. 同步文档（progress / status / feature_list）
5. 提交 PR（Claude PreToolUse + git pre-push 拦截直推 main）
6. CI 在 GitHub 上二次验证
7. Review → merge
