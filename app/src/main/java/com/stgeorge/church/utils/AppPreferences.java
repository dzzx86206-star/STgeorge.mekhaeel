package com.stgeorge.church.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Stores the user's app-wide preferences: language (ar/en), light/dark theme
 * override, and the reading font scale used by the Agpeya and Bible readers.
 *
 * These are intentionally kept in their own SharedPreferences file (separate
 * from {@link com.stgeorge.church.helpers.SessionManager}) so logging out
 * never resets a person's language or theme choice.
 */
public class AppPreferences {

    private final SharedPreferences prefs;

    public AppPreferences(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(Constants.APP_PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ---- Language ----

    public String getLanguage() {
        return prefs.getString(Constants.PREF_LANGUAGE, Constants.LANGUAGE_ARABIC);
    }

    public void setLanguage(String languageCode) {
        prefs.edit().putString(Constants.PREF_LANGUAGE, languageCode).apply();
    }

    // ---- Night mode ----

    public String getNightMode() {
        return prefs.getString(Constants.PREF_NIGHT_MODE, Constants.NIGHT_MODE_SYSTEM);
    }

    public void setNightMode(String mode) {
        prefs.edit().putString(Constants.PREF_NIGHT_MODE, mode).apply();
    }

    /** Applies the saved night-mode preference app-wide. Call this once at app startup. */
    public void applyNightMode() {
        switch (getNightMode()) {
            case Constants.NIGHT_MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case Constants.NIGHT_MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    // ---- Font scale (كتاب الأجبية / الكتاب المقدس) ----

    public float getFontScale() {
        return prefs.getFloat(Constants.PREF_FONT_SCALE, 1.0f);
    }

    public void setFontScale(float scale) {
        prefs.edit().putFloat(Constants.PREF_FONT_SCALE, scale).apply();
    }
}
