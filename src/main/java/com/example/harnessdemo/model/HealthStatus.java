package com.example.harnessdemo.model;

public record HealthStatus(String status, long timestamp) {
  public static HealthStatus ok() {
    return new HealthStatus("OK", System.currentTimeMillis());
  }
}
