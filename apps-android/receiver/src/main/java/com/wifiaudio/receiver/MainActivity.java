package com.wifiaudio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.activity.compose.ComponentActivityKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import com.wifiaudio.receiver.MainActivity;
import com.wifiaudio.receiver.PlayerService;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\b\u0003\n\u0002\b\u0004*\u0002\f\u000f\b\u0007\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\b\u0010\n\u001a\u00020\u0007H\u0002J\b\u0010\u0011\u001a\u00020\u0007H\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\rR\u0010\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010¨\u0006\u0012"}, d2 = {"Lcom/wifiaudio/receiver/MainActivity;", "Landroidx/activity/ComponentActivity;", "<init>", "()V", "prefs", "Landroid/content/SharedPreferences;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "registerReceivers", "stateReceiver", "com/wifiaudio/receiver/MainActivity$stateReceiver$1", "Lcom/wifiaudio/receiver/MainActivity$stateReceiver$1;", "statsReceiver", "com/wifiaudio/receiver/MainActivity$statsReceiver$1", "Lcom/wifiaudio/receiver/MainActivity$statsReceiver$1;", "onDestroy", "receiver_release"}, k = 1, mv = {2, 3, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class MainActivity extends ComponentActivity {
    public static final int $stable = 8;
    private SharedPreferences prefs;
    private final MainActivity$stateReceiver$1 stateReceiver = new BroadcastReceiver() { // from class: com.wifiaudio.receiver.MainActivity$stateReceiver$1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (Intrinsics.areEqual(intent != null ? intent.getAction() : null, PlayerService.ACTION_STATE)) {
                String stringExtra = intent.getStringExtra(PlayerService.EXTRA_STATE);
                if (stringExtra == null) {
                    stringExtra = "";
                }
                String stringExtra2 = intent.getStringExtra("msg");
                AppState.INSTANCE.onPlayerState(stringExtra, stringExtra2 != null ? stringExtra2 : "");
            }
        }
    };
    private final MainActivity$statsReceiver$1 statsReceiver = new BroadcastReceiver() { // from class: com.wifiaudio.receiver.MainActivity$statsReceiver$1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (Intrinsics.areEqual(intent != null ? intent.getAction() : null, PlayerService.ACTION_STATS)) {
                AppState appState = AppState.INSTANCE;
                String stringExtra = intent.getStringExtra(PlayerService.EXTRA_CUR_TRANSPORT);
                if (stringExtra == null) {
                    stringExtra = "-";
                }
                appState.setTransport(stringExtra);
                AppState appState2 = AppState.INSTANCE;
                String stringExtra2 = intent.getStringExtra(PlayerService.EXTRA_CODEC);
                appState2.setCodec(stringExtra2 != null ? stringExtra2 : "-");
                AppState.INSTANCE.setRate(intent.getIntExtra(PlayerService.EXTRA_RATE, 0));
                AppState.INSTANCE.setChannels(intent.getIntExtra(PlayerService.EXTRA_CHANNELS, 0));
                AppState.INSTANCE.setKbps(intent.getIntExtra(PlayerService.EXTRA_BITRATE, 0));
                AppState.INSTANCE.setAudioMs(intent.getIntExtra(PlayerService.EXTRA_AUDIO_MS, 0));
                AppState.INSTANCE.setJitterMs(intent.getIntExtra(PlayerService.EXTRA_JITTER_MS, 0));
                AppState.INSTANCE.setLatencyMs(intent.getIntExtra(PlayerService.EXTRA_LATENCY_MS, 0));
                AppState.INSTANCE.setRxKb(intent.getIntExtra(PlayerService.EXTRA_RX_KB, 0));
                AppState.INSTANCE.setTargetMs(intent.getIntExtra(PlayerService.EXTRA_TARGET_MS, 0));
                AppState.INSTANCE.setDropped(intent.getLongExtra(PlayerService.EXTRA_DROPPED, 0L));
                AppState.INSTANCE.setUnderrun(intent.getLongExtra(PlayerService.EXTRA_UNDERRUN, 0L));
                AppState appState3 = AppState.INSTANCE;
                String stringExtra3 = intent.getStringExtra(PlayerService.EXTRA_OUTPUT);
                if (stringExtra3 == null) {
                    stringExtra3 = "";
                }
                appState3.setOutput(stringExtra3);
                AppState.INSTANCE.setSenderRawMs(intent.getIntExtra(PlayerService.EXTRA_SENDER_MS, -1));
                ArrayList<String> stringArrayListExtra = intent.getStringArrayListExtra(PlayerService.EXTRA_LOG);
                if (stringArrayListExtra != null) {
                    AppState.INSTANCE.setLogs(CollectionsKt.toList(stringArrayListExtra));
                }
            }
        }
    };

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("wifiaudio_prefs", 0);
        Intrinsics.checkNotNullExpressionValue(sharedPreferences, "getSharedPreferences(...)");
        this.prefs = sharedPreferences;
        PlayerService.setStateListener(new AnonymousClass1());
        PlayerService.setStatsListener(new AnonymousClass2());
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS") != 0) {
            requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
        }
        ComponentActivityKt.setContent$default(this, null, ComposableLambdaKt.composableLambdaInstance(2022527449, true, new Function2() { // from class: com.wifiaudio.receiver.MainActivity$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(Object obj, Object obj2) {
                return MainActivity.onCreate$lambda$0(this.f$0, (Composer) obj, ((Integer) obj2).intValue());
            }
        }), 1, null);
        registerReceivers();
    }

    /* compiled from: MainActivity.kt */
    @Metadata(d1 = {"\u0000!\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J0\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0016¨\u0006\u000b"}, d2 = {"com/wifiaudio/receiver/MainActivity$onCreate$1", "Lcom/wifiaudio/receiver/PlayerService$StateListener;", "onState", "", PlayerService.EXTRA_STATE, "", "msg", PlayerService.EXTRA_CODEC, PlayerService.EXTRA_RATE, "", PlayerService.EXTRA_CHANNELS, "receiver_release"}, k = 1, mv = {2, 3, 0}, xi = 48)
    /* renamed from: com.wifiaudio.receiver.MainActivity$onCreate$1, reason: invalid class name */
    public static final class AnonymousClass1 implements PlayerService.StateListener {
        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void onState$lambda$0(String str, String str2) {
            AppState.INSTANCE.onPlayerState(str, str2);
        }

        @Override // com.wifiaudio.receiver.PlayerService.StateListener
        public void onState(final String state, final String msg, String codec, int rate, int channels) {
            Intrinsics.checkNotNullParameter(state, "state");
            Intrinsics.checkNotNullParameter(msg, "msg");
            Intrinsics.checkNotNullParameter(codec, "codec");
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.wifiaudio.receiver.MainActivity$onCreate$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.AnonymousClass1.onState$lambda$0(state, msg);
                }
            });
        }
    }

    /* compiled from: MainActivity.kt */
    @Metadata(d1 = {"\u00001\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0096\u0001\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\b\u0010\u0014\u001a\u0004\u0018\u00010\u00052\u000e\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u00162\u0006\u0010\u0017\u001a\u00020\bH\u0016¨\u0006\u0018"}, d2 = {"com/wifiaudio/receiver/MainActivity$onCreate$2", "Lcom/wifiaudio/receiver/PlayerService$StatsListener;", "onStats", "", "transport", "", PlayerService.EXTRA_CODEC, PlayerService.EXTRA_RATE, "", PlayerService.EXTRA_CHANNELS, "kbps", "audioMs", "jitterMs", "latencyMs", "rxKb", "decFrames", "targetMs", PlayerService.EXTRA_DROPPED, "", "underruns", PlayerService.EXTRA_OUTPUT, "logs", "Ljava/util/ArrayList;", "senderRawMs", "receiver_release"}, k = 1, mv = {2, 3, 0}, xi = 48)
    /* renamed from: com.wifiaudio.receiver.MainActivity$onCreate$2, reason: invalid class name */
    public static final class AnonymousClass2 implements PlayerService.StatsListener {
        AnonymousClass2() {
        }

        @Override // com.wifiaudio.receiver.PlayerService.StatsListener
        public void onStats(final String transport, final String codec, final int rate, final int channels, final int kbps, final int audioMs, final int jitterMs, final int latencyMs, final int rxKb, int decFrames, final int targetMs, final long dropped, final long underruns, final String output, final ArrayList<String> logs, final int senderRawMs) {
            MainActivity.this.runOnUiThread(new Runnable() { // from class: com.wifiaudio.receiver.MainActivity$onCreate$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.AnonymousClass2.onStats$lambda$0(transport, codec, rate, channels, kbps, audioMs, jitterMs, latencyMs, rxKb, targetMs, dropped, underruns, output, senderRawMs, logs);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final void onStats$lambda$0(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, long j, long j2, String str3, int i9, ArrayList arrayList) {
            List<String> listEmptyList;
            AppState appState = AppState.INSTANCE;
            if (str == null) {
                str = "-";
            }
            appState.setTransport(str);
            AppState appState2 = AppState.INSTANCE;
            if (str2 == null) {
                str2 = "-";
            }
            appState2.setCodec(str2);
            AppState.INSTANCE.setRate(i);
            AppState.INSTANCE.setChannels(i2);
            AppState.INSTANCE.setKbps(i3);
            AppState.INSTANCE.setAudioMs(i4);
            AppState.INSTANCE.setJitterMs(i5);
            AppState.INSTANCE.setLatencyMs(i6);
            AppState.INSTANCE.setRxKb(i7);
            AppState.INSTANCE.setTargetMs(i8);
            AppState.INSTANCE.setDropped(j);
            AppState.INSTANCE.setUnderrun(j2);
            AppState.INSTANCE.setOutput(str3 == null ? "" : str3);
            AppState.INSTANCE.setSenderRawMs(i9);
            AppState appState3 = AppState.INSTANCE;
            if (arrayList == null || (listEmptyList = CollectionsKt.toList(arrayList)) == null) {
                listEmptyList = CollectionsKt.emptyList();
            }
            appState3.setLogs(listEmptyList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit onCreate$lambda$0(MainActivity mainActivity, Composer composer, int i) {
        ComposerKt.sourceInformation(composer, "C94@4100L16:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i & 3) != 2, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(2022527449, i, -1, "com.wifiaudio.receiver.MainActivity.onCreate.<anonymous> (MainActivity.kt:94)");
            }
            MainActivity mainActivity2 = mainActivity;
            SharedPreferences sharedPreferences = mainActivity.prefs;
            if (sharedPreferences == null) {
                Intrinsics.throwUninitializedPropertyAccessException("prefs");
                sharedPreferences = null;
            }
            MainActivityKt.App(mainActivity2, sharedPreferences, composer, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private final void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(PlayerService.ACTION_STATE);
        IntentFilter intentFilter2 = new IntentFilter(PlayerService.ACTION_STATS);
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(this.stateReceiver, intentFilter, 4);
            registerReceiver(this.statsReceiver, intentFilter2, 4);
        } else {
            registerReceiver(this.stateReceiver, intentFilter);
            registerReceiver(this.statsReceiver, intentFilter2);
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        try {
            unregisterReceiver(this.stateReceiver);
        } catch (Throwable unused) {
        }
        try {
            unregisterReceiver(this.statsReceiver);
        } catch (Throwable unused2) {
        }
        PlayerService.setStateListener(null);
        PlayerService.setStatsListener(null);
        super.onDestroy();
    }
}
