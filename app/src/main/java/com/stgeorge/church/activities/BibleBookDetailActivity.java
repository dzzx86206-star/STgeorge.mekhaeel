package com.stgeorge.church.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;


import com.stgeorge.church.R;
import com.stgeorge.church.models.BibleBookContent;
import com.stgeorge.church.models.BibleChapter;
import com.stgeorge.church.utils.TafsirRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Shows the full tafsir (commentary) text of a single Bible book.
 *
 * Some books' commentary is several megabytes of text (e.g. تفسير إنجيل
 * يوحنا). A single Android TextView can hit rendering/canvas limits with
 * that much text, so this screen renders it in a WebView instead, which
 * handles very long documents reliably.
 */
public class BibleBookDetailActivity extends BaseActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_book_detail);

        webView = findViewById(R.id.wvBookContent);
        progressBar = findViewById(R.id.pbBookLoading);
        webView.getSettings().setTextZoom(100);

        String testament = getIntent().getStringExtra(BibleBookListActivity.EXTRA_TESTAMENT);
        String bookId = getIntent().getStringExtra(BibleBookListActivity.EXTRA_BOOK_ID);
        String bookTitle = getIntent().getStringExtra(BibleBookListActivity.EXTRA_BOOK_TITLE);

        if (bookTitle != null) {
            setTitle(bookTitle);
        }

        loadContent(testament, bookId, bookTitle);
    }

    private void loadContent(String testament, String bookId, String bookTitle) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            BibleBookContent content = new TafsirRepository(this).getBookContent(testament, bookId);
            String html = buildHtml(bookTitle, content);
            mainHandler.post(() -> {
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            });
        });
    }

    private String buildHtml(String bookTitle, BibleBookContent content) {
        StringBuilder body = new StringBuilder();

        if (content == null || content.getChapters().isEmpty()) {
            body.append("<p>").append(escape(getString(R.string.tafsir_load_error))).append("</p>");
        } else {
            for (BibleChapter chapter : content.getChapters()) {
                String chapterTitle = chapter.getTitle();
                if (chapterTitle != null && !chapterTitle.isEmpty()) {
                    body.append("<h2>").append(escape(chapterTitle)).append("</h2>");
                }
                String chapterContent = chapter.getContent();
                if (chapterContent != null) {
                    body.append("<p>").append(toHtmlParagraph(chapterContent)).append("</p>");
                }
            }
        }

        return "<html><head><meta charset='utf-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<style>"
                + "body{direction:rtl;text-align:right;font-family:sans-serif;"
                + "background-color:#FFFBF6;color:#1F1B16;"
                + "padding:20px;line-height:1.9;font-size:17px;}"
                + "h1{color:#8B5E2A;font-size:22px;text-align:center;}"
                + "h2{color:#8B5E2A;font-size:18px;margin-top:28px;}"
                + "</style></head><body>"
                + "<h1>" + escape(bookTitle == null ? "" : bookTitle) + "</h1>"
                + body
                + "</body></html>";
    }

    private String toHtmlParagraph(String text) {
        return escape(text).replace("\n", "<br>");
    }

    private String escape(String text) {
        return TextUtils.htmlEncode(text == null ? "" : text);
    }

    @Override
    protected void onDestroy() {
        executor.shutdown();
        super.onDestroy();
    }
}
