# Current Active Feature

M7: Doc Sync Guard — 防止代码与文档版本漂移的自动化机制

## Milestones

| Milestone | Status | Target |
|-----------|--------|--------|
| M1: Harness Setup | Done | 2026-07-08 |
| M2: Health Endpoint | Done | 2026-07-14 |
| M3: User CRUD API | Done | 2026-07-15 |
| M4: P0 Review Fixes (PR #5) | Done | 2026-07-22 |
| M5: P1 Review Fixes (PR #6) | Done | 2026-07-23 |
| M6: P2 Review Fixes (PR #7) | Done | 2026-07-23 |
| M7: Doc Sync Guard | In Progress | 2026-07-24 |

## Next Action

- [x] 修复版本漂移（Spring Boot 3.2.x → 3.4.x）
- [x] 修复 README 中已删除文件的引用
- [x] 创建 `scripts/doc-sync-check.sh` 检查脚本
- [x] 集成到 CI（`.github/workflows/ci.yml`）
- [x] 集成到 pre-commit hook
- [ ] 更新文档规范说明
- [ ] 提交 PR #8
