package com.example.harnessdemo.controller;

import com.example.harnessdemo.dto.HealthResponse;
import com.example.harnessdemo.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  private final HealthService healthService;

  public HealthController(HealthService healthService) {
    this.healthService = healthService;
  }

  @GetMapping("/api/health")
  public HealthResponse health() {
    return HealthResponse.from(healthService.check());
  }
}
