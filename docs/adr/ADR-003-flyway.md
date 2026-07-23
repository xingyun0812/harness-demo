# ADR-003: Database Migration with Flyway

- **Status**: accepted
- **Date**: 2026-07-14
- **Deciders**: <your-name>

## Context

项目需要版本化数据库 Schema 变更，确保多环境（dev/CI/prod）数据库结构一致。

## Decision

选择 Flyway 作为数据库迁移工具，集成方式为 Spring Boot 自动配置 + `classpath:db/migration/` 下的 SQL 脚本。

理由：
- Spring Boot 提供 Flyway 自动配置，集成成本极低
- SQL-based 迁移更透明，DBA 友好
- 支持 H2/MySQL/Kingbase 所有目标数据库
- `baseline-on-migrate: true` 允许对已有数据库执行首次迁移

## Consequences

- Positive: 数据库变更版本化，可复现
- Positive: Spring Boot 自动配置，几乎零配置
- Negative: Kingbase 的 Flyway 方言需要额外依赖（`flyway-database-kingbase`，社区实现非官方）
- Negative: 回滚需要手写 SQL，Flyway 不提供自动回滚

## Kingbase 特殊情况

Kingbase profile（`application-kingbase.yml`）默认 **禁用 Flyway**（`flyway.enabled: false`），原因：

1. `flyway-database-kingbase` 是社区实现的方言包，非 Flyway 官方支持，稳定性未充分验证
2. 生产 Kingbase 环境 schema 通常由 DBA 用独立工具管理，Flyway 自动迁移与 DBA 流程可能冲突
3. H2（CI/默认 dev）和 MySQL profile 下 Flyway 正常启用，多数据库迁移只在 H2/MySQL 上验证

如需对 Kingbase 启用 Flyway：

```yaml
# application-kingbase.yml
spring.flyway.enabled: true   # 或通过环境变量 KINGBASE_FLYWAY_ENABLED=true
```

并提前验证 `db/migration/` 下所有 SQL 在 Kingbase 方言下可执行（如 `AUTO_INCREMENT` → `SERIAL` 等差异）。
