package com.wifiaudio;

import android.app.Application;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public final class FakeContext extends ContextWrapper {
    private static final Object ACTIVITY_THREAD;
    private static final Class<?> ACTIVITY_THREAD_CLASS;
    private static final Throwable INIT_ERROR;
    private static final FakeContext INSTANCE;
    public static final String PACKAGE_NAME = "com.android.shell";

    @Override // android.content.ContextWrapper, android.content.Context
    public Context getApplicationContext() {
        return this;
    }

    static {
        Object obj;
        Throwable th;
        Class<?> cls;
        try {
            cls = Class.forName("android.app.ActivityThread");
            Constructor<?> declaredConstructor = cls.getDeclaredConstructor(null);
            declaredConstructor.setAccessible(true);
            Object objNewInstance = declaredConstructor.newInstance(null);
            Field declaredField = cls.getDeclaredField("sCurrentActivityThread");
            declaredField.setAccessible(true);
            declaredField.set(null, objNewInstance);
            Field declaredField2 = cls.getDeclaredField("mSystemThread");
            declaredField2.setAccessible(true);
            declaredField2.setBoolean(objNewInstance, true);
            obj = objNewInstance;
            th = null;
        } catch (Throwable th2) {
            obj = null;
            th = th2;
            cls = null;
        }
        ACTIVITY_THREAD_CLASS = cls;
        ACTIVITY_THREAD = obj;
        INIT_ERROR = th;
        Context systemContextQuiet = getSystemContextQuiet();
        if (systemContextQuiet == null) {
            INSTANCE = new FakeContext(null);
        } else {
            INSTANCE = new FakeContext(systemContextQuiet);
        }
    }

    public static void apply() {
        Throwable th = INIT_ERROR;
        if (th != null) {
            Util.log("FakeCtx", "ActivityThread init failed, using fallback", th);
            return;
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
            Constructor<?> declaredConstructor = Class.forName("android.app.ConfigurationController").getDeclaredConstructor(Class.forName("android.app.ActivityThreadInternal"));
            declaredConstructor.setAccessible(true);
            Object obj = ACTIVITY_THREAD;
            Object objNewInstance = declaredConstructor.newInstance(obj);
            Field declaredField = ACTIVITY_THREAD_CLASS.getDeclaredField("mConfigurationController");
            declaredField.setAccessible(true);
            declaredField.set(obj, objNewInstance);
        } catch (Throwable th) {
            Util.log("FakeCtx", "fillConfigurationController skipped", th);
        }
    }

    private static void fillAppInfo() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread$AppBindData");
            Constructor<?> declaredConstructor = cls.getDeclaredConstructor(null);
            declaredConstructor.setAccessible(true);
            Object objNewInstance = declaredConstructor.newInstance(null);
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.packageName = PACKAGE_NAME;
            Field declaredField = cls.getDeclaredField("appInfo");
            declaredField.setAccessible(true);
            declaredField.set(objNewInstance, applicationInfo);
            Field declaredField2 = ACTIVITY_THREAD_CLASS.getDeclaredField("mBoundApplication");
            declaredField2.setAccessible(true);
            declaredField2.set(ACTIVITY_THREAD, objNewInstance);
        } catch (Throwable th) {
            Util.log("FakeCtx", "fillAppInfo skipped", th);
        }
    }

    private static void fillAppContext() {
        try {
            Application applicationNewApplication = Instrumentation.newApplication(Application.class, get());
            Field declaredField = ACTIVITY_THREAD_CLASS.getDeclaredField("mInitialApplication");
            declaredField.setAccessible(true);
            declaredField.set(ACTIVITY_THREAD, applicationNewApplication);
        } catch (Throwable th) {
            Util.log("FakeCtx", "fillAppContext skipped", th);
        }
    }

    private static Context getSystemContextQuiet() {
        Context context;
        Object obj = ACTIVITY_THREAD;
        if (obj != null) {
            try {
                Context context2 = (Context) ACTIVITY_THREAD_CLASS.getDeclaredMethod("getSystemContext", null).invoke(obj, null);
                if (context2 != null) {
                    return context2;
                }
            } catch (Throwable unused) {
            }
        }
        Class<?> cls = ACTIVITY_THREAD_CLASS;
        if (cls != null) {
            try {
                Object objInvoke = cls.getMethod("currentActivityThread", null).invoke(null, null);
                if (objInvoke != null) {
                    Context context3 = (Context) cls.getMethod("getSystemContext", null).invoke(objInvoke, null);
                    if (context3 != null) {
                        return context3;
                    }
                }
            } catch (Throwable unused2) {
            }
        }
        try {
            context = (Context) Class.forName("android.app.ContextImpl").getMethod("createSystemContext", Class.forName("android.app.ActivityThread")).invoke(null, ACTIVITY_THREAD);
        } catch (Throwable unused3) {
        }
        if (context != null) {
            return context;
        }
        return null;
    }

    public static FakeContext get() {
        return INSTANCE;
    }

    private FakeContext(Context context) {
        super(context);
    }

    public boolean usable() {
        return getBaseContext() != null;
    }

    public static Throwable getInitError() {
        return INIT_ERROR;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public String getPackageName() {
        return PACKAGE_NAME;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public String getOpPackageName() {
        return PACKAGE_NAME;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String str) {
        Context baseContext = getBaseContext();
        if (baseContext == null) {
            throw new IllegalStateException("FakeContext base is null (init error: " + INIT_ERROR + ")");
        }
        Object systemService = baseContext.getSystemService(str);
        if (systemService == null) {
            return null;
        }
        try {
            for (Class<?> superclass = systemService.getClass(); superclass != null; superclass = superclass.getSuperclass()) {
                try {
                    Field declaredField = superclass.getDeclaredField("mContext");
                    declaredField.setAccessible(true);
                    declaredField.set(systemService, this);
                    break;
                } catch (NoSuchFieldException unused) {
                }
            }
        } catch (Throwable unused2) {
        }
        return systemService;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public ContentResolver getContentResolver() {
        Context baseContext = getBaseContext();
        if (baseContext == null) {
            throw new IllegalStateException("FakeContext base is null");
        }
        return baseContext.getContentResolver();
    }
}
