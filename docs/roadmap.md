# Roadmap

## Current Status

Harness setup complete, health endpoint deployed, User CRUD API implemented. Remote connected, CI pipeline verified.

## Planned Features

- [x] Health check endpoint (`GET /api/health`)
- [x] Example CRUD REST API (User CRUD with MyBatis-Plus + Flyway)
- [x] Database integration (H2 dev, MySQL/Kingbase profiles)
- [x] CI/CD pipeline connected
- [x] Docker / docker-compose (functional: `docker compose up --build`; Maven is the primary dev path, Docker is optional)

## Future Enhancements (consider before promoting as template)

- [ ] Spring Security / authentication baseline
- [ ] Rate limiting and request validation hardening
- [ ] Metrics and structured logging refinement

## Out of Scope (for this demo)

- Production-grade security — 研发中心推广时需根据安全基线补充

## Known Limitations

- No authentication/authorization (acceptable for demo, add per project baseline)
