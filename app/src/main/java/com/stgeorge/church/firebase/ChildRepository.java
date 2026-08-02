package com.stgeorge.church.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stgeorge.church.models.Child;
import com.stgeorge.church.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** بيانات أطفال مدارس الأحد. */
public class ChildRepository {

    public interface ListCallback {
        void onSuccess(List<Child> children);
        void onFailure(String message);
    }

    public interface WriteCallback {
        void onSuccess();
        void onFailure(String message);
    }

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public void getByClass(String classId, ListCallback callback) {
        firestore.collection(Constants.COLLECTION_CHILDREN)
                .whereEqualTo("classId", classId)
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(toList(snapshot)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /** البحث عن طفل — client-side filter by name over the class's children (small lists per class). */
    public void search(String classId, String query, ListCallback callback) {
        getByClass(classId, new ListCallback() {
            @Override
            public void onSuccess(List<Child> children) {
                String needle = query.trim().toLowerCase(Locale.ROOT);
                List<Child> filtered = new ArrayList<>();
                for (Child child : children) {
                    if (child.getName() != null && child.getName().toLowerCase(Locale.ROOT).contains(needle)) {
                        filtered.add(child);
                    }
                }
                callback.onSuccess(filtered);
            }

            @Override
            public void onFailure(String message) {
                callback.onFailure(message);
            }
        });
    }

    public void add(Child child, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_CHILDREN)
                .add(child)
                .addOnSuccessListener(ref -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void update(Child child, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_CHILDREN)
                .document(child.getId())
                .set(child)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void delete(String childId, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_CHILDREN)
                .document(childId)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @NonNull
    private List<Child> toList(QuerySnapshot snapshot) {
        List<Child> list = new ArrayList<>();
        if (snapshot != null) {
            snapshot.forEach(doc -> {
                Child child = doc.toObject(Child.class);
                child.setId(doc.getId());
                list.add(child);
            });
        }
        return list;
    }
}
