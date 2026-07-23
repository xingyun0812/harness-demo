package com.example.harnessdemo.service;

import com.example.harnessdemo.model.HealthStatus;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 健康检查服务 — 对底层依赖做轻量探活，而不是无条件返回 OK.
 *
 * <p>当前检查项：
 * <ul>
 *   <li>DataSource 连通性（{@code SELECT 1}）</li>
 * </ul>
 *
 * <p>设计原则：健康检查端点应反映真实可用性，否则监控告警会失真——DB 挂了仍回 OK 会误导运维。
 * 作为模板，这里示范如何对依赖做探活；新项目接入更多依赖（Redis/MQ/下游 RPC）时在此扩展。
 *
 * @author xingyun0812
 */
@Service
public class HealthService {

  private static final Logger log = LoggerFactory.getLogger(HealthService.class);

  private final DataSource dataSource;

  public HealthService(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public HealthStatus check() {
    try (var conn = dataSource.getConnection();
         var stmt = conn.createStatement()) {
      stmt.execute("SELECT 1");
      return HealthStatus.ok();
    } catch (SQLException ex) {
      log.warn("health check failed: {}", ex.getMessage());
      return HealthStatus.down();
    }
  }
}
