package com.wifiaudio.receiver;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt__SnapshotStateKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\"\n\u0002\u0010\t\n\u0002\b\u0010\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010b\u001a\u00020c2\u0006\u0010d\u001a\u00020\r2\u0006\u0010e\u001a\u00020\rR+\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u00058F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u000b\u0010\f\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR+\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\r8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0013\u0010\f\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R+\u0010\u0014\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\r8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0017\u0010\f\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0012R+\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u0004\u001a\u00020\u00188F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u001e\u0010\f\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR+\u0010\u001f\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\r8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\"\u0010\f\u001a\u0004\b \u0010\u0010\"\u0004\b!\u0010\u0012R+\u0010#\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\r8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b&\u0010\f\u001a\u0004\b$\u0010\u0010\"\u0004\b%\u0010\u0012R+\u0010(\u001a\u00020'2\u0006\u0010\u0004\u001a\u00020'8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b-\u0010\f\u001a\u0004\b)\u0010*\"\u0004\b+\u0010,R+\u0010.\u001a\u00020'2\u0006\u0010\u0004\u001a\u00020'8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b1\u0010\f\u001a\u0004\b/\u0010*\"\u0004\b0\u0010,R+\u00102\u001a\u00020'2\u0006\u0010\u0004\u001a\u00020'8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b5\u0010\f\u001a\u0004\b3\u0010*\"\u0004\b4\u0010,R+\u00106\u001a\u00020'2\u0006\u0010\u0004\u001a\u00020'8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b9\u0010\f\u001a\u0004\b7\u0010*\"\u0004\b8\u0010,R+\u0010:\u001a\u00020'2\u0006\u0010\u0004\u001a\u00020'8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b=\u0010\f\u001a\u0004\b;\u0010*\"\u0004\b<\u0010,R+\u0010>\u001a\u00020'2\u0006\u0010\u0004\u001a\u00020'8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bA\u0010\f\u001a\u0004\b?\u0010*\"\u0004\b@\u0010,R+\u0010B\u001a\u00020'2\u0006\u0010\u0004\u001a\u00020'8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bE\u0010\f\u001a\u0004\bC\u0010*\"\u0004\bD\u0010,R+\u0010F\u001a\u00020'2\u0006\u0010\u0004\u001a\u00020'8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bI\u0010\f\u001a\u0004\bG\u0010*\"\u0004\bH\u0010,R+\u0010K\u001a\u00020J2\u0006\u0010\u0004\u001a\u00020J8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bN\u0010\f\u001a\u0004\bL\u0010\u001b\"\u0004\bM\u0010\u001dR+\u0010O\u001a\u00020J2\u0006\u0010\u0004\u001a\u00020J8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bR\u0010\f\u001a\u0004\bP\u0010\u001b\"\u0004\bQ\u0010\u001dR+\u0010S\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\r8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bV\u0010\f\u001a\u0004\bT\u0010\u0010\"\u0004\bU\u0010\u0012R+\u0010W\u001a\u00020'2\u0006\u0010\u0004\u001a\u00020'8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bZ\u0010\f\u001a\u0004\bX\u0010*\"\u0004\bY\u0010,R7\u0010\\\u001a\b\u0012\u0004\u0012\u00020\r0[2\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\r0[8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\ba\u0010\f\u001a\u0004\b]\u0010^\"\u0004\b_\u0010`¨\u0006f"}, d2 = {"Lcom/wifiaudio/receiver/AppState;", "", "<init>", "()V", "<set-?>", "", "connected", "getConnected", "()Z", "setConnected", "(Z)V", "connected$delegate", "Landroidx/compose/runtime/MutableState;", "", "statusText", "getStatusText", "()Ljava/lang/String;", "setStatusText", "(Ljava/lang/String;)V", "statusText$delegate", "hero", "getHero", "setHero", "hero$delegate", "Landroidx/compose/ui/graphics/Color;", "heroColor", "getHeroColor-0d7_KjU", "()J", "setHeroColor-8_81llA", "(J)V", "heroColor$delegate", "transport", "getTransport", "setTransport", "transport$delegate", PlayerService.EXTRA_CODEC, "getCodec", "setCodec", "codec$delegate", "", PlayerService.EXTRA_RATE, "getRate", "()I", "setRate", "(I)V", "rate$delegate", PlayerService.EXTRA_CHANNELS, "getChannels", "setChannels", "channels$delegate", "kbps", "getKbps", "setKbps", "kbps$delegate", "audioMs", "getAudioMs", "setAudioMs", "audioMs$delegate", "jitterMs", "getJitterMs", "setJitterMs", "jitterMs$delegate", "latencyMs", "getLatencyMs", "setLatencyMs", "latencyMs$delegate", "rxKb", "getRxKb", "setRxKb", "rxKb$delegate", "targetMs", "getTargetMs", "setTargetMs", "targetMs$delegate", "", PlayerService.EXTRA_DROPPED, "getDropped", "setDropped", "dropped$delegate", PlayerService.EXTRA_UNDERRUN, "getUnderrun", "setUnderrun", "underrun$delegate", PlayerService.EXTRA_OUTPUT, "getOutput", "setOutput", "output$delegate", "senderRawMs", "getSenderRawMs", "setSenderRawMs", "senderRawMs$delegate", "", "logs", "getLogs", "()Ljava/util/List;", "setLogs", "(Ljava/util/List;)V", "logs$delegate", "onPlayerState", "", PlayerService.EXTRA_STATE, "msg", "receiver_release"}, k = 1, mv = {2, 3, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class AppState {
    public static final int $stable = 0;
    public static final AppState INSTANCE = new AppState();

    /* renamed from: connected$delegate, reason: from kotlin metadata */
    private static final MutableState connected = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(false, null, 2, null);

    /* renamed from: statusText$delegate, reason: from kotlin metadata */
    private static final MutableState statusText = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("等待连接…", null, 2, null);

    /* renamed from: hero$delegate, reason: from kotlin metadata */
    private static final MutableState hero = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("未连接", null, 2, null);

    /* renamed from: heroColor$delegate, reason: from kotlin metadata */
    private static final MutableState heroColor = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(Color.m5401boximpl(ColorKt.Color(4281631487L)), null, 2, null);

    /* renamed from: transport$delegate, reason: from kotlin metadata */
    private static final MutableState transport = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("-", null, 2, null);

    /* renamed from: codec$delegate, reason: from kotlin metadata */
    private static final MutableState codec = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("-", null, 2, null);

    /* renamed from: rate$delegate, reason: from kotlin metadata */
    private static final MutableState rate = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: channels$delegate, reason: from kotlin metadata */
    private static final MutableState channels = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: kbps$delegate, reason: from kotlin metadata */
    private static final MutableState kbps = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: audioMs$delegate, reason: from kotlin metadata */
    private static final MutableState audioMs = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: jitterMs$delegate, reason: from kotlin metadata */
    private static final MutableState jitterMs = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: latencyMs$delegate, reason: from kotlin metadata */
    private static final MutableState latencyMs = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: rxKb$delegate, reason: from kotlin metadata */
    private static final MutableState rxKb = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: targetMs$delegate, reason: from kotlin metadata */
    private static final MutableState targetMs = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: dropped$delegate, reason: from kotlin metadata */
    private static final MutableState dropped = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0L, null, 2, null);

    /* renamed from: underrun$delegate, reason: from kotlin metadata */
    private static final MutableState underrun = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0L, null, 2, null);

    /* renamed from: output$delegate, reason: from kotlin metadata */
    private static final MutableState output = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);

    /* renamed from: senderRawMs$delegate, reason: from kotlin metadata */
    private static final MutableState senderRawMs = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(-1, null, 2, null);

    /* renamed from: logs$delegate, reason: from kotlin metadata */
    private static final MutableState logs = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(CollectionsKt.emptyList(), null, 2, null);

    private AppState() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean getConnected() {
        return ((Boolean) connected.getValue()).booleanValue();
    }

    public final void setConnected(boolean z) {
        connected.setValue(Boolean.valueOf(z));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String getStatusText() {
        return (String) statusText.getValue();
    }

    public final void setStatusText(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        statusText.setValue(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String getHero() {
        return (String) hero.getValue();
    }

    public final void setHero(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        hero.setValue(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: getHeroColor-0d7_KjU, reason: not valid java name */
    public final long m8886getHeroColor0d7_KjU() {
        return ((Color) heroColor.getValue()).m5421unboximpl();
    }

    /* renamed from: setHeroColor-8_81llA, reason: not valid java name */
    public final void m8887setHeroColor8_81llA(long j) {
        heroColor.setValue(Color.m5401boximpl(j));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String getTransport() {
        return (String) transport.getValue();
    }

    public final void setTransport(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        transport.setValue(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String getCodec() {
        return (String) codec.getValue();
    }

    public final void setCodec(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        codec.setValue(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getRate() {
        return ((Number) rate.getValue()).intValue();
    }

    public final void setRate(int i) {
        rate.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getChannels() {
        return ((Number) channels.getValue()).intValue();
    }

    public final void setChannels(int i) {
        channels.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getKbps() {
        return ((Number) kbps.getValue()).intValue();
    }

    public final void setKbps(int i) {
        kbps.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getAudioMs() {
        return ((Number) audioMs.getValue()).intValue();
    }

    public final void setAudioMs(int i) {
        audioMs.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getJitterMs() {
        return ((Number) jitterMs.getValue()).intValue();
    }

    public final void setJitterMs(int i) {
        jitterMs.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getLatencyMs() {
        return ((Number) latencyMs.getValue()).intValue();
    }

    public final void setLatencyMs(int i) {
        latencyMs.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getRxKb() {
        return ((Number) rxKb.getValue()).intValue();
    }

    public final void setRxKb(int i) {
        rxKb.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getTargetMs() {
        return ((Number) targetMs.getValue()).intValue();
    }

    public final void setTargetMs(int i) {
        targetMs.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final long getDropped() {
        return ((Number) dropped.getValue()).longValue();
    }

    public final void setDropped(long j) {
        dropped.setValue(Long.valueOf(j));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final long getUnderrun() {
        return ((Number) underrun.getValue()).longValue();
    }

    public final void setUnderrun(long j) {
        underrun.setValue(Long.valueOf(j));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String getOutput() {
        return (String) output.getValue();
    }

    public final void setOutput(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        output.setValue(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getSenderRawMs() {
        return ((Number) senderRawMs.getValue()).intValue();
    }

    public final void setSenderRawMs(int i) {
        senderRawMs.setValue(Integer.valueOf(i));
    }

    public final List<String> getLogs() {
        return (List) logs.getValue();
    }

    public final void setLogs(List<String> list) {
        Intrinsics.checkNotNullParameter(list, "<set-?>");
        logs.setValue(list);
    }

    public final void onPlayerState(String state, String msg) {
        Intrinsics.checkNotNullParameter(state, "state");
        Intrinsics.checkNotNullParameter(msg, "msg");
        switch (state.hashCode()) {
            case -1884319283:
                if (state.equals("stopped")) {
                    setConnected(false);
                    setHero("已断开");
                    m8887setHeroColor8_81llA(ColorKt.Color(4281631487L));
                    break;
                } else {
                    return;
                }
            case -775651656:
                if (state.equals("connecting")) {
                    setConnected(true);
                    setHero("连接中");
                    m8887setHeroColor8_81llA(ColorKt.Color(4293304892L));
                    break;
                } else {
                    return;
                }
            case -493563858:
                if (state.equals("playing")) {
                    setConnected(true);
                    setHero("播放中");
                    m8887setHeroColor8_81llA(ColorKt.Color(4281313372L));
                    break;
                } else {
                    return;
                }
            case 96784904:
                if (state.equals("error")) {
                    setConnected(false);
                    setHero("未连接");
                    m8887setHeroColor8_81llA(ColorKt.Color(4281631487L));
                    break;
                } else {
                    return;
                }
            default:
                return;
        }
        setStatusText(msg);
    }
}
