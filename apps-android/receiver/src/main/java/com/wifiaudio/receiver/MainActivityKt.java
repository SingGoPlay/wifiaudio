package com.wifiaudio.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import androidx.activity.ComponentActivity;
import androidx.autofill.HintConstants;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.foundation.text.KeyboardActions;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt__SnapshotStateKt;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.TextLayoutResult;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.VisualTransformation;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import androidx.core.view.PointerIconCompat;
import androidx.window.core.layout.WindowSizeClass;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.io.CloseableKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.Dispatchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import top.yukonga.miuix.kmp.basic.ButtonDefaults;
import top.yukonga.miuix.kmp.basic.ButtonKt;
import top.yukonga.miuix.kmp.basic.CardColors;
import top.yukonga.miuix.kmp.basic.CardDefaults;
import top.yukonga.miuix.kmp.basic.CardKt;
import top.yukonga.miuix.kmp.basic.SwitchKt;
import top.yukonga.miuix.kmp.basic.TextFieldKt;
import top.yukonga.miuix.kmp.basic.TextKt;
import top.yukonga.miuix.kmp.theme.MiuixThemeKt;
import top.yukonga.miuix.kmp.theme.TextStyles;
import top.yukonga.miuix.kmp.theme.ThemeController;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000^\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\n\u001a\u001d\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\u0010\u0006\u001a%\u0010\u0007\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\u0003¢\u0006\u0002\u0010\n\u001a\u0015\u0010\u000b\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003¢\u0006\u0002\u0010\f\u001a-\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0013H\u0003¢\u0006\u0002\u0010\u0014\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0016\u001a'\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0003¢\u0006\u0002\u0010\u001a\u001a\u001d\u0010\u001b\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0003¢\u0006\u0002\u0010\u0006\u001aI\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\u000f2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000f0\u001e2\u0006\u0010\u001f\u001a\u00020 2\u0012\u0010!\u001a\u000e\u0012\u0004\u0012\u00020 \u0012\u0004\u0012\u00020\u00010\"2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0003¢\u0006\u0002\u0010#\u001a\u001d\u0010$\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0003¢\u0006\u0002\u0010\u0006\u001a\u0015\u0010%\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003¢\u0006\u0002\u0010\f\u001aT\u0010&\u001a\u00020\u0001*\u00020\u00032\u0006\u0010'\u001a\u00020\u000f2\u0006\u0010(\u001a\u00020\u000f2\u0006\u0010)\u001a\u00020 2\u0006\u0010*\u001a\u00020 2\u0006\u0010+\u001a\u00020 2\u0006\u0010,\u001a\u00020\u000f2\u0006\u0010-\u001a\u00020 2\u0006\u0010.\u001a\u00020/2\u0006\u0010\u0004\u001a\u00020\u0005H\u0002\u001a\f\u00100\u001a\u00020\u0001*\u00020\u0003H\u0002\u001a\f\u00101\u001a\u00020\u0001*\u00020\u0003H\u0002\u001a\f\u0010\u0004\u001a\u00020\u0005*\u00020\u0003H\u0002\u001a\f\u00102\u001a\u00020\u000f*\u00020\u0003H\u0002\u001a\u0016\u00103\u001a\b\u0012\u0004\u0012\u0002040\u001e2\u0006\u0010\u0004\u001a\u00020\u0005H\u0002\u001a\u001e\u00105\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00052\f\u00106\u001a\b\u0012\u0004\u0012\u0002040\u001eH\u0002\u001a\u0018\u00107\u001a\u00020/2\u0006\u00108\u001a\u00020\u000f2\u0006\u00109\u001a\u00020\u000fH\u0002\u001a\u0016\u0010:\u001a\u00020\u000f2\u0006\u0010;\u001a\u00020<H\u0082@¢\u0006\u0002\u0010=\u001a\u001d\u0010>\u001a\u00020\u00012\u0006\u0010?\u001a\u00020\u000f2\u0006\u0010;\u001a\u00020<H\u0003¢\u0006\u0002\u0010@¨\u0006A²\u0006\n\u0010B\u001a\u00020\u000fX\u008a\u008e\u0002²\u0006\n\u0010C\u001a\u00020\u000fX\u008a\u008e\u0002²\u0006\n\u0010D\u001a\u00020/X\u008a\u008e\u0002²\u0006\n\u0010\u001f\u001a\u00020 X\u008a\u008e\u0002²\u0006\n\u0010E\u001a\u00020\u000fX\u008a\u008e\u0002²\u0006\n\u0010F\u001a\u00020\u000fX\u008a\u008e\u0002"}, d2 = {"App", "", "activity", "Landroidx/activity/ComponentActivity;", "prefs", "Landroid/content/SharedPreferences;", "(Landroidx/activity/ComponentActivity;Landroid/content/SharedPreferences;Landroidx/compose/runtime/Composer;I)V", "MainScreen", "controller", "Ltop/yukonga/miuix/kmp/theme/ThemeController;", "(Landroidx/activity/ComponentActivity;Landroid/content/SharedPreferences;Ltop/yukonga/miuix/kmp/theme/ThemeController;Landroidx/compose/runtime/Composer;I)V", "HeroCard", "(Landroidx/activity/ComponentActivity;Landroidx/compose/runtime/Composer;I)V", "HeroButton", "text", "", "modifier", "Landroidx/compose/ui/Modifier;", "onClick", "Lkotlin/Function0;", "(Ljava/lang/String;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V", "StatsCard", "(Landroidx/compose/runtime/Composer;I)V", "StatCell", "label", "value", "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "ConfigCard", "MiuixDropdown", "options", "", "selected", "", "onSelect", "Lkotlin/Function1;", "(Ljava/lang/String;Ljava/util/List;ILkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "PresetCard", "DiagLogCard", "connect", PlayerService.EXTRA_IP, PlayerService.EXTRA_PORT, "transportIdx", "outputIdx", "bufferIdx", "customMs", PlayerService.EXTRA_VOLUME, "autoReconnect", "", "disconnect", "runDiag", "scanNearby", "loadPresets", "Lorg/json/JSONObject;", "savePresets", "list", "isNewer", "latest", "current", "checkUpdate", "ctx", "Landroid/content/Context;", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "UpdateBanner", "tag", "(Ljava/lang/String;Landroid/content/Context;Landroidx/compose/runtime/Composer;I)V", "receiver_release", "updateTag", "nearby", "expanded", HintConstants.AUTOFILL_HINT_NAME, "diag"}, k = 2, mv = {2, 3, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class MainActivityKt {

    /* compiled from: MainActivity.kt */
    @Metadata(k = 3, mv = {2, 3, 0}, xi = 48)
    @DebugMetadata(c = "com.wifiaudio.receiver.MainActivityKt", f = "MainActivity.kt", i = {0, 0}, l = {762}, m = "checkUpdate", n = {"ctx", "current"}, nl = {-1}, s = {"L$0", "L$1"}, v = 2)
    /* renamed from: com.wifiaudio.receiver.MainActivityKt$checkUpdate$1, reason: invalid class name */
    static final class AnonymousClass1 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        AnonymousClass1(Continuation<? super AnonymousClass1> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return MainActivityKt.checkUpdate(null, this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit App$lambda$2(ComponentActivity componentActivity, SharedPreferences sharedPreferences, int i, Composer composer, int i2) {
        App(componentActivity, sharedPreferences, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$5(ComponentActivity componentActivity, SharedPreferences sharedPreferences, int i, Composer composer, int i2) {
        ConfigCard(componentActivity, sharedPreferences, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit DiagLogCard$lambda$4(ComponentActivity componentActivity, int i, Composer composer, int i2) {
        DiagLogCard(componentActivity, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit HeroButton$lambda$1(String str, Modifier modifier, Function0 function0, int i, int i2, Composer composer, int i3) {
        HeroButton(str, modifier, function0, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1), i2);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit HeroCard$lambda$1(ComponentActivity componentActivity, int i, Composer composer, int i2) {
        HeroCard(componentActivity, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$6(ComponentActivity componentActivity, SharedPreferences sharedPreferences, ThemeController themeController, int i, Composer composer, int i2) {
        MainScreen(componentActivity, sharedPreferences, themeController, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MiuixDropdown$lambda$4(String str, List list, int i, Function1 function1, Modifier modifier, int i2, int i3, Composer composer, int i4) {
        MiuixDropdown(str, list, i, function1, modifier, composer, RecomposeScopeImplKt.updateChangedFlags(i2 | 1), i3);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PresetCard$lambda$8(ComponentActivity componentActivity, SharedPreferences sharedPreferences, int i, Composer composer, int i2) {
        PresetCard(componentActivity, sharedPreferences, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit StatCell$lambda$1(String str, String str2, Modifier modifier, int i, int i2, Composer composer, int i3) {
        StatCell(str, str2, modifier, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1), i2);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit StatsCard$lambda$0(int i, Composer composer, int i2) {
        StatsCard(composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit UpdateBanner$lambda$0(String str, Context context, int i, Composer composer, int i2) {
        UpdateBanner(str, context, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit UpdateBanner$lambda$2(String str, Context context, int i, Composer composer, int i2) {
        UpdateBanner(str, context, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    public static final void App(final ComponentActivity activity, final SharedPreferences prefs, Composer composer, final int i) {
        int i2;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(prefs, "prefs");
        Composer composerStartRestartGroup = composer.startRestartGroup(-1424114941);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(App)N(activity,prefs)190@8423L51,191@8515L55,191@8479L91:MainActivity.kt#kv94a1");
        if ((i & 6) == 0) {
            i2 = (composerStartRestartGroup.changedInstance(activity) ? 4 : 2) | i;
        } else {
            i2 = i;
        }
        if ((i & 48) == 0) {
            i2 |= composerStartRestartGroup.changedInstance(prefs) ? 32 : 16;
        }
        if (!composerStartRestartGroup.shouldExecute((i2 & 19) != 18, i2 & 1)) {
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1424114941, i2, -1, "com.wifiaudio.receiver.App (MainActivity.kt:188)");
            }
            boolean z = prefs.getBoolean("night_mode", false);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1520113654, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChanged = composerStartRestartGroup.changed(z);
            Object objRememberedValue = composerStartRestartGroup.rememberedValue();
            if (zChanged || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                ThemeController themeController = new ThemeController(null, null, null, null, null, null, Boolean.valueOf(z), 63, null);
                composerStartRestartGroup.updateRememberedValue(themeController);
                objRememberedValue = themeController;
            }
            final ThemeController themeController2 = (ThemeController) objRememberedValue;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            MiuixThemeKt.MiuixTheme(themeController2, (TextStyles) null, false, (Function2<? super Composer, ? super Integer, Unit>) ComposableLambdaKt.rememberComposableLambda(-1491921614, true, new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda37
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.App$lambda$1(activity, prefs, themeController2, (Composer) obj, ((Integer) obj2).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 3072, 6);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda38
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.App$lambda$2(activity, prefs, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit App$lambda$1(ComponentActivity componentActivity, SharedPreferences sharedPreferences, ThemeController themeController, Composer composer, int i) {
        ComposerKt.sourceInformation(composer, "C192@8525L39:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i & 3) != 2, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1491921614, i, -1, "com.wifiaudio.receiver.App.<anonymous> (MainActivity.kt:192)");
            }
            MainScreen(componentActivity, sharedPreferences, themeController, composer, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private static final void MainScreen(ComponentActivity componentActivity, final SharedPreferences sharedPreferences, final ThemeController themeController, Composer composer, final int i) {
        int i2;
        final ComponentActivity componentActivity2;
        Composer composerStartRestartGroup = composer.startRestartGroup(-404313046);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(MainScreen)N(activity,prefs,controller)198@8711L66,200@8820L31,201@8877L49,201@8856L70,209@9106L21,205@8932L1310:MainActivity.kt#kv94a1");
        if ((i & 6) == 0) {
            i2 = i | (composerStartRestartGroup.changedInstance(componentActivity) ? 4 : 2);
        } else {
            i2 = i;
        }
        if ((i & 48) == 0) {
            i2 |= composerStartRestartGroup.changedInstance(sharedPreferences) ? 32 : 16;
        }
        if (composerStartRestartGroup.shouldExecute((i2 & 19) != 18, i2 & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-404313046, i2, -1, "com.wifiaudio.receiver.MainScreen (MainActivity.kt:197)");
            }
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1997776756, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(Boolean.valueOf(sharedPreferences.getBoolean("night_mode", false)), null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue);
            }
            final MutableState mutableState = (MutableState) objRememberedValue;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1997773303, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue2 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue2 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue2);
            }
            MutableState mutableState2 = (MutableState) objRememberedValue2;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            Unit unit = Unit.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1997771461, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composerStartRestartGroup.changedInstance(componentActivity);
            MainActivityKt$MainScreen$1$1 mainActivityKt$MainScreen$1$1RememberedValue = composerStartRestartGroup.rememberedValue();
            if (zChangedInstance || mainActivityKt$MainScreen$1$1RememberedValue == Composer.INSTANCE.getEmpty()) {
                mainActivityKt$MainScreen$1$1RememberedValue = new MainActivityKt$MainScreen$1$1(componentActivity, mutableState2, null);
                composerStartRestartGroup.updateRememberedValue(mainActivityKt$MainScreen$1$1RememberedValue);
            }
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            EffectsKt.LaunchedEffect(unit, (Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object>) mainActivityKt$MainScreen$1$1RememberedValue, composerStartRestartGroup, 6);
            Modifier modifierM915padding3ABfNKs = PaddingKt.m915padding3ABfNKs(ScrollKt.verticalScroll$default(BackgroundKt.m286backgroundbw27NRU$default(SizeKt.fillMaxSize$default(Modifier.INSTANCE, 0.0f, 1, null), ColorKt.Color(((Boolean) mutableState.getValue()).booleanValue() ? 4280558628L : 4294440951L), null, 2, null), ScrollKt.rememberScrollState(0, composerStartRestartGroup, 0, 1), false, null, false, 14, null), Dp.m8245constructorimpl(16));
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composerStartRestartGroup, 0);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composerStartRestartGroup, 0));
            CompositionLocalMap currentCompositionLocalMap = composerStartRestartGroup.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composerStartRestartGroup, modifierM915padding3ABfNKs);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composerStartRestartGroup.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composerStartRestartGroup.startReusableNode();
            if (composerStartRestartGroup.getInserting()) {
                composerStartRestartGroup.createNode(constructor);
            } else {
                composerStartRestartGroup.useNode();
            }
            Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composerStartRestartGroup);
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -873850047, "C213@9210L33,216@9280L635,232@9957L18,238@10086L27,241@10149L27,244@10215L21:MainActivity.kt#kv94a1");
            UpdateBanner(MainScreen$lambda$2(mutableState2), componentActivity, composerStartRestartGroup, (i2 << 3) & 112);
            Alignment.Vertical centerVertically = Alignment.INSTANCE.getCenterVertically();
            Modifier modifierM919paddingqDBjuR0$default = PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(12), 7, null);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            MeasurePolicy measurePolicyRowMeasurePolicy = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), centerVertically, composerStartRestartGroup, 48);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode2 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composerStartRestartGroup, 0));
            CompositionLocalMap currentCompositionLocalMap2 = composerStartRestartGroup.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier2 = ComposedModifierKt.materializeModifier(composerStartRestartGroup, modifierM919paddingqDBjuR0$default);
            Function0<ComposeUiNode> constructor2 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composerStartRestartGroup.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composerStartRestartGroup.startReusableNode();
            if (composerStartRestartGroup.getInserting()) {
                composerStartRestartGroup.createNode(constructor2);
            } else {
                composerStartRestartGroup.useNode();
            }
            Composer composerM4661constructorimpl2 = Updater.m4661constructorimpl(composerStartRestartGroup);
            Updater.m4669setimpl(composerM4661constructorimpl2, measurePolicyRowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl2, currentCompositionLocalMap2, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl2, Integer.valueOf(iHashCode2), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl2, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl2, modifierMaterializeModifier2, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1440897452, "C217@9406L189,223@9623L133,226@9793L112,223@9608L297:MainActivity.kt#kv94a1");
            int i3 = i2;
            TextKt.m11101TextNvy7gAk("🎧 WiFiAudio 接收端", RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null), 0L, null, TextUnitKt.getSp(22), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composerStartRestartGroup, 1597446, 0, 262060);
            composerStartRestartGroup = composerStartRestartGroup;
            Modifier modifierM915padding3ABfNKs2 = PaddingKt.m915padding3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(0));
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 185034473, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance2 = composerStartRestartGroup.changedInstance(sharedPreferences);
            Object objRememberedValue3 = composerStartRestartGroup.rememberedValue();
            if (zChangedInstance2 || objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue3 = new Function0() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda13
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.MainScreen$lambda$5$0$0$0(mutableState, sharedPreferences);
                    }
                };
                composerStartRestartGroup.updateRememberedValue(objRememberedValue3);
            }
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            CardKt.m10637CardiWtaglI(modifierM915padding3ABfNKs2, 0.0f, null, null, null, false, (Function0) objRememberedValue3, null, ComposableLambdaKt.rememberComposableLambda(1981137017, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda14
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.MainScreen$lambda$5$0$1(mutableState, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 100663302, 190);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            composerStartRestartGroup.endNode();
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            int i4 = i3 & 14;
            componentActivity2 = componentActivity;
            HeroCard(componentActivity2, composerStartRestartGroup, i4);
            if (AppState.INSTANCE.getConnected()) {
                composerStartRestartGroup.startReplaceGroup(1357310059);
                ComposerKt.sourceInformation(composerStartRestartGroup, "235@10037L11");
                StatsCard(composerStartRestartGroup, 0);
            } else {
                composerStartRestartGroup.startReplaceGroup(-873050558);
            }
            composerStartRestartGroup.endReplaceGroup();
            int i5 = i3 & 126;
            ConfigCard(componentActivity2, sharedPreferences, composerStartRestartGroup, i5);
            PresetCard(componentActivity2, sharedPreferences, composerStartRestartGroup, i5);
            DiagLogCard(componentActivity2, composerStartRestartGroup, i4);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            composerStartRestartGroup.endNode();
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            componentActivity2 = componentActivity;
            composerStartRestartGroup.skipToGroupEnd();
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda15
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.MainScreen$lambda$6(componentActivity2, sharedPreferences, themeController, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    private static final String MainScreen$lambda$2(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$5$0$0$0(MutableState mutableState, SharedPreferences sharedPreferences) {
        mutableState.setValue(Boolean.valueOf(!((Boolean) mutableState.getValue()).booleanValue()));
        sharedPreferences.edit().putBoolean("night_mode", ((Boolean) mutableState.getValue()).booleanValue()).apply();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$5$0$1(MutableState mutableState, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C227@9811L80:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1981137017, i, -1, "com.wifiaudio.receiver.MainScreen.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:227)");
            }
            TextKt.m11101TextNvy7gAk(((Boolean) mutableState.getValue()).booleanValue() ? "☀️" : "🌙", PaddingKt.m915padding3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(10)), 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 48, 0, 262140);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private static final void HeroCard(final ComponentActivity componentActivity, Composer composer, final int i) {
        int i2;
        Composer composerStartRestartGroup = composer.startRestartGroup(-1641586267);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(HeroCard)N(activity)253@10476L41,254@10524L1361,250@10314L1571:MainActivity.kt#kv94a1");
        if ((i & 6) == 0) {
            i2 = (composerStartRestartGroup.changedInstance(componentActivity) ? 4 : 2) | i;
        } else {
            i2 = i;
        }
        if (!composerStartRestartGroup.shouldExecute((i2 & 3) != 2, i2 & 1)) {
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1641586267, i2, -1, "com.wifiaudio.receiver.HeroCard (MainActivity.kt:249)");
            }
            CardKt.m10636CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(12), 7, null), Dp.m8245constructorimpl(20), null, CardDefaults.INSTANCE.m10630defaultColorsdgg9oW8(AppState.INSTANCE.m8886getHeroColor0d7_KjU(), 0L, composerStartRestartGroup, CardDefaults.$stable << 6, 2), ComposableLambdaKt.rememberComposableLambda(2007855702, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda40
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.HeroCard$lambda$0(componentActivity, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 24630, 4);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda41
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.HeroCard$lambda$1(componentActivity, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit HeroCard$lambda$0(final ComponentActivity componentActivity, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C255@10534L1345:MainActivity.kt#kv94a1");
        if (composer.shouldExecute((i & 17) != 16, i & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(2007855702, i, -1, "com.wifiaudio.receiver.HeroCard.<anonymous> (MainActivity.kt:255)");
            }
            Modifier modifierM915padding3ABfNKs = PaddingKt.m915padding3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(20));
            ComposerKt.sourceInformationMarkerStart(composer, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composer, modifierM915padding3ABfNKs);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, -358143768, "C256@10606L452,266@11071L29,267@11113L91,268@11217L30,269@11260L609:MainActivity.kt#kv94a1");
            Alignment.Vertical centerVertically = Alignment.INSTANCE.getCenterVertically();
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Modifier.Companion companion = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), centerVertically, composer, 48);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode2 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap2 = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier2 = ComposedModifierKt.materializeModifier(composer, companion);
            Function0<ComposeUiNode> constructor2 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor2);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl2 = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl2, measurePolicyRowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl2, currentCompositionLocalMap2, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl2, Integer.valueOf(iHashCode2), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl2, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl2, modifierMaterializeModifier2, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, 705358115, "C257@10676L210,263@10903L29,264@10949L95:MainActivity.kt#kv94a1");
            BoxKt.Box(BackgroundKt.m286backgroundbw27NRU$default(ClipKt.clip(SizeKt.m963size3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(12)), RoundedCornerShapeKt.getCircleShape()), Color.m5410copywmQWz5c$default(Color.INSTANCE.m5448getWhite0d7_KjU(), 0.9f, 0.0f, 0.0f, 0.0f, 14, null), null, 2, null), composer, 0);
            SpacerKt.Spacer(SizeKt.m968width3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(10)), composer, 6);
            TextKt.m11101TextNvy7gAk(AppState.INSTANCE.getHero(), null, Color.INSTANCE.m5448getWhite0d7_KjU(), null, TextUnitKt.getSp(22), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597824, 0, 262058);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(4)), composer, 6);
            TextKt.m11101TextNvy7gAk(AppState.INSTANCE.getStatusText(), null, Color.m5410copywmQWz5c$default(Color.INSTANCE.m5448getWhite0d7_KjU(), 0.85f, 0.0f, 0.0f, 0.0f, 14, null), null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24960, 0, 262122);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(14)), composer, 6);
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Modifier.Companion companion2 = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy2 = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), Alignment.INSTANCE.getTop(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode3 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap3 = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier3 = ComposedModifierKt.materializeModifier(composer, companion2);
            Function0<ComposeUiNode> constructor3 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor3);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl3 = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl3, measurePolicyRowMeasurePolicy2, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl3, currentCompositionLocalMap3, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl3, Integer.valueOf(iHashCode3), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl3, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl3, modifierMaterializeModifier3, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance2 = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, -1849221409, "C270@11344L178,270@11282L240,274@11602L253,274@11539L316:MainActivity.kt#kv94a1");
            float f = 6;
            Modifier modifierM919paddingqDBjuR0$default = PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance2, Modifier.INSTANCE, 1.0f, false, 2, null), 0.0f, 0.0f, Dp.m8245constructorimpl(f), 0.0f, 11, null);
            ComposerKt.sourceInformationMarkerStart(composer, -475292711, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composer.changedInstance(componentActivity);
            Object objRememberedValue = composer.rememberedValue();
            if (zChangedInstance || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = new Function0() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda30
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.HeroCard$lambda$0$0$1$0$0(componentActivity);
                    }
                };
                composer.updateRememberedValue(objRememberedValue);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            HeroButton("🔊 测试音", modifierM919paddingqDBjuR0$default, (Function0) objRememberedValue, composer, 6, 0);
            Modifier modifierM919paddingqDBjuR0$default2 = PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance2, Modifier.INSTANCE, 1.0f, false, 2, null), Dp.m8245constructorimpl(f), 0.0f, 0.0f, 0.0f, 14, null);
            ComposerKt.sourceInformationMarkerStart(composer, -475284380, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance2 = composer.changedInstance(componentActivity);
            Object objRememberedValue2 = composer.rememberedValue();
            if (zChangedInstance2 || objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue2 = new Function0() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda31
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.HeroCard$lambda$0$0$1$1$0(componentActivity);
                    }
                };
                composer.updateRememberedValue(objRememberedValue2);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            HeroButton("🔍 诊断", modifierM919paddingqDBjuR0$default2, (Function0) objRememberedValue2, composer, 6, 0);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit HeroCard$lambda$0$0$1$0$0(ComponentActivity componentActivity) {
        Intent action = new Intent(componentActivity, (Class<?>) PlayerService.class).setAction(PlayerService.ACTION_TEST_TONE);
        Intrinsics.checkNotNullExpressionValue(action, "setAction(...)");
        componentActivity.startService(action);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit HeroCard$lambda$0$0$1$1$0(ComponentActivity componentActivity) {
        Intent intent = new Intent(componentActivity, (Class<?>) PlayerService.class);
        intent.setAction(PlayerService.ACTION_STATE);
        componentActivity.startService(intent);
        Intrinsics.checkNotNull(componentActivity, "null cannot be cast to non-null type com.wifiaudio.receiver.MainActivity");
        runDiag((MainActivity) componentActivity);
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0043  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0052  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x005b  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x005d  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0066  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00c5  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00d1  */
    /* JADX WARN: Removed duplicated region for block: B:50:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static final void HeroButton(final String str, Modifier modifier, final Function0<Unit> function0, Composer composer, final int i, final int i2) {
        int i3;
        Modifier modifier2;
        Function0<Unit> function02;
        Composer composer2;
        final Modifier modifier3;
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup;
        Composer composerStartRestartGroup = composer.startRestartGroup(-614568591);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(HeroButton)N(text,modifier,onClick)287@12079L102,287@11996L185:MainActivity.kt#kv94a1");
        if ((i & 6) == 0) {
            i3 = (composerStartRestartGroup.changed(str) ? 4 : 2) | i;
        } else {
            i3 = i;
        }
        int i4 = i2 & 2;
        if (i4 == 0) {
            if ((i & 48) == 0) {
                modifier2 = modifier;
                i3 |= composerStartRestartGroup.changed(modifier2) ? 32 : 16;
            }
            if ((i & 384) != 0) {
                function02 = function0;
                i3 |= composerStartRestartGroup.changedInstance(function02) ? 256 : 128;
            } else {
                function02 = function0;
            }
            if (composerStartRestartGroup.shouldExecute((i3 & 147) == 146, i3 & 1)) {
                composer2 = composerStartRestartGroup;
                composer2.skipToGroupEnd();
                modifier3 = modifier2;
            } else {
                Modifier.Companion companion = i4 != 0 ? Modifier.INSTANCE : modifier2;
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-614568591, i3, -1, "com.wifiaudio.receiver.HeroButton (MainActivity.kt:286)");
                }
                Modifier modifier4 = companion;
                composer2 = composerStartRestartGroup;
                ButtonKt.m10622ButtoneZJyerg(function02, SizeKt.m949height3ABfNKs(companion, Dp.m8245constructorimpl(40)), false, Dp.m8245constructorimpl(16), 0.0f, 0.0f, null, null, null, null, ComposableLambdaKt.rememberComposableLambda(101854687, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda27
                    @Override // kotlin.jvm.functions.Function3
                    public final Object invoke(Object obj, Object obj2, Object obj3) {
                        return MainActivityKt.HeroButton$lambda$0(str, (RowScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                    }
                }, composerStartRestartGroup, 54), composer2, ((i3 >> 6) & 14) | 3072, 6, PointerIconCompat.TYPE_NO_DROP);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
                modifier3 = modifier4;
            }
            scopeUpdateScopeEndRestartGroup = composer2.endRestartGroup();
            if (scopeUpdateScopeEndRestartGroup == null) {
                scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda28
                    @Override // kotlin.jvm.functions.Function2
                    public final Object invoke(Object obj, Object obj2) {
                        return MainActivityKt.HeroButton$lambda$1(str, modifier3, function0, i, i2, (Composer) obj, ((Integer) obj2).intValue());
                    }
                });
                return;
            }
            return;
        }
        i3 |= 48;
        modifier2 = modifier;
        if ((i & 384) != 0) {
        }
        if (composerStartRestartGroup.shouldExecute((i3 & 147) == 146, i3 & 1)) {
        }
        scopeUpdateScopeEndRestartGroup = composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup == null) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit HeroButton$lambda$0(String str, RowScope Button, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Button, "$this$Button");
        ComposerKt.sourceInformation(composer, "C288@12089L86:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(101854687, i, -1, "com.wifiaudio.receiver.HeroButton.<anonymous> (MainActivity.kt:288)");
            }
            TextKt.m11101TextNvy7gAk(str, null, Color.INSTANCE.m5448getWhite0d7_KjU(), null, TextUnitKt.getSp(14), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597824, 0, 262058);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private static final void StatsCard(Composer composer, final int i) {
        Composer composerStartRestartGroup = composer.startRestartGroup(-449510310);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(StatsCard)294@12227L3043:MainActivity.kt#kv94a1");
        if (!composerStartRestartGroup.shouldExecute(i != 0, i & 1)) {
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-449510310, i, -1, "com.wifiaudio.receiver.StatsCard (MainActivity.kt:293)");
            }
            CardKt.m10636CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(12), 7, null), 0.0f, null, null, ComposableSingletons$MainActivityKt.INSTANCE.m8889getLambda$424896757$receiver_release(), composerStartRestartGroup, 24582, 14);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda29
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.StatsCard$lambda$0(i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void StatCell(final String str, final String str2, Modifier modifier, Composer composer, final int i, final int i2) {
        int i3;
        final Modifier modifier2;
        Composer composerStartRestartGroup = composer.startRestartGroup(1958950406);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(StatCell)N(label,value,modifier)349@15400L330,349@15374L356:MainActivity.kt#kv94a1");
        if ((i & 6) == 0) {
            i3 = (composerStartRestartGroup.changed(str) ? 4 : 2) | i;
        } else {
            i3 = i;
        }
        if ((i & 48) == 0) {
            i3 |= composerStartRestartGroup.changed(str2) ? 32 : 16;
        }
        int i4 = i2 & 4;
        if (i4 != 0) {
            i3 |= 384;
        } else if ((i & 384) == 0) {
            i3 |= composerStartRestartGroup.changed(modifier) ? 256 : 128;
        }
        if (!composerStartRestartGroup.shouldExecute((i3 & 147) != 146, i3 & 1)) {
            composerStartRestartGroup.skipToGroupEnd();
            modifier2 = modifier;
        } else {
            if (i4 != 0) {
                modifier = Modifier.INSTANCE;
            }
            Modifier modifier3 = modifier;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1958950406, i3, -1, "com.wifiaudio.receiver.StatCell (MainActivity.kt:348)");
            }
            CardKt.m10636CardEUb7tLY(modifier3, 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(445705205, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda10
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.StatCell$lambda$0(str2, str, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, ((i3 >> 6) & 14) | 24576, 14);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
            modifier2 = modifier3;
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda12
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.StatCell$lambda$1(str, str2, modifier2, i, i2, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit StatCell$lambda$0(String str, String str2, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C350@15410L314:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(445705205, i, -1, "com.wifiaudio.receiver.StatCell.<anonymous> (MainActivity.kt:350)");
            }
            Modifier modifierM917paddingVpY3zN4$default = PaddingKt.m917paddingVpY3zN4$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, Dp.m8245constructorimpl(10), 1, null);
            Alignment.Horizontal centerHorizontally = Alignment.INSTANCE.getCenterHorizontally();
            ComposerKt.sourceInformationMarkerStart(composer, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), centerHorizontally, composer, 48);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composer, modifierM917paddingVpY3zN4$default);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, -1557415653, "C351@15545L93,352@15651L63:MainActivity.kt#kv94a1");
            TextKt.m11101TextNvy7gAk(str, null, ColorKt.Color(4281631487L), null, TextUnitKt.getSp(22), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597824, 0, 262058);
            TextKt.m11101TextNvy7gAk(str2, null, ColorKt.Color(4287402928L), null, TextUnitKt.getSp(11), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24960, 0, 262122);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private static final void ConfigCard(final ComponentActivity componentActivity, final SharedPreferences sharedPreferences, Composer composer, final int i) {
        int i2;
        Composer composerStartRestartGroup = composer.startRestartGroup(-1299300938);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(ConfigCard)N(activity,prefs)371@16210L35,373@16272L611,373@16251L632,386@16954L4278,386@16889L4343:MainActivity.kt#kv94a1");
        if ((i & 6) == 0) {
            i2 = (composerStartRestartGroup.changedInstance(componentActivity) ? 4 : 2) | i;
        } else {
            i2 = i;
        }
        if ((i & 48) == 0) {
            i2 |= composerStartRestartGroup.changedInstance(sharedPreferences) ? 32 : 16;
        }
        if (!composerStartRestartGroup.shouldExecute((i2 & 19) != 18, i2 & 1)) {
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1299300938, i2, -1, "com.wifiaudio.receiver.ConfigCard (MainActivity.kt:370)");
            }
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1207284633, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("扫描中…", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue);
            }
            final MutableState mutableState = (MutableState) objRememberedValue;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            Unit unit = Unit.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1207287193, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composerStartRestartGroup.changedInstance(componentActivity) | composerStartRestartGroup.changedInstance(sharedPreferences);
            MainActivityKt$ConfigCard$1$1 mainActivityKt$ConfigCard$1$1RememberedValue = composerStartRestartGroup.rememberedValue();
            if (zChangedInstance || mainActivityKt$ConfigCard$1$1RememberedValue == Composer.INSTANCE.getEmpty()) {
                mainActivityKt$ConfigCard$1$1RememberedValue = new MainActivityKt$ConfigCard$1$1(componentActivity, sharedPreferences, mutableState, null);
                composerStartRestartGroup.updateRememberedValue(mainActivityKt$ConfigCard$1$1RememberedValue);
            }
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            EffectsKt.LaunchedEffect(unit, (Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object>) mainActivityKt$ConfigCard$1$1RememberedValue, composerStartRestartGroup, 6);
            CardKt.m10636CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(12), 7, null), 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(-768734233, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda3
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.ConfigCard$lambda$4(componentActivity, sharedPreferences, mutableState, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda4
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.ConfigCard$lambda$5(componentActivity, sharedPreferences, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    private static final String ConfigCard$lambda$1(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4(final ComponentActivity componentActivity, final SharedPreferences sharedPreferences, MutableState mutableState, ColumnScope Card, Composer composer, int i) {
        final MutableState mutableState2;
        String str;
        String str2;
        String str3;
        String str4;
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C387@16964L4262:MainActivity.kt#kv94a1");
        if (composer.shouldExecute((i & 17) != 16, i & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-768734233, i, -1, "com.wifiaudio.receiver.ConfigCard.<anonymous> (MainActivity.kt:387)");
            }
            Modifier modifierM915padding3ABfNKs = PaddingKt.m915padding3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(16));
            ComposerKt.sourceInformationMarkerStart(composer, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composer, modifierM915padding3ABfNKs);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, 1899065842, "C388@17036L70,389@17119L30,394@17287L471,407@17904L163,411@18082L247,393@17253L1076,419@18342L30,422@18406L75,423@18509L62,425@18644L132,423@18494L282,430@18813L578,445@19404L30,452@19672L28,448@19490L276,455@19779L253,461@20045L29,464@20139L339,481@20827L30,484@20893L323:MainActivity.kt#kv94a1");
            TextKt.m11101TextNvy7gAk("📡 连接配置", null, 0L, null, TextUnitKt.getSp(14), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597446, 0, 262062);
            float f = 10;
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f)), composer, 6);
            final boolean connected = AppState.INSTANCE.getConnected();
            ComposerKt.sourceInformationMarkerStart(composer, 754001172, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChanged = composer.changed(connected) | composer.changedInstance(componentActivity) | composer.changedInstance(sharedPreferences);
            Object objRememberedValue = composer.rememberedValue();
            if (zChanged || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = new Function0() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda16
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.ConfigCard$lambda$4$0$0$0(connected, componentActivity, sharedPreferences);
                    }
                };
                composer.updateRememberedValue(objRememberedValue);
            }
            Function0 function0 = (Function0) objRememberedValue;
            ComposerKt.sourceInformationMarkerEnd(composer);
            ButtonKt.m10622ButtoneZJyerg(function0, SizeKt.m949height3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(58)), false, Dp.m8245constructorimpl(18), 0.0f, 0.0f, ButtonDefaults.INSTANCE.m10616buttonColorsro_MJ88(ColorKt.Color(connected ? 4293280576L : 4281631487L), 0L, Color.INSTANCE.m5448getWhite0d7_KjU(), 0L, composer, (ButtonDefaults.$stable << 12) | 384, 10), null, null, null, ComposableLambdaKt.rememberComposableLambda(718168523, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda17
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.ConfigCard$lambda$4$0$1(connected, (RowScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composer, 54), composer, 3120, 6, 948);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(12)), composer, 6);
            TextKt.m11101TextNvy7gAk("附近设备（自动发现，点击填入）", null, ColorKt.Color(4287402928L), null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24966, 0, 262122);
            float f2 = 8;
            Modifier modifierM919paddingqDBjuR0$default = PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, Dp.m8245constructorimpl(4), 0.0f, Dp.m8245constructorimpl(f2), 5, null);
            ComposerKt.sourceInformationMarkerStart(composer, 754039867, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composer.changedInstance(componentActivity);
            Object objRememberedValue2 = composer.rememberedValue();
            if (zChangedInstance || objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                mutableState2 = mutableState;
                objRememberedValue2 = new Function0() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda18
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.ConfigCard$lambda$4$0$2$0(componentActivity, mutableState2);
                    }
                };
                composer.updateRememberedValue(objRememberedValue2);
            } else {
                mutableState2 = mutableState;
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            CardKt.m10637CardiWtaglI(modifierM919paddingqDBjuR0$default, 0.0f, null, null, null, false, (Function0) objRememberedValue2, null, ComposableLambdaKt.rememberComposableLambda(-118467886, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda19
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.ConfigCard$lambda$4$0$3(mutableState2, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composer, 54), composer, 100663302, 190);
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Modifier.Companion companion = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), Alignment.INSTANCE.getTop(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode2 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap2 = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier2 = ComposedModifierKt.materializeModifier(composer, companion);
            Function0<ComposeUiNode> constructor2 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor2);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl2 = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl2, measurePolicyRowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl2, currentCompositionLocalMap2, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl2, Integer.valueOf(iHashCode2), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl2, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl2, modifierMaterializeModifier2, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, -887209980, "C433@18924L21,431@18835L245,437@19097L28,440@19233L23,438@19142L235:MainActivity.kt#kv94a1");
            String ip = FormState.INSTANCE.getIp();
            float f3 = 52;
            Modifier modifierM949height3ABfNKs = SizeKt.m949height3ABfNKs(RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null), Dp.m8245constructorimpl(f3));
            ComposerKt.sourceInformationMarkerStart(composer, -167164682, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue3 = composer.rememberedValue();
            if (objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue3 = new Function1() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda20
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.ConfigCard$lambda$4$0$4$0$0((String) obj);
                    }
                };
                composer.updateRememberedValue(objRememberedValue3);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            TextFieldKt.m11094TextFieldz9KcpoQ(ip, (Function1<? super String, Unit>) objRememberedValue3, modifierM949height3ABfNKs, 0L, 0L, 0.0f, "IP / 域名 / URL（可带端口）", 0L, 0L, false, false, false, (TextStyle) null, (KeyboardOptions) null, (KeyboardActions) null, (Function2<? super Composer, ? super Integer, Unit>) null, (Function2<? super Composer, ? super Integer, Unit>) null, false, 0, 0, (VisualTransformation) null, (Function1<? super TextLayoutResult, Unit>) null, (MutableInteractionSource) null, (Brush) null, composer, 1572912, 0, 0, 16777144);
            SpacerKt.Spacer(SizeKt.m968width3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f2)), composer, 6);
            String port = FormState.INSTANCE.getPort();
            Modifier modifierM949height3ABfNKs2 = SizeKt.m949height3ABfNKs(SizeKt.m968width3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(110)), Dp.m8245constructorimpl(f3));
            ComposerKt.sourceInformationMarkerStart(composer, -167154792, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue4 = composer.rememberedValue();
            if (objRememberedValue4 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue4 = new Function1() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda21
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.ConfigCard$lambda$4$0$4$1$0((String) obj);
                    }
                };
                composer.updateRememberedValue(objRememberedValue4);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            TextFieldKt.m11094TextFieldz9KcpoQ(port, (Function1<? super String, Unit>) objRememberedValue4, modifierM949height3ABfNKs2, 0L, 0L, 0.0f, "端口", 0L, 0L, false, false, false, (TextStyle) null, (KeyboardOptions) null, (KeyboardActions) null, (Function2<? super Composer, ? super Integer, Unit>) null, (Function2<? super Composer, ? super Integer, Unit>) null, false, 0, 0, (VisualTransformation) null, (Function1<? super TextLayoutResult, Unit>) null, (MutableInteractionSource) null, (Brush) null, composer, 1573296, 0, 0, 16777144);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f)), composer, 6);
            List listListOf = CollectionsKt.listOf((Object[]) new String[]{"AudioTrack", "AAudio 低延迟"});
            int outputIdx = FormState.INSTANCE.getOutputIdx();
            ComposerKt.sourceInformationMarkerStart(composer, 754077049, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue5 = composer.rememberedValue();
            if (objRememberedValue5 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue5 = new Function1() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda23
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.ConfigCard$lambda$4$0$5$0(((Integer) obj).intValue());
                    }
                };
                composer.updateRememberedValue(objRememberedValue5);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            MiuixDropdown("输出", listListOf, outputIdx, (Function1) objRememberedValue5, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
            TextKt.m11101TextNvy7gAk(AppState.INSTANCE.getConnected() ? "协议: " + AppState.INSTANCE.getTransport() + "（发射端决定）" : "协议: 由发射端决定", PaddingKt.m919paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, Dp.m8245constructorimpl(2), 0.0f, 0.0f, 13, null), ColorKt.Color(4287402928L), null, TextUnitKt.getSp(10), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 25008, 0, 262120);
            Composer composer2 = composer;
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f2)), composer2, 6);
            ComposerKt.sourceInformationMarkerStart(composer2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Modifier.Companion companion2 = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy2 = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), Alignment.INSTANCE.getTop(), composer2, 0);
            ComposerKt.sourceInformationMarkerStart(composer2, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode3 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer2, 0));
            CompositionLocalMap currentCompositionLocalMap3 = composer2.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier3 = ComposedModifierKt.materializeModifier(composer2, companion2);
            Function0<ComposeUiNode> constructor3 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer2, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer2.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer2.startReusableNode();
            if (composer2.getInserting()) {
                composer2.createNode(constructor3);
            } else {
                composer2.useNode();
            }
            Composer composerM4661constructorimpl3 = Updater.m4661constructorimpl(composer2);
            Updater.m4669setimpl(composerM4661constructorimpl3, measurePolicyRowMeasurePolicy2, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl3, currentCompositionLocalMap3, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl3, Integer.valueOf(iHashCode3), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl3, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl3, modifierMaterializeModifier3, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer2, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance2 = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer2, 787198332, "C469@20366L28,465@20161L303:MainActivity.kt#kv94a1");
            List listListOf2 = CollectionsKt.listOf((Object[]) new String[]{"默认", "低延迟(游戏)", "高音质(稳定)", "自定义"});
            int bufferIdx = FormState.INSTANCE.getBufferIdx();
            ComposerKt.sourceInformationMarkerStart(composer2, 579589108, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue6 = composer2.rememberedValue();
            if (objRememberedValue6 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue6 = new Function1() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda24
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.ConfigCard$lambda$4$0$6$0$0(((Integer) obj).intValue());
                    }
                };
                composer2.updateRememberedValue(objRememberedValue6);
            }
            ComposerKt.sourceInformationMarkerEnd(composer2);
            MiuixDropdown("缓冲", listListOf2, bufferIdx, (Function1) objRememberedValue6, RowScope.weight$default(rowScopeInstance2, Modifier.INSTANCE, 1.0f, false, 2, null), composer2, 3126, 0);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            if (FormState.INSTANCE.getBufferIdx() == 3) {
                composer2.startReplaceGroup(1902419328);
                ComposerKt.sourceInformation(composer2, "476@20634L27,474@20539L261");
                String customMs = FormState.INSTANCE.getCustomMs();
                Modifier modifierM949height3ABfNKs3 = SizeKt.m949height3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(f3));
                ComposerKt.sourceInformationMarkerStart(composer2, 754107832, "CC(remember):MainActivity.kt#9igjgp");
                Object objRememberedValue7 = composer2.rememberedValue();
                if (objRememberedValue7 == Composer.INSTANCE.getEmpty()) {
                    objRememberedValue7 = new Function1() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda25
                        @Override // kotlin.jvm.functions.Function1
                        public final Object invoke(Object obj) {
                            return MainActivityKt.ConfigCard$lambda$4$0$7$0((String) obj);
                        }
                    };
                    composer2.updateRememberedValue(objRememberedValue7);
                }
                ComposerKt.sourceInformationMarkerEnd(composer2);
                str3 = "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp";
                str = "CC(remember):MainActivity.kt#9igjgp";
                str4 = "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo";
                str2 = "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh";
                TextFieldKt.m11094TextFieldz9KcpoQ(customMs, (Function1<? super String, Unit>) objRememberedValue7, modifierM949height3ABfNKs3, 0L, 0L, 0.0f, "缓冲毫秒（20~500，越小延迟越低）", 0L, 0L, false, false, false, (TextStyle) null, (KeyboardOptions) null, (KeyboardActions) null, (Function2<? super Composer, ? super Integer, Unit>) null, (Function2<? super Composer, ? super Integer, Unit>) null, false, 0, 0, (VisualTransformation) null, (Function1<? super TextLayoutResult, Unit>) null, (MutableInteractionSource) null, (Brush) null, composer, 1573296, 0, 0, 16777144);
                composer2 = composer;
                composer2.endReplaceGroup();
            } else {
                str = "CC(remember):MainActivity.kt#9igjgp";
                str2 = "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh";
                str3 = "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp";
                str4 = "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo";
                composer2.startReplaceGroup(1902684037);
                composer2.endReplaceGroup();
            }
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f)), composer2, 6);
            Alignment.Vertical centerVertically = Alignment.INSTANCE.getCenterVertically();
            ComposerKt.sourceInformationMarkerStart(composer2, 844473419, str4);
            Modifier.Companion companion3 = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy3 = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), centerVertically, composer2, 48);
            ComposerKt.sourceInformationMarkerStart(composer2, -1159599143, str2);
            int iHashCode4 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer2, 0));
            CompositionLocalMap currentCompositionLocalMap4 = composer2.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier4 = ComposedModifierKt.materializeModifier(composer2, companion3);
            Function0<ComposeUiNode> constructor4 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer2, -553112988, str3);
            if (!(composer2.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer2.startReusableNode();
            if (composer2.getInserting()) {
                composer2.createNode(constructor4);
            } else {
                composer2.useNode();
            }
            Composer composerM4661constructorimpl4 = Updater.m4661constructorimpl(composer2);
            Updater.m4669setimpl(composerM4661constructorimpl4, measurePolicyRowMeasurePolicy3, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl4, currentCompositionLocalMap4, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl4, Integer.valueOf(iHashCode4), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl4, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl4, modifierMaterializeModifier4, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer2, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance3 = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer2, -249814053, "C485@20963L71,488@21152L32,486@21051L151:MainActivity.kt#kv94a1");
            TextKt.m11101TextNvy7gAk("断线自动重连", RowScope.weight$default(rowScopeInstance3, Modifier.INSTANCE, 1.0f, false, 2, null), 0L, null, TextUnitKt.getSp(13), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24582, 0, 262124);
            boolean autoReconnect = FormState.INSTANCE.getAutoReconnect();
            ComposerKt.sourceInformationMarkerStart(composer, 2070157305, str);
            Object objRememberedValue8 = composer.rememberedValue();
            if (objRememberedValue8 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue8 = new Function1() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda26
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.ConfigCard$lambda$4$0$8$0$0(((Boolean) obj).booleanValue());
                    }
                };
                composer.updateRememberedValue(objRememberedValue8);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            SwitchKt.Switch(autoReconnect, (Function1) objRememberedValue8, null, null, false, composer, 48, 28);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$0$0(boolean z, ComponentActivity componentActivity, SharedPreferences sharedPreferences) {
        if (!z) {
            connect(componentActivity, FormState.INSTANCE.getIp(), FormState.INSTANCE.getPort(), FormState.INSTANCE.getTransportIdx(), FormState.INSTANCE.getOutputIdx(), FormState.INSTANCE.getBufferIdx(), FormState.INSTANCE.getCustomMs(), FormState.INSTANCE.getVolume(), FormState.INSTANCE.getAutoReconnect(), sharedPreferences);
        } else {
            disconnect(componentActivity);
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$1(boolean z, RowScope Button, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Button, "$this$Button");
        ComposerKt.sourceInformation(composer, "C412@18100L215:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(718168523, i, -1, "com.wifiaudio.receiver.ConfigCard.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:412)");
            }
            TextKt.m11101TextNvy7gAk(z ? "⏹ 断 开" : "▶ 连 接", null, Color.INSTANCE.m5448getWhite0d7_KjU(), null, TextUnitKt.getSp(20), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597824, 0, 262058);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$2$0(ComponentActivity componentActivity, MutableState mutableState) {
        mutableState.setValue(scanNearby(componentActivity));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$3(MutableState mutableState, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C426@18662L100:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-118467886, i, -1, "com.wifiaudio.receiver.ConfigCard.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:426)");
            }
            TextKt.m11101TextNvy7gAk(ConfigCard$lambda$1(mutableState), PaddingKt.m915padding3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(12)), ColorKt.Color(4281631487L), null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 25008, 0, 262120);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$4$0$0(String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        FormState.INSTANCE.setIp(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$4$1$0(String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        FormState.INSTANCE.setPort(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$5$0(int i) {
        FormState.INSTANCE.setOutputIdx(i);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$6$0$0(int i) {
        FormState.INSTANCE.setBufferIdx(i);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$7$0(String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        FormState.INSTANCE.setCustomMs(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigCard$lambda$4$0$8$0$0(boolean z) {
        FormState.INSTANCE.setAutoReconnect(z);
        return Unit.INSTANCE;
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x0080  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0082  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x02ac  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x02b6  */
    /* JADX WARN: Removed duplicated region for block: B:80:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static final void MiuixDropdown(final String str, final List<String> list, final int i, Function1<? super Integer, Unit> function1, Modifier modifier, Composer composer, final int i2, final int i3) {
        int i4;
        Modifier modifier2;
        final Function1<? super Integer, Unit> function12;
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup;
        Composer composerStartRestartGroup = composer.startRestartGroup(778346954);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(MiuixDropdown)N(label,options,selected,onSelect,modifier)497@21405L34,498@21444L1537:MainActivity.kt#kv94a1");
        if ((i2 & 6) == 0) {
            i4 = (composerStartRestartGroup.changed(str) ? 4 : 2) | i2;
        } else {
            i4 = i2;
        }
        if ((i2 & 48) == 0) {
            i4 |= composerStartRestartGroup.changedInstance(list) ? 32 : 16;
        }
        if ((i2 & 384) == 0) {
            i4 |= composerStartRestartGroup.changed(i) ? 256 : 128;
        }
        if ((i2 & 3072) == 0) {
            i4 |= composerStartRestartGroup.changedInstance(function1) ? 2048 : 1024;
        }
        int i5 = i3 & 16;
        if (i5 == 0) {
            if ((i2 & 24576) == 0) {
                modifier2 = modifier;
                i4 |= composerStartRestartGroup.changed(modifier2) ? 16384 : 8192;
            }
            if (composerStartRestartGroup.shouldExecute((i4 & 9363) == 9362, i4 & 1)) {
                function12 = function1;
                composerStartRestartGroup.skipToGroupEnd();
            } else {
                Modifier.Companion companion = i5 != 0 ? Modifier.INSTANCE : modifier2;
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(778346954, i4, -1, "com.wifiaudio.receiver.MiuixDropdown (MainActivity.kt:496)");
                }
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 667118092, "CC(remember):MainActivity.kt#9igjgp");
                Object objRememberedValue = composerStartRestartGroup.rememberedValue();
                if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                    objRememberedValue = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
                    composerStartRestartGroup.updateRememberedValue(objRememberedValue);
                }
                final MutableState mutableState = (MutableState) objRememberedValue;
                ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
                Modifier modifierM917paddingVpY3zN4$default = PaddingKt.m917paddingVpY3zN4$default(companion, 0.0f, Dp.m8245constructorimpl(4), 1, null);
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
                MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composerStartRestartGroup, 0);
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
                int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composerStartRestartGroup, 0));
                CompositionLocalMap currentCompositionLocalMap = composerStartRestartGroup.getCurrentCompositionLocalMap();
                Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composerStartRestartGroup, modifierM917paddingVpY3zN4$default);
                Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
                if (!(composerStartRestartGroup.getApplier() instanceof Applier)) {
                    ComposablesKt.invalidApplier();
                }
                composerStartRestartGroup.startReusableNode();
                if (composerStartRestartGroup.getInserting()) {
                    composerStartRestartGroup.createNode(constructor);
                } else {
                    composerStartRestartGroup.useNode();
                }
                Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composerStartRestartGroup);
                Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
                Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
                Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
                Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
                Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
                ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1595076281, "C499@21507L63,500@21594L19,500@21671L354,500@21579L446:MainActivity.kt#kv94a1");
                Modifier modifier3 = companion;
                TextKt.m11101TextNvy7gAk(str, null, ColorKt.Color(4287402928L), null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composerStartRestartGroup, (i4 & 14) | 24960, 0, 262122);
                float f = 2;
                Modifier modifierM919paddingqDBjuR0$default = PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, Dp.m8245constructorimpl(f), 0.0f, 0.0f, 13, null);
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1749662579, "CC(remember):MainActivity.kt#9igjgp");
                Object objRememberedValue2 = composerStartRestartGroup.rememberedValue();
                if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                    objRememberedValue2 = new Function0() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda32
                        @Override // kotlin.jvm.functions.Function0
                        public final Object invoke() {
                            return MainActivityKt.MiuixDropdown$lambda$3$0$0(mutableState);
                        }
                    };
                    composerStartRestartGroup.updateRememberedValue(objRememberedValue2);
                }
                ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
                CardKt.m10637CardiWtaglI(modifierM919paddingqDBjuR0$default, 0.0f, null, null, null, false, (Function0) objRememberedValue2, null, ComposableLambdaKt.rememberComposableLambda(-1270445387, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda34
                    @Override // kotlin.jvm.functions.Function3
                    public final Object invoke(Object obj, Object obj2, Object obj3) {
                        return MainActivityKt.MiuixDropdown$lambda$3$1(list, i, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                    }
                }, composerStartRestartGroup, 54), composerStartRestartGroup, 102236166, 190);
                composerStartRestartGroup = composerStartRestartGroup;
                if (MiuixDropdown$lambda$1(mutableState)) {
                    composerStartRestartGroup.startReplaceGroup(-1594556381);
                    ComposerKt.sourceInformation(composerStartRestartGroup, "511@22181L784,511@22120L845");
                    function12 = function1;
                    CardKt.m10636CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, Dp.m8245constructorimpl(f), 0.0f, 0.0f, 13, null), 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(-1274138932, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda35
                        @Override // kotlin.jvm.functions.Function3
                        public final Object invoke(Object obj, Object obj2, Object obj3) {
                            return MainActivityKt.MiuixDropdown$lambda$3$2(list, function12, mutableState, i, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                        }
                    }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
                    composerStartRestartGroup.endReplaceGroup();
                } else {
                    function12 = function1;
                    composerStartRestartGroup.startReplaceGroup(-1593665534);
                    composerStartRestartGroup.endReplaceGroup();
                }
                ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
                ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
                composerStartRestartGroup.endNode();
                ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
                ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
                ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
                modifier2 = modifier3;
            }
            scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
            if (scopeUpdateScopeEndRestartGroup == null) {
                final Function1<? super Integer, Unit> function13 = function12;
                final Modifier modifier4 = modifier2;
                scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda36
                    @Override // kotlin.jvm.functions.Function2
                    public final Object invoke(Object obj, Object obj2) {
                        return MainActivityKt.MiuixDropdown$lambda$4(str, list, i, function13, modifier4, i2, i3, (Composer) obj, ((Integer) obj2).intValue());
                    }
                });
                return;
            }
            return;
        }
        i4 |= 24576;
        modifier2 = modifier;
        if (composerStartRestartGroup.shouldExecute((i4 & 9363) == 9362, i4 & 1)) {
        }
        scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup == null) {
        }
    }

    private static final boolean MiuixDropdown$lambda$1(MutableState<Boolean> mutableState) {
        return mutableState.getValue().booleanValue();
    }

    private static final void MiuixDropdown$lambda$2(MutableState<Boolean> mutableState, boolean z) {
        mutableState.setValue(Boolean.valueOf(z));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MiuixDropdown$lambda$3$0$0(MutableState mutableState) {
        MiuixDropdown$lambda$2(mutableState, true);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MiuixDropdown$lambda$3$1(List list, int i, ColumnScope Card, Composer composer, int i2) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C501@21685L330:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i2 & 17) != 16, i2 & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1270445387, i2, -1, "com.wifiaudio.receiver.MiuixDropdown.<anonymous>.<anonymous> (MainActivity.kt:501)");
            }
            Modifier modifierM916paddingVpY3zN4 = PaddingKt.m916paddingVpY3zN4(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(12), Dp.m8245constructorimpl(10));
            Arrangement.HorizontalOrVertical spaceBetween = Arrangement.INSTANCE.getSpaceBetween();
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            MeasurePolicy measurePolicyRowMeasurePolicy = RowKt.rowMeasurePolicy(spaceBetween, Alignment.INSTANCE.getTop(), composer, 6);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composer, modifierM916paddingVpY3zN4);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyRowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, 1966403862, "C505@21885L65,506@21967L34:MainActivity.kt#kv94a1");
            TextKt.m11101TextNvy7gAk((String) ((i < 0 || i >= list.size()) ? "" : list.get(i)), null, 0L, null, TextUnitKt.getSp(14), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24576, 0, 262126);
            TextKt.m11101TextNvy7gAk("▾", null, 0L, null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24582, 0, 262126);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MiuixDropdown$lambda$3$2(List list, final Function1 function1, final MutableState mutableState, final int i, ColumnScope Card, Composer composer, int i2) {
        Composer composer2 = composer;
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer2, "C512@22199L752:MainActivity.kt#kv94a1");
        final int i3 = 0;
        if (!composer2.shouldExecute((i2 & 17) != 16, i2 & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1274138932, i2, -1, "com.wifiaudio.receiver.MiuixDropdown.<anonymous>.<anonymous> (MainActivity.kt:512)");
            }
            Modifier modifierM917paddingVpY3zN4$default = PaddingKt.m917paddingVpY3zN4$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, Dp.m8245constructorimpl(4), 1, null);
            ComposerKt.sourceInformationMarkerStart(composer2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composer2, 0);
            ComposerKt.sourceInformationMarkerStart(composer2, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer2, 0));
            CompositionLocalMap currentCompositionLocalMap = composer2.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composer2, modifierM917paddingVpY3zN4$default);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer2, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer2.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer2.startReusableNode();
            if (composer2.getInserting()) {
                composer2.createNode(constructor);
            } else {
                composer2.useNode();
            }
            Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composer2);
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer2, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer2, -168735811, "C:MainActivity.kt#kv94a1");
            composer2.startReplaceGroup(825841150);
            ComposerKt.sourceInformation(composer2, "*515@22390L33,517@22514L397,514@22346L565");
            for (Object obj : list) {
                int i4 = i3 + 1;
                if (i3 < 0) {
                    CollectionsKt.throwIndexOverflow();
                }
                final String str = (String) obj;
                Modifier modifierFillMaxWidth$default = SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null);
                ComposerKt.sourceInformationMarkerStart(composer2, -838526327, "CC(remember):MainActivity.kt#9igjgp");
                boolean zChanged = composer2.changed(function1) | composer2.changed(i3);
                Object objRememberedValue = composer2.rememberedValue();
                if (zChanged || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                    objRememberedValue = new Function0() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda22
                        @Override // kotlin.jvm.functions.Function0
                        public final Object invoke() {
                            return MainActivityKt.MiuixDropdown$lambda$3$2$0$0$0$0(function1, i3, mutableState);
                        }
                    };
                    composer2.updateRememberedValue(objRememberedValue);
                }
                ComposerKt.sourceInformationMarkerEnd(composer2);
                CardKt.m10637CardiWtaglI(modifierFillMaxWidth$default, 0.0f, null, null, null, false, (Function0) objRememberedValue, null, ComposableLambdaKt.rememberComposableLambda(-1982024739, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda33
                    @Override // kotlin.jvm.functions.Function3
                    public final Object invoke(Object obj2, Object obj3, Object obj4) {
                        return MainActivityKt.MiuixDropdown$lambda$3$2$0$0$1(i3, i, str, (ColumnScope) obj2, (Composer) obj3, ((Integer) obj4).intValue());
                    }
                }, composer2, 54), composer2, 100663302, 190);
                composer2 = composer;
                i3 = i4;
            }
            composer.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MiuixDropdown$lambda$3$2$0$0$0$0(Function1 function1, int i, MutableState mutableState) {
        MiuixDropdown$lambda$2(mutableState, false);
        function1.invoke(Integer.valueOf(i));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MiuixDropdown$lambda$3$2$0$0$1(int i, int i2, String str, ColumnScope Card, Composer composer, int i3) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C518@22544L341:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i3 & 17) != 16, i3 & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1982024739, i3, -1, "com.wifiaudio.receiver.MiuixDropdown.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:518)");
            }
            TextKt.m11101TextNvy7gAk(str, PaddingKt.m916paddingVpY3zN4(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(16), Dp.m8245constructorimpl(11)), i == i2 ? ColorKt.Color(4281631487L) : Color.INSTANCE.m5447getUnspecified0d7_KjU(), null, TextUnitKt.getSp(14), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24624, 0, 262120);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private static final void PresetCard(final ComponentActivity componentActivity, final SharedPreferences sharedPreferences, Composer composer, final int i) {
        int i2;
        Composer composerStartRestartGroup = composer.startRestartGroup(1012983609);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(PresetCard)N(activity,prefs)534@23095L47,535@23163L30,536@23210L31,569@24524L1093,569@24459L1158:MainActivity.kt#kv94a1");
        if ((i & 48) == 0) {
            i2 = (composerStartRestartGroup.changedInstance(sharedPreferences) ? 32 : 16) | i;
        } else {
            i2 = i;
        }
        if (!composerStartRestartGroup.shouldExecute((i2 & 17) != 16, i2 & 1)) {
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1012983609, i2, -1, "com.wifiaudio.receiver.PresetCard (MainActivity.kt:533)");
            }
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1480325016, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(loadPresets(sharedPreferences), null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue);
            }
            final MutableState mutableState = (MutableState) objRememberedValue;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1480322857, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue2 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue2 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue2);
            }
            final MutableState mutableState2 = (MutableState) objRememberedValue2;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1480321352, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue3 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue3 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue3);
            }
            final MutableState mutableState3 = (MutableState) objRememberedValue3;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            CardKt.m10636CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(12), 7, null), 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(1543550314, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.PresetCard$lambda$7(mutableState, sharedPreferences, mutableState2, mutableState3, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda11
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.PresetCard$lambda$8(componentActivity, sharedPreferences, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    private static final int PresetCard$lambda$2(MutableState<Integer> mutableState) {
        return mutableState.getValue().intValue();
    }

    private static final void PresetCard$lambda$3(MutableState<Integer> mutableState, int i) {
        mutableState.setValue(Integer.valueOf(i));
    }

    private static final String PresetCard$lambda$5(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final void PresetCard$save(MutableState<List<JSONObject>> mutableState, SharedPreferences sharedPreferences, MutableState<String> mutableState2) throws JSONException {
        List<JSONObject> mutableList = CollectionsKt.toMutableList((Collection) mutableState.getValue());
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(HintConstants.AUTOFILL_HINT_NAME, PresetCard$lambda$5(mutableState2));
        String string = sharedPreferences.getString("last_ip", "");
        jSONObject.put(PlayerService.EXTRA_IP, string != null ? string : "");
        String string2 = sharedPreferences.getString("last_port", "47800");
        jSONObject.put(PlayerService.EXTRA_PORT, string2 != null ? string2 : "47800");
        jSONObject.put("transport", sharedPreferences.getInt("last_transport", 0));
        jSONObject.put(PlayerService.EXTRA_OUTPUT, sharedPreferences.getInt("last_output", 0));
        jSONObject.put(PlayerService.EXTRA_BUFFER, sharedPreferences.getInt("last_buffer", 0));
        mutableList.add(jSONObject);
        mutableState.setValue(mutableList);
        savePresets(sharedPreferences, mutableList);
    }

    private static final void PresetCard$apply(MutableState<List<JSONObject>> mutableState, SharedPreferences sharedPreferences, int i) {
        JSONObject jSONObject = (JSONObject) CollectionsKt.getOrNull(mutableState.getValue(), i);
        if (jSONObject == null) {
            return;
        }
        sharedPreferences.edit().putString("last_ip", jSONObject.optString(PlayerService.EXTRA_IP)).putString("last_port", jSONObject.optString(PlayerService.EXTRA_PORT, "47800")).putInt("last_transport", jSONObject.optInt("transport")).putInt("last_output", jSONObject.optInt(PlayerService.EXTRA_OUTPUT)).putInt("last_buffer", jSONObject.optInt(PlayerService.EXTRA_BUFFER)).apply();
        FormState formState = FormState.INSTANCE;
        String strOptString = jSONObject.optString(PlayerService.EXTRA_IP);
        Intrinsics.checkNotNullExpressionValue(strOptString, "optString(...)");
        formState.setIp(strOptString);
        FormState formState2 = FormState.INSTANCE;
        String strOptString2 = jSONObject.optString(PlayerService.EXTRA_PORT, "47800");
        Intrinsics.checkNotNullExpressionValue(strOptString2, "optString(...)");
        formState2.setPort(strOptString2);
        FormState.INSTANCE.setTransportIdx(jSONObject.optInt("transport"));
        FormState.INSTANCE.setOutputIdx(jSONObject.optInt(PlayerService.EXTRA_OUTPUT));
        FormState.INSTANCE.setBufferIdx(jSONObject.optInt(PlayerService.EXTRA_BUFFER));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PresetCard$lambda$7(final MutableState mutableState, final SharedPreferences sharedPreferences, final MutableState mutableState2, final MutableState mutableState3, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C570@24534L1077:MainActivity.kt#kv94a1");
        if (composer.shouldExecute((i & 17) != 16, i & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1543550314, i, -1, "com.wifiaudio.receiver.PresetCard.<anonymous> (MainActivity.kt:570)");
            }
            Modifier modifierM915padding3ABfNKs = PaddingKt.m915padding3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(16));
            ComposerKt.sourceInformationMarkerStart(composer, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composer, modifierM915padding3ABfNKs);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, 187446240, "C571@24606L71,572@24690L30,577@24956L28,573@24733L317,580@25063L29,581@25105L496:MainActivity.kt#kv94a1");
            TextKt.m11101TextNvy7gAk("🗂️ 连接预设", null, 0L, null, TextUnitKt.getSp(14), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597446, 0, 262062);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(10)), composer, 6);
            Iterable iterable = (Iterable) mutableState.getValue();
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10));
            Iterator it = iterable.iterator();
            while (it.hasNext()) {
                arrayList.add(((JSONObject) it.next()).optString(HintConstants.AUTOFILL_HINT_NAME, "未命名"));
            }
            ArrayList arrayListListOf = arrayList;
            if (arrayListListOf.isEmpty()) {
                arrayListListOf = CollectionsKt.listOf("（无预设）");
            }
            List list = arrayListListOf;
            int iPresetCard$lambda$2 = PresetCard$lambda$2(mutableState2);
            ComposerKt.sourceInformationMarkerStart(composer, -1933605764, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composer.changedInstance(sharedPreferences);
            Object objRememberedValue = composer.rememberedValue();
            if (zChangedInstance || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = new Function1() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda7
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.PresetCard$lambda$7$0$2$0(mutableState, sharedPreferences, mutableState2, ((Integer) obj).intValue());
                    }
                };
                composer.updateRememberedValue(objRememberedValue);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            MiuixDropdown("选择预设（选中即加载）", list, iPresetCard$lambda$2, (Function1) objRememberedValue, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 24582, 0);
            float f = 8;
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f)), composer, 6);
            Alignment.Vertical centerVertically = Alignment.INSTANCE.getCenterVertically();
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Modifier.Companion companion = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), centerVertically, composer, 48);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode2 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap2 = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier2 = ComposedModifierKt.materializeModifier(composer, companion);
            Function0<ComposeUiNode> constructor2 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor2);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl2 = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl2, measurePolicyRowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl2, currentCompositionLocalMap2, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl2, Integer.valueOf(iHashCode2), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl2, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl2, modifierMaterializeModifier2, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, 1696232419, "C584@25256L13,582@25175L213,588@25405L28,589@25467L33,589@25450L137:MainActivity.kt#kv94a1");
            String strPresetCard$lambda$5 = PresetCard$lambda$5(mutableState3);
            float f2 = 52;
            Modifier modifierM949height3ABfNKs = SizeKt.m949height3ABfNKs(RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null), Dp.m8245constructorimpl(f2));
            ComposerKt.sourceInformationMarkerStart(composer, 1440192689, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue2 = composer.rememberedValue();
            if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue2 = new Function1() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda8
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.PresetCard$lambda$7$0$3$0$0(mutableState3, (String) obj);
                    }
                };
                composer.updateRememberedValue(objRememberedValue2);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            TextFieldKt.m11094TextFieldz9KcpoQ(strPresetCard$lambda$5, (Function1<? super String, Unit>) objRememberedValue2, modifierM949height3ABfNKs, 0L, 0L, 0.0f, "预设名", 0L, 0L, false, false, false, (TextStyle) null, (KeyboardOptions) null, (KeyboardActions) null, (Function2<? super Composer, ? super Integer, Unit>) null, (Function2<? super Composer, ? super Integer, Unit>) null, false, 0, 0, (VisualTransformation) null, (Function1<? super TextLayoutResult, Unit>) null, (MutableInteractionSource) null, (Brush) null, composer, 1572912, 0, 0, 16777144);
            SpacerKt.Spacer(SizeKt.m968width3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f)), composer, 6);
            ComposerKt.sourceInformationMarkerStart(composer, 1440199461, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance2 = composer.changedInstance(sharedPreferences);
            Object objRememberedValue3 = composer.rememberedValue();
            if (zChangedInstance2 || objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue3 = new Function0() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda9
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.PresetCard$lambda$7$0$3$1$0(mutableState3, mutableState, sharedPreferences);
                    }
                };
                composer.updateRememberedValue(objRememberedValue3);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            ButtonKt.m10622ButtoneZJyerg((Function0) objRememberedValue3, SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f2)), false, 0.0f, 0.0f, 0.0f, null, null, null, null, ComposableSingletons$MainActivityKt.INSTANCE.m8890getLambda$644695758$receiver_release(), composer, 48, 6, PointerIconCompat.TYPE_GRAB);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        } else {
            composer.skipToGroupEnd();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PresetCard$lambda$7$0$2$0(MutableState mutableState, SharedPreferences sharedPreferences, MutableState mutableState2, int i) {
        PresetCard$apply(mutableState, sharedPreferences, i);
        PresetCard$lambda$3(mutableState2, i);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PresetCard$lambda$7$0$3$0$0(MutableState mutableState, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        mutableState.setValue(it);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit PresetCard$lambda$7$0$3$1$0(MutableState mutableState, MutableState mutableState2, SharedPreferences sharedPreferences) throws JSONException {
        if (!StringsKt.isBlank(PresetCard$lambda$5(mutableState))) {
            PresetCard$save(mutableState2, sharedPreferences, mutableState);
        }
        return Unit.INSTANCE;
    }

    private static final void DiagLogCard(final ComponentActivity componentActivity, Composer composer, final int i) {
        Composer composerStartRestartGroup = composer.startRestartGroup(761742338);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(DiagLogCard)N(activity)599@25704L31,600@25805L769,600@25740L834:MainActivity.kt#kv94a1");
        int i2 = i & 1;
        if (!composerStartRestartGroup.shouldExecute(i2 != 0, i2)) {
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(761742338, i, -1, "com.wifiaudio.receiver.DiagLogCard (MainActivity.kt:598)");
            }
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1889948545, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue);
            }
            final MutableState mutableState = (MutableState) objRememberedValue;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            CardKt.m10636CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(12), 7, null), 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(-1514690127, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda5
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.DiagLogCard$lambda$3(mutableState, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda6
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.DiagLogCard$lambda$4(componentActivity, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    private static final String DiagLogCard$lambda$1(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit DiagLogCard$lambda$3(MutableState mutableState, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C601@25815L753:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1514690127, i, -1, "com.wifiaudio.receiver.DiagLogCard.<anonymous> (MainActivity.kt:601)");
            }
            Modifier modifierM915padding3ABfNKs = PaddingKt.m915padding3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(16));
            ComposerKt.sourceInformationMarkerStart(composer, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composer, modifierM915padding3ABfNKs);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, -1631210455, "C602@25887L71:MainActivity.kt#kv94a1");
            TextKt.m11101TextNvy7gAk("🩺 诊断与日志", null, 0L, null, TextUnitKt.getSp(14), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597446, 0, 262062);
            Composer composer2 = composer;
            if (DiagLogCard$lambda$1(mutableState).length() > 0) {
                composer2.startReplaceGroup(-1631104901);
                ComposerKt.sourceInformation(composer2, "604@26012L76");
                TextKt.m11101TextNvy7gAk(DiagLogCard$lambda$1(mutableState), PaddingKt.m919paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, Dp.m8245constructorimpl(8), 0.0f, 0.0f, 13, null), 0L, null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24624, 0, 262124);
                composer2 = composer;
                composer2.endReplaceGroup();
            } else {
                composer2.startReplaceGroup(-1631017977);
                composer2.endReplaceGroup();
            }
            if (!AppState.INSTANCE.getLogs().isEmpty()) {
                composer2.startReplaceGroup(-1630948382);
                ComposerKt.sourceInformation(composer2, "607@26165L229");
                TextKt.m11101TextNvy7gAk(CollectionsKt.joinToString$default(AppState.INSTANCE.getLogs(), "\n", null, null, 0, null, null, 62, null), PaddingKt.m919paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, Dp.m8245constructorimpl(8), 0.0f, 0.0f, 13, null), ColorKt.Color(4287402928L), null, TextUnitKt.getSp(10), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 25008, 0, 262120);
                composer.endReplaceGroup();
            } else {
                composer2.startReplaceGroup(-1630687145);
                ComposerKt.sourceInformation(composer2, "614@26432L112");
                TextKt.m11101TextNvy7gAk("（连接后显示实时日志）", PaddingKt.m919paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, Dp.m8245constructorimpl(8), 0.0f, 0.0f, 13, null), ColorKt.Color(4287402928L), null, TextUnitKt.getSp(11), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 25014, 0, 262120);
                composer.endReplaceGroup();
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private static final void connect(ComponentActivity componentActivity, String str, String str2, int i, int i2, int i3, String str3, int i4, boolean z, SharedPreferences sharedPreferences) {
        String str4;
        int iIntValue;
        String string = StringsKt.trim((CharSequence) str).toString();
        String str5 = string;
        if (StringsKt.isBlank(str5)) {
            AppState.INSTANCE.setStatusText("请填写发送端 IP 地址");
            return;
        }
        int iIntValue2 = 0;
        String strSubstringAfter$default = StringsKt.contains$default((CharSequence) str5, (CharSequence) "://", false, 2, (Object) null) ? StringsKt.substringAfter$default(string, "://", (String) null, 2, (Object) null) : string;
        int iIndexOf$default = StringsKt.indexOf$default((CharSequence) strSubstringAfter$default, '/', 0, false, 6, (Object) null);
        if (iIndexOf$default >= 0) {
            strSubstringAfter$default = strSubstringAfter$default.substring(0, iIndexOf$default);
            Intrinsics.checkNotNullExpressionValue(strSubstringAfter$default, "substring(...)");
        }
        int iLastIndexOf$default = StringsKt.lastIndexOf$default((CharSequence) strSubstringAfter$default, ':', 0, false, 6, (Object) null);
        if (iLastIndexOf$default > 0) {
            String strSubstring = strSubstringAfter$default.substring(0, iLastIndexOf$default);
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            if (!StringsKt.contains$default((CharSequence) strSubstring, ':', false, 2, (Object) null)) {
                String strSubstring2 = strSubstringAfter$default.substring(0, iLastIndexOf$default);
                Intrinsics.checkNotNullExpressionValue(strSubstring2, "substring(...)");
                String strSubstring3 = strSubstringAfter$default.substring(iLastIndexOf$default + 1);
                Intrinsics.checkNotNullExpressionValue(strSubstring3, "substring(...)");
                Integer intOrNull = StringsKt.toIntOrNull(strSubstring3);
                if (strSubstring2.length() > 0 && intOrNull != null && 1 <= (iIntValue = intOrNull.intValue()) && iIntValue < 65536) {
                    iIntValue2 = intOrNull.intValue();
                    strSubstringAfter$default = strSubstring2;
                }
            }
        }
        if (iIntValue2 <= 0) {
            Integer intOrNull2 = StringsKt.toIntOrNull(str2);
            iIntValue2 = intOrNull2 != null ? intOrNull2.intValue() : 47800;
        }
        sharedPreferences.edit().putString("last_ip", string).putString("last_port", str2).putInt("last_transport", i).putInt("last_output", i2).putInt("last_buffer", i3).putString("last_custom_ms", str3).putInt("last_volume", i4).putBoolean("last_reconnect", z).apply();
        if (i3 == 1) {
            str4 = "low";
        } else if (i3 == 2) {
            str4 = "high";
        } else if (i3 != 3) {
            str4 = DebugKt.DEBUG_PROPERTY_VALUE_AUTO;
        } else {
            Integer intOrNull3 = StringsKt.toIntOrNull(str3);
            str4 = "custom:" + (intOrNull3 != null ? intOrNull3.intValue() : 80);
        }
        Intent action = new Intent(componentActivity, (Class<?>) PlayerService.class).setAction(PlayerService.ACTION_CONNECT);
        Intrinsics.checkNotNullExpressionValue(action, "setAction(...)");
        action.putExtra(PlayerService.EXTRA_IP, strSubstringAfter$default);
        action.putExtra(PlayerService.EXTRA_PORT, iIntValue2);
        action.putExtra("transport", DebugKt.DEBUG_PROPERTY_VALUE_AUTO);
        action.putExtra(PlayerService.EXTRA_BUFFER, str4);
        action.putExtra(PlayerService.EXTRA_AAUDIO, i2 == 1 ? "1" : "0");
        action.putExtra(PlayerService.EXTRA_VOLUME, i4);
        action.putExtra(PlayerService.EXTRA_AUTORECONNECT, z ? "1" : "0");
        componentActivity.startForegroundService(action);
        AppState.INSTANCE.setConnected(true);
        AppState.INSTANCE.setHero("连接中");
        AppState.INSTANCE.m8887setHeroColor8_81llA(ColorKt.Color(4293304892L));
        AppState.INSTANCE.setStatusText("正在连接 " + strSubstringAfter$default + ":" + iIntValue2 + " …");
    }

    private static final void disconnect(ComponentActivity componentActivity) {
        Intent action = new Intent(componentActivity, (Class<?>) PlayerService.class).setAction(PlayerService.ACTION_STOP);
        Intrinsics.checkNotNullExpressionValue(action, "setAction(...)");
        componentActivity.startService(action);
        AppState.INSTANCE.setConnected(false);
        AppState.INSTANCE.setHero("已断开");
        AppState.INSTANCE.setStatusText("已断开");
    }

    private static final void runDiag(ComponentActivity componentActivity) {
        String string = prefs(componentActivity).getString("last_ip", "");
        String str = string != null ? string : "";
        String string2 = prefs(componentActivity).getString("last_port", "47800");
        Integer intOrNull = StringsKt.toIntOrNull(string2 != null ? string2 : "47800");
        AppState.INSTANCE.setStatusText("诊断发送端 " + str + ":" + (intOrNull != null ? intOrNull.intValue() : 47800) + " …（请查看发送端状态）");
    }

    private static final SharedPreferences prefs(ComponentActivity componentActivity) {
        SharedPreferences sharedPreferences = componentActivity.getSharedPreferences("wifiaudio_prefs", 0);
        Intrinsics.checkNotNullExpressionValue(sharedPreferences, "getSharedPreferences(...)");
        return sharedPreferences;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String scanNearby(ComponentActivity componentActivity) {
        String str = "47800";
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND);
            byte[] bytes = "WFAU?".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            InetAddress byName = InetAddress.getByName("255.255.255.255");
            Object systemService = componentActivity.getApplicationContext().getSystemService("wifi");
            Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.net.wifi.WifiManager");
            WifiManager.MulticastLock multicastLockCreateMulticastLock = ((WifiManager) systemService).createMulticastLock("wifiaudio-scan");
            multicastLockCreateMulticastLock.setReferenceCounted(false);
            multicastLockCreateMulticastLock.acquire();
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            String string = prefs(componentActivity).getString("last_port", "47800");
            if (string != null) {
                str = string;
            }
            Integer intOrNull = StringsKt.toIntOrNull(str);
            int iIntValue = intOrNull != null ? intOrNull.intValue() : 47800;
            for (int i = 0; i < 2; i++) {
                datagramSocket.send(new DatagramPacket(bytes, bytes.length, byName, iIntValue));
                byte[] bArr = new byte[256];
                while (true) {
                    try {
                        DatagramPacket datagramPacket = new DatagramPacket(bArr, 256);
                        datagramSocket.receive(datagramPacket);
                        String hostAddress = datagramPacket.getAddress().getHostAddress();
                        if (hostAddress != null) {
                            linkedHashSet.add(hostAddress);
                        }
                    } catch (SocketTimeoutException unused) {
                    }
                }
            }
            multicastLockCreateMulticastLock.release();
            datagramSocket.close();
            return linkedHashSet.isEmpty() ? "未发现发送端（确认同一 WiFi）" : CollectionsKt.joinToString$default(linkedHashSet, "  ", null, null, 0, null, null, 62, null);
        } catch (Throwable unused2) {
            return "扫描失败";
        }
    }

    private static final List<JSONObject> loadPresets(SharedPreferences sharedPreferences) {
        try {
            JSONArray jSONArray = new JSONArray(sharedPreferences.getString("presets", "[]"));
            IntRange intRangeUntil = RangesKt.until(0, jSONArray.length());
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(intRangeUntil, 10));
            Iterator<Integer> it = intRangeUntil.iterator();
            while (it.hasNext()) {
                arrayList.add(jSONArray.getJSONObject(((IntIterator) it).nextInt()));
            }
            return arrayList;
        } catch (Throwable unused) {
            return CollectionsKt.emptyList();
        }
    }

    private static final void savePresets(SharedPreferences sharedPreferences, List<? extends JSONObject> list) {
        JSONArray jSONArray = new JSONArray();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            jSONArray.put((JSONObject) it.next());
        }
        sharedPreferences.edit().putString("presets", jSONArray.toString()).apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean isNewer(String str, String str2) {
        List listSplit$default = StringsKt.split$default((CharSequence) StringsKt.trim((CharSequence) StringsKt.removePrefix(str, (CharSequence) "v")).toString(), new String[]{"."}, false, 0, 6, (Object) null);
        ArrayList arrayList = new ArrayList();
        Iterator it = listSplit$default.iterator();
        while (it.hasNext()) {
            Integer intOrNull = StringsKt.toIntOrNull((String) it.next());
            if (intOrNull != null) {
                arrayList.add(intOrNull);
            }
        }
        ArrayList arrayList2 = arrayList;
        List listSplit$default2 = StringsKt.split$default((CharSequence) StringsKt.trim((CharSequence) str2).toString(), new String[]{"."}, false, 0, 6, (Object) null);
        ArrayList arrayList3 = new ArrayList();
        Iterator it2 = listSplit$default2.iterator();
        while (it2.hasNext()) {
            Integer intOrNull2 = StringsKt.toIntOrNull((String) it2.next());
            if (intOrNull2 != null) {
                arrayList3.add(intOrNull2);
            }
        }
        ArrayList arrayList4 = arrayList3;
        int iMax = Math.max(arrayList2.size(), arrayList4.size());
        int i = 0;
        while (i < iMax) {
            int iIntValue = ((Number) ((i < 0 || i >= arrayList2.size()) ? 0 : arrayList2.get(i))).intValue();
            int iIntValue2 = ((Number) ((i < 0 || i >= arrayList4.size()) ? 0 : arrayList4.get(i))).intValue();
            if (iIntValue != iIntValue2) {
                return iIntValue > iIntValue2;
            }
            i++;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0016  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final Object checkUpdate(Context context, Continuation<? super String> continuation) throws Throwable {
        AnonymousClass1 anonymousClass1;
        String str = "";
        if (continuation instanceof AnonymousClass1) {
            anonymousClass1 = (AnonymousClass1) continuation;
            if ((anonymousClass1.label & Integer.MIN_VALUE) != 0) {
                anonymousClass1.label -= Integer.MIN_VALUE;
            } else {
                anonymousClass1 = new AnonymousClass1(continuation);
            }
        }
        Object objWithContext = anonymousClass1.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = anonymousClass1.label;
        if (i == 0) {
            ResultKt.throwOnFailure(objWithContext);
            try {
                String str2 = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                if (str2 != null) {
                    str = str2;
                }
            } catch (Throwable unused) {
            }
            CoroutineDispatcher io = Dispatchers.getIO();
            AnonymousClass2 anonymousClass2 = new AnonymousClass2(str, null);
            anonymousClass1.L$0 = SpillingKt.nullOutSpilledVariable(context);
            anonymousClass1.L$1 = SpillingKt.nullOutSpilledVariable(str);
            anonymousClass1.label = 1;
            objWithContext = BuildersKt.withContext(io, anonymousClass2, anonymousClass1);
            if (objWithContext == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else {
            if (i != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure(objWithContext);
        }
        Intrinsics.checkNotNullExpressionValue(objWithContext, "withContext(...)");
        return objWithContext;
    }

    /* compiled from: MainActivity.kt */
    @Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\n \u0002*\u0004\u0018\u00010\u00010\u0001*\u00020\u0003H\n"}, d2 = {"<anonymous>", "", "kotlin.jvm.PlatformType", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 3, 0}, xi = 48)
    @DebugMetadata(c = "com.wifiaudio.receiver.MainActivityKt$checkUpdate$2", f = "MainActivity.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, nl = {}, s = {}, v = 2)
    /* renamed from: com.wifiaudio.receiver.MainActivityKt$checkUpdate$2, reason: invalid class name */
    static final class AnonymousClass2 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super String>, Object> {
        final /* synthetic */ String $current;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass2(String str, Continuation<? super AnonymousClass2> continuation) {
            super(2, continuation);
            this.$current = str;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new AnonymousClass2(this.$current, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super String> continuation) {
            return ((AnonymousClass2) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) throws Throwable {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            if (this.label != 0) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure(obj);
            try {
                URLConnection uRLConnectionOpenConnection = new URL("https://api.github.com/repos/SingGoPlay/wifiaudio/releases/latest").openConnection();
                uRLConnectionOpenConnection.setConnectTimeout(ProgressIndicatorKt.CircularAnimationProgressDuration);
                uRLConnectionOpenConnection.setReadTimeout(ProgressIndicatorKt.CircularAnimationProgressDuration);
                InputStream inputStream = uRLConnectionOpenConnection.getInputStream();
                Intrinsics.checkNotNullExpressionValue(inputStream, "getInputStream(...)");
                Reader inputStreamReader = new InputStreamReader(inputStream, Charsets.UTF_8);
                BufferedReader bufferedReader = inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, 8192);
                try {
                    String text = TextStreamsKt.readText(bufferedReader);
                    CloseableKt.closeFinally(bufferedReader, null);
                    String strOptString = new JSONObject(text).optString("tag_name", "");
                    Intrinsics.checkNotNull(strOptString);
                    return (strOptString.length() <= 0 || !MainActivityKt.isNewer(strOptString, this.$current)) ? "" : strOptString;
                } finally {
                }
            } catch (Throwable unused) {
                return "";
            }
        }
    }

    private static final void UpdateBanner(final String str, final Context context, Composer composer, final int i) {
        int i2;
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup;
        Function2<? super Composer, ? super Integer, Unit> function2;
        Composer composerStartRestartGroup = composer.startRestartGroup(-1302367576);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(UpdateBanner)N(tag,ctx)781@32853L40,782@32900L1170,779@32721L1349:MainActivity.kt#kv94a1");
        if ((i & 6) == 0) {
            i2 = (composerStartRestartGroup.changed(str) ? 4 : 2) | i;
        } else {
            i2 = i;
        }
        if ((i & 48) == 0) {
            i2 |= composerStartRestartGroup.changedInstance(context) ? 32 : 16;
        }
        if (!composerStartRestartGroup.shouldExecute((i2 & 19) != 18, i2 & 1)) {
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1302367576, i2, -1, "com.wifiaudio.receiver.UpdateBanner (MainActivity.kt:777)");
            }
            if (str.length() != 0) {
                CardKt.m10636CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(12), 7, null), 0.0f, null, CardDefaults.INSTANCE.m10630defaultColorsdgg9oW8(ColorKt.Color(4293587711L), 0L, composerStartRestartGroup, (CardDefaults.$stable << 6) | 6, 2), ComposableLambdaKt.rememberComposableLambda(161183767, true, new Function3() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda1
                    @Override // kotlin.jvm.functions.Function3
                    public final Object invoke(Object obj, Object obj2, Object obj3) {
                        return MainActivityKt.UpdateBanner$lambda$1(str, context, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                    }
                }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 6);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
            } else {
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
                scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
                if (scopeUpdateScopeEndRestartGroup != null) {
                    function2 = new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda42
                        @Override // kotlin.jvm.functions.Function2
                        public final Object invoke(Object obj, Object obj2) {
                            return MainActivityKt.UpdateBanner$lambda$0(str, context, i, (Composer) obj, ((Integer) obj2).intValue());
                        }
                    };
                    scopeUpdateScopeEndRestartGroup.updateScope(function2);
                }
                return;
            }
        }
        scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            function2 = new Function2() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda2
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.UpdateBanner$lambda$2(str, context, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            };
            scopeUpdateScopeEndRestartGroup.updateScope(function2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit UpdateBanner$lambda$1(String str, final Context context, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C783@32910L1154:MainActivity.kt#kv94a1");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(161183767, i, -1, "com.wifiaudio.receiver.UpdateBanner.<anonymous> (MainActivity.kt:783)");
            }
            Modifier modifierM916paddingVpY3zN4 = PaddingKt.m916paddingVpY3zN4(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(14), Dp.m8245constructorimpl(10));
            Alignment.Vertical centerVertically = Alignment.INSTANCE.getCenterVertically();
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            MeasurePolicy measurePolicyRowMeasurePolicy = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), centerVertically, composer, 48);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composer, modifierM916paddingVpY3zN4);
            Function0<ComposeUiNode> constructor = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyRowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, -989407314, "C787@33092L239,801@33696L40,795@33376L252,794@33344L710:MainActivity.kt#kv94a1");
            TextKt.m11101TextNvy7gAk("📦 发现新版本 v" + str + "，点「去下载」更新", RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null), ColorKt.Color(4281631487L), null, TextUnitKt.getSp(13), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597824, 0, 262056);
            CardColors cardColorsM10630defaultColorsdgg9oW8 = CardDefaults.INSTANCE.m10630defaultColorsdgg9oW8(ColorKt.Color(4281631487L), 0L, composer, (CardDefaults.$stable << 6) | 6, 2);
            ComposerKt.sourceInformationMarkerStart(composer, -2110117969, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composer.changedInstance(context);
            Object objRememberedValue = composer.rememberedValue();
            if (zChangedInstance || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = new Function0() { // from class: com.wifiaudio.receiver.MainActivityKt$$ExternalSyntheticLambda39
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.UpdateBanner$lambda$1$0$0$0(context);
                    }
                };
                composer.updateRememberedValue(objRememberedValue);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            CardKt.m10637CardiWtaglI(null, 0.0f, null, cardColorsM10630defaultColorsdgg9oW8, null, false, (Function0) objRememberedValue, null, ComposableSingletons$MainActivityKt.INSTANCE.m8888getLambda$379477218$receiver_release(), composer, 100663296, 183);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit UpdateBanner$lambda$1$0$0$0(Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/SingGoPlay/wifiaudio/releases/latest")));
        } catch (Throwable unused) {
        }
        return Unit.INSTANCE;
    }
}
