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
```

## Requirements

- JDK 17+
- Maven (wrapper not included)

## Project Structure

```
src/
  main/java/com/example/harnessdemo/
    controller/     # REST controllers (health endpoint)
    service/        # Business logic (planned)
    repository/     # Data access (planned)
    model/          # Domain models (planned)
    config/         # Configuration (planned)
  test/             # Unit & integration tests
```

See `CLAUDE.md` for full build/test/lint commands.
