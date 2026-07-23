# `.claude/` Directory

Claude Code 项目级配置目录。本文件说明各子项用途与生效机制。

## 目录结构

```
.claude/
  settings.json     # 权限模型 + Claude Code hooks（PreToolUse 等）
  launch.json       # 开发服务器启动配置（Claude Code 专用，非 VS Code 标准）
  agents/           # 可通过 /agent 调用的子代理
    code-review.md  # 代码审查代理
    eval-gate.md    # 质量门禁代理
  workflows/        # 工作流定义
    ci-monitor.md  # CI 状态监控
```

## settings.json

Claude Code 启动时加载，影响会话中所有工具调用。包含：

- `permissions`：三级权限模型（`allow` / `ask` / `deny`）
- `hooks`：按事件（`PreToolUse` / `PostToolUse` / `Stop` 等）注册的 hook 脚本

本项目用 `hooks.PreToolUse`（matcher: `Bash`）拦截直推 `main`，脚本在
`scripts/claude-pre-push-guard.sh`。详见 `docs/harness-standards.md §4.2` 和 `§4.5`。

## launch.json

⚠️ **注意**：本文件的 `"type": "claude"` 是 **Claude Code 专有类型**，
不是 VS Code 标准的 `java` / `chrome` / `node` 等调试器类型。

**用途**：供 Claude Code 的 `dev-server-start` 能力读取，一键启动开发服务器
（`mvn spring-boot:run`）并监听端口，Claude 可在启动后自动验证服务可用性。

**不要用 VS Code 的 F5 启动**：按 F5 会报"无法识别 type: claude"。
本地开发直接用终端 `mvn spring-boot:run` 即可。

**字段说明**：

| 字段 | 含义 |
|------|------|
| `name` | 配置名（显示用） |
| `type` | `"claude"` — Claude Code 专用，非 VS Code 标准 |
| `runtimeExecutable` | 启动命令（如 `mvn`） |
| `runtimeArgs` | 启动参数（如 `["spring-boot:run"]`） |
| `port` | 应用监听端口（用于 Claude 验证服务就绪） |

## agents/

每个 `.md` 文件是一个可被子代理加载的 agent 提示词。通过 `/agent <name>` 调用，
或在主代理工作流中引用。详见 `docs/harness-standards.md §4.3`。

## workflows/

工作流定义，通过 `/workflow <name>` 触发。详见 `docs/harness-standards.md §4.4`。
