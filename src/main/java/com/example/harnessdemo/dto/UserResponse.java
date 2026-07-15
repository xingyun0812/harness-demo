package com.example.harnessdemo.dto;

import com.example.harnessdemo.model.User;
import java.time.LocalDateTime;

public record UserResponse(Long id, String username, String email, String phone, LocalDateTime createdAt) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getPhone(),
        user.getCreatedAt());
  }
}
