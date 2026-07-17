# harness-demo

Spring Boot demo project for Claude Code Java harness setup.

## Quick Start

```bash
# Build & test (with coverage gate)
mvn clean verify

# Run dev server
mvn spring-boot:run

# Check health
curl http://localhost:8080/api/health

# Docker
docker compose up --build
```

## Requirements

- JDK 17+
- Maven 3.9+ (or use `./mvnw`)
- Docker & Docker Compose (optional, for containerized run)

## Project Structure

```
src/
  main/java/com/example/harnessdemo/
    controller/     # REST controllers
    service/        # Business logic
    repository/     # Data access (MyBatis-Plus)
    model/          # Domain models
    dto/            # Request/Response DTOs
    config/         # Configuration classes
  test/             # Unit & integration tests
```

## Build Commands

| Command | Purpose |
|---------|--------|
| `mvn clean verify` | Build + test + coverage gate + lint |
| `mvn spring-boot:run` | Start dev server |
| `mvn checkstyle:check` | Google style check |
| `mvn pmd:check` | Alibaba p3c coding rules check |
| `mvn org.owasp:dependency-check-maven:check` | Dependency security scan |

See `CLAUDE.md` for full build/test/lint commands.
