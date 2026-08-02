package com.stgeorge.church.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.stgeorge.church.R;
import com.stgeorge.church.firebase.AuthHelper;
import com.stgeorge.church.helpers.SessionManager;
import com.stgeorge.church.utils.AppPreferences;
import com.stgeorge.church.utils.Constants;

/**
 * الإعدادات — language (ar/en), light/dark theme, reading font size,
 * change password, and logout. Two of the "recommended additions" from the
 * spec (دعم لغتين + دعم الثيم الفاتح والداكن) are fully implemented here.
 */
public class SettingsActivity extends BaseActivity {

    private AppPreferences appPreferences;
    private RadioGroup radioGroupLanguage;
    private RadioGroup radioGroupTheme;
    private SeekBar seekFontScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        appPreferences = new AppPreferences(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        radioGroupLanguage = findViewById(R.id.radioGroupLanguage);
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        seekFontScale = findViewById(R.id.seekFontScale);
        MaterialButton btnChangePassword = findViewById(R.id.btnChangePassword);
        MaterialButton btnLogout = findViewById(R.id.btnLogoutSettings);

        setUpLanguageSelection();
        setUpThemeSelection();
        setUpFontScale();

        btnChangePassword.setOnClickListener(v -> sendPasswordReset());
        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void setUpLanguageSelection() {
        boolean isArabic = Constants.LANGUAGE_ARABIC.equals(appPreferences.getLanguage());
        radioGroupLanguage.check(isArabic ? R.id.radioArabic : R.id.radioEnglish);

        radioGroupLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            String newLanguage = checkedId == R.id.radioArabic
                    ? Constants.LANGUAGE_ARABIC : Constants.LANGUAGE_ENGLISH;
            if (!newLanguage.equals(appPreferences.getLanguage())) {
                appPreferences.setLanguage(newLanguage);
                restartToApply();
            }
        });
    }

    private void setUpThemeSelection() {
        String mode = appPreferences.getNightMode();
        int checkedId = R.id.radioThemeSystem;
        if (Constants.NIGHT_MODE_LIGHT.equals(mode)) {
            checkedId = R.id.radioThemeLight;
        } else if (Constants.NIGHT_MODE_DARK.equals(mode)) {
            checkedId = R.id.radioThemeDark;
        }
        radioGroupTheme.check(checkedId);

        radioGroupTheme.setOnCheckedChangeListener((group, checkedButtonId) -> {
            String newMode;
            if (checkedButtonId == R.id.radioThemeLight) {
                newMode = Constants.NIGHT_MODE_LIGHT;
            } else if (checkedButtonId == R.id.radioThemeDark) {
                newMode = Constants.NIGHT_MODE_DARK;
            } else {
                newMode = Constants.NIGHT_MODE_SYSTEM;
            }
            appPreferences.setNightMode(newMode);
            appPreferences.applyNightMode();
        });
    }

    private void setUpFontScale() {
        // SeekBar 0..8 maps to a 0.8x .. 1.6x scale (0.1 steps), applied by the
        // Agpeya/Bible readers when rendering prayer/verse text.
        float current = appPreferences.getFontScale();
        int progress = Math.round((current - 0.8f) / 0.1f);
        seekFontScale.setProgress(Math.max(0, Math.min(8, progress)));

        seekFontScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    appPreferences.setFontScale(0.8f + (progress * 0.1f));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void sendPasswordReset() {
        SessionManager sessionManager = new SessionManager(this);
        String username = sessionManager.getUserName();
        new AuthHelper().sendPasswordReset(username, new AuthHelper.ResetCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(SettingsActivity.this, R.string.reset_success, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(SettingsActivity.this, R.string.reset_error_generic, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.settings_logout)
                .setPositiveButton(R.string.settings_logout, (dialog, which) -> doLogout())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void doLogout() {
        new AuthHelper().logout();
        new SessionManager(this).clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void restartToApply() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
