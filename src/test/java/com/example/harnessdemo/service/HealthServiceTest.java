package com.example.harnessdemo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.harnessdemo.model.HealthStatus;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class HealthServiceTest {

  @Test
  void checkShouldReturnOkWhenDataSourceIsHealthy() throws SQLException {
    DataSource dataSource = mock(DataSource.class);
    Connection conn = mock(Connection.class);
    Statement stmt = mock(Statement.class);
    when(dataSource.getConnection()).thenReturn(conn);
    when(conn.createStatement()).thenReturn(stmt);

    HealthService healthService = new HealthService(dataSource);
    HealthStatus status = healthService.check();

    assertThat(status.status()).isEqualTo("OK");
    assertThat(status.timestamp()).isPositive();
  }

  @Test
  void checkShouldReturnDownWhenDataSourceFails() throws SQLException {
    DataSource dataSource = mock(DataSource.class);
    when(dataSource.getConnection()).thenThrow(new SQLException("connection refused"));

    HealthService healthService = new HealthService(dataSource);
    HealthStatus status = healthService.check();

    assertThat(status.status()).isEqualTo("DOWN");
    assertThat(status.timestamp()).isPositive();
  }
}
