package com.example.harnessdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通用 API 返回结果包装.
 *
 * @param <T> 业务数据类型
 * @author <your-name>
 */
@Data
@Schema(description = "通用接口返回结构")
public class ApiResult<T> {

  @Schema(description = "响应码，200成功", example = "200")
  private int code;

  @Schema(description = "提示信息", example = "操作成功")
  private String msg;

  @Schema(description = "业务数据")
  private T data;

  public static <T> ApiResult<T> success(T data) {
    ApiResult<T> result = new ApiResult<>();
    result.setCode(200);
    result.setMsg("success");
    result.setData(data);
    return result;
  }

  public static <T> ApiResult<T> error(int code, String msg) {
    ApiResult<T> result = new ApiResult<>();
    result.setCode(code);
    result.setMsg(msg);
    return result;
  }
}
