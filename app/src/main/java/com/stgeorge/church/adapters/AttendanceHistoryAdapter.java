package com.stgeorge.church.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.models.AttendanceSession;
import com.stgeorge.church.utils.Constants;

import java.util.List;

/** سجل الطفل — one row per Friday session this child appeared in (present or absent). */
public class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder> {

    public static class Entry {
        public final String dateKey;
        public final boolean present;

        public Entry(String dateKey, boolean present) {
            this.dateKey = dateKey;
            this.present = present;
        }
    }

    private final List<Entry> entries;

    public AttendanceHistoryAdapter(List<Entry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.tvDate.setText(entry.dateKey);
        holder.tvStatus.setText(entry.present ? R.string.attendance_present : R.string.attendance_absent);
        holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(
                entry.present ? com.stgeorge.church.R.color.md_secondary : com.stgeorge.church.R.color.md_error));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
