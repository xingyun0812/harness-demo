package com.example.harnessdemo.controller;

import com.example.harnessdemo.dto.ApiResult;
import com.example.harnessdemo.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器 — 按异常类型路由 HTTP 状态码，统一包装为 {@link ApiResult} 返回.
 *
 * <p>路由规则：
 * <ul>
 *   <li>{@link ResourceNotFoundException} → 404</li>
 *   <li>{@link MethodArgumentNotValidException} → 400（Bean Validation 校验失败）</li>
 *   <li>其他 {@link RuntimeException} → 500</li>
 * </ul>
 *
 * <p>设计原则：按异常类型路由，而非用错误消息字符串做控制流——后者会误判（任何带 "not found"
 * 的 500 异常都会被错当成 404）。
 *
 * @author <your-name>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResult<Void>> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResult.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResult<Void>> handleValidation(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .reduce((a, b) -> a + "; " + b)
        .orElse("validation failed");

    return ResponseEntity.badRequest().body(ApiResult.error(400, msg));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResult<Void>> handleGeneric(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
  }
}
