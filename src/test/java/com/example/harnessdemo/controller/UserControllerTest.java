package com.example.harnessdemo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.harnessdemo.dto.CreateUserRequest;
import com.example.harnessdemo.dto.UserResponse;
import com.example.harnessdemo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @Test
  void createShouldReturnCreated() throws Exception {
    CreateUserRequest request = new CreateUserRequest("alice", "alice@example.com", "13800138000");
    UserResponse response = new UserResponse(1L, "alice", "alice@example.com", "13800138000", LocalDateTime.now());

    when(userService.create(any())).thenReturn(response);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.username").value("alice"));
  }

  @Test
  void listAllShouldReturnList() throws Exception {
    UserResponse response = new UserResponse(1L, "alice", "alice@example.com", "13800138000", LocalDateTime.now());

    when(userService.listAll()).thenReturn(List.of(response));

    mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("alice"));
  }

  @Test
  void getByIdShouldReturnUser() throws Exception {
    UserResponse response = new UserResponse(1L, "alice", "alice@example.com", "13800138000", LocalDateTime.now());

    when(userService.getById(1L)).thenReturn(response);

    mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("alice"));
  }

  @Test
  void createShouldFailWhenUsernameBlank() throws Exception {
    CreateUserRequest request = new CreateUserRequest("", "alice@example.com", "13800138000");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
  }
}
