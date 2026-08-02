package com.stgeorge.church.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.stgeorge.church.R;
import com.stgeorge.church.models.Child;

import java.util.List;
import java.util.Map;

/**
 * الحضور الأسبوعي — one row per child with a Present/Absent toggle.
 * `statusByChildId` holds the in-progress selections (true = present,
 * false = absent, missing = not yet marked) until "حفظ الحضور" is tapped.
 */
public class AttendanceChildAdapter extends RecyclerView.Adapter<AttendanceChildAdapter.ViewHolder> {

    private final List<Child> items;
    private final Map<String, Boolean> statusByChildId;

    public AttendanceChildAdapter(List<Child> items, Map<String, Boolean> statusByChildId) {
        this.items = items;
        this.statusByChildId = statusByChildId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Child child = items.get(position);
        holder.tvChildName.setText(child.getName());

        holder.toggleGroup.clearOnButtonCheckedListeners();
        Boolean status = statusByChildId.get(child.getId());
        holder.toggleGroup.clearChecked();
        if (Boolean.TRUE.equals(status)) {
            holder.toggleGroup.check(R.id.btnPresent);
        } else if (Boolean.FALSE.equals(status)) {
            holder.toggleGroup.check(R.id.btnAbsent);
        }

        holder.toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            statusByChildId.put(child.getId(), checkedId == R.id.btnPresent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvChildName;
        MaterialButtonToggleGroup toggleGroup;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChildName = itemView.findViewById(R.id.tvChildName);
            toggleGroup = itemView.findViewById(R.id.toggleGroup);
        }
    }
}
