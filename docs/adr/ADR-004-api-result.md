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
  "code": 0,
  "msg": "success",
  "data": { ... }
}
```

- 成功时 `code = 0`，`msg = "success"`，`data` 为实际响应体
- 失败时 `code` 为 HTTP 错误码数字，`msg` 为错误描述，`data` 为 null
- Controller 方法统一返回 `ApiResult<T>`，由 Spring Boot 序列化为 JSON
- `GlobalExceptionHandler` 将所有异常统一包装为 `ApiResult`

## Consequences

- Positive: 前后端契约明确，前端可以统一处理响应
- Positive: `GlobalExceptionHandler` 确保所有异常都被正确包装，不会泄漏内部信息
- Negative: 每一层都需要知道 ApiResult 包装
- Neutral: 类似 Spring 的 `ResponseEntity`，但语义更明确、字段更精简
