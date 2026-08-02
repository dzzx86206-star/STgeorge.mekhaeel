package com.stgeorge.church.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.models.BibleBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the books of one testament (تفسير العهد القديم / الجديد) as a
 * tappable list, reusing the same card style as {@code AgpeyaHourAdapter}.
 */
public class BibleBookAdapter extends RecyclerView.Adapter<BibleBookAdapter.BookViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(BibleBook book);
    }

    private final List<BibleBook> books = new ArrayList<>();
    private final OnBookClickListener listener;

    public BibleBookAdapter(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<BibleBook> newBooks) {
        books.clear();
        books.addAll(newBooks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bible_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BibleBook book = books.get(position);
        holder.tvOrder.setText(String.valueOf(book.getOrder()));
        holder.tvTitle.setText(book.getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final TextView tvOrder;
        final TextView tvTitle;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrder = itemView.findViewById(R.id.tvBookOrder);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
        }
    }
}
