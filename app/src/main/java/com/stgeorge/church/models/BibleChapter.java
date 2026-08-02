package com.stgeorge.church.models;

/**
 * One entry of the "chapters" array inside a book's tafsir JSON file
 * (assets/tafsir/old|new/&lt;book&gt;.json). At present each book file has a
 * single chapter entry titled "كامل السفر" holding the full commentary text,
 * but the model supports multiple chapters per book for future files that
 * split content chapter-by-chapter.
 */
public class BibleChapter {

    private int chapter;    // chapter number, or 1 as a placeholder for "whole book"
    private String title;   // e.g. "كامل السفر" or "الإصحاح الأول"
    private String content; // full commentary text for this entry

    public BibleChapter() {
    }

    public BibleChapter(int chapter, String title, String content) {
        this.chapter = chapter;
        this.title = title;
        this.content = content;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
