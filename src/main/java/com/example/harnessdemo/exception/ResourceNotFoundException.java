package com.example.harnessdemo.exception;

/**
 * 资源未找到异常 — 标识按标识符查询时目标资源不存在.
 *
 * <p>由 {@link com.example.harnessdemo.controller.GlobalExceptionHandler} 捕获后映射为 HTTP 404，
 * 避免用错误消息字符串做控制流（这是该异常引入的根因）。
 *
 * @author <your-name>
 */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * 构造资源未找到异常.
   *
   * @param resourceType 资源类型名称，如 "User"
   * @param id 资源标识符
   */
  public ResourceNotFoundException(String resourceType, Object id) {
    super(resourceType + " not found: " + id);
  }

  /**
   * 构造资源未找到异常.
   *
   * @param message 异常消息
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
