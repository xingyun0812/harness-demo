# Session Handoff

## Last Session (2026-07-22)

### Review-driven fixes (two PRs)

**PR #5 — P0 review findings (merged to main, commit 61cc885)**

- P0-1: Unified coverage threshold to 80% across `ci.yml`, `CLAUDE.md`, `architecture.md`, `eval-gate.md`, `roadmap.md`, `PROJECT_STATUS.md`, `feature_list.json`, `harness-standards.md`
- P0-2: Aligned `ADR-004` with implementation (`code = 0` → `code = 200`)
- P0-3: Created `client/` package (was documented but missing)
- P0-4: Replaced string-matching exception handler with typed `ResourceNotFoundException` + 2 regression tests
- P0-5: Replaced fake `.claude/hooks/pre-push` with real Claude Code `PreToolUse` hook (`scripts/claude-pre-push-guard.sh`), documented branch protection
- Bonus: Fixed pre-existing test isolation bug (`@DirtiesContext` on `HarnessDemoIntegrationTest`) that had been breaking `UserRepositoryTest.selectAll_returnsAllUsers` for 5 days on main

**PR #6 — P1 review findings (this branch)**

- P1-6: OWASP aligned — removed `failBuildOnCVSS=7` from `pom.xml` (was blocking, docs said non-blocking), added OWASP step to CI with `continue-on-error: true`, updated `harness-standards.md §1.4`
- P1-7: `@Transactional` on `UserService` write methods + `readOnly=true` on reads; documented rule in new `harness-standards.md §1.6`
- P1-8: `HealthService` now probes DataSource (`SELECT 1`) instead of unconditionally returning OK; added `down()` factory to `HealthStatus`; rewrote `HealthServiceTest` with mock DataSource (OK + DOWN paths); added DOWN case to `HealthControllerTest`
- P1-9: Fixed `docker-compose.yml` healthcheck (`curl` → `wget --spider`, alpine has BusyBox wget not curl); resolved roadmap contradiction (Docker was in both "Future" and "Out of Scope" — now marked functional in "Planned Features")
- P1-10: This session-handoff update

## What's Next

1. Merge PR #6 once CI passes
2. Enable GitHub branch protection on `main` (Settings → Branches → require PR + status checks + approvals) — documented in `harness-standards.md §2.6`
3. Consider P2 items from the review:
   - Expand `code-review` / `eval-gate` agent prompts (checklist, output format, triggers)
   - `TraceIdFilter` write-back response header + add unit test
   - Add `.editorconfig` aligned with Google style
   - Align `application-kingbase.yml` Flyway disabled with ADR-003
   - Fill `memory/` with real entries or mark as placeholder
   - Remove personal `@author xingyun0812` from template files
   - Document `launch.json` `type: "claude"` runner

## Pending Decisions

- Whether to add Spring Security baseline (currently out of scope, per-project)
- Whether to fill `memory/` with real team knowledge or leave as skeleton
- Whether to keep personal `@author xingyun0812` or template as `@author <your-name>`

## Known State

- `main` is clean, CI green, P0 merged
- This branch (`fix/p1-review-findings`) has P1 changes pending review
- Local machine lacks JDK 17 — all verification done via CI
