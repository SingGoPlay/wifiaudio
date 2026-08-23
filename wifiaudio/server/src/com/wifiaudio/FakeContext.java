package com.wifiaudio;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 为 app_process 进程伪造一个可用的系统 Context。
 * 参考 Genymobile scrcpy 的 Workarounds + FakeContext 实现。
 *
 * 重要：本类做了多层降级，任何一步失败都不会导致进程崩溃：
 *   1. 反射构造 ActivityThread 并注册为 sCurrentActivityThread（标准路径）
 *   2. 失败则回退 currentActivityThread() / ContextImpl.createSystemContext()
 *   3. 全部失败则抛带详细原因的 RuntimeException（由 Main 记录后退出）
 * 原始错误原因会完整打印到日志，便于定位厂商 ROM 的差异。
 */
@SuppressLint("PrivateApi")
public final class FakeContext extends ContextWrapper {

    public static final String PACKAGE_NAME = "com.android.shell";

    private static final Class<?> ACTIVITY_THREAD_CLASS;
    private static final Object ACTIVITY_THREAD;
    private static final Throwable INIT_ERROR;

    private static final FakeContext INSTANCE;

    static {
        Throwable err = null;
        Object at = null;
        Class<?> atc = null;
        try {
            atc = Class.forName("android.app.ActivityThread");
            Constructor<?> ctor = atc.getDeclaredConstructor();
            ctor.setAccessible(true);
            at = ctor.newInstance();

            // ActivityThread.sCurrentActivityThread = activityThread;
            Field sCurrent = atc.getDeclaredField("sCurrentActivityThread");
            sCurrent.setAccessible(true);
            sCurrent.set(null, at);

            // activityThread.mSystemThread = true;
            Field mSystemThread = atc.getDeclaredField("mSystemThread");
            mSystemThread.setAccessible(true);
            mSystemThread.setBoolean(at, true);
        } catch (Throwable t) {
            err = t;
            at = null;
            atc = null;
        }
        ACTIVITY_THREAD_CLASS = atc;
        ACTIVITY_THREAD = at;
        INIT_ERROR = err;

        Context sys = getSystemContextQuiet();
        if (sys == null) {
            // 无法获得系统 Context：仍构造 INSTANCE（ContextWrapper 允许 null base），
            // 但 getSystemService 会失败。由 Main 检测并报告。
            INSTANCE = new FakeContext(null);
        } else {
            INSTANCE = new FakeContext(sys);
        }
    }

    /** 在 Main.main 开头调用，初始化 framework 上下文（必须在任何系统服务调用前） */
    public static void apply() {
        if (INIT_ERROR != null) {
            Util.log("FakeCtx", "ActivityThread init failed, using fallback", INIT_ERROR);
            return; // 静态块已做降级
        }
        if (Build.VERSION.SDK_INT >= 31) {
            fillConfigurationController();
        }
        if (!"ONYX".equalsIgnoreCase(Build.BRAND)) {
            fillAppInfo();
        }
        fillAppContext();
    }

    private static void fillConfigurationController() {
        try {
            Class<?> ccClass = Class.forName("android.app.ConfigurationController");
            Class<?> atiClass = Class.forName("android.app.ActivityThreadInternal");
            Constructor<?> ctor = ccClass.getDeclaredConstructor(atiClass);
            ctor.setAccessible(true);
            Object cc = ctor.newInstance(ACTIVITY_THREAD);
            Field f = ACTIVITY_THREAD_CLASS.getDeclaredField("mConfigurationController");
            f.setAccessible(true);
            f.set(ACTIVITY_THREAD, cc);
        } catch (Throwable t) {
            Util.log("FakeCtx", "fillConfigurationController skipped", t);
        }
    }

    private static void fillAppInfo() {
        try {
            Class<?> abdClass = Class.forName("android.app.ActivityThread$AppBindData");
            Constructor<?> ctor = abdClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object abd = ctor.newInstance();

            ApplicationInfo ai = new ApplicationInfo();
            ai.packageName = PACKAGE_NAME;
            Field appInfoField = abdClass.getDeclaredField("appInfo");
            appInfoField.setAccessible(true);
            appInfoField.set(abd, ai);

            Field mBound = ACTIVITY_THREAD_CLASS.getDeclaredField("mBoundApplication");
            mBound.setAccessible(true);
            mBound.set(ACTIVITY_THREAD, abd);
        } catch (Throwable t) {
            Util.log("FakeCtx", "fillAppInfo skipped", t);
        }
    }

    private static void fillAppContext() {
        try {
            Application app = Instrumentation.newApplication(Application.class, get());
            Field mInitial = ACTIVITY_THREAD_CLASS.getDeclaredField("mInitialApplication");
            mInitial.setAccessible(true);
            mInitial.set(ACTIVITY_THREAD, app);
        } catch (Throwable t) {
            Util.log("FakeCtx", "fillAppContext skipped", t);
        }
    }

    /** 多级获取系统 Context；全部失败返回 null（不抛） */
    private static Context getSystemContextQuiet() {
        // 方式1: 我们构造的 ActivityThread.getSystemContext()
        if (ACTIVITY_THREAD != null) {
            try {
                Method m = ACTIVITY_THREAD_CLASS.getDeclaredMethod("getSystemContext");
                Context c = (Context) m.invoke(ACTIVITY_THREAD);
                if (c != null) return c;
            } catch (Throwable ignored) {
            }
        }
        // 方式2: currentActivityThread()（app_process 下通常为 null）
        if (ACTIVITY_THREAD_CLASS != null) {
            try {
                Object at = ACTIVITY_THREAD_CLASS.getMethod("currentActivityThread").invoke(null);
                if (at != null) {
                    Method m = ACTIVITY_THREAD_CLASS.getMethod("getSystemContext");
                    Context c = (Context) m.invoke(at);
                    if (c != null) return c;
                }
            } catch (Throwable ignored) {
            }
        }
        // 方式3: ContextImpl.createSystemContext(null)
        try {
            Class<?> ci = Class.forName("android.app.ContextImpl");
            Method m = ci.getMethod("createSystemContext", Class.forName("android.app.ActivityThread"));
            Context c = (Context) m.invoke(null, ACTIVITY_THREAD);
            if (c != null) return c;
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static FakeContext get() {
        return INSTANCE;
    }

    private FakeContext(Context base) {
        super(base);
    }

    /** 检查 context 是否可用（base 是否为 null） */
    public boolean usable() {
        return getBaseContext() != null;
    }

    /** 返回初始化错误（调试用） */
    public static Throwable getInitError() {
        return INIT_ERROR;
    }

    @Override
    public String getPackageName() {
        return PACKAGE_NAME;
    }

    @Override
    public String getOpPackageName() {
        return PACKAGE_NAME;
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public Object getSystemService(String name) {
        Context base = getBaseContext();
        if (base == null) {
            throw new IllegalStateException("FakeContext base is null (init error: " + INIT_ERROR + ")");
        }
        Object service = base.getSystemService(name);
        if (service == null) {
            return null;
        }
        // 部分服务内部持有 context，替换其 mContext 以避免包名校验问题
        try {
            Class<?> cls = service.getClass();
            while (cls != null) {
                try {
                    Field f = cls.getDeclaredField("mContext");
                    f.setAccessible(true);
                    f.set(service, this);
                    break;
                } catch (NoSuchFieldException e) {
                    cls = cls.getSuperclass();
                }
            }
        } catch (Throwable ignored) {
        }
        return service;
    }

    @Override
    public ContentResolver getContentResolver() {
        Context base = getBaseContext();
        if (base == null) {
            throw new IllegalStateException("FakeContext base is null");
        }
        return base.getContentResolver();
    }
}
