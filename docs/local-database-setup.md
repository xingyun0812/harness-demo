# 本地数据库切换指南

本项目支持三种数据库 profile：`default`（H2 内存库，无需配置）、`mysql`、`kingbase`。

---

## 默认：H2 内存库（开箱即用）

不设置任何 profile 即使用 H2，适合本地开发和 CI。

```bash
mvn spring-boot:run
# 或
mvn clean verify
```

H2 控制台访问：`http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:harness-demo`
- Username: `sa`
- Password: _(空)_

---

## MySQL

### 1. 启动本地 MySQL（推荐用 Docker）

```bash
docker run -d \
  --name harness-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=harness_demo \
  -p 3306:3306 \
  mysql:8.0

# 等待 MySQL 就绪（约 15 秒）
docker logs -f harness-mysql 2>&1 | grep "ready for connections"
```

### 2. 启动应用

```bash
# 方式一：环境变量（推荐）
SPRING_PROFILES_ACTIVE=mysql mvn spring-boot:run

# 方式二：命令行参数
mvn spring-boot:run -Dspring-boot.run.profiles=mysql

# 方式三：自定义账号密码
SPRING_PROFILES_ACTIVE=mysql \
DATASOURCE_USERNAME=myuser \
DATASOURCE_PASSWORD=mypassword \
  mvn spring-boot:run
```

### 3. 配置参数说明（`application-mysql.yml`）

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `DATASOURCE_USERNAME` | `root` | MySQL 用户名 |
| `DATASOURCE_PASSWORD` | `root` | MySQL 密码 |
| JDBC URL | `localhost:3306/harness_demo` | 通过 `DATASOURCE_URL` 覆盖 |

### 4. 关闭并清理

```bash
docker stop harness-mysql && docker rm harness-mysql
```

---

## Kingbase

### 注意事项

> ⚠️ Kingbase 环境下 **Flyway 默认禁用**（`spring.flyway.enabled=false`）。
> Schema 需 DBA 手动创建，详见 [ADR-003](adr/ADR-003-flyway.md)。

### 1. 启动本地 Kingbase（需公司内部镜像）

```bash
# 使用公司内部 Kingbase Docker 镜像（镜像名根据实际情况替换）
docker run -d \
  --name harness-kingbase \
  -e KES_USER=system \
  -e KES_PASSWORD=system \
  -e DB_NAME=harness_demo \
  -p 54321:54321 \
  <your-company-registry>/kingbase8:latest

# 手动建表（Flyway 禁用，需执行迁移脚本）
docker exec -it harness-kingbase ksql -U system -d harness_demo \
  -f /path/to/src/main/resources/db/migration/V1__init.sql
```

### 2. 启动应用

```bash
SPRING_PROFILES_ACTIVE=kingbase mvn spring-boot:run

# 自定义账号
SPRING_PROFILES_ACTIVE=kingbase \
DATASOURCE_USERNAME=system \
DATASOURCE_PASSWORD=system \
  mvn spring-boot:run
```

### 3. 配置参数说明（`application-kingbase.yml`）

| 参数 | 默认值 | 说明 |
|------|--------|------|
| `DATASOURCE_USERNAME` | `system` | Kingbase 用户名 |
| `DATASOURCE_PASSWORD` | `system` | Kingbase 密码 |
| JDBC URL | `localhost:54321/harness_demo` | 通过 `DATASOURCE_URL` 覆盖 |

---

## 在 IDE 中切换 profile（IntelliJ IDEA）

1. 打开 **Run/Debug Configurations**
2. 选中 `HarnessDemoApplication`
3. **Active profiles** 字段填入 `mysql` 或 `kingbase`
4. 点击运行

---

## 常见问题

**Q: MySQL 启动后应用报 `Communications link failure`**
- 确认 MySQL 容器已完全就绪（等 `ready for connections` 日志）
- 检查端口 3306 是否被占用：`lsof -i :3306`

**Q: H2 数据在重启后丢失**
- 这是预期行为，H2 内存库每次重启清空
- 如需持久化，将 `DATASOURCE_URL` 改为 `jdbc:h2:file:./data/harness-demo`

**Q: Kingbase Flyway 报错**
- 默认禁用，如需启用设置 `KINGBASE_FLYWAY_ENABLED=true`，并确认迁移脚本在 Kingbase 方言下可执行
