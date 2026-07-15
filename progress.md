# Current Active Feature

None — M1 (harness setup) and health endpoint complete. Doc-sync pre-commit hook added.

## Milestones

| Milestone | Status | Target |
|-----------|--------|--------|
| M1: Harness Setup | Done | 2026-07-08 |
| M2: Health Endpoint | Done | 2026-07-14 |

## Next Action

Connect remote repository, verify CI pipeline.

## Recently Completed

- Pre-commit doc-sync check (`scripts/check-doc-sync.sh`) — warns when source changes lack matching doc updates
- Doc-sync rules extracted to `.doc-sync.yml` — scripts reads config instead of hardcoded rules
