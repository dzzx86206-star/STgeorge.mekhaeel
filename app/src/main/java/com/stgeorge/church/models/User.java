package com.stgeorge.church.models;

/**
 * Represents a single app user (manager, priest, or servant).
 * Mirrors the "users" document shape stored in Firestore.
 */
public class User {

    private String userId;
    private String username;
    private String fullName;
    private String role;       // manager | priest | servant | member
    private String phone;
    private String classAssigned; // legacy single-class field, superseded by SchoolClass.assignedServantIds
    private Boolean active;       // null/true = active; false = disabled by manager (blocks login)

    public User() {
        // Required empty constructor for Firestore deserialization
    }

    public User(String userId, String username, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.active = true;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getClassAssigned() { return classAssigned; }
    public void setClassAssigned(String classAssigned) { this.classAssigned = classAssigned; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @com.google.firebase.firestore.Exclude
    public boolean isAccountActive() { return active == null || active; }
}
