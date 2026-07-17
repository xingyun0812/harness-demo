package com.example.harnessdemo.service;

import com.example.harnessdemo.model.HealthStatus;
import org.springframework.stereotype.Service;

/**
 * 健康检查服务.
 *
 * @author xingyun0812
 */
@Service
public class HealthService {

  public HealthStatus check() {
    return HealthStatus.ok();
  }
}
