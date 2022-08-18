package vip.radium.security;

import vip.radium.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

public final class ReflectionHelper {
    private static final int EXIT_CODE = ThreadLocalRandom.current().nextInt(2147483647);
    private static final Class<?> CLASS_CLASS;
    private static final Class<?> API_CLASS;
    private static final Class<?> RUNTIME_CLASS;

    public static boolean refreshed;

    static {
        CLASS_CLASS = getClass(StringUtils.fromCharCodes(SecurityManager.CLASS_CLASS));
        RUNTIME_CLASS = getClass(StringUtils.fromCharCodes(SecurityManager.RUNTIME_CLASS_PATH));
        API_CLASS = getAPIClass(StringUtils.fromCharCodes(SecurityManager.API_CLASS_PATH));
    }

    private ReflectionHelper() {
    }

    private static Class<?> getAPIClass(String name) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(name);
        } catch (ClassNotFoundException var2) {
            exit();
            return null;
        }
    }

    private static Class<?> getClass(String name) {
        try {
            if (CLASS_CLASS == null) {
                return Class.forName(name);
            } else {
                Method forNameM = getMethod(StringUtils.fromCharCodes(SecurityManager.FOR_NAME_METHOD), CLASS_CLASS, String.class);
                return (Class) forNameM.invoke(null, name);
            }
        } catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException var2) {
            exit();
            return null;
        }
    }

    private static Method getMethod(String name, Class<?> parent, Class<?>... params) {
        try {
            return parent.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException var4) {
            exit();
            return null;
        }
    }

    public static void shutdown() {
        try {
            Method shutdownM = getMethod(StringUtils.fromCharCodes(SecurityManager.SHUTDOWN_METHOD), API_CLASS);
            shutdownM.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException var1) {
            shutdown();
        }

    }

    public static void exit() {
        try {
            Method exitM = getMethod(StringUtils.fromCharCodes(SecurityManager.EXIT_METHOD), RUNTIME_CLASS, Integer.TYPE);
            Method getRuntimeM = getMethod(StringUtils.fromCharCodes(SecurityManager.GET_RUNTIME_METHOD), RUNTIME_CLASS);
            exitM.invoke(getRuntimeM.invoke(null));
            Runtime.getRuntime().exit(EXIT_CODE);
        } catch (InvocationTargetException | IllegalAccessException var5) {
            System.exit(EXIT_CODE);
        } finally {
            shutdown();
            exit(); // If all else is blocked infinitely loop
        }
    }

    public static void refresh() {
        try {
            Method refreshM = getMethod(StringUtils.fromCharCodes(SecurityManager.REFRESH_METHOD), API_CLASS);
            refreshM.invoke(null);
            refreshed = true;
        } catch (InvocationTargetException | IllegalAccessException var1) {
            exit();
            refreshed = false;
            while (true) {
            }
        } finally {
            if (!refreshed) {
                exit();
                while (true) { }
            }
        }
    }

    public static String getUsername() {
        try {
            final Method getUsernameM = getMethod(StringUtils.fromCharCodes(
                    SecurityManager.GET_USERNAME_METHOD), API_CLASS);
            return (String) getUsernameM.invoke(null);
        } catch (Exception var2) {
            exit();
            return null;
        }
    }

    public static int getUid() {
        int retValue = -1;
        try {
            final Method getUidM = getMethod(StringUtils.fromCharCodes(SecurityManager.GET_UID_METHOD), API_CLASS);
            retValue = (Integer) getUidM.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException var6) {
            exit();
        } finally {
            if (retValue == -1)
                shutdown();
            while (true) {
            }
        }
    }
}
