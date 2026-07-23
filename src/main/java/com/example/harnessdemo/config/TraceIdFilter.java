package com.example.harnessdemo.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Servlet Filter — 为每个请求注入 traceId（从请求头读取或自动生成），写入 MDC 并回写响应头.
 *
 * <p>traceId 流向：
 * <ol>
 *   <li>从请求头 {@code X-Trace-Id} 读取；缺失则自动生成（16 位无连字符 UUID）</li>
 *   <li>写入 MDC（key = "traceId"），供 logback 在日志中输出</li>
 *   <li>回写到响应头 {@code X-Trace-Id}，调用方可关联本次请求</li>
 *   <li>请求结束后清理 MDC（finally）</li>
 * </ol>
 *
 * <p>回写响应头是为了排障：调用方拿到响应里的 traceId 后，可在日志平台按 traceId 反查整条链路。
 * 不回写则调用方只能靠时间窗口猜，难以精确关联。
 *
 * @author <your-name>
 */
@Component
@Order(0)
public class TraceIdFilter implements Filter {

  /** traceId 请求/响应头名称. */
  static final String TRACE_ID_HEADER = "X-Trace-Id";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;
    String traceId = req.getHeader(TRACE_ID_HEADER);
    if (traceId == null || traceId.isBlank()) {
      traceId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    resp.setHeader(TRACE_ID_HEADER, traceId);
    MDC.put("traceId", traceId);
    try {
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
