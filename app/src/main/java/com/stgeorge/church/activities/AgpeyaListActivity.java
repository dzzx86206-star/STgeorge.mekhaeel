package com.stgeorge.church.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stgeorge.church.R;
import com.stgeorge.church.adapters.AgpeyaHourAdapter;
import com.stgeorge.church.utils.AgpeyaRepository;

/**
 * Lists the eight canonical Agpeya prayer hours (كتاب الأجبية / السواعي).
 * Tapping one opens {@link AgpeyaDetailActivity} with the full prayer text.
 */
public class AgpeyaListActivity extends BaseActivity {

    public static final String EXTRA_HOUR_ID = "extra_hour_id";

    private RecyclerView recyclerView;
    private AgpeyaHourAdapter adapter;
    private AgpeyaRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agpeya_list);

        setTitle(R.string.agpeya_title);

        repository = new AgpeyaRepository(this);

        recyclerView = findViewById(R.id.rvAgpeyaHours);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AgpeyaHourAdapter(hour -> {
            Intent intent = new Intent(AgpeyaListActivity.this, AgpeyaDetailActivity.class);
            intent.putExtra(EXTRA_HOUR_ID, hour.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        adapter.submitList(repository.getAllHours());
    }
}
