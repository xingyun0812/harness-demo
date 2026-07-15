package com.example.harnessdemo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HealthStatusTest {

  @Test
  void okShouldReturnHealthy() {
    HealthStatus status = HealthStatus.ok();

    assertThat(status.status()).isEqualTo("OK");
    assertThat(status.timestamp()).isPositive();
  }
}
