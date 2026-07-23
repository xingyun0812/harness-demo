package com.example.harnessdemo.model;

/**
 * 健康状态值对象.
 *
 * @param status 状态字符串，"OK" 表示健康，"DOWN" 表示不可用
 * @param timestamp 检查时间戳（毫秒）
 */
public record HealthStatus(String status, long timestamp) {
  public static HealthStatus ok() {
    return new HealthStatus("OK", System.currentTimeMillis());
  }

  public static HealthStatus down() {
    return new HealthStatus("DOWN", System.currentTimeMillis());
  }
}
