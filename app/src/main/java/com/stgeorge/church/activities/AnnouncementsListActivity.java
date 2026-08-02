package com.stgeorge.church.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.stgeorge.church.R;
import com.stgeorge.church.adapters.AnnouncementAdapter;
import com.stgeorge.church.firebase.AnnouncementRepository;
import com.stgeorge.church.helpers.SessionManager;
import com.stgeorge.church.models.Announcement;
import com.stgeorge.church.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * الإعلانات — section 3 of the spec, fully wired to Firestore:
 * list / add / edit / delete, with add-edit-delete restricted to staff
 * roles (manager, priest, servant) while members (المخدوم) get read-only
 * access, matching "الصلاحيات" in the spec.
 *
 * Automatic push notifications on a new announcement ("إشعارات تلقائية")
 * are meant to be sent from a Cloud Function triggered on document create —
 * that's server-side config, not something the Android client should do
 * directly with an admin FCM call.
 */
public class AnnouncementsListActivity extends BaseActivity {

    private RecyclerView rvAnnouncements;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private ProgressBar progressLoading;
    private FloatingActionButton fabAdd;

    private final AnnouncementRepository repository = new AnnouncementRepository();
    private final List<Announcement> announcements = new ArrayList<>();
    private AnnouncementAdapter adapter;
    private boolean canManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.nav_announcements);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        SessionManager sessionManager = new SessionManager(this);
        String role = sessionManager.getUserRole();
        canManage = Constants.ROLE_MANAGER.equals(role)
                || Constants.ROLE_PRIEST.equals(role)
                || Constants.ROLE_SERVANT.equals(role);

        rvAnnouncements = findViewById(R.id.rvAnnouncements);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressLoading = findViewById(R.id.progressLoading);
        fabAdd = findViewById(R.id.fabAdd);

        rvAnnouncements.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnnouncementAdapter(announcements, canManage, new AnnouncementAdapter.ActionListener() {
            @Override
            public void onEdit(Announcement announcement) {
                showAnnouncementDialog(announcement);
            }

            @Override
            public void onDelete(Announcement announcement) {
                confirmDelete(announcement);
            }
        });
        rvAnnouncements.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadAnnouncements);
        fabAdd.setVisibility(canManage ? View.VISIBLE : View.GONE);
        fabAdd.setOnClickListener(v -> showAnnouncementDialog(null));

        loadAnnouncements();
    }

    private void loadAnnouncements() {
        swipeRefresh.setRefreshing(true);
        progressLoading.setVisibility(announcements.isEmpty() ? View.VISIBLE : View.GONE);

        repository.getAll(new AnnouncementRepository.ListCallback() {
            @Override
            public void onSuccess(List<Announcement> result) {
                swipeRefresh.setRefreshing(false);
                progressLoading.setVisibility(View.GONE);
                announcements.clear();
                announcements.addAll(result);
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(announcements.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(String message) {
                swipeRefresh.setRefreshing(false);
                progressLoading.setVisibility(View.GONE);
                Toast.makeText(AnnouncementsListActivity.this,
                        R.string.generic_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAnnouncementDialog(Announcement existing) {
        View formView = getLayoutInflater().inflate(R.layout.dialog_announcement_form, null);
        TextInputEditText etTitle = formView.findViewById(R.id.etTitle);
        TextInputEditText etBody = formView.findViewById(R.id.etBody);

        if (existing != null) {
            etTitle.setText(existing.getTitle());
            etBody.setText(existing.getBody());
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(existing == null ? R.string.announcement_add_title : R.string.announcement_edit_title)
                .setView(formView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
                    String body = etBody.getText() != null ? etBody.getText().toString().trim() : "";
                    if (title.isEmpty() || body.isEmpty()) {
                        Toast.makeText(this, R.string.announcement_error_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (existing == null) {
                        createAnnouncement(title, body);
                    } else {
                        updateAnnouncement(existing.getId(), title, body);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void createAnnouncement(String title, String body) {
        SessionManager sessionManager = new SessionManager(this);
        Announcement announcement = new Announcement(
                title, body, sessionManager.getUserName(), sessionManager.getUserId());

        repository.add(announcement, new AnnouncementRepository.WriteCallback() {
            @Override
            public void onSuccess() {
                loadAnnouncements();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(AnnouncementsListActivity.this,
                        R.string.generic_save_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAnnouncement(String id, String title, String body) {
        repository.update(id, title, body, new AnnouncementRepository.WriteCallback() {
            @Override
            public void onSuccess() {
                loadAnnouncements();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(AnnouncementsListActivity.this,
                        R.string.generic_save_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(Announcement announcement) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.announcement_delete_confirm_title)
                .setMessage(announcement.getTitle())
                .setPositiveButton(R.string.delete, (dialog, which) ->
                        repository.delete(announcement.getId(), new AnnouncementRepository.WriteCallback() {
                            @Override
                            public void onSuccess() {
                                loadAnnouncements();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(AnnouncementsListActivity.this,
                                        R.string.generic_delete_error, Toast.LENGTH_SHORT).show();
                            }
                        }))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
