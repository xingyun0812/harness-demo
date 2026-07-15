# Contributing to harness-demo

## Workflow

1. Create an Issue describing the change
2. Create a feature branch from `main`
3. Make your changes
4. Ensure CI passes (lint, test, coverage)
5. Open a Pull Request
6. Request review
7. Merge after approval

## Rules

- **No direct pushes to `main` or `master`** — always use feature branches + PRs
- All PRs must pass CI before merging
- Architecture decisions must be documented in `docs/architecture/adr/`
- Follow the existing code style (Checkstyle Google style)

## Development Setup

```bash
# Install git hooks
bash scripts/setup-hooks.sh

# Build and test
mvn clean verify
```

## Commit Messages

Use conventional commits:
- `feat:` new feature
- `fix:` bug fix
- `docs:` documentation
- `refactor:` code refactoring
- `test:` testing
- `chore:` maintenance
