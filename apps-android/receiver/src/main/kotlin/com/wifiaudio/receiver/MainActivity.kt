package com.wifiaudio.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.os.Build
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Switch
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

/**
 * WiFiAudio 接收端 v4.0 —— Kotlin + Compose + miuix 重构
 * 播放引擎（PlayerService）与 JNI 软解保持 Java 原样，UI 全面重写。
 */
class MainActivity : ComponentActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("wifiaudio_prefs", MODE_PRIVATE)
        // 状态事件走 PlayerService 的内存回调（ACTION_STATE 广播从未发送，必须用 StateListener）
        PlayerService.setStateListener(object : PlayerService.StateListener {
            override fun onState(state: String, msg: String, codec: String, rate: Int, channels: Int) {
                runOnUiThread { AppState.onPlayerState(state, msg) }
            }
        })
        // 统计事件走内存回调（广播在部分 ROM 收不到，回调最可靠）
        PlayerService.setStatsListener(object : PlayerService.StatsListener {
            override fun onStats(transport: String?, codec: String?, rate: Int, channels: Int, kbps: Int,
                                 audioMs: Int, jitterMs: Int, latencyMs: Int, rxKb: Int, decFrames: Int,
                                 targetMs: Int, dropped: Long, underruns: Long, output: String?,
                                 logs: java.util.ArrayList<String>?) {
                runOnUiThread {
                    AppState.transport = transport ?: "-"
                    AppState.codec = codec ?: "-"
                    AppState.rate = rate
                    AppState.channels = channels
                    AppState.kbps = kbps
                    AppState.audioMs = audioMs
                    AppState.jitterMs = jitterMs
                    AppState.latencyMs = latencyMs
                    AppState.rxKb = rxKb
                    AppState.targetMs = targetMs
                    AppState.dropped = dropped
                    AppState.underrun = underruns
                    AppState.output = output ?: ""
                    AppState.logs = logs?.toList() ?: emptyList()
                }
            }
        })
        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
        setContent {
            App(this, prefs)
        }
        registerReceivers()
    }

    private fun registerReceivers() {
        val sf = IntentFilter(PlayerService.ACTION_STATE)
        val st = IntentFilter(PlayerService.ACTION_STATS)
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(stateReceiver, sf, Context.RECEIVER_NOT_EXPORTED)
            registerReceiver(statsReceiver, st, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(stateReceiver, sf)
            registerReceiver(statsReceiver, st)
        }
    }

    private val stateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != PlayerService.ACTION_STATE) return
            val state = intent.getStringExtra(PlayerService.EXTRA_STATE) ?: ""
            val msg = intent.getStringExtra(PlayerService.EXTRA_MSG) ?: ""
            AppState.onPlayerState(state, msg)
        }
    }

    private val statsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != PlayerService.ACTION_STATS) return
            AppState.transport = intent.getStringExtra(PlayerService.EXTRA_CUR_TRANSPORT) ?: "-"
            AppState.codec = intent.getStringExtra(PlayerService.EXTRA_CODEC) ?: "-"
            AppState.rate = intent.getIntExtra(PlayerService.EXTRA_RATE, 0)
            AppState.channels = intent.getIntExtra(PlayerService.EXTRA_CHANNELS, 0)
            AppState.kbps = intent.getIntExtra(PlayerService.EXTRA_BITRATE, 0)
            AppState.audioMs = intent.getIntExtra(PlayerService.EXTRA_AUDIO_MS, 0)
            AppState.jitterMs = intent.getIntExtra(PlayerService.EXTRA_JITTER_MS, 0)
            AppState.latencyMs = intent.getIntExtra(PlayerService.EXTRA_LATENCY_MS, 0)
            AppState.rxKb = intent.getIntExtra(PlayerService.EXTRA_RX_KB, 0)
            AppState.targetMs = intent.getIntExtra(PlayerService.EXTRA_TARGET_MS, 0)
            AppState.dropped = intent.getLongExtra(PlayerService.EXTRA_DROPPED, 0)
            AppState.underrun = intent.getLongExtra(PlayerService.EXTRA_UNDERRUN, 0)
            AppState.output = intent.getStringExtra(PlayerService.EXTRA_OUTPUT) ?: ""
            val ls = intent.getStringArrayListExtra(PlayerService.EXTRA_LOG)
            if (ls != null) AppState.logs = ls.toList()
        }
    }

    override fun onDestroy() {
        try { unregisterReceiver(stateReceiver) } catch (_: Throwable) {}
        try { unregisterReceiver(statsReceiver) } catch (_: Throwable) {}
        PlayerService.setStateListener(null)
        PlayerService.setStatsListener(null)
        super.onDestroy()
    }
}

/** 全局 UI 状态（Compose 可观察） */
object AppState {
    var connected by mutableStateOf(false)
    var statusText by mutableStateOf("等待连接…")
    var hero by mutableStateOf("未连接")
    var heroColor by mutableStateOf(Color(0xFF3482FF))

    var transport by mutableStateOf("-")
    var codec by mutableStateOf("-")
    var rate by mutableStateOf(0)
    var channels by mutableStateOf(0)
    var kbps by mutableStateOf(0)
    var audioMs by mutableStateOf(0)
    var jitterMs by mutableStateOf(0)
    var latencyMs by mutableStateOf(0)
    var rxKb by mutableStateOf(0)
    var targetMs by mutableStateOf(0)
    var dropped by mutableStateOf(0L)
    var underrun by mutableStateOf(0L)
    var output by mutableStateOf("")
    var logs by mutableStateOf(emptyList<String>())

    fun onPlayerState(state: String, msg: String) {
        when (state) {
            "playing" -> { connected = true; hero = "播放中"; heroColor = Color(0xFF2FA85C) }
            "connecting" -> { connected = true; hero = "连接中"; heroColor = Color(0xFFE6A23C) }
            "error" -> { connected = false; hero = "未连接"; heroColor = Color(0xFF3482FF) }
            "stopped" -> { connected = false; hero = "已断开"; heroColor = Color(0xFF3482FF) }
            else -> return
        }
        statusText = msg
    }
}

@Composable
fun App(activity: ComponentActivity, prefs: SharedPreferences) {
    val night = prefs.getBoolean("night_mode", false)
    val controller = remember(night) { ThemeController(isDark = night) }
    MiuixTheme(controller = controller) {
        MainScreen(activity, prefs, controller)
    }
}

@Composable
private fun MainScreen(activity: ComponentActivity, prefs: SharedPreferences, controller: ThemeController) {
    val night = remember { mutableStateOf(prefs.getBoolean("night_mode", false)) }
    // 更新检测（页面最顶端横幅）
    var updateTag by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        updateTag = checkUpdate(activity)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (night.value) Color(0xFF242424) else Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ===== 更新提示横幅（仪表盘上方） =====
        UpdateBanner(updateTag, activity)

        // ===== 标题行 =====
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text(
                text = "🎧 WiFiAudio 接收端",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Card(onClick = {
                night.value = !night.value
                prefs.edit().putBoolean("night_mode", night.value).apply()
            }, modifier = Modifier.padding(0.dp)) {
                Text(text = if (night.value) "☀️" else "🌙", modifier = Modifier.padding(10.dp))
            }
        }

        // ===== Hero 状态卡 =====
        HeroCard(activity)

        // ===== 实时统计 =====
        if (AppState.connected) StatsCard()

        // ===== 连接配置 =====
        ConfigCard(activity, prefs)

        // ===== 预设 =====
        PresetCard(activity, prefs)

        // ===== 诊断与日志 =====
        DiagLogCard(activity)
    }
}

@Composable
private fun HeroCard(activity: ComponentActivity) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        cornerRadius = 20.dp,
        colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = AppState.heroColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                )
                Spacer(Modifier.width(10.dp))
                Text(text = AppState.hero, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text(text = AppState.statusText, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
            Spacer(Modifier.height(14.dp))
            Row {
                HeroButton("🔊 测试音", Modifier.weight(1f).padding(end = 6.dp)) {
                    val i = Intent(activity, PlayerService::class.java).setAction(PlayerService.ACTION_TEST_TONE)
                    activity.startService(i)
                }
                HeroButton("🔍 诊断", Modifier.weight(1f).padding(start = 6.dp)) {
                    val i = Intent(activity, PlayerService::class.java)
                    i.setAction(PlayerService.ACTION_STATE)
                    activity.startService(i)
                    (activity as MainActivity).runDiag()
                }
            }
        }
    }
}

@Composable
private fun HeroButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(40.dp), cornerRadius = 16.dp) {
        Text(text = text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StatsCard() {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = "📊 实时统计", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row {
                StatCell("采样率", if (AppState.rate > 0) (AppState.rate / 1000).toString() + "k" else "—", Modifier.weight(1f).padding(horizontal = 4.dp))
                StatCell("码率", if (AppState.kbps > 0) AppState.kbps.toString() else "—", Modifier.weight(1f).padding(horizontal = 4.dp))
                StatCell("缓冲", AppState.audioMs.toString() + "ms", Modifier.weight(1f).padding(horizontal = 4.dp))
            }
            Spacer(Modifier.height(8.dp))
            Row {
                StatCell("延迟", AppState.latencyMs.toString() + "ms", Modifier.weight(1f).padding(horizontal = 4.dp))
                StatCell("抖动", if (AppState.jitterMs > 0) AppState.jitterMs.toString() + "ms" else "—", Modifier.weight(1f).padding(horizontal = 4.dp))
                StatCell("接收", if (AppState.rxKb >= 1024) (AppState.rxKb / 1024).toString() + "MB" else AppState.rxKb.toString() + "KB", Modifier.weight(1f).padding(horizontal = 4.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = buildString {
                    append("协议 ").append(AppState.transport)
                    if (AppState.output.isNotEmpty()) append(" · 输出 ").append(AppState.output)
                    append(" · ").append(AppState.codec)
                    if (AppState.codec == "PCM") append("（实测）")
                    if (AppState.targetMs > 0) append(" · 自适应目标 ").append(AppState.targetMs).append("ms")
                    if (AppState.dropped > 0 || AppState.underrun > 0) {
                        append(" · 跳帧 ").append(AppState.dropped).append(" 下溢 ").append(AppState.underrun)
                    }
                },
                fontSize = 11.sp,
                color = Color(0xFF8C93B0)
            )
        }
    }
}

@Composable
private fun StatCell(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3482FF))
            Text(text = label, fontSize = 11.sp, color = Color(0xFF8C93B0))
        }
    }
}

/** 连接表单全局状态（预设应用时同步更新） */
object FormState {
    var ip by mutableStateOf("")
    var port by mutableStateOf("47800")
    var transportIdx by mutableStateOf(0)
    var outputIdx by mutableStateOf(0)
    var decodeIdx by mutableStateOf(0)
    var bufferIdx by mutableStateOf(0)
    var customMs by mutableStateOf("")
    var volume by mutableStateOf(100)
    var autoReconnect by mutableStateOf(false)
}

@Composable
private fun ConfigCard(activity: ComponentActivity, prefs: SharedPreferences) {
    var nearby by remember { mutableStateOf("扫描中…") }

    LaunchedEffect(Unit) {
        nearby = activity.scanNearby()
        // 载入最近连接
        FormState.ip = prefs.getString("last_ip", "") ?: ""
        FormState.port = prefs.getString("last_port", "47800") ?: "47800"
        FormState.transportIdx = prefs.getInt("last_transport", 0).coerceIn(0, 1)
        FormState.outputIdx = prefs.getInt("last_output", 0)
        FormState.decodeIdx = prefs.getInt("last_decode", 0)
        FormState.bufferIdx = prefs.getInt("last_buffer", 0)
        FormState.customMs = prefs.getString("last_custom_ms", "") ?: ""
        FormState.volume = prefs.getInt("last_volume", 100)
        FormState.autoReconnect = prefs.getBoolean("last_reconnect", false)
    }

    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = "📡 连接配置", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            // 附近设备
            Text(text = "附近设备（自动发现，点击填入）", fontSize = 12.sp, color = Color(0xFF8C93B0))
            Card(onClick = {
                nearby = activity.scanNearby()
            }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp)) {
                Text(text = nearby, fontSize = 12.sp, modifier = Modifier.padding(12.dp), color = Color(0xFF3482FF))
            }

            // IP + 端口
            Row {
                TextField(
                    value = FormState.ip,
                    onValueChange = { FormState.ip = it },
                    label = "IP / 域名 / URL（可带端口）",
                    modifier = Modifier.weight(1f).height(52.dp)
                )
                Spacer(Modifier.width(8.dp))
                TextField(
                    value = FormState.port,
                    onValueChange = { FormState.port = it },
                    label = "端口",
                    modifier = Modifier.width(110.dp).height(52.dp)
                )
            }
            Spacer(Modifier.height(10.dp))

            // 输出方式（传输协议由发射端决定，见状态卡片"协议"）
            MiuixDropdown(
                label = "输出",
                options = listOf("AudioTrack", "AAudio 低延迟"),
                selected = FormState.outputIdx,
                onSelect = { FormState.outputIdx = it },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = if (AppState.connected) "协议: ${AppState.transport}（发射端决定）" else "协议: 由发射端决定",
                fontSize = 10.sp,
                color = Color(0xFF8C93B0),
                modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(Modifier.height(8.dp))

            // 解码方式 / 缓冲
            Row {
                MiuixDropdown(
                    label = "解码方式",
                    options = listOf("自动", "仅硬解", "仅软解"),
                    selected = FormState.decodeIdx,
                    onSelect = { FormState.decodeIdx = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                MiuixDropdown(
                    label = "缓冲",
                    options = listOf("默认", "低延迟(游戏)", "高音质(稳定)", "自定义"),
                    selected = FormState.bufferIdx,
                    onSelect = { FormState.bufferIdx = it },
                    modifier = Modifier.weight(1f)
                )
            }
            if (FormState.bufferIdx == 3) {
                TextField(
                    value = FormState.customMs,
                    onValueChange = { FormState.customMs = it },
                    label = "缓冲毫秒（20~500，越小延迟越低）",
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )
            }
            Spacer(Modifier.height(10.dp))

            // 音量
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "音量", fontSize = 13.sp, modifier = Modifier.width(40.dp))
                Slider(
                    value = FormState.volume.toFloat(),
                    onValueChange = { FormState.volume = it.toInt(); activity.sendVolume(it.toInt()) },
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = FormState.autoReconnect,
                    onCheckedChange = { FormState.autoReconnect = it }
                )
            }
            Text(text = "右侧开关 = 断线自动重连", fontSize = 10.sp, color = Color(0xFF8C93B0))

            Spacer(Modifier.height(12.dp))

            // 连接按钮
            val connected = AppState.connected
            Button(
                onClick = {
                    if (!connected) {
                        activity.connect(
                            FormState.ip, FormState.port, FormState.transportIdx, FormState.outputIdx,
                            FormState.decodeIdx, FormState.bufferIdx, FormState.customMs,
                            FormState.volume, FormState.autoReconnect, prefs
                        )
                    } else {
                        activity.disconnect()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                cornerRadius = 16.dp
            ) {
                Text(
                    text = if (connected) "断 开" else "连 接",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MiuixDropdown(label: String, options: List<String>, selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF8C93B0))
        Card(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth().padding(top = 2.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = options.getOrElse(selected) { "" }, fontSize = 14.sp)
                Text(text = "▾", fontSize = 12.sp)
            }
        }
        if (expanded) {
            // 内联展开选项（不用 material3 DropdownMenu，避免主题兼容问题）
            Card(modifier = Modifier.fillMaxWidth().padding(top = 2.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    options.forEachIndexed { i, o ->
                        Card(
                            onClick = { expanded = false; onSelect(i) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = o,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 11.dp),
                                fontSize = 14.sp,
                                color = if (i == selected) Color(0xFF3482FF) else Color.Unspecified
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetCard(activity: ComponentActivity, prefs: SharedPreferences) {
    val presets = remember { mutableStateOf(loadPresets(prefs)) }
    var selected by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }

    fun save() {
        val list = presets.value.toMutableList()
        val jo = JSONObject()
        jo.put("name", name)
        jo.put("ip", prefs.getString("last_ip", "") ?: "")
        jo.put("port", prefs.getString("last_port", "47800") ?: "47800")
        jo.put("transport", prefs.getInt("last_transport", 0))
        jo.put("output", prefs.getInt("last_output", 0))
        jo.put("decode", prefs.getInt("last_decode", 0))
        jo.put("buffer", prefs.getInt("last_buffer", 0))
        list.add(jo)
        presets.value = list
        savePresets(prefs, list)
    }

    fun apply(idx: Int) {
        val p = presets.value.getOrNull(idx) ?: return
        prefs.edit()
            .putString("last_ip", p.optString("ip"))
            .putString("last_port", p.optString("port", "47800"))
            .putInt("last_transport", p.optInt("transport"))
            .putInt("last_output", p.optInt("output"))
            .putInt("last_decode", p.optInt("decode"))
            .putInt("last_buffer", p.optInt("buffer"))
            .apply()
        // 同步刷新连接表单
        FormState.ip = p.optString("ip")
        FormState.port = p.optString("port", "47800")
        FormState.transportIdx = p.optInt("transport")
        FormState.outputIdx = p.optInt("output")
        FormState.decodeIdx = p.optInt("decode")
        FormState.bufferIdx = p.optInt("buffer")
    }

    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = "🗂️ 连接预设", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            MiuixDropdown(
                label = "选择预设（选中即加载）",
                options = presets.value.map { it.optString("name", "未命名") }.ifEmpty { listOf("（无预设）") },
                selected = selected,
                onSelect = { apply(it); selected = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "预设名",
                    modifier = Modifier.weight(1f).height(52.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { if (name.isNotBlank()) save() }, modifier = Modifier.height(52.dp)) {
                    Text("保存")
                }
            }
        }
    }
}

@Composable
private fun DiagLogCard(activity: ComponentActivity) {
    var diag by remember { mutableStateOf("") }
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = "🩺 诊断与日志", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            if (diag.isNotEmpty()) {
                Text(text = diag, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }
            if (AppState.logs.isNotEmpty()) {
                Text(
                    text = AppState.logs.joinToString("\n"),
                    fontSize = 10.sp,
                    color = Color(0xFF8C93B0),
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Text(text = "（连接后显示实时日志）", fontSize = 11.sp, color = Color(0xFF8C93B0), modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

// ================= 逻辑扩展（挂到 Activity） =================

private fun ComponentActivity.sendVolume(v: Int) {
    val i = Intent(this, PlayerService::class.java).setAction(PlayerService.ACTION_VOLUME)
    i.putExtra(PlayerService.EXTRA_VOLUME, v)
    startService(i)
}

private fun ComponentActivity.connect(
    ip: String, port: String, transportIdx: Int, outputIdx: Int, decodeIdx: Int,
    bufferIdx: Int, customMs: String, volume: Int, autoReconnect: Boolean, prefs: SharedPreferences
) {
    val raw = ip.trim()
    if (raw.isBlank()) { AppState.statusText = "请填写发送端 IP 地址"; return }
    // 解析输入：支持 "host" / "host:port" / "http://host:port/path"（端口转发/反代后的公网地址）
    // 输入里带端口时优先使用（不再强制拼默认端口）；路径部分自动剥掉
    var host = raw
    if (host.contains("://")) host = host.substringAfter("://")
    val slash = host.indexOf('/')
    if (slash >= 0) host = host.substring(0, slash) // 去掉路径（如 /status）
    var portOverride = 0
    val colon = host.lastIndexOf(':')
    if (colon > 0 && !host.substring(0, colon).contains(':')) { // 排除 IPv6
        val h = host.substring(0, colon)
        val p = host.substring(colon + 1).toIntOrNull()
        if (h.isNotEmpty() && p != null && p in 1..65535) {
            host = h
            portOverride = p
        }
    }
    val p = if (portOverride > 0) portOverride else (port.toIntOrNull() ?: 47800)
    prefs.edit()
        .putString("last_ip", raw).putString("last_port", port)
        .putInt("last_transport", transportIdx).putInt("last_output", outputIdx)
        .putInt("last_decode", decodeIdx).putInt("last_buffer", bufferIdx)
        .putString("last_custom_ms", customMs).putInt("last_volume", volume)
        .putBoolean("last_reconnect", autoReconnect).apply()
    val buffer = when (bufferIdx) {
        1 -> "low"
        2 -> "high"
        3 -> "custom:" + (customMs.toIntOrNull() ?: 80)
        else -> "auto"
    }
    val i = Intent(this, PlayerService::class.java).setAction(PlayerService.ACTION_CONNECT)
    i.putExtra(PlayerService.EXTRA_IP, host)
    i.putExtra(PlayerService.EXTRA_PORT, p)
    i.putExtra(PlayerService.EXTRA_TRANSPORT, "auto") // 传输协议由发射端决定
    i.putExtra(PlayerService.EXTRA_BUFFER, buffer)
    i.putExtra(PlayerService.EXTRA_AAUDIO, if (outputIdx == 1) "1" else "0")
    i.putExtra(PlayerService.EXTRA_VOLUME, volume)
    i.putExtra(PlayerService.EXTRA_AUTORECONNECT, if (autoReconnect) "1" else "0")
    i.putExtra(PlayerService.EXTRA_DECODE_MODE, arrayOf("auto", "hard", "soft")[decodeIdx])
    i.putExtra(PlayerService.EXTRA_DECODER_NAME, "")
    startForegroundService(i)
    AppState.connected = true
    AppState.hero = "连接中"
    AppState.heroColor = Color(0xFFE6A23C)
    AppState.statusText = "正在连接 $host:$p …"
}

private fun ComponentActivity.disconnect() {
    val i = Intent(this, PlayerService::class.java).setAction(PlayerService.ACTION_STOP)
    startService(i)
    AppState.connected = false
    AppState.hero = "已断开"
    AppState.statusText = "已断开"
}

private fun ComponentActivity.runDiag() {
    val ip = prefs().getString("last_ip", "") ?: ""
    val port = (prefs().getString("last_port", "47800") ?: "47800").toIntOrNull() ?: 47800
    AppState.statusText = "诊断发送端 $ip:$port …（请查看发送端 WebUI 状态）"
}

private fun ComponentActivity.prefs(): SharedPreferences {
    return getSharedPreferences("wifiaudio_prefs", android.content.Context.MODE_PRIVATE)
}

/** UDP 广播扫描附近发送端 */
private fun ComponentActivity.scanNearby(): String {
    return try {
        val sock = DatagramSocket()
        sock.soTimeout = 1200
        val magic = "WFAU?".toByteArray()
        val broadcast = InetAddress.getByName("255.255.255.255")
        val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val lock = wifi.createMulticastLock("wifiaudio-scan")
        lock.setReferenceCounted(false)
        lock.acquire()
        val found = LinkedHashSet<String>()
        val targetPort = (prefs().getString("last_port", "47800") ?: "47800").toIntOrNull() ?: 47800
        repeat(2) {
            sock.send(DatagramPacket(magic, magic.size, broadcast, targetPort))
            val buf = ByteArray(256)
            while (true) {
                try {
                    val pkt = DatagramPacket(buf, buf.size)
                    sock.receive(pkt)
                    val host = pkt.address.hostAddress ?: continue
                    found.add(host)
                } catch (_: java.net.SocketTimeoutException) {
                    break
                }
            }
        }
        lock.release()
        sock.close()
        if (found.isEmpty()) "未发现发送端（确认同一 WiFi）" else found.joinToString("  ")
    } catch (_: Throwable) {
        "扫描失败"
    }
}

private fun loadPresets(prefs: SharedPreferences): List<JSONObject> {
    return try {
        val arr = JSONArray(prefs.getString("presets", "[]"))
        (0 until arr.length()).map { arr.getJSONObject(it) }
    } catch (_: Throwable) {
        emptyList()
    }
}

private fun savePresets(prefs: SharedPreferences, list: List<JSONObject>) {
    val arr = JSONArray()
    list.forEach { arr.put(it) }
    prefs.edit().putString("presets", arr.toString()).apply()
}

// ================= 更新检测 =================

/** 比较版本号（支持 v4.13 / 4.7 格式），latest > current 返回 true */
private fun isNewer(latest: String, current: String): Boolean {
    val l = latest.removePrefix("v").trim().split(".").mapNotNull { it.toIntOrNull() }
    val c = current.trim().split(".").mapNotNull { it.toIntOrNull() }
    for (i in 0 until maxOf(l.size, c.size)) {
        val a = l.getOrElse(i) { 0 }
        val b = c.getOrElse(i) { 0 }
        if (a != b) return a > b
    }
    return false
}

/** 检查 GitHub Releases 最新版本；有新版本返回 tag，否则返回空串 */
private suspend fun checkUpdate(ctx: Context): String {
    val current = try {
        ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: ""
    } catch (_: Throwable) {
        ""
    }
    return withContext(Dispatchers.IO) {
        try {
            val conn = java.net.URL("https://api.github.com/repos/SingGoPlay/wifiaudio/releases/latest").openConnection()
            conn.connectTimeout = 6000
            conn.readTimeout = 6000
            val text = conn.getInputStream().bufferedReader().use { it.readText() }
            val tag = JSONObject(text).optString("tag_name", "")
            if (tag.isNotEmpty() && isNewer(tag, current)) tag else ""
        } catch (_: Throwable) {
            ""
        }
    }
}

/** 页面顶端的更新提示横幅 */
@Composable
private fun UpdateBanner(tag: String, ctx: Context) {
    if (tag.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = Color(0xFFEAF2FF))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📦 发现新版本 v$tag，点「去下载」更新",
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                color = Color(0xFF3482FF),
                fontWeight = FontWeight.Bold
            )
            Card(
                onClick = {
                    try {
                        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SingGoPlay/wifiaudio/releases/latest")))
                    } catch (_: Throwable) {
                    }
                },
                colors = top.yukonga.miuix.kmp.basic.CardDefaults.defaultColors(color = Color(0xFF3482FF))
            ) {
                Text(
                    text = "去下载",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
