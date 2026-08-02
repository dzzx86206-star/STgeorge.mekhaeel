package com.stgeorge.church.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.adapters.AttendanceHistoryAdapter;
import com.stgeorge.church.firebase.AttendanceRepository;
import com.stgeorge.church.models.AttendanceSession;
import com.stgeorge.church.models.Child;

import java.util.ArrayList;
import java.util.List;

/** سجل الطفل — كل جمعة حضر أو غاب فيها الطفل، مع الإجمالي. */
public class ChildRecordActivity extends BaseActivity {

    private static final String EXTRA_CHILD_ID = "extra_child_id";
    private static final String EXTRA_CHILD_NAME = "extra_child_name";
    private static final String EXTRA_CLASS_ID = "extra_class_id";

    public static Intent newIntent(Context context, Child child) {
        Intent intent = new Intent(context, ChildRecordActivity.class);
        intent.putExtra(EXTRA_CHILD_ID, child.getId());
        intent.putExtra(EXTRA_CHILD_NAME, child.getName());
        intent.putExtra(EXTRA_CLASS_ID, child.getClassId());
        return intent;
    }

    private final AttendanceRepository attendanceRepository = new AttendanceRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_record);

        String childId = getIntent().getStringExtra(EXTRA_CHILD_ID);
        String childName = getIntent().getStringExtra(EXTRA_CHILD_NAME);
        String classId = getIntent().getStringExtra(EXTRA_CLASS_ID);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(childName);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvStatsSummary = findViewById(R.id.tvStatsSummary);
        TextView tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        attendanceRepository.getHistoryForClass(classId, new AttendanceRepository.ListCallback() {
            @Override
            public void onSuccess(List<AttendanceSession> sessions) {
                List<AttendanceHistoryAdapter.Entry> entries = new ArrayList<>();
                int presentCount = 0;
                int absentCount = 0;

                for (AttendanceSession session : sessions) {
                    boolean wasPresent = session.getPresentChildIds().contains(childId);
                    boolean wasAbsent = session.getAbsentChildIds().contains(childId);
                    if (wasPresent) {
                        entries.add(new AttendanceHistoryAdapter.Entry(session.getDateKey(), true));
                        presentCount++;
                    } else if (wasAbsent) {
                        entries.add(new AttendanceHistoryAdapter.Entry(session.getDateKey(), false));
                        absentCount++;
                    }
                }

                tvStatsSummary.setText(getString(R.string.child_record_stats_format, presentCount, absentCount));
                rvHistory.setAdapter(new AttendanceHistoryAdapter(entries));
                tvEmpty.setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(ChildRecordActivity.this, R.string.generic_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
