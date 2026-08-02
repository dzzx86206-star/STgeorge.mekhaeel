package com.stgeorge.church.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.models.Child;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    public interface Listener {
        void onOpen(Child child);
        void onEdit(Child child);
        void onDelete(Child child);
    }

    private final List<Child> items;
    private final Listener listener;

    public ChildAdapter(List<Child> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Child item = items.get(position);
        holder.tvChildName.setText(item.getName());

        String phone = item.getGuardianPhone() != null ? item.getGuardianPhone() : "";
        holder.tvChildMeta.setText(holder.itemView.getContext()
                .getString(R.string.child_meta_format, phone));

        holder.rowChild.setOnClickListener(v -> listener.onOpen(item));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rowChild;
        TextView tvChildName, tvChildMeta, btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowChild = itemView.findViewById(R.id.rowChild);
            tvChildName = itemView.findViewById(R.id.tvChildName);
            tvChildMeta = itemView.findViewById(R.id.tvChildMeta);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
