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
| Coverage | JaCoCo (80% threshold) |
| Lint | Checkstyle (Google style) |
| Security | OWASP Dependency Check |

## Architecture Principles

1. **Layered architecture**: Controller → DTO → Service → Model / Client / Repository
2. **API-first**: Define REST contracts before implementation
3. **Testable**: Business logic in services, testable without Spring context
4. **Documented**: Architecture decisions in `docs/adr/`

## Module Structure

```
src/
  main/java/com/example/harnessdemo/
    controller/     # REST controllers (exposes HTTP API)
    dto/            # Request/response DTOs (network boundary contracts)
    service/        # Business logic interfaces + implementations
    repository/     # Data access interfaces + implementations
    model/          # Domain models with business behavior
    exception/      # Domain exceptions (typed error handling, mapped to HTTP status)
    client/         # External service clients (HTTP/RPC outbound gateways)
    config/         # Spring configuration
  test/java/com/example/harnessdemo/
```

Call flow: `Controller → DTO ↔ Service → Model | Repository | Client`
Error flow: `Service → exception → GlobalExceptionHandler → ApiResult`
