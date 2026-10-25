# v4.18 → v4.20 变更清单（由构建产物反推）

基线：GitHub `SingGoPlay/wifiaudio` master（v4.18，模块 v3.7）。
对照物：模块 v3.9 刷机包、接收端/管理器 4.20 APK。

## 一、最核心的变化：编码链路整体下线，只保留 PCM

这是 4.19/4.20 的主线改动，三端同步生效。

| 位置 | v4.18 | v4.20 |
|---|---|---|
| 发送端 | `OpusEncoder.java`、`AacEncoder.java` | **两个文件删除**（dex 内已无对应类） |
| 接收端 | `OpusDecoder`、`AacDecoder`、`DecoderEngine`、`SoftDecoderEngine`、`AsyncHardDecoder` | **5 个解码类全部删除** |
| 模块配置 | `codec` / `aac_bitrate` / `opus_bitrate` | **全部移除**（`config.conf`、`wifiaudio.sh` 默认 JSON、WebUI） |
| 发送端状态 | `encoderFrames`、`encoderActive` | 移除；`codec` 恒为 `"pcm"` |
| WebUI 文案 | 「PCM 无损低延迟 / OPUS / AAC」 | 「…本版本只传未压缩 PCM」 |
| 接收端提示 | — | 新增：对端是压缩编码时提示「本版本只支持未压缩 PCM —— 请把发射端模块也升级到 4.19」 |

## 二、发送端模块（v3.7 → v3.9）

1. **打包方式换了**：v3.7 是模块目录（`customize.sh` + 根 `service.sh`）；v3.9 改成标准刷机 zip —— 新增 `install.sh`、`common/service.sh`、`META-INF/com/google/android/update-binary`、`updater-script`（模板来自「爱玩机工具箱」，脚本内可见其署名）。
2. **开机策略**：`common/service.sh` 为纯手动模式（开机不启任何进程、不读配置），完全由管理器 App 通过 `wifiaudio.sh start` 唤起。
3. **预设**：`presets/weak.conf` 删除，仅保留 `game` / `movie` / `music`。
4. **`uninstall.sh`**：简化为 `stop` + 清理 `capture.pid`、`status.json`。
5. 版本号 `v3.7/307` → `v3.9/309`；HTTP 响应头为 `Server: WiFiAudio/4.19`。

## 三、发送端服务（`server/`，Java）

| 文件 | 变化 |
|---|---|
| `Main.java` | 386 → 268 行。新增 **auto-off 守护线程**（读 `auto_off_min`，到点 `System.exit(0)`）；采集 `Status.captureLatencyMs`、`Status.recordBufferFrames`；编码初始化逻辑删除 |
| `Config.java` | 删除 codec/码率读取；新增 `autoOffMinutes()`（键 `auto_off_min`） |
| `Status.java` | 移除 `encoderFrames`/`encoderActive`；新增 `captureLatencyMs`、`recordBufferFrames`、`tcpClients`、`udpClients`、`tcpQueueFrames`、`senderLatencyMs()` |
| `UdpServer.java` | 208 → 281 行：新增「写前量」限制与自适应放宽（设备缓冲计数不可信时放弃限制以保证出声） |
| `Util.java` | 135 → 200 行：新增 `causeMsg()` 等日志辅助 |
| `HttpServer.java` | 356 → 324 行（编码协商相关删除） |
| `Mixer.java` | 251 → 223 行（编码 Sink 分支删除） |

> `auto_off_min` 只出现在发送端 dex 中，管理器 APK 内无此键 —— 即**定时关闭在发送端已实现，但管理器 UI 尚未暴露入口**。

## 四、接收端 App（4.18 → 4.20）

新增能力（来自 dex 字符串 + 新增类）：

1. **诊断面板**：`🔍 诊断` / `🩺 诊断与日志` / 「诊断发送端 …（请查看发送端状态）」。
2. **延迟分解**：把总延迟拆成「发送端实测延迟 / 网络到达抖动 / 抖动缓冲目标 / 链路到达间隔 / 输出设备欠载 / 队列」，并区分「(发送端实测)」「(发送端估算)」；发送端未提供时按 40ms 估算。
3. **连接预设**：`🗂️ 连接预设`、`选择预设（选中即加载）`、保存、预设名 —— 新增 `FormState.java` 承载表单状态。
4. **新增 `AppState.java`**：全局 Compose 可观察状态（v4.18 无此文件），字段含 `senderRawMs`、`jitterMs`、`targetMs`、`output`、`dropped`、`underrun` 等。
5. **AAudio 容错增强**：打开失败/连续写失败 → 标记失效并回退 `AudioTrack`；采样率不符时提示「本机原生 … kHz，建议把发射端采样率设为 48kHz」；关闭时若有未退出写入则保留句柄不释放（防崩）。
6. **缓冲档位化**：低延迟(游戏) / 高音质(稳定) / 自定义（20~500ms），欠载时提示调高一档。
7. **协议校验**：新增「协议不匹配：目标不是 WiFiAudio 发送端」。
8. **内置测试音**：`🔊 测试音 — 1kHz 正弦波 1 秒`（不再依赖独立 Tone App）。
9. 断线提示改为「连接断开，3 秒后自动重连...」。

代码规模：业务 Java 反编译产物 6222 行（`PlayerService` 1804、`MainActivityKt` 2529、`AppState` 302、`MainActivity` 227、`AudioOut` 170、`FormState` 116）。

## 五、管理器 App（4.18 → 4.20）

1. 新增 **服务日志查看**（`📄 服务日志`，无日志时显示「（无日志，服务未运行）」）。
2. 新增 **重启服务** 按钮（`↻ 重启服务` / 「已重启服务」）。
3. 运行状态显示改为 `● 运行中` / `○ 未运行`。
4. 统计新增 **发送队列**、**分发丢帧**（对应 `tcpQueueFrames` / `mixerDrops`）。
5. 分区重组：`🌐 模式与网络`、`🔊 音频设置（音频格式 / 采样率 / 比特率）`，编码选择项移除。
6. 保留 v4.18 已有的：更新检测横幅（`📦 发现新版本 v…` + 「去下载」）、冷启动 Root 检测、预设管理、夜间模式。

## 六、构建环境变化

- `compileSdk` 37 → **36**，`versionCode/versionName` = 420 / "4.20"
- Gradle 8.13、Kotlin 2.3.20、JDK 17、Compose + miuix 0.9.0（来自 APK 内 `kotlin-tooling-metadata.json`）
- 均未混淆（`isMinifyEnabled=false`），这是本次能高质量还原的前提
