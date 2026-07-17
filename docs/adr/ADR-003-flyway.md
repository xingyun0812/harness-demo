# ADR-003: Database Migration with Flyway

- **Status**: accepted
- **Date**: 2026-07-14
- **Deciders**: xingyun0812

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
- Negative: Kingbase 的 Flyway 方言需要额外依赖（`flyway-database-kingbase`）
- Negative: 回滚需要手写 SQL，Flyway 不提供自动回滚
