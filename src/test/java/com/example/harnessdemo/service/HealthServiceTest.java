package com.example.harnessdemo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.harnessdemo.model.HealthStatus;
import org.junit.jupiter.api.Test;

class HealthServiceTest {

  private final HealthService healthService = new HealthService();

  @Test
  void checkShouldReturnOkStatus() {
    HealthStatus status = healthService.check();

    assertThat(status.status()).isEqualTo("OK");
    assertThat(status.timestamp()).isPositive();
  }
}
