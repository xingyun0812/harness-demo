package com.example.harnessdemo.config;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.slf4j.MDC;

class TraceIdFilterTest {

  private final TraceIdFilter filter = new TraceIdFilter();

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  @Test
  void shouldUseTraceIdFromRequestHeaderWhenPresent() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Trace-Id", "req-trace-123");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = (req, resp) -> { };

    filter.doFilter(request, response, chain);

    assertThat(response.getHeader("X-Trace-Id")).isEqualTo("req-trace-123");
  }

  @Test
  void shouldGenerateTraceIdWhenHeaderMissing() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = (req, resp) -> { };

    filter.doFilter(request, response, chain);

    String generated = response.getHeader("X-Trace-Id");
    assertThat(generated).isNotBlank();
    assertThat(generated).hasSize(16);
  }

  @Test
  void shouldGenerateTraceIdWhenHeaderBlank() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Trace-Id", "   ");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = (req, resp) -> { };

    filter.doFilter(request, response, chain);

    String generated = response.getHeader("X-Trace-Id");
    assertThat(generated).isNotBlank();
    assertThat(generated).hasSize(16);
  }

  @Test
  void shouldPutTraceIdIntoMdcDuringRequestAndClearAfter() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Trace-Id", "mdc-trace-456");
    MockHttpServletResponse response = new MockHttpServletResponse();
    final String[] mdcDuringRequest = new String[1];
    FilterChain chain = (req, resp) -> mdcDuringRequest[0] = MDC.get("traceId");

    filter.doFilter(request, response, chain);

    // Inside the chain, MDC had the traceId
    assertThat(mdcDuringRequest[0]).isEqualTo("mdc-trace-456");
    // After the filter completes, MDC is cleared
    assertThat(MDC.get("traceId")).isNull();
  }

  @Test
  void shouldClearMdcEvenWhenChainThrows() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Trace-Id", "err-trace-789");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain chain = (req, resp) -> { throw new RuntimeException("downstream error"); };

    try {
      filter.doFilter(request, response, chain);
    } catch (RuntimeException expected) {
      // expected
    }

    // MDC must be cleared even when the chain throws (finally block)
    assertThat(MDC.get("traceId")).isNull();
  }
}
