# Session Handoff

## Last Session (2026-07-14)

- Fixed Cursor review findings: branch `master` → `main`, mvn verify passing, docs synced
- Brought all 5 docs in line with actual project state
- JaCoCo now excludes `*Application.class` for coverage headroom
- git hooks installed (pre-commit: checkstyle, pre-push: block main)
- .gitignore: added `.mybatis/`, removed stale items
- settings.json: removed docker/curl from auto-allow (curl → ask), cleaned up deny
- Deleted empty `.pre-commit-config.yaml` (hooks installed via `scripts/setup-hooks.sh`)

## What's Next

1. Connect remote repository
2. Verify CI pipeline on push
3. Implement next feature (e.g., CRUD REST API)

## Pending Decisions

- Git remote repository URL
- Database selection (if any)
