package com.wifiaudio.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.core.AnimationKt;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.DarkThemeKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
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
import androidx.core.app.NotificationCompat;
import androidx.core.view.PointerIconCompat;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.EmptyCoroutineContext;
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
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.scheduling.WorkQueueKt;
import org.json.JSONObject;
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
@Metadata(d1 = {"\u0000N\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0015\u001a\u0015\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007¢\u0006\u0002\u0010\u0004\u001a\u0015\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003¢\u0006\u0002\u0010\u0004\u001a\u001d\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH\u0003¢\u0006\u0002\u0010\n\u001a'\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\b2\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0003¢\u0006\u0002\u0010\u0010\u001a;\u0010\u0011\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\b2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0013H\u0003¢\u0006\u0002\u0010\u0014\u001aI\u0010\u0015\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\b2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\b0\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u00010\u00132\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0003¢\u0006\u0002\u0010\u001b\u001a\b\u0010\u001c\u001a\u00020\u001dH\u0002\u001a\u0018\u0010\u001e\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u001f\u001a\u00020\u001dH\u0002\u001a\u0018\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\b2\u0006\u0010#\u001a\u00020\bH\u0002\u001a\u0016\u0010$\u001a\u00020\b2\u0006\u0010%\u001a\u00020&H\u0082@¢\u0006\u0002\u0010'\u001a\u001d\u0010(\u001a\u00020\u00012\u0006\u0010)\u001a\u00020\b2\u0006\u0010%\u001a\u00020&H\u0003¢\u0006\u0002\u0010*¨\u0006+²\u0006\n\u0010\u001f\u001a\u00020\u001dX\u008a\u008e\u0002²\u0006\n\u0010,\u001a\u00020!X\u008a\u008e\u0002²\u0006\n\u0010-\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u0010.\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u0010/\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u00100\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u00101\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u00102\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u00103\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u00104\u001a\u00020\u0019X\u008a\u008e\u0002²\u0006\n\u00105\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u00106\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u00107\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u00108\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u00109\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u0010:\u001a\u00020\bX\u008a\u008e\u0002²\u0006\n\u0010;\u001a\u00020!X\u008a\u008e\u0002"}, d2 = {"App", "", "activity", "Landroidx/activity/ComponentActivity;", "(Landroidx/activity/ComponentActivity;Landroidx/compose/runtime/Composer;I)V", "MainScreen", "StatusLine", "k", "", "v", "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/runtime/Composer;I)V", "ManagerStat", "label", "value", "modifier", "Landroidx/compose/ui/Modifier;", "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "ConfigField", "onChange", "Lkotlin/Function1;", "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V", "MiuixDropdown", "options", "", "selected", "", "onSelect", "(Ljava/lang/String;Ljava/util/List;ILkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "loadConfig", "Lorg/json/JSONObject;", "saveConfig", "config", "isNewer", "", "latest", "current", "checkUpdate", "ctx", "Landroid/content/Context;", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "UpdateBanner", "tag", "(Ljava/lang/String;Landroid/content/Context;Landroidx/compose/runtime/Composer;I)V", "manager_release", "running", "stIp", "stCodec", "stRate", "stBitrate", "stMode", "stPort", "stHttpUrl", "stClients", "stErr", "stQueueMs", "stDrops", "logText", "toast", "updateTag", "expanded"}, k = 2, mv = {2, 3, 0}, xi = 48)
/* loaded from: classes2.dex */
public final class MainActivityKt {

    /* compiled from: MainActivity.kt */
    @Metadata(k = 3, mv = {2, 3, 0}, xi = 48)
    @DebugMetadata(c = "com.wifiaudio.manager.MainActivityKt", f = "MainActivity.kt", i = {0, 0}, l = {439}, m = "checkUpdate", n = {"ctx", "current"}, nl = {-1}, s = {"L$0", "L$1"}, v = 2)
    /* renamed from: com.wifiaudio.manager.MainActivityKt$checkUpdate$1, reason: invalid class name */
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
    public static final Unit App$lambda$2(ComponentActivity componentActivity, int i, Composer composer, int i2) {
        App(componentActivity, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ConfigField$lambda$1(String str, String str2, Modifier modifier, Function1 function1, int i, int i2, Composer composer, int i3) {
        ConfigField(str, str2, modifier, function1, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1), i2);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$53(ComponentActivity componentActivity, int i, Composer composer, int i2) {
        MainScreen(componentActivity, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ManagerStat$lambda$1(String str, String str2, Modifier modifier, int i, int i2, Composer composer, int i3) {
        ManagerStat(str, str2, modifier, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1), i2);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MiuixDropdown$lambda$4(String str, List list, int i, Function1 function1, Modifier modifier, int i2, int i3, Composer composer, int i4) {
        MiuixDropdown(str, list, i, function1, modifier, composer, RecomposeScopeImplKt.updateChangedFlags(i2 | 1), i3);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit StatusLine$lambda$1(String str, String str2, int i, Composer composer, int i2) {
        StatusLine(str, str2, composer, RecomposeScopeImplKt.updateChangedFlags(i | 1));
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

    public static final void App(final ComponentActivity activity, Composer composer, final int i) {
        int i2;
        Intrinsics.checkNotNullParameter(activity, "activity");
        Composer composerStartRestartGroup = composer.startRestartGroup(-2070377004);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(App)N(activity)81@2899L30,82@2970L36,82@2934L72:MainActivity.kt#3gmcmr");
        if ((i & 6) == 0) {
            i2 = (composerStartRestartGroup.changedInstance(activity) ? 4 : 2) | i;
        } else {
            i2 = i;
        }
        if (!composerStartRestartGroup.shouldExecute((i2 & 3) != 2, i2 & 1)) {
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-2070377004, i2, -1, "com.wifiaudio.manager.App (MainActivity.kt:80)");
            }
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1062440846, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                ThemeController themeController = new ThemeController(null, null, null, null, null, null, null, WorkQueueKt.MASK, null);
                composerStartRestartGroup.updateRememberedValue(themeController);
                objRememberedValue = themeController;
            }
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            MiuixThemeKt.MiuixTheme((ThemeController) objRememberedValue, (TextStyles) null, false, (Function2<? super Composer, ? super Integer, Unit>) ComposableLambdaKt.rememberComposableLambda(743078917, true, new Function2() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.App$lambda$1(activity, (Composer) obj, ((Integer) obj2).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 3078, 6);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda11
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.App$lambda$2(activity, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit App$lambda$1(ComponentActivity componentActivity, Composer composer, int i) {
        ComposerKt.sourceInformation(composer, "C83@2980L20:MainActivity.kt#3gmcmr");
        if (!composer.shouldExecute((i & 3) != 2, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(743078917, i, -1, "com.wifiaudio.manager.App.<anonymous> (MainActivity.kt:83)");
            }
            MainScreen(componentActivity, composer, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private static final void MainScreen(final ComponentActivity componentActivity, Composer composer, final int i) {
        int i2;
        final ComponentActivity componentActivity2;
        MutableState mutableState;
        MutableState mutableState2;
        MutableState mutableState3;
        MutableState mutableState4;
        MutableState mutableState5;
        MutableState mutableState6;
        MutableState mutableState7;
        MutableState mutableState8;
        MutableState mutableState9;
        MutableState mutableState10;
        MutableState mutableState11;
        MutableState mutableState12;
        Composer composerStartRestartGroup = composer.startRestartGroup(-986265132);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(MainScreen)N(activity)89@3092L24,91@3145L41,93@3216L34,94@3267L32,95@3319L32,96@3370L32,97@3424L32,98@3475L32,99@3526L32,100@3580L32,101@3634L30,102@3682L31,103@3735L32,104@3787L32,105@3839L31,106@3888L31,108@3962L31,109@4019L49,109@3998L70,145@5671L265,145@5650L286,157@6033L21,158@6126L21,154@5942L8491:MainActivity.kt#3gmcmr");
        if ((i & 6) == 0) {
            i2 = i | (composerStartRestartGroup.changedInstance(componentActivity) ? 4 : 2);
        } else {
            i2 = i;
        }
        if (!composerStartRestartGroup.shouldExecute((i2 & 3) != 2, i2 & 1)) {
            componentActivity2 = componentActivity;
            composerStartRestartGroup.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-986265132, i2, -1, "com.wifiaudio.manager.MainScreen (MainActivity.kt:88)");
            }
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 773894976, "CC(rememberCoroutineScope)N(getContext)600@27430L68:Effects.kt#9igjgp");
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 683736516, "CC(remember):Effects.kt#9igjgp");
            Object objRememberedValue = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, composerStartRestartGroup);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue);
            }
            final CoroutineScope coroutineScope = (CoroutineScope) objRememberedValue;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386984285, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue2 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue2 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(loadConfig(), null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue2);
            }
            final MutableState mutableState13 = (MutableState) objRememberedValue2;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386986550, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue3 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue3 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue3);
            }
            MutableState mutableState14 = (MutableState) objRememberedValue3;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386988180, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue4 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue4 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue4 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("—", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue4);
            }
            MutableState mutableState15 = (MutableState) objRememberedValue4;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386989844, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue5 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue5 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue5 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("—", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue5);
            }
            MutableState mutableState16 = (MutableState) objRememberedValue5;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386991476, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue6 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue6 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue6 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("—", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue6);
            }
            MutableState mutableState17 = (MutableState) objRememberedValue6;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386993204, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue7 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue7 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue7 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("—", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue7);
            }
            MutableState mutableState18 = (MutableState) objRememberedValue7;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386994836, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue8 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue8 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue8 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("—", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue8);
            }
            MutableState mutableState19 = (MutableState) objRememberedValue8;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386996468, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue9 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue9 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue9 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("—", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue9);
            }
            MutableState mutableState20 = (MutableState) objRememberedValue9;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386998196, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue10 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue10 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue10 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("—", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue10);
            }
            MutableState mutableState21 = (MutableState) objRememberedValue10;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1386999922, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue11 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue11 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue11 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default(0, null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue11);
            }
            MutableState mutableState22 = (MutableState) objRememberedValue11;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1387001459, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue12 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue12 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue12 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue12);
            }
            MutableState mutableState23 = (MutableState) objRememberedValue12;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1387003156, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue13 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue13 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue13 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("—", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue13);
            }
            MutableState mutableState24 = (MutableState) objRememberedValue13;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1387004820, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue14 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue14 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue14 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("0", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue14);
            }
            MutableState mutableState25 = (MutableState) objRememberedValue14;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1387006483, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue15 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue15 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue15 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue15);
            }
            final MutableState mutableState26 = (MutableState) objRememberedValue15;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1387008051, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue16 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue16 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue16 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue16);
            }
            final MutableState mutableState27 = (MutableState) objRememberedValue16;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1387010419, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue17 = composerStartRestartGroup.rememberedValue();
            if (objRememberedValue17 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue17 = SnapshotStateKt__SnapshotStateKt.mutableStateOf$default("", null, 2, null);
                composerStartRestartGroup.updateRememberedValue(objRememberedValue17);
            }
            MutableState mutableState28 = (MutableState) objRememberedValue17;
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            Unit unit = Unit.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1387012261, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composerStartRestartGroup.changedInstance(componentActivity);
            MainActivityKt$MainScreen$1$1 mainActivityKt$MainScreen$1$1RememberedValue = composerStartRestartGroup.rememberedValue();
            if (zChangedInstance || mainActivityKt$MainScreen$1$1RememberedValue == Composer.INSTANCE.getEmpty()) {
                mainActivityKt$MainScreen$1$1RememberedValue = new MainActivityKt$MainScreen$1$1(componentActivity, mutableState28, null);
                composerStartRestartGroup.updateRememberedValue(mainActivityKt$MainScreen$1$1RememberedValue);
            }
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            EffectsKt.LaunchedEffect(unit, (Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object>) mainActivityKt$MainScreen$1$1RememberedValue, composerStartRestartGroup, 6);
            Unit unit2 = Unit.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1387065341, "CC(remember):MainActivity.kt#9igjgp");
            MainActivityKt$MainScreen$2$1 mainActivityKt$MainScreen$2$1RememberedValue = composerStartRestartGroup.rememberedValue();
            if (mainActivityKt$MainScreen$2$1RememberedValue == Composer.INSTANCE.getEmpty()) {
                mutableState2 = mutableState14;
                mutableState3 = mutableState15;
                mutableState4 = mutableState16;
                mutableState5 = mutableState17;
                mutableState6 = mutableState18;
                mutableState7 = mutableState19;
                mutableState8 = mutableState20;
                mutableState9 = mutableState22;
                mutableState10 = mutableState23;
                mutableState = mutableState24;
                mutableState12 = mutableState25;
                mutableState11 = mutableState21;
                mainActivityKt$MainScreen$2$1RememberedValue = new MainActivityKt$MainScreen$2$1(mutableState14, mutableState15, mutableState16, mutableState17, mutableState18, mutableState19, mutableState20, mutableState22, mutableState23, mutableState24, mutableState25, mutableState21, mutableState26, null);
                composerStartRestartGroup.updateRememberedValue(mainActivityKt$MainScreen$2$1RememberedValue);
            } else {
                mutableState = mutableState24;
                mutableState2 = mutableState14;
                mutableState3 = mutableState15;
                mutableState4 = mutableState16;
                mutableState5 = mutableState17;
                mutableState6 = mutableState18;
                mutableState7 = mutableState19;
                mutableState8 = mutableState20;
                mutableState9 = mutableState22;
                mutableState10 = mutableState23;
                mutableState11 = mutableState21;
                mutableState12 = mutableState25;
            }
            ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
            EffectsKt.LaunchedEffect(unit2, (Function2<? super CoroutineScope, ? super Continuation<? super Unit>, ? extends Object>) mainActivityKt$MainScreen$2$1RememberedValue, composerStartRestartGroup, 6);
            Modifier modifierM915padding3ABfNKs = PaddingKt.m915padding3ABfNKs(ScrollKt.verticalScroll$default(BackgroundKt.m286backgroundbw27NRU$default(SizeKt.fillMaxSize$default(Modifier.INSTANCE, 0.0f, 1, null), ColorKt.Color(DarkThemeKt.isSystemInDarkTheme(composerStartRestartGroup, 0) ? 4280558628L : 4294440951L), null, 2, null), ScrollKt.rememberScrollState(0, composerStartRestartGroup, 0, 1), false, null, false, 14, null), Dp.m8245constructorimpl(16));
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
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 928709472, "C162@6230L33,164@6273L80,165@6362L130,168@6594L3111,168@6529L3176,233@9807L968,233@9742L1033,257@10878L1401,257@10813L1466,287@12383L1471,287@12318L1536,319@13955L307,319@13890L372:MainActivity.kt#3gmcmr");
            UpdateBanner(MainScreen$lambda$46(mutableState28), componentActivity, composerStartRestartGroup, (i2 << 3) & 112);
            TextKt.m11051TextNvy7gAk("🎛️ WiFiAudio 管理器", null, 0L, null, TextUnitKt.getSp(22), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composerStartRestartGroup, 1597446, 0, 262062);
            float f = 12;
            TextKt.m11051TextNvy7gAk("root 直连配置（Kotlin + miuix）", PaddingKt.m919paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(f), 7, null), ColorKt.Color(4287402928L), null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composerStartRestartGroup, 25014, 0, 262120);
            final MutableState mutableState29 = mutableState2;
            final MutableState mutableState30 = mutableState3;
            final MutableState mutableState31 = mutableState4;
            final MutableState mutableState32 = mutableState5;
            final MutableState mutableState33 = mutableState6;
            final MutableState mutableState34 = mutableState7;
            final MutableState mutableState35 = mutableState8;
            final MutableState mutableState36 = mutableState11;
            final MutableState mutableState37 = mutableState9;
            final MutableState mutableState38 = mutableState10;
            final MutableState mutableState39 = mutableState;
            final MutableState mutableState40 = mutableState12;
            CardKt.m10586CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(f), 7, null), 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(-554620275, true, new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda12
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.MainScreen$lambda$52$0(coroutineScope, mutableState29, mutableState27, mutableState30, mutableState31, mutableState32, mutableState33, mutableState34, mutableState35, mutableState37, mutableState39, mutableState40, mutableState36, mutableState38, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
            Modifier modifierM919paddingqDBjuR0$default = PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(f), 7, null);
            final MutableState mutableState41 = mutableState11;
            Function3 function3 = new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda13
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.MainScreen$lambda$52$1(componentActivity, coroutineScope, mutableState13, mutableState27, mutableState29, mutableState30, mutableState31, mutableState32, mutableState33, mutableState34, mutableState35, mutableState37, mutableState38, mutableState39, mutableState40, mutableState41, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            };
            componentActivity2 = componentActivity;
            composerStartRestartGroup = composerStartRestartGroup;
            CardKt.m10586CardEUb7tLY(modifierM919paddingqDBjuR0$default, 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(396693814, true, function3, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
            CardKt.m10586CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(f), 7, null), 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(1476235861, true, new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda14
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.MainScreen$lambda$52$2(mutableState13, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
            CardKt.m10586CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(f), 7, null), 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(-1739189388, true, new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda15
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.MainScreen$lambda$52$3(componentActivity2, mutableState13, mutableState27, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
            CardKt.m10586CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(f), 7, null), 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(-659647341, true, new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda16
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.MainScreen$lambda$52$4(mutableState26, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
            if (MainScreen$lambda$43(mutableState27).length() > 0) {
                composerStartRestartGroup.startReplaceGroup(936474041);
                ComposerKt.sourceInformation(composerStartRestartGroup, "327@14310L107");
                TextKt.m11051TextNvy7gAk(MainScreen$lambda$43(mutableState27), PaddingKt.m919paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(8), 7, null), ColorKt.Color(4281631487L), null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composerStartRestartGroup, 25008, 0, 262120);
                composerStartRestartGroup = composerStartRestartGroup;
                composerStartRestartGroup.endReplaceGroup();
            } else {
                composerStartRestartGroup.startReplaceGroup(936586788);
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
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda17
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.MainScreen$lambda$53(componentActivity2, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    private static final JSONObject MainScreen$lambda$1(MutableState<JSONObject> mutableState) {
        return mutableState.getValue();
    }

    private static final boolean MainScreen$lambda$4(MutableState<Boolean> mutableState) {
        return mutableState.getValue().booleanValue();
    }

    private static final void MainScreen$lambda$5(MutableState<Boolean> mutableState, boolean z) {
        mutableState.setValue(Boolean.valueOf(z));
    }

    private static final String MainScreen$lambda$7(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$10(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$13(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$16(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$19(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$22(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$25(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final int MainScreen$lambda$28(MutableState<Integer> mutableState) {
        return mutableState.getValue().intValue();
    }

    private static final void MainScreen$lambda$29(MutableState<Integer> mutableState, int i) {
        mutableState.setValue(Integer.valueOf(i));
    }

    private static final String MainScreen$lambda$31(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$34(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$37(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$40(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$43(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final String MainScreen$lambda$46(MutableState<String> mutableState) {
        return mutableState.getValue();
    }

    private static final void MainScreen$setCfg(MutableState<JSONObject> mutableState, String str, Object obj) {
        try {
            JSONObject jSONObject = new JSONObject(MainScreen$lambda$1(mutableState).toString());
            jSONObject.put(str, obj);
            mutableState.setValue(jSONObject);
        } catch (Throwable unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:26:0x017b  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0197  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void MainScreen$refreshStatus(MutableState<Boolean> mutableState, MutableState<String> mutableState2, MutableState<String> mutableState3, MutableState<String> mutableState4, MutableState<String> mutableState5, MutableState<String> mutableState6, MutableState<String> mutableState7, MutableState<Integer> mutableState8, MutableState<String> mutableState9, MutableState<String> mutableState10, MutableState<String> mutableState11, MutableState<String> mutableState12) {
        Object objM8901constructorimpl;
        String str;
        MutableState<String> mutableState13;
        String str2;
        int iOptInt;
        String str3;
        try {
            Result.Companion companion = Result.INSTANCE;
            objM8901constructorimpl = Result.m8901constructorimpl(new JSONObject(RootShell.readStatus()));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM8901constructorimpl = Result.m8901constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m8907isFailureimpl(objM8901constructorimpl)) {
            objM8901constructorimpl = null;
        }
        JSONObject jSONObject = (JSONObject) objM8901constructorimpl;
        if (jSONObject == null) {
            return;
        }
        MainScreen$lambda$5(mutableState, jSONObject.optBoolean("running"));
        String strOptString = jSONObject.optString("ip", "—");
        Intrinsics.checkNotNullExpressionValue(strOptString, "optString(...)");
        mutableState2.setValue(strOptString);
        String strOptString2 = jSONObject.optString("codec", "—");
        Intrinsics.checkNotNullExpressionValue(strOptString2, "optString(...)");
        String upperCase = strOptString2.toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(upperCase, "toUpperCase(...)");
        mutableState3.setValue(upperCase);
        int iOptInt2 = jSONObject.optInt("sampleRate", 0);
        if (iOptInt2 > 0) {
            str = (iOptInt2 / 1000) + " kHz";
        } else {
            str = "—";
        }
        mutableState4.setValue(str);
        long jOptLong = jSONObject.optLong("bitrate", 0L);
        if (jOptLong >= AnimationKt.MillisToNanos) {
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            str2 = String.format("%.1f Mbps", Arrays.copyOf(new Object[]{Double.valueOf(jOptLong / 1000000.0d)}, 1));
            Intrinsics.checkNotNullExpressionValue(str2, "format(...)");
        } else {
            if (jOptLong <= 0) {
                mutableState13 = mutableState5;
                str2 = "—";
                mutableState13.setValue(str2);
                String strOptString3 = jSONObject.optString("mode", "—");
                String strOptString4 = jSONObject.optString(NotificationCompat.CATEGORY_TRANSPORT, "—");
                Intrinsics.checkNotNullExpressionValue(strOptString4, "optString(...)");
                String upperCase2 = strOptString4.toUpperCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(upperCase2, "toUpperCase(...)");
                mutableState6.setValue(strOptString3 + " / " + upperCase2);
                mutableState7.setValue("T" + jSONObject.optInt("tcpPort", 47800) + " U" + jSONObject.optInt("udpPort", 48800) + " H" + jSONObject.optInt("httpPort", 47801));
                MainScreen$lambda$29(mutableState8, jSONObject.optInt("clients", 0));
                String strOptString5 = jSONObject.optString("lastError", "");
                Intrinsics.checkNotNullExpressionValue(strOptString5, "optString(...)");
                mutableState9.setValue(strOptString5);
                iOptInt = jSONObject.optInt("tcpQueueFrames", 0);
                int iCoerceAtLeast = RangesKt.coerceAtLeast(jSONObject.optInt("sampleRate", 48000), 1);
                if (iOptInt <= 0) {
                    str3 = (((iOptInt * 512) * 1000) / iCoerceAtLeast) + "ms";
                } else {
                    str3 = "0ms";
                }
                mutableState10.setValue(str3);
                mutableState11.setValue(String.valueOf(jSONObject.optLong("mixerDrops", 0L)));
                mutableState12.setValue("http://" + jSONObject.optString("ip", "—") + ":" + jSONObject.optInt("httpPort", 47801) + "/stream");
            }
            str2 = (jOptLong / 1000) + " kbps";
        }
        mutableState13 = mutableState5;
        mutableState13.setValue(str2);
        String strOptString32 = jSONObject.optString("mode", "—");
        String strOptString42 = jSONObject.optString(NotificationCompat.CATEGORY_TRANSPORT, "—");
        Intrinsics.checkNotNullExpressionValue(strOptString42, "optString(...)");
        String upperCase22 = strOptString42.toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(upperCase22, "toUpperCase(...)");
        mutableState6.setValue(strOptString32 + " / " + upperCase22);
        mutableState7.setValue("T" + jSONObject.optInt("tcpPort", 47800) + " U" + jSONObject.optInt("udpPort", 48800) + " H" + jSONObject.optInt("httpPort", 47801));
        MainScreen$lambda$29(mutableState8, jSONObject.optInt("clients", 0));
        String strOptString52 = jSONObject.optString("lastError", "");
        Intrinsics.checkNotNullExpressionValue(strOptString52, "optString(...)");
        mutableState9.setValue(strOptString52);
        iOptInt = jSONObject.optInt("tcpQueueFrames", 0);
        int iCoerceAtLeast2 = RangesKt.coerceAtLeast(jSONObject.optInt("sampleRate", 48000), 1);
        if (iOptInt <= 0) {
        }
        mutableState10.setValue(str3);
        mutableState11.setValue(String.valueOf(jSONObject.optLong("mixerDrops", 0L)));
        mutableState12.setValue("http://" + jSONObject.optString("ip", "—") + ":" + jSONObject.optInt("httpPort", 47801) + "/stream");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$0(final CoroutineScope coroutineScope, MutableState mutableState, final MutableState mutableState2, MutableState mutableState3, MutableState mutableState4, MutableState mutableState5, MutableState mutableState6, MutableState mutableState7, MutableState mutableState8, MutableState mutableState9, MutableState mutableState10, MutableState mutableState11, MutableState mutableState12, MutableState mutableState13, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C169@6608L3087:MainActivity.kt#3gmcmr");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-554620275, i, -1, "com.wifiaudio.manager.MainScreen.<anonymous>.<anonymous> (MainActivity.kt:169)");
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
            ComposerKt.sourceInformationMarkerStart(composer, 1171404151, "C170@6684L1134,193@7835L30,195@7911L198,199@8126L29,200@8172L204,204@8393L29,205@8439L202,209@8658L29,210@8704L220,214@8941L207:MainActivity.kt#3gmcmr");
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
            ComposerKt.sourceInformationMarkerStart(composer, -716753762, "C171@6758L275,177@7054L27,181@7321L40,180@7207L66,179@7167L633:MainActivity.kt#3gmcmr");
            TextKt.m11051TextNvy7gAk(MainScreen$lambda$4(mutableState) ? "● 运行中" : "○ 未运行", null, ColorKt.Color(MainScreen$lambda$4(mutableState) ? 4281313372L : 4287402928L), null, TextUnitKt.getSp(18), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597440, 0, 262058);
            SpacerKt.Spacer(RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null), composer, 0);
            CardColors cardColorsM10580defaultColorsdgg9oW8 = CardDefaults.INSTANCE.m10580defaultColorsdgg9oW8(ColorKt.Color(4281631487L), 0L, composer, (CardDefaults.$stable << 6) | 6, 2);
            float fM8245constructorimpl = Dp.m8245constructorimpl(14);
            ComposerKt.sourceInformationMarkerStart(composer, -1962770347, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composer.changedInstance(coroutineScope);
            Object objRememberedValue = composer.rememberedValue();
            if (zChangedInstance || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = new Function0() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda9
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.MainScreen$lambda$52$0$0$0$0$0(coroutineScope, mutableState2);
                    }
                };
                composer.updateRememberedValue(objRememberedValue);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            CardKt.m10587CardiWtaglI(null, fM8245constructorimpl, null, cardColorsM10580defaultColorsdgg9oW8, null, false, (Function0) objRememberedValue, null, ComposableSingletons$MainActivityKt.INSTANCE.m8887getLambda$1005946914$manager_release(), composer, 100663344, 181);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(12)), composer, 6);
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
            ComposerKt.sourceInformationMarkerStart(composer, -1880462387, "C196@7937L64,197@8022L69:MainActivity.kt#3gmcmr");
            float f = 4;
            ManagerStat("IP", MainScreen$lambda$7(mutableState3), PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance2, Modifier.INSTANCE, 1.0f, false, 2, null), 0.0f, 0.0f, Dp.m8245constructorimpl(f), 0.0f, 11, null), composer, 6, 0);
            ManagerStat("编码", MainScreen$lambda$10(mutableState4), PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance2, Modifier.INSTANCE, 1.0f, false, 2, null), Dp.m8245constructorimpl(f), 0.0f, 0.0f, 0.0f, 14, null), composer, 6, 0);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            float f2 = 8;
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f2)), composer, 6);
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Modifier.Companion companion3 = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy3 = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), Alignment.INSTANCE.getTop(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode4 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap4 = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier4 = ComposedModifierKt.materializeModifier(composer, companion3);
            Function0<ComposeUiNode> constructor4 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor4);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl4 = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl4, measurePolicyRowMeasurePolicy3, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl4, currentCompositionLocalMap4, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl4, Integer.valueOf(iHashCode4), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl4, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl4, modifierMaterializeModifier4, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance3 = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, 1577710152, "C201@8198L67,202@8286L72:MainActivity.kt#3gmcmr");
            ManagerStat("采样率", MainScreen$lambda$13(mutableState5), PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance3, Modifier.INSTANCE, 1.0f, false, 2, null), 0.0f, 0.0f, Dp.m8245constructorimpl(f), 0.0f, 11, null), composer, 6, 0);
            ManagerStat("比特率", MainScreen$lambda$16(mutableState6), PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance3, Modifier.INSTANCE, 1.0f, false, 2, null), Dp.m8245constructorimpl(f), 0.0f, 0.0f, 0.0f, 14, null), composer, 6, 0);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f2)), composer, 6);
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Modifier.Companion companion4 = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy4 = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), Alignment.INSTANCE.getTop(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode5 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap5 = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier5 = ComposedModifierKt.materializeModifier(composer, companion4);
            Function0<ComposeUiNode> constructor5 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor5);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl5 = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl5, measurePolicyRowMeasurePolicy4, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl5, currentCompositionLocalMap5, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl5, Integer.valueOf(iHashCode5), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl5, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl5, modifierMaterializeModifier5, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance4 = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, 740915147, "C206@8465L69,207@8555L68:MainActivity.kt#3gmcmr");
            ManagerStat("模式/传输", MainScreen$lambda$19(mutableState7), PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance4, Modifier.INSTANCE, 1.0f, false, 2, null), 0.0f, 0.0f, Dp.m8245constructorimpl(f), 0.0f, 11, null), composer, 6, 0);
            ManagerStat("端口", MainScreen$lambda$22(mutableState8), PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance4, Modifier.INSTANCE, 1.0f, false, 2, null), Dp.m8245constructorimpl(f), 0.0f, 0.0f, 0.0f, 14, null), composer, 6, 0);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f2)), composer, 6);
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Modifier.Companion companion5 = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy5 = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), Alignment.INSTANCE.getTop(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode6 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap6 = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier6 = ComposedModifierKt.materializeModifier(composer, companion5);
            Function0<ComposeUiNode> constructor6 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor6);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl6 = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl6, measurePolicyRowMeasurePolicy5, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl6, currentCompositionLocalMap6, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl6, Integer.valueOf(iHashCode6), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl6, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl6, modifierMaterializeModifier6, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance5 = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, -95879238, "C211@8730L81,212@8832L74:MainActivity.kt#3gmcmr");
            ManagerStat("连接数", String.valueOf(MainScreen$lambda$28(mutableState9)), PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance5, Modifier.INSTANCE, 1.0f, false, 2, null), 0.0f, 0.0f, Dp.m8245constructorimpl(f), 0.0f, 11, null), composer, 6, 0);
            ManagerStat("音频格式", "PCM16 无损", PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance5, Modifier.INSTANCE, 1.0f, false, 2, null), Dp.m8245constructorimpl(f), 0.0f, 0.0f, 0.0f, 14, null), composer, 54, 0);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            Modifier.Companion companion6 = Modifier.INSTANCE;
            MeasurePolicy measurePolicyRowMeasurePolicy6 = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), Alignment.INSTANCE.getTop(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode7 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap7 = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier7 = ComposedModifierKt.materializeModifier(composer, companion6);
            Function0<ComposeUiNode> constructor7 = ComposeUiNode.INSTANCE.getConstructor();
            ComposerKt.sourceInformationMarkerStart(composer, -553112988, "CC(ReusableComposeNode)N(factory,update,content)399@15590L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(constructor7);
            } else {
                composer.useNode();
            }
            Composer composerM4661constructorimpl7 = Updater.m4661constructorimpl(composer);
            Updater.m4669setimpl(composerM4661constructorimpl7, measurePolicyRowMeasurePolicy6, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl7, currentCompositionLocalMap7, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl7, Integer.valueOf(iHashCode7), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl7, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl7, modifierMaterializeModifier7, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance6 = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, -932674584, "C215@8967L71,216@9059L71:MainActivity.kt#3gmcmr");
            ManagerStat("发送队列", MainScreen$lambda$34(mutableState10), PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance6, Modifier.INSTANCE, 1.0f, false, 2, null), 0.0f, 0.0f, Dp.m8245constructorimpl(f), 0.0f, 11, null), composer, 6, 0);
            ManagerStat("分发丢帧", MainScreen$lambda$37(mutableState11), PaddingKt.m919paddingqDBjuR0$default(RowScope.weight$default(rowScopeInstance6, Modifier.INSTANCE, 1.0f, false, 2, null), Dp.m8245constructorimpl(f), 0.0f, 0.0f, 0.0f, 14, null), composer, 6, 0);
            Composer composer2 = composer;
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            if (!Intrinsics.areEqual(MainScreen$lambda$25(mutableState12), "—") && !StringsKt.isBlank(MainScreen$lambda$25(mutableState12))) {
                composer2.startReplaceGroup(1173849213);
                ComposerKt.sourceInformation(composer2, "219@9235L238");
                TextKt.m11051TextNvy7gAk("HTTP 串流: " + MainScreen$lambda$25(mutableState12), PaddingKt.m919paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, Dp.m8245constructorimpl(f2), 0.0f, 0.0f, 13, null), ColorKt.Color(4281631487L), null, TextUnitKt.getSp(11), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 25008, 0, 262120);
                composer2 = composer;
                composer2.endReplaceGroup();
            } else {
                composer2.startReplaceGroup(1174095787);
                composer2.endReplaceGroup();
            }
            if (MainScreen$lambda$31(mutableState13).length() > 0) {
                composer2.startReplaceGroup(1174161662);
                ComposerKt.sourceInformation(composer2, "227@9554L109");
                TextKt.m11051TextNvy7gAk("⚠ " + MainScreen$lambda$31(mutableState13), PaddingKt.m919paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, Dp.m8245constructorimpl(f), 0.0f, 0.0f, 13, null), ColorKt.Color(4293993762L), null, TextUnitKt.getSp(11), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 25008, 0, 262120);
                composer2 = composer;
                composer2.endReplaceGroup();
            } else {
                composer2.startReplaceGroup(1174284267);
                composer2.endReplaceGroup();
            }
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$0$0$0$0$0(CoroutineScope coroutineScope, MutableState mutableState) {
        BuildersKt__Builders_commonKt.launch$default(coroutineScope, null, null, new MainActivityKt$MainScreen$3$1$1$1$1$1$1(mutableState, null), 3, null);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$1(final ComponentActivity componentActivity, final CoroutineScope coroutineScope, final MutableState mutableState, final MutableState mutableState2, final MutableState mutableState3, final MutableState mutableState4, final MutableState mutableState5, final MutableState mutableState6, final MutableState mutableState7, final MutableState mutableState8, final MutableState mutableState9, final MutableState mutableState10, final MutableState mutableState11, final MutableState mutableState12, final MutableState mutableState13, final MutableState mutableState14, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C234@9821L944:MainActivity.kt#3gmcmr");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(396693814, i, -1, "com.wifiaudio.manager.MainScreen.<anonymous>.<anonymous> (MainActivity.kt:234)");
            }
            Modifier modifierM915padding3ABfNKs = PaddingKt.m915padding3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(16));
            Alignment.Vertical centerVertically = Alignment.INSTANCE.getCenterVertically();
            ComposerKt.sourceInformationMarkerStart(composer, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            MeasurePolicy measurePolicyRowMeasurePolicy = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), centerVertically, composer, 48);
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
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyRowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, 1969617558, "C238@9988L227,244@10344L389,242@10232L519:MainActivity.kt#3gmcmr");
            Modifier modifierWeight$default = RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null);
            ComposerKt.sourceInformationMarkerStart(composer, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
            MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composer, 0);
            ComposerKt.sourceInformationMarkerStart(composer, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
            int iHashCode2 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composer, 0));
            CompositionLocalMap currentCompositionLocalMap2 = composer.getCurrentCompositionLocalMap();
            Modifier modifierMaterializeModifier2 = ComposedModifierKt.materializeModifier(composer, modifierWeight$default);
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
            Updater.m4669setimpl(composerM4661constructorimpl2, measurePolicyColumnMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl2, currentCompositionLocalMap2, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl2, Integer.valueOf(iHashCode2), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl2, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl2, modifierMaterializeModifier2, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composer, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            ColumnScopeInstance columnScopeInstance = ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composer, -224881816, "C239@10038L69,240@10128L69:MainActivity.kt#3gmcmr");
            TextKt.m11051TextNvy7gAk("⚡ 启用广播", null, 0L, null, TextUnitKt.getSp(14), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597446, 0, 262062);
            TextKt.m11051TextNvy7gAk("默认关闭（最省电）", null, ColorKt.Color(4287402928L), null, TextUnitKt.getSp(11), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24966, 0, 262122);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            boolean zAreEqual = Intrinsics.areEqual(MainScreen$lambda$1(mutableState).optString("enabled"), "1");
            ComposerKt.sourceInformationMarkerStart(composer, -1183378921, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composer.changedInstance(componentActivity) | composer.changedInstance(coroutineScope);
            Object objRememberedValue = composer.rememberedValue();
            if (zChangedInstance || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                Function1 function1 = new Function1() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda25
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.MainScreen$lambda$52$1$0$1$0(componentActivity, coroutineScope, mutableState, mutableState2, mutableState3, mutableState4, mutableState5, mutableState6, mutableState7, mutableState8, mutableState9, mutableState10, mutableState11, mutableState12, mutableState13, mutableState14, ((Boolean) obj).booleanValue());
                    }
                };
                composer.updateRememberedValue(function1);
                objRememberedValue = function1;
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            SwitchKt.Switch(zAreEqual, (Function1) objRememberedValue, null, null, false, composer, 0, 28);
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
    public static final Unit MainScreen$lambda$52$1$0$1$0(ComponentActivity componentActivity, CoroutineScope coroutineScope, MutableState mutableState, MutableState mutableState2, MutableState mutableState3, MutableState mutableState4, MutableState mutableState5, MutableState mutableState6, MutableState mutableState7, MutableState mutableState8, MutableState mutableState9, MutableState mutableState10, MutableState mutableState11, MutableState mutableState12, MutableState mutableState13, MutableState mutableState14, boolean z) {
        MainScreen$setCfg(mutableState, "enabled", Integer.valueOf(z ? 1 : 0));
        saveConfig(componentActivity, MainScreen$lambda$1(mutableState));
        BuildersKt__Builders_commonKt.launch$default(coroutineScope, null, null, new MainActivityKt$MainScreen$3$2$1$2$1$1(z, null), 3, null);
        mutableState2.setValue(z ? "已开启（服务启动中…）" : "已关闭（服务已停止）");
        MainScreen$refreshStatus(mutableState3, mutableState4, mutableState5, mutableState6, mutableState7, mutableState8, mutableState9, mutableState10, mutableState11, mutableState12, mutableState13, mutableState14);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:38:0x01ad  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x01c3  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x01ce  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0218  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0264  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x029b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final Unit MainScreen$lambda$52$2(MutableState mutableState, ColumnScope Card, Composer composer, int i) {
        Object objRememberedValue;
        final MutableState mutableState2;
        Object objRememberedValue2;
        Object objRememberedValue3;
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C258@10892L1377:MainActivity.kt#3gmcmr");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1476235861, i, -1, "com.wifiaudio.manager.MainScreen.<anonymous>.<anonymous> (MainActivity.kt:258)");
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
            ComposerKt.sourceInformationMarkerStart(composer, 894878749, "C259@10968L70,260@11055L29,266@11407L71,262@11102L450,273@11804L30,269@11569L339,280@12151L30,276@11925L330:MainActivity.kt#3gmcmr");
            TextKt.m11051TextNvy7gAk("🔊 音频设置", null, 0L, null, TextUnitKt.getSp(14), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597446, 0, 262062);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(8)), composer, 6);
            int i2 = 3;
            List listListOf = CollectionsKt.listOf((Object[]) new String[]{"自动（跟随设备）", "48 kHz", "96 kHz", "192 kHz"});
            String strOptString = MainScreen$lambda$1(mutableState).optString("pcm_rate", DebugKt.DEBUG_PROPERTY_VALUE_AUTO);
            if (strOptString == null) {
                i2 = 0;
                ComposerKt.sourceInformationMarkerStart(composer, 1829995206, "CC(remember):MainActivity.kt#9igjgp");
                objRememberedValue = composer.rememberedValue();
                if (objRememberedValue != Composer.INSTANCE.getEmpty()) {
                    mutableState2 = mutableState;
                    objRememberedValue = new Function1() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda6
                        @Override // kotlin.jvm.functions.Function1
                        public final Object invoke(Object obj) {
                            return MainActivityKt.MainScreen$lambda$52$2$0$0$0(mutableState2, ((Integer) obj).intValue());
                        }
                    };
                    composer.updateRememberedValue(objRememberedValue);
                } else {
                    mutableState2 = mutableState;
                }
                ComposerKt.sourceInformationMarkerEnd(composer);
                MiuixDropdown("PCM 采样率（无损直通）", listListOf, i2, (Function1) objRememberedValue, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                List listListOf2 = CollectionsKt.listOf((Object[]) new String[]{"本地静音（声音只走 WiFi）", "本地照常出声"});
                boolean zAreEqual = Intrinsics.areEqual(MainScreen$lambda$1(mutableState2).optString("local_render"), "1");
                ComposerKt.sourceInformationMarkerStart(composer, 1830007869, "CC(remember):MainActivity.kt#9igjgp");
                objRememberedValue2 = composer.rememberedValue();
                if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                    objRememberedValue2 = new Function1() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda7
                        @Override // kotlin.jvm.functions.Function1
                        public final Object invoke(Object obj) {
                            return MainActivityKt.MainScreen$lambda$52$2$0$1$0(mutableState2, ((Integer) obj).intValue());
                        }
                    };
                    composer.updateRememberedValue(objRememberedValue2);
                }
                ComposerKt.sourceInformationMarkerEnd(composer);
                MiuixDropdown("本地声音", listListOf2, zAreEqual ? 1 : 0, (Function1) objRememberedValue2, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                List listListOf3 = CollectionsKt.listOf((Object[]) new String[]{"关闭", "开启（尽力而为）"});
                boolean zAreEqual2 = Intrinsics.areEqual(MainScreen$lambda$1(mutableState2).optString("capture_call"), "1");
                ComposerKt.sourceInformationMarkerStart(composer, 1830018973, "CC(remember):MainActivity.kt#9igjgp");
                objRememberedValue3 = composer.rememberedValue();
                if (objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                    objRememberedValue3 = new Function1() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda8
                        @Override // kotlin.jvm.functions.Function1
                        public final Object invoke(Object obj) {
                            return MainActivityKt.MainScreen$lambda$52$2$0$2$0(mutableState2, ((Integer) obj).intValue());
                        }
                    };
                    composer.updateRememberedValue(objRememberedValue3);
                }
                ComposerKt.sourceInformationMarkerEnd(composer);
                MiuixDropdown("蜂窝通话捕获", listListOf3, zAreEqual2 ? 1 : 0, (Function1) objRememberedValue3, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
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
                int iHashCode2 = strOptString.hashCode();
                if (iHashCode2 == 49739052) {
                    if (strOptString.equals("48000")) {
                        i2 = 1;
                    }
                    ComposerKt.sourceInformationMarkerStart(composer, 1829995206, "CC(remember):MainActivity.kt#9igjgp");
                    objRememberedValue = composer.rememberedValue();
                    if (objRememberedValue != Composer.INSTANCE.getEmpty()) {
                    }
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    MiuixDropdown("PCM 采样率（无损直通）", listListOf, i2, (Function1) objRememberedValue, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                    List listListOf22 = CollectionsKt.listOf((Object[]) new String[]{"本地静音（声音只走 WiFi）", "本地照常出声"});
                    boolean zAreEqual3 = Intrinsics.areEqual(MainScreen$lambda$1(mutableState2).optString("local_render"), "1");
                    ComposerKt.sourceInformationMarkerStart(composer, 1830007869, "CC(remember):MainActivity.kt#9igjgp");
                    objRememberedValue2 = composer.rememberedValue();
                    if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                    }
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    MiuixDropdown("本地声音", listListOf22, zAreEqual3 ? 1 : 0, (Function1) objRememberedValue2, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                    List listListOf32 = CollectionsKt.listOf((Object[]) new String[]{"关闭", "开启（尽力而为）"});
                    boolean zAreEqual22 = Intrinsics.areEqual(MainScreen$lambda$1(mutableState2).optString("capture_call"), "1");
                    ComposerKt.sourceInformationMarkerStart(composer, 1830018973, "CC(remember):MainActivity.kt#9igjgp");
                    objRememberedValue3 = composer.rememberedValue();
                    if (objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                    }
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    MiuixDropdown("蜂窝通话捕获", listListOf32, zAreEqual22 ? 1 : 0, (Function1) objRememberedValue3, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    composer.endNode();
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    if (ComposerKt.isTraceInProgress()) {
                    }
                } else if (iHashCode2 != 54297075) {
                    if (iHashCode2 != 1457006310 || !strOptString.equals("192000")) {
                    }
                    ComposerKt.sourceInformationMarkerStart(composer, 1829995206, "CC(remember):MainActivity.kt#9igjgp");
                    objRememberedValue = composer.rememberedValue();
                    if (objRememberedValue != Composer.INSTANCE.getEmpty()) {
                    }
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    MiuixDropdown("PCM 采样率（无损直通）", listListOf, i2, (Function1) objRememberedValue, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                    List listListOf222 = CollectionsKt.listOf((Object[]) new String[]{"本地静音（声音只走 WiFi）", "本地照常出声"});
                    boolean zAreEqual32 = Intrinsics.areEqual(MainScreen$lambda$1(mutableState2).optString("local_render"), "1");
                    ComposerKt.sourceInformationMarkerStart(composer, 1830007869, "CC(remember):MainActivity.kt#9igjgp");
                    objRememberedValue2 = composer.rememberedValue();
                    if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                    }
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    MiuixDropdown("本地声音", listListOf222, zAreEqual32 ? 1 : 0, (Function1) objRememberedValue2, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                    List listListOf322 = CollectionsKt.listOf((Object[]) new String[]{"关闭", "开启（尽力而为）"});
                    boolean zAreEqual222 = Intrinsics.areEqual(MainScreen$lambda$1(mutableState2).optString("capture_call"), "1");
                    ComposerKt.sourceInformationMarkerStart(composer, 1830018973, "CC(remember):MainActivity.kt#9igjgp");
                    objRememberedValue3 = composer.rememberedValue();
                    if (objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                    }
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    MiuixDropdown("蜂窝通话捕获", listListOf322, zAreEqual222 ? 1 : 0, (Function1) objRememberedValue3, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    composer.endNode();
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    if (ComposerKt.isTraceInProgress()) {
                    }
                } else {
                    if (strOptString.equals("96000")) {
                        i2 = 2;
                    }
                    ComposerKt.sourceInformationMarkerStart(composer, 1829995206, "CC(remember):MainActivity.kt#9igjgp");
                    objRememberedValue = composer.rememberedValue();
                    if (objRememberedValue != Composer.INSTANCE.getEmpty()) {
                    }
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    MiuixDropdown("PCM 采样率（无损直通）", listListOf, i2, (Function1) objRememberedValue, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                    List listListOf2222 = CollectionsKt.listOf((Object[]) new String[]{"本地静音（声音只走 WiFi）", "本地照常出声"});
                    boolean zAreEqual322 = Intrinsics.areEqual(MainScreen$lambda$1(mutableState2).optString("local_render"), "1");
                    ComposerKt.sourceInformationMarkerStart(composer, 1830007869, "CC(remember):MainActivity.kt#9igjgp");
                    objRememberedValue2 = composer.rememberedValue();
                    if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                    }
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    MiuixDropdown("本地声音", listListOf2222, zAreEqual322 ? 1 : 0, (Function1) objRememberedValue2, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                    List listListOf3222 = CollectionsKt.listOf((Object[]) new String[]{"关闭", "开启（尽力而为）"});
                    boolean zAreEqual2222 = Intrinsics.areEqual(MainScreen$lambda$1(mutableState2).optString("capture_call"), "1");
                    ComposerKt.sourceInformationMarkerStart(composer, 1830018973, "CC(remember):MainActivity.kt#9igjgp");
                    objRememberedValue3 = composer.rememberedValue();
                    if (objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                    }
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    MiuixDropdown("蜂窝通话捕获", listListOf3222, zAreEqual2222 ? 1 : 0, (Function1) objRememberedValue3, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    composer.endNode();
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    ComposerKt.sourceInformationMarkerEnd(composer);
                    if (ComposerKt.isTraceInProgress()) {
                    }
                }
            }
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$2$0$0$0(MutableState mutableState, int i) {
        MainScreen$setCfg(mutableState, "pcm_rate", new String[]{DebugKt.DEBUG_PROPERTY_VALUE_AUTO, "48000", "96000", "192000"}[i]);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$2$0$1$0(MutableState mutableState, int i) {
        MainScreen$setCfg(mutableState, "local_render", Integer.valueOf(i));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$2$0$2$0(MutableState mutableState, int i) {
        MainScreen$setCfg(mutableState, "capture_call", Integer.valueOf(i));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$3(final ComponentActivity componentActivity, final MutableState mutableState, final MutableState mutableState2, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C288@12397L1447:MainActivity.kt#3gmcmr");
        if (composer.shouldExecute((i & 17) != 16, i & 1)) {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1739189388, i, -1, "com.wifiaudio.manager.MainScreen.<anonymous>.<anonymous> (MainActivity.kt:288)");
            }
            float f = 16;
            Modifier modifierM915padding3ABfNKs = PaddingKt.m915padding3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(f));
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
            ComposerKt.sourceInformationMarkerStart(composer, 816890648, "C289@12473L71,290@12561L29,295@12835L54,291@12607L356,298@12980L379,304@13377L30,306@13462L116,305@13424L406:MainActivity.kt#3gmcmr");
            TextKt.m11051TextNvy7gAk("🌐 模式与网络", null, 0L, null, TextUnitKt.getSp(14), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597446, 0, 262062);
            float f2 = 8;
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f2)), composer, 6);
            List listListOf = CollectionsKt.listOf((Object[]) new String[]{"TCP（稳定）", "UDP（低延迟）"});
            boolean zAreEqual = Intrinsics.areEqual(MainScreen$lambda$1(mutableState).optString(NotificationCompat.CATEGORY_TRANSPORT), "udp");
            ComposerKt.sourceInformationMarkerStart(composer, -112185740, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue = composer.rememberedValue();
            if (objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = new Function1() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda30
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.MainScreen$lambda$52$3$0$0$0(mutableState, ((Integer) obj).intValue());
                    }
                };
                composer.updateRememberedValue(objRememberedValue);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            MiuixDropdown("传输协议", listListOf, zAreEqual ? 1 : 0, (Function1) objRememberedValue, SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), composer, 27702, 0);
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
            ComposerKt.sourceInformationMarkerStart(composer, -1172264390, "C299@13088L49,299@13006L131,300@13158L28,301@13291L50,301@13207L134:MainActivity.kt#3gmcmr");
            String strOptString = MainScreen$lambda$1(mutableState).optString("tcp_port", "47800");
            Intrinsics.checkNotNullExpressionValue(strOptString, "optString(...)");
            Modifier modifierWeight$default = RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null);
            ComposerKt.sourceInformationMarkerStart(composer, 1486208011, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue2 = composer.rememberedValue();
            if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue2 = new Function1() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda1
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.MainScreen$lambda$52$3$0$1$0$0(mutableState, (String) obj);
                    }
                };
                composer.updateRememberedValue(objRememberedValue2);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            ConfigField("TCP 端口", strOptString, modifierWeight$default, (Function1) objRememberedValue2, composer, 3078, 0);
            SpacerKt.Spacer(SizeKt.m968width3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(f2)), composer, 6);
            String strOptString2 = MainScreen$lambda$1(mutableState).optString("http_port", "47801");
            Intrinsics.checkNotNullExpressionValue(strOptString2, "optString(...)");
            Modifier modifierWeight$default2 = RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null);
            ComposerKt.sourceInformationMarkerStart(composer, 1486214508, "CC(remember):MainActivity.kt#9igjgp");
            Object objRememberedValue3 = composer.rememberedValue();
            if (objRememberedValue3 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue3 = new Function1() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda2
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return MainActivityKt.MainScreen$lambda$52$3$0$1$1$0(mutableState, (String) obj);
                    }
                };
                composer.updateRememberedValue(objRememberedValue3);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            ConfigField("HTTP 端口", strOptString2, modifierWeight$default2, (Function1) objRememberedValue3, composer, 3078, 0);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            ComposerKt.sourceInformationMarkerEnd(composer);
            SpacerKt.Spacer(SizeKt.m949height3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(12)), composer, 6);
            ComposerKt.sourceInformationMarkerStart(composer, -112165614, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composer.changedInstance(componentActivity);
            Object objRememberedValue4 = composer.rememberedValue();
            if (zChangedInstance || objRememberedValue4 == Composer.INSTANCE.getEmpty()) {
                objRememberedValue4 = new Function0() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda3
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.MainScreen$lambda$52$3$0$2$0(componentActivity, mutableState, mutableState2);
                    }
                };
                composer.updateRememberedValue(objRememberedValue4);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            ButtonKt.m10572ButtoneZJyerg((Function0) objRememberedValue4, SizeKt.m949height3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(46)), false, Dp.m8245constructorimpl(f), 0.0f, 0.0f, null, null, null, null, ComposableSingletons$MainActivityKt.INSTANCE.getLambda$29086480$manager_release(), composer, 3120, 6, PointerIconCompat.TYPE_NO_DROP);
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
    public static final Unit MainScreen$lambda$52$3$0$0$0(MutableState mutableState, int i) {
        MainScreen$setCfg(mutableState, NotificationCompat.CATEGORY_TRANSPORT, i == 1 ? "udp" : "tcp");
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$3$0$1$0$0(MutableState mutableState, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        Integer intOrNull = StringsKt.toIntOrNull(it);
        MainScreen$setCfg(mutableState, "tcp_port", Integer.valueOf(intOrNull != null ? intOrNull.intValue() : 47800));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$3$0$1$1$0(MutableState mutableState, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        Integer intOrNull = StringsKt.toIntOrNull(it);
        MainScreen$setCfg(mutableState, "http_port", Integer.valueOf(intOrNull != null ? intOrNull.intValue() : 47801));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$3$0$2$0(ComponentActivity componentActivity, MutableState mutableState, MutableState mutableState2) {
        saveConfig(componentActivity, MainScreen$lambda$1(mutableState));
        mutableState2.setValue("配置已保存");
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit MainScreen$lambda$52$4(MutableState mutableState, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C320@13969L283:MainActivity.kt#3gmcmr");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-659647341, i, -1, "com.wifiaudio.manager.MainScreen.<anonymous>.<anonymous> (MainActivity.kt:320)");
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
            ComposerKt.sourceInformationMarkerStart(composer, 738864293, "C321@14045L70,322@14132L106:MainActivity.kt#3gmcmr");
            TextKt.m11051TextNvy7gAk("📄 服务日志", null, 0L, null, TextUnitKt.getSp(14), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597446, 0, 262062);
            TextKt.m11051TextNvy7gAk(MainScreen$lambda$40(mutableState), PaddingKt.m919paddingqDBjuR0$default(Modifier.INSTANCE, 0.0f, Dp.m8245constructorimpl(8), 0.0f, 0.0f, 13, null), ColorKt.Color(4287402928L), null, TextUnitKt.getSp(10), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 25008, 0, 262120);
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

    private static final void StatusLine(final String str, String str2, Composer composer, final int i) {
        int i2;
        final String str3;
        Composer composer2;
        Composer composerStartRestartGroup = composer.startRestartGroup(-1370945131);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(StatusLine)N(k,v)334@14500L215:MainActivity.kt#3gmcmr");
        if ((i & 6) == 0) {
            i2 = i | (composerStartRestartGroup.changed(str) ? 4 : 2);
        } else {
            i2 = i;
        }
        if ((i & 48) == 0) {
            i2 |= composerStartRestartGroup.changed(str2) ? 32 : 16;
        }
        if (!composerStartRestartGroup.shouldExecute((i2 & 19) != 18, i2 & 1)) {
            str3 = str2;
            composer2 = composerStartRestartGroup;
            composer2.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1370945131, i2, -1, "com.wifiaudio.manager.StatusLine (MainActivity.kt:333)");
            }
            Modifier modifierM917paddingVpY3zN4$default = PaddingKt.m917paddingVpY3zN4$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, Dp.m8245constructorimpl(2), 1, null);
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo");
            MeasurePolicy measurePolicyRowMeasurePolicy = RowKt.rowMeasurePolicy(Arrangement.INSTANCE.getStart(), Alignment.INSTANCE.getTop(), composerStartRestartGroup, 0);
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
            Updater.m4669setimpl(composerM4661constructorimpl, measurePolicyRowMeasurePolicy, ComposeUiNode.INSTANCE.getSetMeasurePolicy());
            Updater.m4669setimpl(composerM4661constructorimpl, currentCompositionLocalMap, ComposeUiNode.INSTANCE.getSetResolvedCompositionLocals());
            Updater.m4665initimpl(composerM4661constructorimpl, Integer.valueOf(iHashCode), ComposeUiNode.INSTANCE.getSetCompositeKeyHash());
            Updater.m4667reconcileimpl(composerM4661constructorimpl, ComposeUiNode.INSTANCE.getApplyOnDeactivatedNodeAssertion());
            Updater.m4669setimpl(composerM4661constructorimpl, modifierMaterializeModifier, ComposeUiNode.INSTANCE.getSetModifier());
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            RowScopeInstance rowScopeInstance = RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 830696620, "C335@14575L93,336@14677L32:MainActivity.kt#3gmcmr");
            composer2 = composerStartRestartGroup;
            TextKt.m11051TextNvy7gAk(str, SizeKt.m968width3ABfNKs(Modifier.INSTANCE, Dp.m8245constructorimpl(90)), ColorKt.Color(4287402928L), null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer2, (i2 & 14) | 25008, 0, 262120);
            str3 = str2;
            TextKt.m11051TextNvy7gAk(str3, null, 0L, null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer2, ((i2 >> 3) & 14) | 24576, 0, 262126);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            composer2.endNode();
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            ComposerKt.sourceInformationMarkerEnd(composer2);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda10
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.StatusLine$lambda$1(str, str3, i, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    private static final void ManagerStat(final String str, final String str2, Modifier modifier, Composer composer, final int i, final int i2) {
        int i3;
        final Modifier modifier2;
        Composer composerStartRestartGroup = composer.startRestartGroup(151321257);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(ManagerStat)N(label,value,modifier)342@14848L378,342@14822L404:MainActivity.kt#3gmcmr");
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
                ComposerKt.traceEventStart(151321257, i3, -1, "com.wifiaudio.manager.ManagerStat (MainActivity.kt:341)");
            }
            CardKt.m10586CardEUb7tLY(modifier3, 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(1916596440, true, new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda4
                @Override // kotlin.jvm.functions.Function3
                public final Object invoke(Object obj, Object obj2, Object obj3) {
                    return MainActivityKt.ManagerStat$lambda$0(str2, str, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                }
            }, composerStartRestartGroup, 54), composerStartRestartGroup, ((i3 >> 6) & 14) | 24576, 14);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
            modifier2 = modifier3;
        }
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup = composerStartRestartGroup.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup != null) {
            scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda5
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return MainActivityKt.ManagerStat$lambda$1(str, str2, modifier2, i, i2, (Composer) obj, ((Integer) obj2).intValue());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ManagerStat$lambda$0(String str, String str2, ColumnScope Card, Composer composer, int i) {
        Intrinsics.checkNotNullParameter(Card, "$this$Card");
        ComposerKt.sourceInformation(composer, "C343@14858L362:MainActivity.kt#3gmcmr");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(1916596440, i, -1, "com.wifiaudio.manager.ManagerStat.<anonymous> (MainActivity.kt:343)");
            }
            Modifier modifierM917paddingVpY3zN4$default = PaddingKt.m917paddingVpY3zN4$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, Dp.m8245constructorimpl(12), 1, null);
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
            ComposerKt.sourceInformationMarkerStart(composer, -479485558, "C347@15027L107,348@15147L63:MainActivity.kt#3gmcmr");
            TextKt.m11051TextNvy7gAk(str, null, ColorKt.Color(4281631487L), null, TextUnitKt.getSp(17), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 1, 0, null, null, composer, 1597824, 24576, 245674);
            TextKt.m11051TextNvy7gAk(str2, null, ColorKt.Color(4287402928L), null, TextUnitKt.getSp(11), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24960, 0, 262122);
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

    /* JADX WARN: Removed duplicated region for block: B:31:0x0057  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0066  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0070  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x01ff  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x020b  */
    /* JADX WARN: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static final void ConfigField(final String str, final String str2, Modifier modifier, final Function1<? super String, Unit> function1, Composer composer, final int i, final int i2) {
        int i3;
        String str3;
        Modifier modifier2;
        Composer composer2;
        final Modifier modifier3;
        ScopeUpdateScope scopeUpdateScopeEndRestartGroup;
        Composer composerStartRestartGroup = composer.startRestartGroup(-1572338465);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(ConfigField)N(label,value,modifier,onChange)355@15361L262:MainActivity.kt#3gmcmr");
        if ((i & 6) == 0) {
            i3 = (composerStartRestartGroup.changed(str) ? 4 : 2) | i;
        } else {
            i3 = i;
        }
        if ((i & 48) == 0) {
            str3 = str2;
            i3 |= composerStartRestartGroup.changed(str3) ? 32 : 16;
        } else {
            str3 = str2;
        }
        int i4 = i2 & 4;
        if (i4 == 0) {
            if ((i & 384) == 0) {
                modifier2 = modifier;
                i3 |= composerStartRestartGroup.changed(modifier2) ? 256 : 128;
            }
            if ((i & 3072) == 0) {
                i3 |= composerStartRestartGroup.changedInstance(function1) ? 2048 : 1024;
            }
            if (composerStartRestartGroup.shouldExecute((i3 & 1171) == 1170, i3 & 1)) {
                composer2 = composerStartRestartGroup;
                composer2.skipToGroupEnd();
                modifier3 = modifier2;
            } else {
                Modifier.Companion companion = i4 != 0 ? Modifier.INSTANCE : modifier2;
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart(-1572338465, i3, -1, "com.wifiaudio.manager.ConfigField (MainActivity.kt:354)");
                }
                int i5 = i3 >> 6;
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo");
                MeasurePolicy measurePolicyColumnMeasurePolicy = ColumnKt.columnMeasurePolicy(Arrangement.INSTANCE.getTop(), Alignment.INSTANCE.getStart(), composerStartRestartGroup, 0);
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1159599143, "CC(Layout)P(!1,2)81@3355L27,84@3521L416:Layout.kt#80mrfh");
                int iHashCode = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(composerStartRestartGroup, 0));
                CompositionLocalMap currentCompositionLocalMap = composerStartRestartGroup.getCurrentCompositionLocalMap();
                Modifier modifierMaterializeModifier = ComposedModifierKt.materializeModifier(composerStartRestartGroup, companion);
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
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -702816544, "C356@15399L63,357@15471L146:MainActivity.kt#3gmcmr");
                TextKt.m11051TextNvy7gAk(str, null, ColorKt.Color(4287402928L), null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composerStartRestartGroup, (i3 & 14) | 24960, 0, 262122);
                TextFieldKt.m11044TextFieldz9KcpoQ(str3, function1, SizeKt.m949height3ABfNKs(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(52)), 0L, 0L, 0.0f, (String) null, 0L, 0L, false, false, false, (TextStyle) null, (KeyboardOptions) null, (KeyboardActions) null, (Function2<? super Composer, ? super Integer, Unit>) null, (Function2<? super Composer, ? super Integer, Unit>) null, false, 0, 0, (VisualTransformation) null, (Function1<? super TextLayoutResult, Unit>) null, (MutableInteractionSource) null, (Brush) null, composerStartRestartGroup, ((i3 >> 3) & 14) | 384 | (i5 & 112), 0, 0, 16777208);
                composer2 = composerStartRestartGroup;
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                composer2.endNode();
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                ComposerKt.sourceInformationMarkerEnd(composer2);
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                }
                modifier3 = companion;
            }
            scopeUpdateScopeEndRestartGroup = composer2.endRestartGroup();
            if (scopeUpdateScopeEndRestartGroup == null) {
                scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda24
                    @Override // kotlin.jvm.functions.Function2
                    public final Object invoke(Object obj, Object obj2) {
                        return MainActivityKt.ConfigField$lambda$1(str, str2, modifier3, function1, i, i2, (Composer) obj, ((Integer) obj2).intValue());
                    }
                });
                return;
            }
            return;
        }
        i3 |= 384;
        modifier2 = modifier;
        if ((i & 3072) == 0) {
        }
        if (composerStartRestartGroup.shouldExecute((i3 & 1171) == 1170, i3 & 1)) {
        }
        scopeUpdateScopeEndRestartGroup = composer2.endRestartGroup();
        if (scopeUpdateScopeEndRestartGroup == null) {
        }
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
        Composer composerStartRestartGroup = composer.startRestartGroup(-1729184908);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(MiuixDropdown)N(label,options,selected,onSelect,modifier)367@15796L34,368@15835L1537:MainActivity.kt#3gmcmr");
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
                    ComposerKt.traceEventStart(-1729184908, i4, -1, "com.wifiaudio.manager.MiuixDropdown (MainActivity.kt:366)");
                }
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 405651766, "CC(remember):MainActivity.kt#9igjgp");
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
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, 772755593, "C369@15898L63,370@15985L19,370@16062L354,370@15970L446:MainActivity.kt#3gmcmr");
                Modifier modifier3 = companion;
                TextKt.m11051TextNvy7gAk(str, null, ColorKt.Color(4287402928L), null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composerStartRestartGroup, (i4 & 14) | 24960, 0, 262122);
                float f = 2;
                Modifier modifierM919paddingqDBjuR0$default = PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, Dp.m8245constructorimpl(f), 0.0f, 0.0f, 13, null);
                ComposerKt.sourceInformationMarkerStart(composerStartRestartGroup, -1776186383, "CC(remember):MainActivity.kt#9igjgp");
                Object objRememberedValue2 = composerStartRestartGroup.rememberedValue();
                if (objRememberedValue2 == Composer.INSTANCE.getEmpty()) {
                    objRememberedValue2 = new Function0() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda26
                        @Override // kotlin.jvm.functions.Function0
                        public final Object invoke() {
                            return MainActivityKt.MiuixDropdown$lambda$3$0$0(mutableState);
                        }
                    };
                    composerStartRestartGroup.updateRememberedValue(objRememberedValue2);
                }
                ComposerKt.sourceInformationMarkerEnd(composerStartRestartGroup);
                CardKt.m10587CardiWtaglI(modifierM919paddingqDBjuR0$default, 0.0f, null, null, null, false, (Function0) objRememberedValue2, null, ComposableLambdaKt.rememberComposableLambda(-1241085655, true, new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda27
                    @Override // kotlin.jvm.functions.Function3
                    public final Object invoke(Object obj, Object obj2, Object obj3) {
                        return MainActivityKt.MiuixDropdown$lambda$3$1(list, i, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                    }
                }, composerStartRestartGroup, 54), composerStartRestartGroup, 102236166, 190);
                composerStartRestartGroup = composerStartRestartGroup;
                if (MiuixDropdown$lambda$1(mutableState)) {
                    composerStartRestartGroup.startReplaceGroup(773275493);
                    ComposerKt.sourceInformation(composerStartRestartGroup, "381@16572L784,381@16511L845");
                    function12 = function1;
                    CardKt.m10586CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, Dp.m8245constructorimpl(f), 0.0f, 0.0f, 13, null), 0.0f, null, null, ComposableLambdaKt.rememberComposableLambda(-1656846798, true, new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda28
                        @Override // kotlin.jvm.functions.Function3
                        public final Object invoke(Object obj, Object obj2, Object obj3) {
                            return MainActivityKt.MiuixDropdown$lambda$3$2(list, function12, mutableState, i, (ColumnScope) obj, (Composer) obj2, ((Integer) obj3).intValue());
                        }
                    }, composerStartRestartGroup, 54), composerStartRestartGroup, 24582, 14);
                    composerStartRestartGroup.endReplaceGroup();
                } else {
                    function12 = function1;
                    composerStartRestartGroup.startReplaceGroup(774166340);
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
                scopeUpdateScopeEndRestartGroup.updateScope(new Function2() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda29
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
        ComposerKt.sourceInformation(composer, "C371@16076L330:MainActivity.kt#3gmcmr");
        if (!composer.shouldExecute((i2 & 17) != 16, i2 & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1241085655, i2, -1, "com.wifiaudio.manager.MiuixDropdown.<anonymous>.<anonymous> (MainActivity.kt:371)");
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
            ComposerKt.sourceInformationMarkerStart(composer, 1718893162, "C375@16276L65,376@16358L34:MainActivity.kt#3gmcmr");
            TextKt.m11051TextNvy7gAk((String) ((i < 0 || i >= list.size()) ? "" : list.get(i)), null, 0L, null, TextUnitKt.getSp(14), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24576, 0, 262126);
            TextKt.m11051TextNvy7gAk("▾", null, 0L, null, TextUnitKt.getSp(12), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24582, 0, 262126);
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
        ComposerKt.sourceInformation(composer2, "C382@16590L752:MainActivity.kt#3gmcmr");
        final int i3 = 0;
        if (!composer2.shouldExecute((i2 & 17) != 16, i2 & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-1656846798, i2, -1, "com.wifiaudio.manager.MiuixDropdown.<anonymous>.<anonymous> (MainActivity.kt:382)");
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
            ComposerKt.sourceInformationMarkerStart(composer2, -151077949, "C:MainActivity.kt#3gmcmr");
            composer2.startReplaceGroup(-1805988552);
            ComposerKt.sourceInformation(composer2, "*385@16781L33,387@16905L397,384@16737L565");
            for (Object obj : list) {
                int i4 = i3 + 1;
                if (i3 < 0) {
                    CollectionsKt.throwIndexOverflow();
                }
                final String str = (String) obj;
                Modifier modifierFillMaxWidth$default = SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null);
                ComposerKt.sourceInformationMarkerStart(composer2, -197108937, "CC(remember):MainActivity.kt#9igjgp");
                boolean zChanged = composer2.changed(function1) | composer2.changed(i3);
                Object objRememberedValue = composer2.rememberedValue();
                if (zChanged || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                    objRememberedValue = new Function0() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda18
                        @Override // kotlin.jvm.functions.Function0
                        public final Object invoke() {
                            return MainActivityKt.MiuixDropdown$lambda$3$2$0$0$0$0(function1, i3, mutableState);
                        }
                    };
                    composer2.updateRememberedValue(objRememberedValue);
                }
                ComposerKt.sourceInformationMarkerEnd(composer2);
                CardKt.m10587CardiWtaglI(modifierFillMaxWidth$default, 0.0f, null, null, null, false, (Function0) objRememberedValue, null, ComposableLambdaKt.rememberComposableLambda(-571303167, true, new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda19
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
        ComposerKt.sourceInformation(composer, "C388@16935L341:MainActivity.kt#3gmcmr");
        if (!composer.shouldExecute((i3 & 17) != 16, i3 & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-571303167, i3, -1, "com.wifiaudio.manager.MiuixDropdown.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:388)");
            }
            TextKt.m11051TextNvy7gAk(str, PaddingKt.m916paddingVpY3zN4(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), Dp.m8245constructorimpl(16), Dp.m8245constructorimpl(11)), i == i2 ? ColorKt.Color(4281631487L) : Color.INSTANCE.m5447getUnspecified0d7_KjU(), null, TextUnitKt.getSp(14), null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 24624, 0, 262120);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private static final JSONObject loadConfig() {
        try {
            String config = RootShell.readConfig();
            if (config == null) {
                config = "{}";
            }
            return new JSONObject(config);
        } catch (Throwable unused) {
            return new JSONObject();
        }
    }

    private static final void saveConfig(ComponentActivity componentActivity, JSONObject jSONObject) {
        RootShell.writeConfig(jSONObject.toString());
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
    public static final Object checkUpdate(Context context, Continuation<? super String> continuation) {
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
    @DebugMetadata(c = "com.wifiaudio.manager.MainActivityKt$checkUpdate$2", f = "MainActivity.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, nl = {}, s = {}, v = 2)
    /* renamed from: com.wifiaudio.manager.MainActivityKt$checkUpdate$2, reason: invalid class name */
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
        public final Object invokeSuspend(Object obj) {
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
        Composer composerStartRestartGroup = composer.startRestartGroup(-133736746);
        ComposerKt.sourceInformation(composerStartRestartGroup, "C(UpdateBanner)N(tag,ctx)458@19258L40,459@19305L1170,456@19126L1349:MainActivity.kt#3gmcmr");
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
                ComposerKt.traceEventStart(-133736746, i2, -1, "com.wifiaudio.manager.UpdateBanner (MainActivity.kt:454)");
            }
            if (str.length() != 0) {
                CardKt.m10586CardEUb7tLY(PaddingKt.m919paddingqDBjuR0$default(SizeKt.fillMaxWidth$default(Modifier.INSTANCE, 0.0f, 1, null), 0.0f, 0.0f, 0.0f, Dp.m8245constructorimpl(12), 7, null), 0.0f, null, CardDefaults.INSTANCE.m10580defaultColorsdgg9oW8(ColorKt.Color(4293587711L), 0L, composerStartRestartGroup, (CardDefaults.$stable << 6) | 6, 2), ComposableLambdaKt.rememberComposableLambda(-779262073, true, new Function3() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda21
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
                    function2 = new Function2() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda20
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
            function2 = new Function2() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda23
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
        ComposerKt.sourceInformation(composer, "C460@19315L1154:MainActivity.kt#3gmcmr");
        if (!composer.shouldExecute((i & 17) != 16, i & 1)) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart(-779262073, i, -1, "com.wifiaudio.manager.UpdateBanner.<anonymous> (MainActivity.kt:460)");
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
            ComposerKt.sourceInformationMarkerStart(composer, 1207923958, "C464@19497L239,478@20101L40,472@19781L252,471@19749L710:MainActivity.kt#3gmcmr");
            TextKt.m11051TextNvy7gAk("📦 发现新版本 v" + str + "，点「去下载」更新", RowScope.weight$default(rowScopeInstance, Modifier.INSTANCE, 1.0f, false, 2, null), ColorKt.Color(4281631487L), null, TextUnitKt.getSp(13), null, FontWeight.INSTANCE.getBold(), null, 0L, null, null, 0L, 0, false, 0, 0, null, null, composer, 1597824, 0, 262056);
            CardColors cardColorsM10580defaultColorsdgg9oW8 = CardDefaults.INSTANCE.m10580defaultColorsdgg9oW8(ColorKt.Color(4281631487L), 0L, composer, (CardDefaults.$stable << 6) | 6, 2);
            ComposerKt.sourceInformationMarkerStart(composer, 177520999, "CC(remember):MainActivity.kt#9igjgp");
            boolean zChangedInstance = composer.changedInstance(context);
            Object objRememberedValue = composer.rememberedValue();
            if (zChangedInstance || objRememberedValue == Composer.INSTANCE.getEmpty()) {
                objRememberedValue = new Function0() { // from class: com.wifiaudio.manager.MainActivityKt$$ExternalSyntheticLambda22
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return MainActivityKt.UpdateBanner$lambda$1$0$0$0(context);
                    }
                };
                composer.updateRememberedValue(objRememberedValue);
            }
            ComposerKt.sourceInformationMarkerEnd(composer);
            CardKt.m10587CardiWtaglI(null, 0.0f, null, cardColorsM10580defaultColorsdgg9oW8, null, false, (Function0) objRememberedValue, null, ComposableSingletons$MainActivityKt.INSTANCE.getLambda$1281507232$manager_release(), composer, 100663296, 183);
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
