package com.stgeorge.church.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stgeorge.church.models.Announcement;
import com.stgeorge.church.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * الإعلانات — Firestore-backed repository. Kept separate from AuthHelper so
 * each content module (news, events, library, ...) gets its own small,
 * readable repository following this same pattern.
 */
public class AnnouncementRepository {

    public interface ListCallback {
        void onSuccess(List<Announcement> announcements);
        void onFailure(String message);
    }

    public interface WriteCallback {
        void onSuccess();
        void onFailure(String message);
    }

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public void getAll(ListCallback callback) {
        firestore.collection(Constants.COLLECTION_ANNOUNCEMENTS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(toList(snapshot)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getLatest(ListCallback callback) {
        firestore.collection(Constants.COLLECTION_ANNOUNCEMENTS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(toList(snapshot)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void add(Announcement announcement, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_ANNOUNCEMENTS)
                .add(announcement)
                .addOnSuccessListener(ref -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void update(String id, String title, String body, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_ANNOUNCEMENTS)
                .document(id)
                .update("title", title, "body", body)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void delete(String id, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_ANNOUNCEMENTS)
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @NonNull
    private List<Announcement> toList(QuerySnapshot snapshot) {
        List<Announcement> list = new ArrayList<>();
        if (snapshot != null) {
            snapshot.forEach(doc -> {
                Announcement announcement = doc.toObject(Announcement.class);
                announcement.setId(doc.getId());
                list.add(announcement);
            });
        }
        return list;
    }
}
