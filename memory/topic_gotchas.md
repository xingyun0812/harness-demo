---
name: gotchas
description: 已知踩坑 — 容易出错的地方，新成员必看
---

## H2 内存库跨 Spring 上下文共享（重要）

H2 内存数据库按 **名字** 共享（`jdbc:h2:mem:harness-demo`），不是按 Spring 上下文。
两个 `@SpringBootTest` 类即使 `webEnvironment` 不同也会共享同一个 H2 实例。

后果：`HarnessDemoIntegrationTest` 通过 HTTP 提交的用户会残留，导致后续的
`UserRepositoryTest.selectAll_returnsAllUsers` 看到 3 行而不是 2 行。

应对（见代码）：

- `HarnessDemoIntegrationTest` 加 `@DirtiesContext(AFTER_CLASS)` 销毁上下文
- `UserRepositoryTest.selectAll_returnsAllUsers` 先清表再插入（不依赖空库）
- 集成测试用 `@Transactional` 回滚自己插入的数据

## `@Transactional` 默认只回滚 RuntimeException

Spring `@Transactional` 不指定 `rollbackFor` 时，**受检异常（IOException 等）不触发回滚**。
Alibaba p3c 的 `TransactionMustHaveRollbackRule` 会强制要求显式声明 `rollbackFor`。

模板约定：所有写方法用 `@Transactional(rollbackFor = Exception.class)`，见
`docs/harness-standards.md §1.6`。

## GlobalExceptionHandler 不要用消息字符串做控制流

错误示例（早期版本）：

```java
if (ex.getMessage().contains("not found")) { return 404; }
```

问题：任何 500 错误只要 message 里带 "not found" 就会被误判成 404。

正确做法：定义类型化领域异常（`exception/ResourceNotFoundException`），按类型路由。
见 `GlobalExceptionHandler.java` 与 `ResourceNotFoundException.java`。

## Kingbase Flyway 默认禁用

`application-kingbase.yml` 设 `flyway.enabled: false`，因为 `flyway-database-kingbase`
是社区实现，非 Flyway 官方支持。生产 Kingbase 环境的 schema 由 DBA 手动维护。
如需启用，见 `docs/adr/ADR-003-flyway.md` 的 Kingbase 章节。

## docker-compose healthcheck 不能用 curl

基础镜像 `eclipse-temurin:17-jre-alpine` **不带 curl**，只有 BusyBox wget。
healthcheck 必须用 `wget --spider`，见 `docker-compose.yml`。

## OWASP 不在 CI 里

`mvn org.owasp:dependency-check-maven:check` 每次需下载 NVD 漏洞库（数百 MB，5–10 分钟），
不适合每次 CI 都跑。CI 里没有这一步，改为本地按需执行（建议每周或 release 前一次）。
见 `docs/harness-standards.md §1.4`。

## `.claude/hooks/` 目录不是 Claude Code 的 hook 机制

Claude Code 的 hook 必须在 `.claude/settings.json` 的 `hooks` 字段按事件
（`PreToolUse`/`PostToolUse` 等）配置，不是在 `.claude/hooks/` 放脚本。
本项目用 `scripts/claude-pre-push-guard.sh` + `settings.json` 的 `PreToolUse` 实现
push 保护，见 `docs/harness-standards.md §4.5`。
