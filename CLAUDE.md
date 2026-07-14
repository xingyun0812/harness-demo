# harness-demo

## Role

harness-demo — 用于演示和测试 Claude Code Java 项目 harness 功能的 Spring Boot 项目。

## Build & Run

```bash
# Start dev server
mvn spring-boot:run

# Build without tests
mvn clean package -DskipTests

# Build with tests
mvn clean verify
```

## Test

```bash
# Unit tests
mvn test

# Full verification (tests + coverage gate)
mvn verify

# Lint (checkstyle)
mvn checkstyle:check

# Security scan (OWASP dependency check)
mvn org.owasp:dependency-check-maven:check
```

## Code Standards

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.x
- **Build**: Maven
- **Style**: Checkstyle (Google style)
- **Coverage Gate**: 60% line coverage (JaCoCo)
- **Architecture Decisions**: `docs/adr/` — mandatory for non-trivial choices
- **Process**: Issue → feature branch → PR — No direct pushes to `main`

## Config & Deploy

| Purpose | Path |
|---------|------|
| Application config | `src/main/resources/application.yml` |
| Docker Compose | `docker-compose.yml` (TODO) |
| Dockerfile | `Dockerfile` (TODO) |

## Key Architecture Files

- `docs/architecture.md` — system overview
- `docs/roadmap.md` — future plans & gaps
- `docs/adr/` — architecture decision records
- `docs/PROJECT_STATUS.md` — current completion status

## Environment

| Variable | Default | Purpose |
|----------|---------|---------|
| `SERVER_PORT` | 8080 | HTTP server port |
| `SPRING_PROFILES_ACTIVE` | dev | Active Spring profile |

## Memory & Documentation

- `memory/MEMORY.md` — memory index (loaded on startup)
- `memory/` — per-topic memory files
- `feature_list.json` — all features with status + verification evidence
- `progress.md` — current active feature, milestones, next action
- `session-handoff.md` — cross-session context handoff
