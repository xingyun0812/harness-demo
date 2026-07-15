package com.example.harnessdemo.dto;

import com.example.harnessdemo.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "用户响应")
public record UserResponse(
    @Schema(description = "用户ID", example = "1") Long id,
    @Schema(description = "用户名", example = "alice") String username,
    @Schema(description = "邮箱", example = "alice@example.com") String email,
    @Schema(description = "手机号", example = "13800138000") String phone,
    @Schema(description = "创建时间") LocalDateTime createdAt) {

  public static UserResponse from(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getPhone(),
        user.getCreatedAt());
  }
}
