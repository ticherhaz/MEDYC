package com.ashrof.medyc.utils;

import android.annotation.SuppressLint;

import androidx.core.app.NotificationCompat;

public class GlobalNotificationBuilder {
    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder sGlobalNotificationCompatBuilder = null;

    /*
     * Empty constructor - We don't initialize builder because we rely on a null state to let us
     * know the Application's process was killed.
     */
    private GlobalNotificationBuilder() {
    }

    public static NotificationCompat.Builder getNotificationCompatBuilderInstance() {
        return sGlobalNotificationCompatBuilder;
    }

    static void setNotificationCompatBuilderInstance(NotificationCompat.Builder builder) {
        sGlobalNotificationCompatBuilder = builder;
    }
}
