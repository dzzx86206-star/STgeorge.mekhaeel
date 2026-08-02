package com.stgeorge.church.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


import com.stgeorge.church.helpers.SessionManager;

/**
 * Brief splash screen that checks whether a session already exists
 * (SessionManager) and routes to MainActivity or LoginActivity accordingly.
 */
public class SplashActivity extends BaseActivity {

    private static final long SPLASH_DELAY_MS = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = sessionManager.hasSession()
                    ? new Intent(this, MainActivity.class)
                    : new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
