package com.stgeorge.church.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/** الإعلانات — one announcement, optionally with an attached image URL (Supabase Storage). */
public class Announcement {

    @Exclude
    private String id;

    private String title;
    private String body;
    private String imageUrl;      // nullable — صور وملفات
    private String authorName;
    private String authorUserId;

    @ServerTimestamp
    private Date createdAt;

    public Announcement() {
        // Required empty constructor for Firestore deserialization
    }

    public Announcement(String title, String body, String authorName, String authorUserId) {
        this.title = title;
        this.body = body;
        this.authorName = authorName;
        this.authorUserId = authorUserId;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(String authorUserId) {
        this.authorUserId = authorUserId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
