package com.example.harnessdemo;

import com.example.harnessdemo.dto.ApiResult;
import com.example.harnessdemo.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 端到端集成测试 — 通过 HTTP 调用完整 Spring 栈.
 *
 * <p>用 {@code @DirtiesContext(AFTER_CLASS)} 是因为这些测试通过 HTTP 提交数据到共享的 H2
 * 内存库（无法用 {@code @Transactional} 回滚——HTTP 请求在 servlet 容器的独立线程/事务中执行）。
 * 不加的话，已提交的数据会泄漏到共享同一 Spring 上下文的后续测试类（如
 * {@code UserRepositoryTest.selectAll_returnsAllUsers} 会看到多余的记录而失败）。
 * 加上后，本类结束后 Spring 上下文会被销毁，下一个测试类拿到全新 H2。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class HarnessDemoIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void healthEndpointReturnsOk() {
    ResponseEntity<ApiResult> response = restTemplate.getForEntity("/api/health", ApiResult.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getCode()).isEqualTo(200);
  }

  @Test
  void createAndListUsers() {
    CreateUserRequest request = new CreateUserRequest("int-user", "int@test.com", "13900139000");

    ResponseEntity<ApiResult> createResponse = restTemplate.postForEntity("/api/users", request, ApiResult.class);
    assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(createResponse.getBody()).isNotNull();
    assertThat(createResponse.getBody().getCode()).isEqualTo(200);

    ResponseEntity<ApiResult> listResponse = restTemplate.getForEntity("/api/users", ApiResult.class);
    assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(listResponse.getBody()).isNotNull();
    assertThat(listResponse.getBody().getData()).isNotNull();
  }

  @Test
  void createUser_withInvalidRequest_returns400() {
    CreateUserRequest invalid = new CreateUserRequest("", null, null);

    ResponseEntity<ApiResult> response = restTemplate.postForEntity("/api/users", invalid, ApiResult.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void getUserById_notFound_returns404() {
    ResponseEntity<ApiResult> response = restTemplate.getForEntity("/api/users/99999", ApiResult.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
