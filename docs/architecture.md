# Architecture Overview

## System Context

harness-demo is a Spring Boot 3.2 web application using Java 17.

## Technology Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.x |
| Build | Maven |
| Testing | JUnit 5 + AssertJ |
| Coverage | JaCoCo (60% threshold) |
| Lint | Checkstyle (Google style) |
| Security | OWASP Dependency Check |

## Architecture Principles

1. **Layered architecture**: Controller → Service → Repository
2. **API-first**: Define REST contracts before implementation
3. **Testable**: Business logic in services, testable without Spring context
4. **Documented**: Architecture decisions in `docs/adr/`

## Module Structure

```
src/
  main/java/com/example/harnessdemo/
    controller/     # REST endpoints
    service/        # Business logic
    repository/     # Data access
    model/          # Domain models / DTOs
    config/         # Spring configuration
  test/java/com/example/harnessdemo/
```
