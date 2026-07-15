package com.example.harnessdemo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.harnessdemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  void shouldReturn404WhenUserNotFound() throws Exception {
    when(userService.getById(99L)).thenThrow(new RuntimeException("User not found: 99"));

    mockMvc.perform(get("/api/users/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found: 99"));
  }

  @Test
  void shouldReturn400ForValidationError() throws Exception {
    mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/users")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .content("{\"username\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").isArray());
  }
}
