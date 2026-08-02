package com.stgeorge.church.activities;

import android.content.Intent;
import android.os.Bundle;


import com.google.android.material.card.MaterialCardView;
import com.stgeorge.church.R;
import com.stgeorge.church.utils.TafsirRepository;

/**
 * Entry point for تفسير الكتاب المقدس: lets the user pick العهد القديم
 * or العهد الجديد, then opens {@link BibleBookListActivity} for that
 * testament.
 */
public class TafsirHomeActivity extends BaseActivity {

    public static final String EXTRA_TESTAMENT = "extra_testament";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tafsir_home);

        setTitle(R.string.tafsir_title);

        MaterialCardView cardOld = findViewById(R.id.cardOldTestament);
        MaterialCardView cardNew = findViewById(R.id.cardNewTestament);

        cardOld.setOnClickListener(v -> openBookList(TafsirRepository.OLD_TESTAMENT));
        cardNew.setOnClickListener(v -> openBookList(TafsirRepository.NEW_TESTAMENT));
    }

    private void openBookList(String testament) {
        Intent intent = new Intent(this, BibleBookListActivity.class);
        intent.putExtra(EXTRA_TESTAMENT, testament);
        startActivity(intent);
    }
}
