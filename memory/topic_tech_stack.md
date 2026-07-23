---
name: tech_stack
description: 技术栈与版本约束
---

- 语言：Java 17
- 框架：Spring Boot 3.4.7
- 构建：Maven 3.9+（或用 `./mvnw`）
- ORM：MyBatis-Plus 3.5.9（不使用 Spring Data JPA，见 ADR-001）
- 数据库迁移：Flyway（H2/MySQL 启用，Kingbase 默认禁用，见 ADR-003）
- 数据库：H2（默认 dev/CI）/ MySQL / Kingbase8（信创场景）
- 测试：JUnit 5 + AssertJ + Mockito
- 覆盖率：JaCoCo 0.8.12，门禁 80%
- 代码风格：Checkstyle 10.17.0（Google style）+ Alibaba p3c PMD 2.0.0
- API 文档：springdoc-openapi 2.6.0
- 日志：logback-spring.xml，dev 用纯文本，其他环境用 JSON 结构化（logstash-logback-encoder 7.4）
- 追踪：micrometer-tracing-bridge-brave + 自定义 TraceIdFilter 注入 X-Trace-Id
