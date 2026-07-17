# ADR-001: Choose MyBatis-Plus as ORM Framework

- **Status**: accepted
- **Date**: 2026-07-14
- **Deciders**: xingyun0812

## Context

项目需要数据访问层支持 CRUD 操作和多数据库（H2/MySQL/Kingbase）。需要选择一个 ORM 框架，权衡开发效率、多数据库兼容性和学习成本。

## Decision

选择 MyBatis-Plus 3.5.9 作为 ORM 框架。

理由：
- 基于 MyBatis 但提供 BaseMapper 免写 SQL，开发效率高
- 国内生态成熟，中文文档丰富
- 多数据库兼容性好（H2/MySQL/Kingbase 已验证）
- 与 Flyway 配合良好，迁移脚本可跨数据库
- 团队 MyBatis 经验更丰富，学习成本低

## Consequences

- Positive: 基础 CRUD 无需手写 SQL 和 XML 映射文件
- Positive: 多数据库切换只需改 DataSource 配置
- Negative: 复杂查询仍需手写 SQL 或使用注解
- Negative: 与 Spring Data JPA 等竞品不直接兼容
