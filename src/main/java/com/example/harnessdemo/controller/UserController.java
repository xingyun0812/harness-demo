package com.example.harnessdemo.controller;

import com.example.harnessdemo.dto.CreateUserRequest;
import com.example.harnessdemo.dto.UserResponse;
import com.example.harnessdemo.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
    return userService.create(request);
  }

  @GetMapping
  public List<UserResponse> listAll() {
    return userService.listAll();
  }

  @GetMapping("/{id}")
  public UserResponse getById(@PathVariable Long id) {
    return userService.getById(id);
  }
}
