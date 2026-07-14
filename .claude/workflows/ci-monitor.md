# ci-monitor

**Workflow**: 监控 CI 运行状态
**Trigger**: `/workflow ci-monitor`

## Steps

1. 打开 GitHub Actions 页面
2. 列出最近的 workflow runs 及其状态
3. 如果有失败的 run，分析失败日志
4. 报告结果和修复建议

## Output

```
CI Status: ✅ / ❌
Last run: <id> at <time>
Failures: <list>
```
