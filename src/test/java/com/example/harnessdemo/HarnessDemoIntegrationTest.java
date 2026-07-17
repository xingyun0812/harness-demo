package com.example.harnessdemo;

import com.example.harnessdemo.dto.ApiResult;
import com.example.harnessdemo.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
