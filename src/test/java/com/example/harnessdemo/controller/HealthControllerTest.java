package com.example.harnessdemo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.harnessdemo.model.HealthStatus;
import com.example.harnessdemo.service.HealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HealthController.class)
class HealthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private HealthService healthService;

  @Test
  void healthShouldReturnOk() throws Exception {
    when(healthService.check()).thenReturn(new HealthStatus("OK", 123L));

    mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.status").value("OK"))
            .andExpect(jsonPath("$.data.timestamp").value(123L));
  }

  @Test
  void healthShouldReturnOkEvenWhenDown() throws Exception {
    // HealthController returns 200 OK with status=DOWN — a degraded-but-reachable service
    // still answers HTTP; the body carries the real state for monitoring.
    when(healthService.check()).thenReturn(new HealthStatus("DOWN", 456L));

    mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.status").value("DOWN"))
            .andExpect(jsonPath("$.data.timestamp").value(456L));
  }
}
