package com.example.harnessdemo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.harnessdemo.exception.ResourceNotFoundException;
import com.example.harnessdemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  void shouldReturn404WhenUserNotFound() throws Exception {
    when(userService.getById(99L)).thenThrow(new ResourceNotFoundException("User", 99L));

    mockMvc.perform(get("/api/users/99"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(404))
        .andExpect(jsonPath("$.msg").value("User not found: 99"));
  }

  @Test
  void shouldReturn400ForValidationError() throws Exception {
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400));
  }

  @Test
  void shouldReturn500ForGenericRuntimeException() throws Exception {
    when(userService.getById(1L)).thenThrow(new RuntimeException("boom"));

    mockMvc.perform(get("/api/users/1"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(500))
        .andExpect(jsonPath("$.msg").value("boom"));
  }

  @Test
  void shouldNotMisclassifyRuntimeExceptionWithNotFoundMessage() throws Exception {
    // Regression: a 500-level error whose message happens to contain "not found"
    // must NOT be misrouted to 404. Guards against string-matching control flow.
    when(userService.getById(1L))
        .thenThrow(new RuntimeException("database connection not found, please retry"));

    mockMvc.perform(get("/api/users/1"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value(500));
  }
}
