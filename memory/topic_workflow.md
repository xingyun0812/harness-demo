---
name: workflow
description: 规范化开发流程 — 从 clone 到 merge
---

## 首次 clone 后必做

```bash
bash scripts/setup-hooks.sh    # 安装 git hooks (pre-commit: checkstyle, pre-push: block main)
```

并在 GitHub 仓库 Settings → Branches 开启 `main` 的 branch protection：

- ✅ Require pull request before merge
- ✅ Require status checks to pass (CI)
- ✅ Require approvals (≥1)

见 `docs/harness-standards.md §2.6`。

## 日常开发循环

1. 从 `main` 创建 feature branch：`git checkout -b feat/xxx`
2. 开发，频繁 `git commit`（pre-commit 自动跑 Checkstyle）
3. `mvn verify` 通过（含 80% 覆盖率门禁）
4. 同步文档（per CLAUDE.md "Document Sync" rule）：
   - `progress.md` / `docs/PROJECT_STATUS.md` / `feature_list.json`
   - 视情况：`docs/architecture.md` / `docs/adr/` / `README.md`
5. 提交 PR（Claude `PreToolUse` hook + git `pre-push` 拦截直推 main）
6. CI 在 GitHub 上二次验证（Checkstyle + verify + coverage）
7. Review → merge

## 测试金字塔

- **单元测试**：`@ExtendWith(MockitoExtension.class)` + `@Mock` / `@InjectMocks`，隔离依赖
- **切片测试**：`@WebMvcTest`（controller 层）、`@MybatisTest`（repository 层）
- **端到端**：`@SpringBootTest` + `TestRestTemplate`，走完整 HTTP 栈

见 `src/test/java/.../` 下各层测试示例。
