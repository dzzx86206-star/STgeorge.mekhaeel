package com.stgeorge.church.models;

/**
 * Represents one book of the Bible in the tafsir (تفسير الكتاب المقدس) index —
 * either from assets/tafsir/old/index.json or assets/tafsir/new/index.json.
 * Only metadata; the full commentary text is loaded separately (and lazily)
 * via {@link com.stgeorge.church.utils.TafsirRepository#getBookContent}.
 */
public class BibleBook {

    private String id;          // e.g. "takoween", "mataa"
    private int order;          // display order within its testament
    private String title;       // e.g. "سفر التكوين"
    private String testament;   // "old" or "new"
    private String file;        // e.g. "takoween.json"

    public BibleBook() {
    }

    public BibleBook(String id, int order, String title, String testament, String file) {
        this.id = id;
        this.order = order;
        this.title = title;
        this.testament = testament;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTestament() {
        return testament;
    }

    public void setTestament(String testament) {
        this.testament = testament;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
