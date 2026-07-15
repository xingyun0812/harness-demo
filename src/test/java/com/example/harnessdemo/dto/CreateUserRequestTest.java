package com.example.harnessdemo.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CreateUserRequestTest {

  @Test
  void shouldConstruct() {
    CreateUserRequest request = new CreateUserRequest("alice", "alice@example.com", "13800138000");

    assertThat(request.username()).isEqualTo("alice");
    assertThat(request.email()).isEqualTo("alice@example.com");
    assertThat(request.phone()).isEqualTo("13800138000");
  }
}
