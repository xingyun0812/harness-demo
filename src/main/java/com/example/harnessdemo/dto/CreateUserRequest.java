package com.example.harnessdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "创建用户请求")
public record CreateUserRequest(
    @NotBlank @Schema(description = "用户名", example = "alice") String username,
    @Schema(description = "邮箱", example = "alice@example.com") String email,
    @Schema(description = "手机号", example = "13800138000") String phone) {
}
