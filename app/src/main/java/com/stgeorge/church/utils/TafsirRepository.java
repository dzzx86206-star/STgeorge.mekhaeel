package com.stgeorge.church.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.stgeorge.church.models.BibleBook;
import com.stgeorge.church.models.BibleBookContent;
import com.stgeorge.church.models.BibleChapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Loads تفسير الكتاب المقدس (Bible commentary) content bundled in
 * assets/tafsir/. Layout on disk:
 *
 * <pre>
 * assets/tafsir/
 *   index.json          -> [{id: "old"|"new", title, count, index_file}, ...]
 *   old/index.json       -> [{id, order, title, testament: "old", file}, ...]  (39 books)
 *   old/&lt;book&gt;.json     -> {book, chapters: [{chapter, title, content}, ...]}
 *   new/index.json       -> [{id, order, title, testament: "new", file}, ...]  (27 books)
 *   new/&lt;book&gt;.json     -> {book, chapters: [{chapter, title, content}, ...]}
 * </pre>
 */
public class TafsirRepository {

    public static final String OLD_TESTAMENT = "old";
    public static final String NEW_TESTAMENT = "new";

    private static final String ASSETS_DIR = "tafsir";

    private final AssetManager assetManager;

    public TafsirRepository(Context context) {
        this.assetManager = context.getApplicationContext().getAssets();
    }

    /**
     * Returns all books of one testament ({@link #OLD_TESTAMENT} or
     * {@link #NEW_TESTAMENT}), sorted by their canonical order.
     */
    public List<BibleBook> getBooks(String testament) {
        List<BibleBook> books = new ArrayList<>();
        String indexPath = ASSETS_DIR + "/" + testament + "/index.json";
        try {
            JSONArray index = new JSONArray(readAsset(indexPath));
            for (int i = 0; i < index.length(); i++) {
                JSONObject entry = index.getJSONObject(i);
                books.add(new BibleBook(
                        entry.optString("id"),
                        entry.optInt("order"),
                        entry.optString("title"),
                        entry.optString("testament", testament),
                        entry.optString("file")
                ));
            }
        } catch (IOException | org.json.JSONException e) {
            // If assets are missing/corrupted, just return whatever loaded so far.
        }

        Collections.sort(books, Comparator.comparingInt(BibleBook::getOrder));
        return books;
    }

    /**
     * Loads the full commentary content for a single book.
     *
     * @param testament {@link #OLD_TESTAMENT} or {@link #NEW_TESTAMENT}
     * @param bookId    e.g. "takoween", "mataa"
     */
    public BibleBookContent getBookContent(String testament, String bookId) {
        String path = ASSETS_DIR + "/" + testament + "/" + bookId + ".json";
        try {
            String json = readAsset(path);
            JSONObject obj = new JSONObject(json);
            List<BibleChapter> chapters = new ArrayList<>();
            JSONArray chaptersArray = obj.optJSONArray("chapters");
            if (chaptersArray != null) {
                for (int i = 0; i < chaptersArray.length(); i++) {
                    JSONObject chapterObj = chaptersArray.getJSONObject(i);
                    chapters.add(new BibleChapter(
                            chapterObj.optInt("chapter"),
                            chapterObj.optString("title"),
                            chapterObj.optString("content")
                    ));
                }
            }
            return new BibleBookContent(obj.optString("book"), chapters);
        } catch (IOException | org.json.JSONException e) {
            return null;
        }
    }

    private String readAsset(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (InputStream is = assetManager.open(path);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }
}
