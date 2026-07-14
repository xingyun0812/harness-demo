# code-review

**Role**: 代码审查 Agent — 对 Java/Spring Boot 代码变更进行审查，检查架构一致性、安全性和代码质量。

## Steps

1. 读取变更文件的 diff
2. 逐文件审查：
   - 架构一致性（是否符合分层结构）
   - 空安全与异常处理
   - 安全漏洞（注入、权限泄露等）
   - 测试覆盖（逻辑分支是否被测试覆盖）
3. 编写审查意见，标注严重等级（CRITICAL / WARNING / INFO）
4. 如有 CRITICAL 问题，要求修改后再合并

## Context

- Spring Boot 3.2 + Java 17 + Maven
- JUnit 5 + AssertJ 测试框架
- 遵循 Controller → Service → Repository 分层
