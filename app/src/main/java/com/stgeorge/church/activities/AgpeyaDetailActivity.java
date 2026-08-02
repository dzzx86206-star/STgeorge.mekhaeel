package com.stgeorge.church.activities;

import android.os.Bundle;
import android.widget.TextView;


import com.stgeorge.church.R;
import com.stgeorge.church.models.AgpeyaHour;
import com.stgeorge.church.utils.AgpeyaRepository;

/**
 * Displays the full text of a single Agpeya prayer hour.
 */
public class AgpeyaDetailActivity extends BaseActivity {

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agpeya_detail);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvContent = findViewById(R.id.tvDetailContent);
        tvSource = findViewById(R.id.tvDetailSource);

        String hourId = getIntent().getStringExtra(AgpeyaListActivity.EXTRA_HOUR_ID);
        AgpeyaHour hour = new AgpeyaRepository(this).getHourById(hourId);

        if (hour != null) {
            setTitle(hour.getTitle());
            tvTitle.setText(hour.getTitle());
            tvContent.setText(hour.getContent());
            if (hour.getSource() != null && !hour.getSource().isEmpty()) {
                tvSource.setText(getString(R.string.agpeya_source_format, hour.getSource()));
            }
        } else {
            tvTitle.setText(R.string.agpeya_load_error);
        }
    }
}
