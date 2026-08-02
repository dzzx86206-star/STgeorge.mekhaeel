package com.stgeorge.church.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.models.Announcement;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    public interface ActionListener {
        void onEdit(Announcement announcement);
        void onDelete(Announcement announcement);
    }

    private final List<Announcement> items;
    private final boolean canManage;
    private final ActionListener listener;

    public AnnouncementAdapter(List<Announcement> items, boolean canManage, ActionListener listener) {
        this.items = items;
        this.canManage = canManage;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvBody.setText(item.getBody());

        String author = item.getAuthorName() != null ? item.getAuthorName() : "";
        String when = item.getCreatedAt() != null
                ? DateUtils.getRelativeTimeSpanString(item.getCreatedAt().getTime()).toString()
                : "";
        holder.tvMeta.setText(holder.itemView.getContext()
                .getString(R.string.announcement_meta_format, author, when));

        holder.layoutActions.setVisibility(canManage ? View.VISIBLE : View.GONE);
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvMeta, btnEdit, btnDelete;
        LinearLayout layoutActions;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
