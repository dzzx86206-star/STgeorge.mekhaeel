package com.stgeorge.church.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stgeorge.church.models.AttendanceSession;
import com.stgeorge.church.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * الحضور الأسبوعي — one document per (class, Friday), keyed deterministically
 * as "{classId}_{dateKey}" so re-saving the same week overwrites instead of
 * creating a duplicate session.
 */
public class AttendanceRepository {

    public interface SessionCallback {
        void onSuccess(AttendanceSession session); // session == null when none saved yet for that date
        void onFailure(String message);
    }

    public interface ListCallback {
        void onSuccess(List<AttendanceSession> sessions);
        void onFailure(String message);
    }

    public interface WriteCallback {
        void onSuccess();
        void onFailure(String message);
    }

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private String docId(String classId, String dateKey) {
        return classId + "_" + dateKey;
    }

    public void getSession(String classId, String dateKey, SessionCallback callback) {
        firestore.collection(Constants.COLLECTION_ATTENDANCE)
                .document(docId(classId, dateKey))
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        AttendanceSession session = doc.toObject(AttendanceSession.class);
                        if (session != null) {
                            session.setId(doc.getId());
                        }
                        callback.onSuccess(session);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void saveSession(AttendanceSession session, WriteCallback callback) {
        firestore.collection(Constants.COLLECTION_ATTENDANCE)
                .document(docId(session.getClassId(), session.getDateKey()))
                .set(session)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /** سجل الطفل — every saved session for a class, most recent first (filtered client-side per child). */
    public void getHistoryForClass(String classId, ListCallback callback) {
        firestore.collection(Constants.COLLECTION_ATTENDANCE)
                .whereEqualTo("classId", classId)
                .orderBy("dateKey", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> callback.onSuccess(toList(snapshot)))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    @NonNull
    private List<AttendanceSession> toList(QuerySnapshot snapshot) {
        List<AttendanceSession> list = new ArrayList<>();
        if (snapshot != null) {
            snapshot.forEach(doc -> {
                AttendanceSession session = doc.toObject(AttendanceSession.class);
                session.setId(doc.getId());
                list.add(session);
            });
        }
        return list;
    }
}
