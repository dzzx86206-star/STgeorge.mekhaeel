package com.stgeorge.church.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.stgeorge.church.R;
import com.stgeorge.church.adapters.NavSectionAdapter;
import com.stgeorge.church.firebase.AuthHelper;
import com.stgeorge.church.fragments.HomeFragment;
import com.stgeorge.church.helpers.SessionManager;
import com.stgeorge.church.models.NavSection;
import com.stgeorge.church.utils.NavProvider;

import java.util.List;

/**
 * The app's home/navigation shell. Every signed-in role lands here after
 * LoginActivity/SplashActivity and sees a nav drawer built from
 * {@link NavProvider} — the single place that decides which of the spec's
 * sections each role (manager/priest/servant/member) is allowed to see.
 *
 * "home" is shown as a Fragment inside this Activity (so switching back to
 * it doesn't relaunch anything); every other section opens either a real
 * screen (Agpeya, Bible, Announcements, Settings) or {@link ComingSoonActivity}
 * for sections still pending their own build stage.
 */
public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView rvDrawerSections;
    private NavSectionAdapter adapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.nav_home);
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        TextView tvDrawerUserName = findViewById(R.id.tvDrawerUserName);
        TextView tvDrawerUserRole = findViewById(R.id.tvDrawerUserRole);
        tvDrawerUserName.setText(sessionManager.getUserName());
        tvDrawerUserRole.setText(roleLabel(sessionManager.getUserRole()));

        rvDrawerSections = findViewById(R.id.rvDrawerSections);
        rvDrawerSections.setLayoutManager(new LinearLayoutManager(this));

        List<NavSection> sections = NavProvider.getSectionsFor(this, sessionManager.getUserRole());
        adapter = new NavSectionAdapter(sections, this::openSection);
        rvDrawerSections.setAdapter(adapter);
        adapter.setSelectedId("home");

        LinearLayout btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> confirmLogout());

        if (savedInstanceState == null) {
            showHomeFragment();
        }
    }

    private void openSection(NavSection section) {
        drawerLayout.closeDrawers();
        adapter.setSelectedId(section.getId());

        switch (section.getId()) {
            case "home":
                showHomeFragment();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(section.getTitle());
                }
                return;
            case "agpeya":
                startActivity(new Intent(this, AgpeyaListActivity.class));
                return;
            case "bible":
                startActivity(new Intent(this, TafsirHomeActivity.class));
                return;
            case "announcements":
                startActivity(new Intent(this, AnnouncementsListActivity.class));
                return;
            case "sunday_school":
                startActivity(new Intent(this, SundaySchoolHomeActivity.class));
                return;
            case "user_management":
                startActivity(new Intent(this, UserManagementActivity.class));
                return;
            case "settings":
                startActivity(new Intent(this, SettingsActivity.class));
                return;
            default:
                // Sections from the spec that don't have their real screen yet —
                // see ComingSoonActivity's javadoc for the build-stage plan.
                startActivity(ComingSoonActivity.newIntent(this, section.getTitle()));
        }
    }

    private void showHomeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, new HomeFragment())
                .commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.nav_home);
        }
    }

    private String roleLabel(String role) {
        if (role == null) {
            return "";
        }
        switch (role) {
            case com.stgeorge.church.utils.Constants.ROLE_MANAGER:
                return getString(R.string.role_manager);
            case com.stgeorge.church.utils.Constants.ROLE_PRIEST:
                return getString(R.string.role_priest);
            case com.stgeorge.church.utils.Constants.ROLE_SERVANT:
                return getString(R.string.role_servant);
            default:
                return getString(R.string.role_member);
        }
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
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
