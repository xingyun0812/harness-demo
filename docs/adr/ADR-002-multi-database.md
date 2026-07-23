# ADR-002: Multi-Database Support Strategy

- **Status**: accepted
- **Date**: 2026-07-14
- **Deciders**: <your-name>

## Context

项目的目标用户可能使用不同的数据库（MySQL、Kingbase8）。需要一种方式在不改代码的情况下切换数据库。

## Decision

采用 Spring Profile + 外部化配置的策略：
- 默认 profile (application.yml) 使用 H2 内存数据库，适合本地开发和 CI
- `mysql` profile 使用 MySQL，`kingbase` profile 使用 Kingbase8
- 所有 DataSource 配置均支持通过环境变量覆盖（DATASOURCE_URL / DRIVER / USERNAME / PASSWORD）
- Flyway 迁移脚本兼容所有目标数据库

## Consequences

- Positive: 切换数据库只需 `--spring.profiles.active=mysql`
- Positive: CI 使用 H2 无需额外基础设施
- Negative: 某些数据库特有功能无法使用（如 MySQL 的 JSON 类型）
- Negative: 所有迁移 SQL 必须在三种数据库上都能运行，增加测试负担
