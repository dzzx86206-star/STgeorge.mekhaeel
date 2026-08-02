package com.stgeorge.church.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.stgeorge.church.models.User;
import com.stgeorge.church.utils.Constants;

/**
 * Persists the currently logged-in user's basic info locally so the app
 * can restore the session (and route to the right role-based screen)
 * without hitting Firestore again on every launch.
 */
public class SessionManager {

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(User user) {
        prefs.edit()
                .putString(Constants.PREF_USER_ID, user.getUserId())
                .putString(Constants.PREF_USER_ROLE, user.getRole())
                .putString(Constants.PREF_USER_NAME, user.getFullName())
                .apply();
    }

    public String getUserId() {
        return prefs.getString(Constants.PREF_USER_ID, null);
    }

    public String getUserRole() {
        return prefs.getString(Constants.PREF_USER_ROLE, null);
    }

    public String getUserName() {
        return prefs.getString(Constants.PREF_USER_NAME, null);
    }

    public boolean hasSession() {
        return getUserId() != null;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
