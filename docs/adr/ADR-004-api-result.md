# ADR-004: API Response Wrapper Format

- **Status**: accepted
- **Date**: 2026-07-14
- **Deciders**: xingyun0812

## Context

REST API 需要用统一的格式返回响应，方便前端统一处理错误和成功场景。

## Decision

设计泛型 `ApiResult<T>` 作为统一的 API 响应包装，格式为：

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

- 成功时 `code = 200`，`msg = "success"`，`data` 为实际响应体（`code` 复用 HTTP 语义，前端只需一套状态码模型）
- 失败时 `code` 为 HTTP 错误码数字（如 400/404/500），`msg` 为错误描述，`data` 为 null
- Controller 方法统一返回 `ApiResult<T>`，由 Spring Boot 序列化为 JSON
- `GlobalExceptionHandler` 将所有异常统一包装为 `ApiResult`，HTTP status 与 `code` 保持一致

> 注：早期草稿曾考虑用 `code = 0` 表示成功，最终选择 `code = 200` 以与 HTTP 状态码对齐，降低前端心智成本。

## Consequences

- Positive: 前后端契约明确，前端可以统一处理响应
- Positive: `GlobalExceptionHandler` 确保所有异常都被正确包装，不会泄漏内部信息
- Negative: 每一层都需要知道 ApiResult 包装
- Neutral: 类似 Spring 的 `ResponseEntity`，但语义更明确、字段更精简
