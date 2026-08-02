package com.stgeorge.church.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.FirebaseApp;
import com.stgeorge.church.R;

/**
 * Application entry point.
 * Initializes Firebase and sets up the default notification channel
 * used by Firebase Cloud Messaging (FCM) for church-wide announcements.
 */
public class STgeorgeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        createNotificationChannel();
        new AppPreferences(this).applyNightMode();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    "إشعارات الكنيسة",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("إشعارات الأخبار والإعلانات والمناسبات الكنسية");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
