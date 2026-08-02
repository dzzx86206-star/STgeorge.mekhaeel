package com.stgeorge.church.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

/**
 * Wraps a Context so its resources resolve in the user's chosen app language,
 * independent of the phone's system language. Used from
 * {@link com.stgeorge.church.activities.BaseActivity#attachBaseContext} so
 * every screen in the app honors the language chosen in الإعدادات (Settings).
 */
public final class LocaleHelper {

    private LocaleHelper() {
    }

    public static Context wrap(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(configuration);
        } else {
            context.getResources().updateConfiguration(configuration,
                    context.getResources().getDisplayMetrics());
            return context;
        }
    }
}
