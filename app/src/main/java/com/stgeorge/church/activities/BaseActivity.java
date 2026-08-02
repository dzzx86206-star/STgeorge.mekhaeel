package com.stgeorge.church.activities;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.stgeorge.church.utils.AppPreferences;
import com.stgeorge.church.utils.LocaleHelper;

/**
 * Common superclass for every Activity in the app.
 *
 * Applying the saved language here (rather than in each Activity) means the
 * "دعم لغتين" (Arabic/English) setting takes effect everywhere the moment it's
 * changed in الإعدادات, without having to remember to wire it into every screen.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        AppPreferences prefs = new AppPreferences(newBase);
        super.attachBaseContext(LocaleHelper.wrap(newBase, prefs.getLanguage()));
    }
}
