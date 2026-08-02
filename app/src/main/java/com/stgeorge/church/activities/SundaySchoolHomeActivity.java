package com.stgeorge.church.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.stgeorge.church.R;
import com.stgeorge.church.adapters.SchoolClassAdapter;
import com.stgeorge.church.firebase.AdminUserRepository;
import com.stgeorge.church.firebase.SchoolClassRepository;
import com.stgeorge.church.helpers.SessionManager;
import com.stgeorge.church.models.SchoolClass;
import com.stgeorge.church.models.User;
import com.stgeorge.church.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * مدارس الأحد — section entry point.
 * Manager/priest see every class; a servant sees only the classes they're
 * assigned to (see {@link SchoolClassRepository#getAssignedTo}). Only the
 * manager can create/edit/delete classes and assign servants to them —
 * matching section 3 of the spec, which frames class setup as the manager's job.
 */
public class SundaySchoolHomeActivity extends BaseActivity {

    private RecyclerView rvClasses;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private ProgressBar progressLoading;

    private final SchoolClassRepository classRepository = new SchoolClassRepository();
    private final List<SchoolClass> classes = new ArrayList<>();
    private SchoolClassAdapter adapter;

    private String role;
    private String userId;
    private boolean canManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sunday_school_home);

        SessionManager sessionManager = new SessionManager(this);
        role = sessionManager.getUserRole();
        userId = sessionManager.getUserId();
        canManage = Constants.ROLE_MANAGER.equals(role);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.nav_sunday_school);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvClasses = findViewById(R.id.rvClasses);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressLoading = findViewById(R.id.progressLoading);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SchoolClassAdapter(classes, canManage, new SchoolClassAdapter.Listener() {
            @Override
            public void onOpen(SchoolClass schoolClass) {
                startActivity(ClassDetailActivity.newIntent(SundaySchoolHomeActivity.this, schoolClass));
            }

            @Override
            public void onEdit(SchoolClass schoolClass) {
                showClassDialog(schoolClass);
            }

            @Override
            public void onDelete(SchoolClass schoolClass) {
                confirmDeleteClass(schoolClass);
            }
        });
        rvClasses.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadClasses);
        fabAdd.setVisibility(canManage ? View.VISIBLE : View.GONE);
        fabAdd.setOnClickListener(v -> showClassDialog(null));

        loadClasses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClasses();
    }

    private void loadClasses() {
        swipeRefresh.setRefreshing(true);
        progressLoading.setVisibility(classes.isEmpty() ? View.VISIBLE : View.GONE);

        SchoolClassRepository.ListCallback callback = new SchoolClassRepository.ListCallback() {
            @Override
            public void onSuccess(List<SchoolClass> result) {
                swipeRefresh.setRefreshing(false);
                progressLoading.setVisibility(View.GONE);
                classes.clear();
                classes.addAll(result);
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(classes.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(String message) {
                swipeRefresh.setRefreshing(false);
                progressLoading.setVisibility(View.GONE);
                Toast.makeText(SundaySchoolHomeActivity.this, R.string.generic_load_error, Toast.LENGTH_SHORT).show();
            }
        };

        if (Constants.ROLE_SERVANT.equals(role)) {
            classRepository.getAssignedTo(userId, callback);
        } else {
            classRepository.getAll(callback);
        }
    }

    private void showClassDialog(SchoolClass existing) {
        View formView = getLayoutInflater().inflate(R.layout.dialog_class_form, null);
        Spinner spinnerStage = formView.findViewById(R.id.spinnerStage);
        android.widget.LinearLayout layoutServantCheckboxes = formView.findViewById(R.id.layoutServantCheckboxes);
        TextView tvNoServants = formView.findViewById(R.id.tvNoServants);

        ArrayAdapter<String> stageAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Constants.SUNDAY_SCHOOL_STAGES);
        spinnerStage.setAdapter(stageAdapter);
        if (existing != null && existing.getName() != null) {
            int index = java.util.Arrays.asList(Constants.SUNDAY_SCHOOL_STAGES).indexOf(existing.getName());
            if (index >= 0) {
                spinnerStage.setSelection(index);
            }
        }

        new AdminUserRepository(this).getAllServants(new AdminUserRepository.ListCallback() {
            @Override
            public void onSuccess(List<User> servants) {
                tvNoServants.setVisibility(servants.isEmpty() ? View.VISIBLE : View.GONE);
                List<CheckBox> checkBoxes = new ArrayList<>();
                for (User servant : servants) {
                    CheckBox checkBox = new CheckBox(SundaySchoolHomeActivity.this);
                    checkBox.setText(servant.getFullName());
                    checkBox.setTag(servant.getUserId());
                    if (existing != null && existing.getAssignedServantIds() != null
                            && existing.getAssignedServantIds().contains(servant.getUserId())) {
                        checkBox.setChecked(true);
                    }
                    layoutServantCheckboxes.addView(checkBox);
                    checkBoxes.add(checkBox);
                }

                new MaterialAlertDialogBuilder(SundaySchoolHomeActivity.this)
                        .setTitle(existing == null ? R.string.class_add_title : R.string.class_edit_title)
                        .setView(formView)
                        .setPositiveButton(R.string.save, (dialog, which) -> {
                            String stage = (String) spinnerStage.getSelectedItem();
                            List<String> selectedServantIds = new ArrayList<>();
                            for (CheckBox checkBox : checkBoxes) {
                                if (checkBox.isChecked()) {
                                    selectedServantIds.add((String) checkBox.getTag());
                                }
                            }
                            saveClass(existing, stage, selectedServantIds);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(SundaySchoolHomeActivity.this, R.string.generic_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveClass(SchoolClass existing, String stage, List<String> servantIds) {
        SchoolClass schoolClass = existing != null ? existing : new SchoolClass();
        schoolClass.setName(stage);
        schoolClass.setAssignedServantIds(servantIds);

        SchoolClassRepository.WriteCallback callback = new SchoolClassRepository.WriteCallback() {
            @Override
            public void onSuccess() {
                loadClasses();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(SundaySchoolHomeActivity.this, R.string.generic_save_error, Toast.LENGTH_SHORT).show();
            }
        };

        if (existing != null) {
            classRepository.update(schoolClass, callback);
        } else {
            classRepository.add(schoolClass, callback);
        }
    }

    private void confirmDeleteClass(SchoolClass schoolClass) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.class_delete_confirm_title)
                .setMessage(schoolClass.getName())
                .setPositiveButton(R.string.delete, (dialog, which) ->
                        classRepository.delete(schoolClass.getId(), new SchoolClassRepository.WriteCallback() {
                            @Override
                            public void onSuccess() {
                                loadClasses();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(SundaySchoolHomeActivity.this,
                                        R.string.generic_delete_error, Toast.LENGTH_SHORT).show();
                            }
                        }))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
