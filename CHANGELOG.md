# Changelog

## [Unreleased] - 2026-07-23

### Changed (P1 review fixes, PR #6)

- OWASP dependency check no longer blocks build (`failBuildOnCVSS=11` in pom); CI runs it as a separate `continue-on-error: true` step (per `harness-standards.md §1.4` "不阻断")
- `HealthService` now probes DataSource (`SELECT 1`) instead of unconditionally returning OK; new `HealthStatus.down()` factory
- `docker-compose.yml` healthcheck switched from `curl` to `wget --spider` (alpine base image has BusyBox wget, not curl)
- Roadmap: Docker moved from "Future + Out of Scope" (contradictory) to "Planned Features" (functional, optional)
- `session-handoff.md`, `progress.md`, `docs/PROJECT_STATUS.md`, `feature_list.json` synced

### Added (P1)

- `@Transactional` on `UserService.create` (write) and `@Transactional(readOnly=true)` on `listAll`/`getById`; transaction rule documented in new `harness-standards.md §1.6`
- `HealthServiceTest` rewritten with mock DataSource (OK + DOWN paths)
- `HealthControllerTest` DOWN case
- `HarnessDemoIntegrationTest.healthEndpointReflectsRealDbProbe`

### Changed (P0 review fixes, PR #5, merged 2026-07-22)

- Coverage threshold unified to 80% across `ci.yml`, `CLAUDE.md`, all docs
- `ADR-004` success code aligned with implementation (`code = 0` → `code = 200`)
- `GlobalExceptionHandler` routes by typed `ResourceNotFoundException` instead of `ex.getMessage().contains("not found")`
- Replaced fake `.claude/hooks/pre-push` with real Claude Code `PreToolUse` hook (`scripts/claude-pre-push-guard.sh`)

### Added (P0)

- `exception/ResourceNotFoundException` + typed routing in `GlobalExceptionHandler` (2 regression tests)
- `client/` package placeholder (was documented but missing)
- `harness-standards.md §2.6` GitHub branch protection, §4.5 rewritten Claude hooks

### Fixed (P0)

- Pre-existing test isolation bug: `@DirtiesContext(AFTER_CLASS)` on `HarnessDemoIntegrationTest` (was leaking rows into `UserRepositoryTest`)

## [0.1.0] - 2026-07-15

### Added

- User CRUD REST API (`POST/GET /api/users`, `GET /api/users/{id}`)
- MyBatis-Plus ORM integration with Flyway migrations
- Multi-database support (H2 dev, MySQL, Kingbase8 profiles)
- OpenAPI / Swagger UI documentation (springdoc-openapi)
- Generic `ApiResult<T>` response wrapper
- Global exception handler with validation error formatting
- MyBatis-Plus integration test (`@MybatisTest`)
- End-to-end integration test (`@SpringBootTest` + `TestRestTemplate`)
- Logback configuration (logback-spring.xml)
- Architecture Decision Records (ADR-001 ~ ADR-004)

## [0.0.1-SNAPSHOT] - 2026-07-08

### Added

- Initial project scaffold with Spring Boot 3.2 + Java 17
- Maven build configuration with JaCoCo coverage and OWASP dependency check
- CLAUDE.md with project instructions for Claude Code
- .claude/ directory with settings, launch config, agents, workflows, hooks
- GitHub issue/PR templates and CONTRIBUTING.md
- Architecture documentation (docs/adr/, docs/architecture.md, docs/roadmap.md)
- CI workflow (GitHub Actions)
- Pre-commit config (Checkstyle)
- Memory system
