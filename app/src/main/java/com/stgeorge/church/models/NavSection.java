package com.stgeorge.church.models;

/**
 * One entry in the home navigation drawer (e.g. "الإعلانات", "مدارس الأحد").
 * `allowedRoles` drives the RBAC filtering: a section is only shown to a
 * signed-in user whose role appears in this list. Pass null to show a
 * section to every role (e.g. الأجبية, الكتاب المقدس, الإعدادات).
 */
public class NavSection {

    private final String id;
    private final String title;
    private final int iconRes;
    private final String[] allowedRoles; // null = visible to all roles

    public NavSection(String id, String title, int iconRes, String[] allowedRoles) {
        this.id = id;
        this.title = title;
        this.iconRes = iconRes;
        this.allowedRoles = allowedRoles;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public boolean isVisibleTo(String role) {
        if (allowedRoles == null) {
            return true;
        }
        for (String allowed : allowedRoles) {
            if (allowed.equals(role)) {
                return true;
            }
        }
        return false;
    }
}
