package com.stgeorge.church.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * الحضور الأسبوعي — one attendance session for one class on one Friday.
 * Document id is deterministically "{classId}_{dateKey}" (see
 * AttendanceRepository) so re-saving the same Friday overwrites rather than
 * duplicating.
 */
public class AttendanceSession {

    @Exclude
    private String id;

    private String classId;
    private String dateKey;              // "yyyy-MM-dd" of the Friday
    private List<String> presentChildIds = new ArrayList<>();
    private List<String> absentChildIds = new ArrayList<>();
    private String takenByUserId;
    private String takenByName;

    @ServerTimestamp
    private Date savedAt;

    public AttendanceSession() {
        // Required empty constructor for Firestore deserialization
    }

    @Exclude
    public String getId() { return id; }

    @Exclude
    public void setId(String id) { this.id = id; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getDateKey() { return dateKey; }
    public void setDateKey(String dateKey) { this.dateKey = dateKey; }

    public List<String> getPresentChildIds() { return presentChildIds; }
    public void setPresentChildIds(List<String> presentChildIds) { this.presentChildIds = presentChildIds; }

    public List<String> getAbsentChildIds() { return absentChildIds; }
    public void setAbsentChildIds(List<String> absentChildIds) { this.absentChildIds = absentChildIds; }

    public String getTakenByUserId() { return takenByUserId; }
    public void setTakenByUserId(String takenByUserId) { this.takenByUserId = takenByUserId; }

    public String getTakenByName() { return takenByName; }
    public void setTakenByName(String takenByName) { this.takenByName = takenByName; }

    public Date getSavedAt() { return savedAt; }
    public void setSavedAt(Date savedAt) { this.savedAt = savedAt; }

    @Exclude
    public int getTotalCount() { return presentChildIds.size() + absentChildIds.size(); }

    @Exclude
    public int getPresentCount() { return presentChildIds.size(); }

    @Exclude
    public int getAbsentCount() { return absentChildIds.size(); }
}
