package com.example.harnessdemo.controller;

import com.example.harnessdemo.dto.ApiResult;
import com.example.harnessdemo.dto.HealthResponse;
import com.example.harnessdemo.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器.
 *
 * @author xingyun0812
 */
@Tag(name = "健康检查")
@RestController
public class HealthController {

  private final HealthService healthService;

  public HealthController(HealthService healthService) {
    this.healthService = healthService;
  }

  @Operation(summary = "健康检查", description = "返回服务运行状态")
  @GetMapping("/api/health")
  public ApiResult<HealthResponse> health() {
    return ApiResult.success(HealthResponse.from(healthService.check()));
  }
}
