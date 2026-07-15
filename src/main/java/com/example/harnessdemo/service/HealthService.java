package com.example.harnessdemo.service;

import com.example.harnessdemo.model.HealthStatus;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

  public HealthStatus check() {
    return HealthStatus.ok();
  }
}
