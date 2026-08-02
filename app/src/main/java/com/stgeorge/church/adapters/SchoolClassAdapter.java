package com.stgeorge.church.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.models.SchoolClass;

import java.util.List;

public class SchoolClassAdapter extends RecyclerView.Adapter<SchoolClassAdapter.ViewHolder> {

    public interface Listener {
        void onOpen(SchoolClass schoolClass);
        void onEdit(SchoolClass schoolClass);
        void onDelete(SchoolClass schoolClass);
    }

    private final List<SchoolClass> items;
    private final boolean canManage;
    private final Listener listener;

    public SchoolClassAdapter(List<SchoolClass> items, boolean canManage, Listener listener) {
        this.items = items;
        this.canManage = canManage;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_school_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SchoolClass item = items.get(position);
        holder.tvClassName.setText(item.getName());
        int servantCount = item.getAssignedServantIds() != null ? item.getAssignedServantIds().size() : 0;
        holder.tvClassMeta.setText(holder.itemView.getContext()
                .getString(R.string.class_servant_count_format, servantCount));

        holder.layoutActions.setVisibility(canManage ? View.VISIBLE : View.GONE);
        holder.rowClass.setOnClickListener(v -> listener.onOpen(item));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rowClass, layoutActions;
        TextView tvClassName, tvClassMeta, btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowClass = itemView.findViewById(R.id.rowClass);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassMeta = itemView.findViewById(R.id.tvClassMeta);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
