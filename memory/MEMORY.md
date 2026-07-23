# Memory Index

本目录是 Claude Code 的项目级记忆系统。Claude Code 启动时会读取 `CLAUDE.md`，其中引用本文件作为索引，按需加载具体记忆。

## 记忆条目

| 文件 | 主题 | 关键词 |
|------|------|--------|
| [topic_project_root.md](topic_project_root.md) | 项目根上下文 | 模板定位、覆盖率门禁、分层、ADR 要求 |
| [topic_tech_stack.md](topic_tech_stack.md) | 技术栈与版本 | Java 17, Spring Boot 3.4, MyBatis-Plus, JaCoCo 80% |
| [topic_gotchas.md](topic_gotchas.md) | 已知踩坑 | H2 共享、@Transactional rollbackFor、字符串控制流、Kingbase Flyway、curl/wget、OWASP、hooks |
| [topic_workflow.md](topic_workflow.md) | 开发流程 | clone → hooks → branch → verify → PR → merge |

## 如何添加新记忆

1. 在本目录创建 `topic_<name>.md`，frontmatter 包含 `name` 和 `description`
2. 在本 `MEMORY.md` 的表格中加一行索引
3. 保持每条记忆聚焦单一主题，避免超大文件
4. 记忆是"事实+约束"，不是教程——教程写 `docs/`
