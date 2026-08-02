package com.stgeorge.church.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Full parsed content of a single book's tafsir JSON file
 * (assets/tafsir/old|new/&lt;book&gt;.json): {"book": "...", "chapters": [...]}
 */
public class BibleBookContent {

    private String book;
    private final List<BibleChapter> chapters = new ArrayList<>();

    public BibleBookContent() {
    }

    public BibleBookContent(String book, List<BibleChapter> chapters) {
        this.book = book;
        if (chapters != null) {
            this.chapters.addAll(chapters);
        }
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public List<BibleChapter> getChapters() {
        return chapters;
    }
}
