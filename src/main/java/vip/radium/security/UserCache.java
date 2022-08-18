package vip.radium.security;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class UserCache {
    public static String username;
    public static int uid;

    private UserCache() {
    }

    public static void init() {
        username = ReflectionHelper.getUsername();
        uid = ReflectionHelper.getUid();
        ReflectionHelper.refresh();
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            if (!ReflectionHelper.refreshed) {
                ReflectionHelper.exit();
            }
        }, 1L, 1L, TimeUnit.MINUTES);
    }

    public static String getUsername() {
        return username;
    }

    public static int getUid() {
        return uid;
    }
}
