package com.example.harnessdemo.controller;

import com.example.harnessdemo.dto.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器 — 统一捕获并包装为 ApiResult 返回.
 *
 * @author xingyun0812
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResult<Void>> handleNotFound(RuntimeException ex) {
    HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("not found")
        ? HttpStatus.NOT_FOUND
        : HttpStatus.INTERNAL_SERVER_ERROR;

    return ResponseEntity.status(status)
        .body(ApiResult.error(status.value(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResult<Void>> handleValidation(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .reduce((a, b) -> a + "; " + b)
        .orElse("validation failed");

    return ResponseEntity.badRequest().body(ApiResult.error(400, msg));
  }
}
