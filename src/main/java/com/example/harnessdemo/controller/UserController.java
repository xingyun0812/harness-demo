package com.example.harnessdemo.controller;

import com.example.harnessdemo.dto.ApiResult;
import com.example.harnessdemo.dto.CreateUserRequest;
import com.example.harnessdemo.dto.UserResponse;
import com.example.harnessdemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "创建用户", description = "创建一个新用户")
  @PostMapping
  public ApiResult<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
    return ApiResult.success(userService.create(request));
  }

  @Operation(summary = "用户列表", description = "获取所有用户列表")
  @GetMapping
  public ApiResult<List<UserResponse>> listAll() {
    return ApiResult.success(userService.listAll());
  }

  @Operation(summary = "获取用户", description = "根据 ID 获取用户详情")
  @GetMapping("/{id}")
  public ApiResult<UserResponse> getById(
      @Parameter(description = "用户ID", example = "1") @PathVariable Long id) {
    return ApiResult.success(userService.getById(id));
  }
}
