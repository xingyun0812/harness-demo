package com.example.harnessdemo.dto;

public record HealthResponse(String status, long timestamp) {
  public static HealthResponse from(com.example.harnessdemo.model.HealthStatus status) {
    return new HealthResponse(status.status(), status.timestamp());
  }
}
