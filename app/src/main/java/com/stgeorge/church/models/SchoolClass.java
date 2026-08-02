package com.stgeorge.church.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

/** فصل من فصول مدارس الأحد (مثال: "أولى ابتدائي")، ومعه الخدام المسؤولين عنه. */
public class SchoolClass {

    @Exclude
    private String id;

    private String name;                      // e.g. "أولى ابتدائي" أو اسم مخصص
    private List<String> assignedServantIds = new ArrayList<>();

    public SchoolClass() {
        // Required empty constructor for Firestore deserialization
    }

    public SchoolClass(String name) {
        this.name = name;
    }

    @Exclude
    public String getId() { return id; }

    @Exclude
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getAssignedServantIds() { return assignedServantIds; }
    public void setAssignedServantIds(List<String> assignedServantIds) { this.assignedServantIds = assignedServantIds; }
}
