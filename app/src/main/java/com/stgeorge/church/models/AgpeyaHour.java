package com.stgeorge.church.models;

/**
 * Represents one of the eight canonical prayer hours of the Agpeya
 * (كتاب الأجبية / صلاة السواعي). Content is loaded from JSON files
 * bundled in assets/agpeya/ by {@link com.stgeorge.church.utils.AgpeyaRepository}.
 */
public class AgpeyaHour {

    private String id;       // e.g. "prime", "third", "midnight"...
    private int order;       // display order, 1..8
    private String title;    // e.g. "صلاة باكر"
    private String content;  // full prayer text
    private String source;   // attribution, e.g. "St-Takla.org"

    public AgpeyaHour() {
    }

    public AgpeyaHour(String id, int order, String title, String content, String source) {
        this.id = id;
        this.order = order;
        this.title = title;
        this.content = content;
        this.source = source;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
