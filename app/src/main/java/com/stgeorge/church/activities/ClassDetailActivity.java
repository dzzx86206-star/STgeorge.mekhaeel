package com.stgeorge.church.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import com.stgeorge.church.adapters.ChildAdapter;
import com.stgeorge.church.firebase.ChildRepository;
import com.stgeorge.church.models.Child;
import com.stgeorge.church.models.SchoolClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/** داخل كل فصل — إدارة الأطفال (إضافة/تعديل/حذف/بحث) + الدخول على الحضور وسجل الطفل. */
public class ClassDetailActivity extends BaseActivity {

    private static final String EXTRA_CLASS_ID = "extra_class_id";
    private static final String EXTRA_CLASS_NAME = "extra_class_name";

    public static Intent newIntent(Context context, SchoolClass schoolClass) {
        Intent intent = new Intent(context, ClassDetailActivity.class);
        intent.putExtra(EXTRA_CLASS_ID, schoolClass.getId());
        intent.putExtra(EXTRA_CLASS_NAME, schoolClass.getName());
        return intent;
    }

    private RecyclerView rvChildren;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;
    private ProgressBar progressLoading;

    private final ChildRepository childRepository = new ChildRepository();
    private final List<Child> children = new ArrayList<>();
    private ChildAdapter adapter;

    private String classId;
    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        classId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        className = getIntent().getStringExtra(EXTRA_CLASS_NAME);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(className);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        MaterialButton btnTakeAttendance = findViewById(R.id.btnTakeAttendance);
        btnTakeAttendance.setOnClickListener(v ->
                startActivity(AttendanceActivity.newIntent(this, classId, className)));

        TextInputEditText etSearch = findViewById(R.id.etSearch);
        rvChildren = findViewById(R.id.rvChildren);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressLoading = findViewById(R.id.progressLoading);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        rvChildren.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChildAdapter(children, new ChildAdapter.Listener() {
            @Override
            public void onOpen(Child child) {
                startActivity(ChildRecordActivity.newIntent(ClassDetailActivity.this, child));
            }

            @Override
            public void onEdit(Child child) {
                showChildDialog(child);
            }

            @Override
            public void onDelete(Child child) {
                confirmDeleteChild(child);
            }
        });
        rvChildren.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadChildren);
        fabAdd.setOnClickListener(v -> showChildDialog(null));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    loadChildren();
                } else {
                    searchChildren(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadChildren();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildren();
    }

    private void loadChildren() {
        swipeRefresh.setRefreshing(true);
        progressLoading.setVisibility(children.isEmpty() ? View.VISIBLE : View.GONE);

        childRepository.getByClass(classId, new ChildRepository.ListCallback() {
            @Override
            public void onSuccess(List<Child> result) {
                applyResult(result);
            }

            @Override
            public void onFailure(String message) {
                swipeRefresh.setRefreshing(false);
                progressLoading.setVisibility(View.GONE);
                Toast.makeText(ClassDetailActivity.this, R.string.generic_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchChildren(String query) {
        childRepository.search(classId, query, new ChildRepository.ListCallback() {
            @Override
            public void onSuccess(List<Child> result) {
                applyResult(result);
            }

            @Override
            public void onFailure(String message) {
                // Keep showing the last known list on a transient search error.
            }
        });
    }

    private void applyResult(List<Child> result) {
        swipeRefresh.setRefreshing(false);
        progressLoading.setVisibility(View.GONE);
        children.clear();
        children.addAll(result);
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(children.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showChildDialog(Child existing) {
        View formView = getLayoutInflater().inflate(R.layout.dialog_child_form, null);
        TextInputEditText etName = formView.findViewById(R.id.etName);
        TextInputEditText etGuardianPhone = formView.findViewById(R.id.etGuardianPhone);
        TextInputEditText etBirthDate = formView.findViewById(R.id.etBirthDate);
        TextInputEditText etNotes = formView.findViewById(R.id.etNotes);

        if (existing != null) {
            etName.setText(existing.getName());
            etGuardianPhone.setText(existing.getGuardianPhone());
            etBirthDate.setText(existing.getBirthDate());
            etNotes.setText(existing.getNotes());
        }

        etBirthDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new android.app.DatePickerDialog(this, (view, year, month, day) -> {
                String value = String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                etBirthDate.setText(value);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        new MaterialAlertDialogBuilder(this)
                .setTitle(existing == null ? R.string.child_add_title : R.string.child_edit_title)
                .setView(formView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String name = textOf(etName);
                    if (name.isEmpty()) {
                        Toast.makeText(this, R.string.child_error_name_empty, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Child child = existing != null ? existing : new Child();
                    child.setName(name);
                    child.setClassId(classId);
                    child.setStage(className);
                    child.setGuardianPhone(textOf(etGuardianPhone));
                    child.setBirthDate(textOf(etBirthDate));
                    child.setNotes(textOf(etNotes));
                    saveChild(existing, child);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private String textOf(TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }

    private void saveChild(Child existing, Child child) {
        ChildRepository.WriteCallback callback = new ChildRepository.WriteCallback() {
            @Override
            public void onSuccess() {
                loadChildren();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(ClassDetailActivity.this, R.string.generic_save_error, Toast.LENGTH_SHORT).show();
            }
        };
        if (existing != null) {
            childRepository.update(child, callback);
        } else {
            childRepository.add(child, callback);
        }
    }

    private void confirmDeleteChild(Child child) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.child_delete_confirm_title)
                .setMessage(child.getName())
                .setPositiveButton(R.string.delete, (dialog, which) ->
                        childRepository.delete(child.getId(), new ChildRepository.WriteCallback() {
                            @Override
                            public void onSuccess() {
                                loadChildren();
                            }

                            @Override
                            public void onFailure(String message) {
                                Toast.makeText(ClassDetailActivity.this,
                                        R.string.generic_delete_error, Toast.LENGTH_SHORT).show();
                            }
                        }))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
