package com.stgeorge.church.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.adapters.BibleBookAdapter;
import com.stgeorge.church.utils.TafsirRepository;

/**
 * Lists all books of one testament (تفسير العهد القديم / الجديد).
 * Tapping a book opens {@link BibleBookDetailActivity} with its full
 * commentary text.
 */
public class BibleBookListActivity extends BaseActivity {

    public static final String EXTRA_TESTAMENT = TafsirHomeActivity.EXTRA_TESTAMENT;
    public static final String EXTRA_BOOK_ID = "extra_book_id";
    public static final String EXTRA_BOOK_TITLE = "extra_book_title";

    private RecyclerView recyclerView;
    private BibleBookAdapter adapter;
    private TafsirRepository repository;
    private String testament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_book_list);

        testament = getIntent().getStringExtra(EXTRA_TESTAMENT);
        if (testament == null) {
            testament = TafsirRepository.OLD_TESTAMENT;
        }

        boolean isOld = TafsirRepository.OLD_TESTAMENT.equals(testament);
        String headerTitle = getString(isOld
                ? R.string.tafsir_old_testament
                : R.string.tafsir_new_testament);
        setTitle(headerTitle);

        TextView tvHeader = findViewById(R.id.tvBookListHeader);
        tvHeader.setText(headerTitle);

        repository = new TafsirRepository(this);

        recyclerView = findViewById(R.id.rvBibleBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BibleBookAdapter(book -> {
            Intent intent = new Intent(BibleBookListActivity.this, BibleBookDetailActivity.class);
            intent.putExtra(EXTRA_TESTAMENT, book.getTestament());
            intent.putExtra(EXTRA_BOOK_ID, book.getId());
            intent.putExtra(EXTRA_BOOK_TITLE, book.getTitle());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        adapter.submitList(repository.getBooks(testament));
    }
}
