package com.stgeorge.church.models;

import com.google.firebase.firestore.Exclude;

/** بيانات طفل داخل مدارس الأحد. */
public class Child {

    @Exclude
    private String id;

    private String name;
    private String stage;          // المرحلة
    private String classId;        // الفصل (SchoolClass.id)
    private String guardianPhone;  // رقم ولي الأمر
    private String birthDate;      // تاريخ الميلاد (اختياري) — "yyyy-MM-dd"
    private String notes;

    public Child() {
        // Required empty constructor for Firestore deserialization
    }

    public Child(String name, String stage, String classId, String guardianPhone) {
        this.name = name;
        this.stage = stage;
        this.classId = classId;
        this.guardianPhone = guardianPhone;
    }

    @Exclude
    public String getId() { return id; }

    @Exclude
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getClassId() { return classId; }
    public void setClassId(String classId) { this.classId = classId; }

    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
