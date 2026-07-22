# Roadmap

## Current Status

Harness setup complete, health endpoint deployed, User CRUD API implemented. Remote connected, CI pipeline verified.

## Planned Features

- [x] Health check endpoint (`GET /api/health`)
- [x] Example CRUD REST API (User CRUD with MyBatis-Plus + Flyway)
- [x] Database integration (H2 dev, MySQL/Kingbase profiles)
- [x] CI/CD pipeline connected

## Future Enhancements (consider before promoting as template)

- [ ] Spring Security / authentication baseline
- [ ] Docker / docker-compose for standardized deployment
- [ ] Rate limiting and request validation hardening
- [ ] Metrics and structured logging refinement

## Out of Scope (for this demo)

- Docker / Compose — harness demo 用 Maven 本地运行足够，不强制容器化
- Production-grade security — 研发中心推广时需根据安全基线补充

## Known Limitations

- No authentication/authorization (acceptable for demo, add per project baseline)
