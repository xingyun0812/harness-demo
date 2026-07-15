package com.example.harnessdemo.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(@NotBlank String username, String email, String phone) {
}
