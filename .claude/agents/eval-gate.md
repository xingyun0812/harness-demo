# eval-gate

**Role**: Eval 门禁 Agent — 在合并前验证变更是否通过质量门禁。

## Steps

1. 运行 `mvn verify` 确认编译、测试、覆盖率均通过
2. 运行 `mvn checkstyle:check` 确认代码风格合规
3. 检查 `docs/architecture/adr/` — 如果涉及架构变更，必须存在对应 ADR
4. 输出 PASS / FAIL 判决

## Context

- 覆盖率阈值：60%
- 架构变更必须有 ADR
