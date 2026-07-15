package com.example.harnessdemo.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.harnessdemo.model.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class UserResponseTest {

  @Test
  void fromShouldMapAllFields() {
    User user = new User();
    user.setId(1L);
    user.setUsername("alice");
    user.setEmail("alice@example.com");
    user.setPhone("13800138000");
    user.setCreatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));

    UserResponse response = UserResponse.from(user);

    assertThat(response.id()).isEqualTo(1L);
    assertThat(response.username()).isEqualTo("alice");
    assertThat(response.email()).isEqualTo("alice@example.com");
    assertThat(response.phone()).isEqualTo("13800138000");
    assertThat(response.createdAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 0, 0));
  }
}
