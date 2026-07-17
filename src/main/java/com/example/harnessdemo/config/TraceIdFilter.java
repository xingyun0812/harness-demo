package com.example.harnessdemo.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Servlet Filter — 为每个请求注入 traceId（从请求头读取或自动生成），写入 MDC.
 *
 * @author xingyun0812
 */
@Component
@Order(0)
public class TraceIdFilter implements Filter {

  private static final String TRACE_ID_HEADER = "X-Trace-Id";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    String traceId = req.getHeader(TRACE_ID_HEADER);
    if (traceId == null || traceId.isBlank()) {
      traceId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    MDC.put("traceId", traceId);
    try {
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
