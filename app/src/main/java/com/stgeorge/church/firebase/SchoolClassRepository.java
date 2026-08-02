package com.stgeorge.church.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stgeorge.church.models.SchoolClass;
import com.stgeorge.church.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/** فصول مدارس الأحد. */
public class SchoolClassRepository {

    public interface ListCallback {
        void onSuccess(List<SchoolClass> classes);
        void onFailure(String message);
    }

    public interface WriteCallback {
        void onSuccess();
        void onFailure(String message);
    }

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    /** المدير والكاهن يريان كل الفصول. */
    public void getAll(ListCallback callback) {
        firestore.collection(Constants.COLLECTION_CLASSES)
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(toList(snapshot)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /** الخادم يرى فصله (أو فصوله) فقط. */
    public void getAssignedTo(String servantUserId, ListCallback callback) {
        firestore.collection(Constants.COLLECTION_CLASSES)
                .whereArrayContains("assignedServantIds", servantUserId)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(toList(snapshot)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void add(SchoolClass schoolClass, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_CLASSES)
                .add(schoolClass)
                .addOnSuccessListener(ref -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void update(SchoolClass schoolClass, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_CLASSES)
                .document(schoolClass.getId())
                .set(schoolClass)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void delete(String classId, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_CLASSES)
                .document(classId)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @NonNull
    private List<SchoolClass> toList(QuerySnapshot snapshot) {
        List<SchoolClass> list = new ArrayList<>();
        if (snapshot != null) {
            snapshot.forEach(doc -> {
                SchoolClass schoolClass = doc.toObject(SchoolClass.class);
                schoolClass.setId(doc.getId());
                list.add(schoolClass);
            });
        }
        return list;
    }
}
