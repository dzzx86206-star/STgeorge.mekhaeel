package com.stgeorge.church.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.stgeorge.church.R;

/**
 * Generic "قيد التطوير" placeholder used by every section from the spec that
 * doesn't have a real screen yet (News, Events, Library, Hymns, Coptic Rite,
 * Sunday School, Servant Notebook, Visitation, Communication, Live Stream,
 * Church Location, Donations, Admin Dashboard, Reports...).
 *
 * This keeps the full navigation drawer matching the spec 1:1 today — every
 * role sees every section they're entitled to — while making it obvious
 * which ones still need their real Firestore-backed screen built. Each of
 * these becomes its own Activity + repository (following AnnouncementsListActivity
 * as the reference pattern) in the next build stages.
 */
public class ComingSoonActivity extends BaseActivity {

    private static final String EXTRA_TITLE = "extra_title";

    public static Intent newIntent(Context context, String title) {
        Intent intent = new Intent(context, ComingSoonActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        toolbar.setTitle(title != null ? title : getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}
