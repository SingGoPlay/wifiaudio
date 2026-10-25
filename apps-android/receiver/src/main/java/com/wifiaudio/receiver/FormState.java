package com.wifiaudio.receiver;

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt__SnapshotStateKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0007\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R+\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u00058F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u000b\u0010\f\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR+\u0010\r\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u00058F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0010\u0010\f\u001a\u0004\b\u000e\u0010\b\"\u0004\b\u000f\u0010\nR+\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u00118F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0017\u0010\f\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R+\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u00118F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u001b\u0010\f\u001a\u0004\b\u0019\u0010\u0014\"\u0004\b\u001a\u0010\u0016R+\u0010\u001c\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u00118F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u001f\u0010\f\u001a\u0004\b\u001d\u0010\u0014\"\u0004\b\u001e\u0010\u0016R+\u0010 \u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u00058F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b#\u0010\f\u001a\u0004\b!\u0010\b\"\u0004\b\"\u0010\nR+\u0010$\u001a\u00020\u00112\u0006\u0010\u0004\u001a\u00020\u00118F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b'\u0010\f\u001a\u0004\b%\u0010\u0014\"\u0004\b&\u0010\u0016R+\u0010)\u001a\u00020(2\u0006\u0010\u0004\u001a\u00020(8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b.\u0010\f\u001a\u0004\b*\u0010+\"\u0004\b,\u0010-¨\u0006/"}, d2 = {"Lcom/wifiaudio/receiver/FormState;", "", "<init>", "()V", "<set-?>", "", PlayerService.EXTRA_IP, "getIp", "()Ljava/lang/String;", "setIp", "(Ljava/lang/String;)V", "ip$delegate", "Landroidx/compose/runtime/MutableState;", PlayerService.EXTRA_PORT, "getPort", "setPort", "port$delegate", "", "transportIdx", "getTransportIdx", "()I", "setTransportIdx", "(I)V", "transportIdx$delegate", "outputIdx", "getOutputIdx", "setOutputIdx", "outputIdx$delegate", "bufferIdx", "getBufferIdx", "setBufferIdx", "bufferIdx$delegate", "customMs", "getCustomMs", "setCustomMs", "customMs$delegate", PlayerService.EXTRA_VOLUME, "getVolume", "setVolume", "volume$delegate", "", "autoReconnect", "getAutoReconnect", "()Z", "setAutoReconnect", "(Z)V", "autoReconnect$delegate", "receiver_release"}, k = 1, mv = {2, 3, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class FormState {
    public static final int $stable = 0;
    public static final FormState INSTANCE = new FormState();

    /* renamed from: ip$delegate, reason: from kotlin metadata */
    private static final MutableState ip = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);

    /* renamed from: port$delegate, reason: from kotlin metadata */
    private static final MutableState port = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("47800", null, 2, null);

    /* renamed from: transportIdx$delegate, reason: from kotlin metadata */
    private static final MutableState transportIdx = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: outputIdx$delegate, reason: from kotlin metadata */
    private static final MutableState outputIdx = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: bufferIdx$delegate, reason: from kotlin metadata */
    private static final MutableState bufferIdx = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);

    /* renamed from: customMs$delegate, reason: from kotlin metadata */
    private static final MutableState customMs = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);

    /* renamed from: volume$delegate, reason: from kotlin metadata */
    private static final MutableState volume = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(100, null, 2, null);

    /* renamed from: autoReconnect$delegate, reason: from kotlin metadata */
    private static final MutableState autoReconnect = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(false, null, 2, null);

    private FormState() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String getIp() {
        return (String) ip.getValue();
    }

    public final void setIp(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        ip.setValue(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String getPort() {
        return (String) port.getValue();
    }

    public final void setPort(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        port.setValue(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getTransportIdx() {
        return ((Number) transportIdx.getValue()).intValue();
    }

    public final void setTransportIdx(int i) {
        transportIdx.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getOutputIdx() {
        return ((Number) outputIdx.getValue()).intValue();
    }

    public final void setOutputIdx(int i) {
        outputIdx.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getBufferIdx() {
        return ((Number) bufferIdx.getValue()).intValue();
    }

    public final void setBufferIdx(int i) {
        bufferIdx.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String getCustomMs() {
        return (String) customMs.getValue();
    }

    public final void setCustomMs(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        customMs.setValue(str);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final int getVolume() {
        return ((Number) volume.getValue()).intValue();
    }

    public final void setVolume(int i) {
        volume.setValue(Integer.valueOf(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final boolean getAutoReconnect() {
        return ((Boolean) autoReconnect.getValue()).booleanValue();
    }

    public final void setAutoReconnect(boolean z) {
        autoReconnect.setValue(Boolean.valueOf(z));
    }
}
