package com.stgeorge.church.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.models.AgpeyaHour;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the eight Agpeya prayer hours as a simple, tappable list.
 */
public class AgpeyaHourAdapter extends RecyclerView.Adapter<AgpeyaHourAdapter.HourViewHolder> {

    public interface OnHourClickListener {
        void onHourClick(AgpeyaHour hour);
    }

    private final List<AgpeyaHour> hours = new ArrayList<>();
    private final OnHourClickListener listener;

    public AgpeyaHourAdapter(OnHourClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<AgpeyaHour> newHours) {
        hours.clear();
        hours.addAll(newHours);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agpeya_hour, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        AgpeyaHour hour = hours.get(position);
        holder.tvOrder.setText(String.valueOf(hour.getOrder()));
        holder.tvTitle.setText(hour.getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHourClick(hour);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    static class HourViewHolder extends RecyclerView.ViewHolder {
        final TextView tvOrder;
        final TextView tvTitle;

        HourViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrder = itemView.findViewById(R.id.tvHourOrder);
            tvTitle = itemView.findViewById(R.id.tvHourTitle);
        }
    }
}
