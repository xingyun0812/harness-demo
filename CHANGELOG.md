# Changelog

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
