---
name: project_root
description: 项目根上下文 — harness-demo 的定位和用途
---

harness-demo 是一个 Spring Boot 3.2 + Java 17 项目，定位为 **Claude Code Java 工程脚手架模板**，
用于演示和测试 Claude Code harness 能力，供团队 fork 后作为新项目起点。

关键约束：

- 作为模板，"自己的规矩自己要守"——文档与实现的漂移即技术债
- 覆盖率门禁 80%（JaCoCo BUNDLE LINE）
- 分层严格：Controller → Service → Repository，外加 dto / model / exception / client / config
- 所有写操作必须 `@Transactional(rollbackFor = Exception.class)`
- 非平凡架构决策必须记 ADR（`docs/adr/`）
