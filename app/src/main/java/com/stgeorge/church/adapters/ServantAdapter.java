package com.stgeorge.church.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.models.User;

import java.util.List;

/** قائمة الخدام — المدير يقدر يضيف ويحذف فقط، فمفيش تعديل ولا تفعيل/إيقاف هنا. */
public class ServantAdapter extends RecyclerView.Adapter<ServantAdapter.ViewHolder> {

    public interface Listener {
        void onDelete(User servant);
    }

    private final List<User> items;
    private final Listener listener;

    public ServantAdapter(List<User> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_servant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User servant = items.get(position);
        holder.tvServantName.setText(servant.getFullName());

        String phone = servant.getPhone() != null ? servant.getPhone() : "";
        holder.tvServantMeta.setText(holder.itemView.getContext()
                .getString(R.string.servant_meta_format, servant.getUsername(), phone));

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(servant));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServantName, tvServantMeta, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServantName = itemView.findViewById(R.id.tvServantName);
            tvServantMeta = itemView.findViewById(R.id.tvServantMeta);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
