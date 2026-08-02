package com.stgeorge.church.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.adapters.AttendanceChildAdapter;
import com.stgeorge.church.firebase.AttendanceRepository;
import com.stgeorge.church.firebase.ChildRepository;
import com.stgeorge.church.helpers.SessionManager;
import com.stgeorge.church.models.AttendanceSession;
import com.stgeorge.church.models.Child;
import com.stgeorge.church.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * الحضور الأسبوعي — opens straight to this week's Friday (see
 * {@link DateUtils#getCurrentFriday}), lists every child in the class with a
 * Present/Absent toggle, and on save shows the totals/percentage from
 * section 6 of the spec. Re-opening the same Friday loads whatever was saved
 * before so it can be corrected rather than duplicated.
 */
public class AttendanceActivity extends BaseActivity {

    private static final String EXTRA_CLASS_ID = "extra_class_id";
    private static final String EXTRA_CLASS_NAME = "extra_class_name";

    public static Intent newIntent(Context context, String classId, String className) {
        Intent intent = new Intent(context, AttendanceActivity.class);
        intent.putExtra(EXTRA_CLASS_ID, classId);
        intent.putExtra(EXTRA_CLASS_NAME, className);
        return intent;
    }

    private final ChildRepository childRepository = new ChildRepository();
    private final AttendanceRepository attendanceRepository = new AttendanceRepository();
    private final List<Child> children = new ArrayList<>();
    private final Map<String, Boolean> statusByChildId = new HashMap<>();
    private AttendanceChildAdapter adapter;

    private String classId;
    private String dateKey;

    private View layoutStats;
    private TextView tvStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        classId = getIntent().getStringExtra(EXTRA_CLASS_ID);
        String className = getIntent().getStringExtra(EXTRA_CLASS_NAME);

        Date friday = DateUtils.getCurrentFriday();
        dateKey = DateUtils.toDateKey(friday);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(className);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvDateHeader = findViewById(R.id.tvDateHeader);
        tvDateHeader.setText(DateUtils.toDisplayString(friday));

        RecyclerView rvAttendance = findViewById(R.id.rvAttendance);
        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceChildAdapter(children, statusByChildId);
        rvAttendance.setAdapter(adapter);

        layoutStats = findViewById(R.id.layoutStats);
        tvStats = findViewById(R.id.tvStats);

        MaterialButton btnSave = findViewById(R.id.btnSaveAttendance);
        btnSave.setOnClickListener(v -> saveAttendance());

        loadChildrenThenExistingSession();
    }

    private void loadChildrenThenExistingSession() {
        childRepository.getByClass(classId, new ChildRepository.ListCallback() {
            @Override
            public void onSuccess(List<Child> result) {
                children.clear();
                children.addAll(result);

                attendanceRepository.getSession(classId, dateKey, new AttendanceRepository.SessionCallback() {
                    @Override
                    public void onSuccess(AttendanceSession session) {
                        statusByChildId.clear();
                        if (session != null) {
                            for (String id : session.getPresentChildIds()) {
                                statusByChildId.put(id, true);
                            }
                            for (String id : session.getAbsentChildIds()) {
                                statusByChildId.put(id, false);
                            }
                            showStats(session.getTotalCount(), session.getPresentCount(), session.getAbsentCount());
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String message) {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(AttendanceActivity.this, R.string.generic_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAttendance() {
        if (children.isEmpty()) {
            return;
        }

        List<String> present = new ArrayList<>();
        List<String> absent = new ArrayList<>();
        for (Child child : children) {
            Boolean status = statusByChildId.get(child.getId());
            if (Boolean.TRUE.equals(status)) {
                present.add(child.getId());
            } else if (Boolean.FALSE.equals(status)) {
                absent.add(child.getId());
            }
        }

        if (present.size() + absent.size() < children.size()) {
            Toast.makeText(this, R.string.attendance_error_incomplete, Toast.LENGTH_SHORT).show();
        }

        SessionManager sessionManager = new SessionManager(this);
        AttendanceSession session = new AttendanceSession();
        session.setClassId(classId);
        session.setDateKey(dateKey);
        session.setPresentChildIds(present);
        session.setAbsentChildIds(absent);
        session.setTakenByUserId(sessionManager.getUserId());
        session.setTakenByName(sessionManager.getUserName());

        attendanceRepository.saveSession(session, new AttendanceRepository.WriteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AttendanceActivity.this, R.string.attendance_saved, Toast.LENGTH_SHORT).show();
                showStats(present.size() + absent.size(), present.size(), absent.size());
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(AttendanceActivity.this, R.string.generic_save_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showStats(int total, int present, int absent) {
        layoutStats.setVisibility(View.VISIBLE);
        String percentage = total > 0
                ? String.format(Locale.US, "%.0f", (present * 100.0) / total)
                : "0";
        tvStats.setText(getString(R.string.attendance_stats_format, total, present, absent, percentage));
    }
}
