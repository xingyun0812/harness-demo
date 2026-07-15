package com.example.harnessdemo.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.harnessdemo.model.HealthStatus;
import org.junit.jupiter.api.Test;

class HealthResponseTest {

  @Test
  void fromShouldMapFields() {
    HealthStatus status = new HealthStatus("OK", 123L);
    HealthResponse response = HealthResponse.from(status);

    assertThat(response.status()).isEqualTo("OK");
    assertThat(response.timestamp()).isEqualTo(123L);
  }
}
