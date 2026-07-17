package com.example.harnessdemo.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResultTest {

  @Test
  void success_createsResultWithData() {
    ApiResult<String> result = ApiResult.success("hello");

    assertThat(result.getCode()).isEqualTo(200);
    assertThat(result.getMsg()).isEqualTo("success");
    assertThat(result.getData()).isEqualTo("hello");
  }

  @Test
  void success_dataCanBeNull() {
    ApiResult<String> result = ApiResult.success(null);

    assertThat(result.getCode()).isEqualTo(200);
    assertThat(result.getData()).isNull();
  }

  @Test
  void error_createsResultWithCodeAndMsg() {
    ApiResult<Void> result = ApiResult.error(404, "not found");

    assertThat(result.getCode()).isEqualTo(404);
    assertThat(result.getMsg()).isEqualTo("not found");
    assertThat(result.getData()).isNull();
  }

  @Test
  void error_dataIsAlwaysNull() {
    ApiResult<Void> result = ApiResult.error(500, "server error");

    assertThat(result.getCode()).isEqualTo(500);
    assertThat(result.getMsg()).isEqualTo("server error");
    assertThat(result.getData()).isNull();
  }
}
