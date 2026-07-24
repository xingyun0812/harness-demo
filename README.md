# harness-demo

> **Claude Code Java 脚手架模板** — 开箱即用的 Spring Boot 3 工程，内置 CI、代码规范、Claude Code 配置。

---

## 从此模板创建新项目

### 第一步：Fork 或克隆

```bash
git clone https://github.com/xingyun0812/harness-demo.git my-new-project
cd my-new-project
git remote set-url origin git@github.com:<your-org>/<your-project>.git
```

### 第二步：一键重命名

```bash
# 手动全局替换占位符（推荐使用 IDE 全局搜索替换）
# 或使用 cookiecutter / maven archetype 等工具
```

详见下方 [必须修改的内容](#必须修改的内容) 手动替换指南。

### 第三步：初始化 Git & 验证

```bash
git add -A && git commit -m "chore: initialize from harness-demo template"
mvn clean verify    # 确认基线绿色（37 个测试，0 失败）
```

### 第四步：配置 GitHub 仓库

1. 在 GitHub 仓库 **Settings → Branches** 开启 `main` 的 branch protection：
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass (选 `CI`)
   - ✅ Do not allow bypassing the above settings
2. 更新 `.github/CODEOWNERS` 中的 GitHub 账号为你的团队 Lead

---

## 必须修改的内容

Fork 后必须手动替换下表中所有占位符（推荐使用 IDE 全局搜索替换）：

| 搜索关键词 | 需替换为 | 影响文件 |
|-----------|---------|---------|
| `com.example` | 你的 groupId | `pom.xml`, 所有 Java 文件包声明 |
| `harnessdemo` | 你的包名（无分隔符） | 目录路径 + Java 包名 |
| `harness-demo` | 你的项目名（kebab-case） | `pom.xml`, `application.yml`, `docker-compose.yml`, `CLAUDE.md` 等 |
| `Demo project for Claude Code harness setup` | 你的项目描述 | `pom.xml` |
| `xingyun0812` | 你的 GitHub 账号 | `.github/CODEOWNERS` |

---

## Quick Start（日常开发）

```bash
# Build & test（含覆盖率门禁 + Checkstyle + PMD）
mvn clean verify

# 启动开发服务器（H2 内存库，无需配置）
mvn spring-boot:run

# 健康检查
curl http://localhost:8080/api/health

# Docker 启动（含 MySQL）
docker compose up --build
```

## Requirements

- JDK 17+
- Maven 3.9+（或用 `./mvnw`）
- Docker & Docker Compose（可选，用于容器化运行）

## Project Structure

```
src/
  main/java/com/example/harnessdemo/
    controller/     # REST controllers + 全局异常处理
    service/        # Business logic（@Transactional 事务）
    repository/     # Data access（MyBatis-Plus）
    model/          # Domain models
    dto/            # Request/Response DTOs
    exception/      # 业务异常（类型化路由）
    client/         # 外部服务调用（HTTP / RPC）
    config/         # Spring 配置（TraceIdFilter、OpenAPI 等）
  test/             # Unit & integration tests（覆盖率 ≥ 80%）
```

## Build Commands

| 命令 | 用途 |
|------|----|
| `mvn clean verify` | 构建 + 测试 + 覆盖率门禁 + Lint |
| `mvn spring-boot:run` | 启动开发服务器 |
| `mvn checkstyle:check` | Google 代码风格检查 |
| `mvn pmd:check` | Alibaba p3c 编码规范检查 |
| `mvn org.owasp:dependency-check-maven:check` | 依赖安全扫描（本地按需执行） |

详见 `CLAUDE.md` 完整命令列表。

## 文档索引

| 文档 | 说明 |
|------|-----|
| [`CLAUDE.md`](CLAUDE.md) | Claude Code 专用规范（必读） |
| [`docs/architecture.md`](docs/architecture.md) | 系统架构说明 |
| [`docs/harness-standards.md`](docs/harness-standards.md) | 工程规范（覆盖率/安全/CI/git 等） |
| [`docs/local-database-setup.md`](docs/local-database-setup.md) | 本地数据库切换（H2/MySQL/Kingbase） |
| [`docs/adr/`](docs/adr/) | Architecture Decision Records |
| [`.claude/README.md`](.claude/README.md) | Claude Code 配置目录说明 |
