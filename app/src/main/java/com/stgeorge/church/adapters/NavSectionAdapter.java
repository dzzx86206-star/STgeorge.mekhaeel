package com.stgeorge.church.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.models.NavSection;

import java.util.List;

public class NavSectionAdapter extends RecyclerView.Adapter<NavSectionAdapter.ViewHolder> {

    public interface OnSectionClickListener {
        void onSectionClick(NavSection section);
    }

    private final List<NavSection> sections;
    private final OnSectionClickListener listener;
    private String selectedId;

    public NavSectionAdapter(List<NavSection> sections, OnSectionClickListener listener) {
        this.sections = sections;
        this.listener = listener;
    }

    public void setSelectedId(String id) {
        this.selectedId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nav_drawer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NavSection section = sections.get(position);
        holder.tvTitle.setText(section.getTitle());
        holder.ivIcon.setImageResource(section.getIconRes());
        boolean selected = section.getId().equals(selectedId);
        holder.itemView.setSelected(selected);
        holder.itemView.setAlpha(selected ? 1f : 0.85f);
        holder.itemView.setOnClickListener(v -> listener.onSectionClick(section));
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
