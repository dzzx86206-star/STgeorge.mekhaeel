package com.stgeorge.church.utils;

/**
 * Central place for account-role constants and Firestore collection names.
 * Keeping these in one file makes the permission system easier to extend
 * as new sections (Sunday school, khadem prep, youth meeting, etc.) are added.
 */
public class Constants {

    // Account roles — match the "role" field stored per-user in Firestore
    public static final String ROLE_MANAGER = "manager";       // المدير العام
    public static final String ROLE_PRIEST = "priest";         // الأب الكاهن
    public static final String ROLE_SERVANT = "servant";        // الخادم
    public static final String ROLE_MEMBER = "member";          // المخدوم

    // Firestore collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_NEWS = "news";
    public static final String COLLECTION_ANNOUNCEMENTS = "announcements";
    public static final String COLLECTION_EVENTS = "events";
    public static final String COLLECTION_EVENT_REGISTRATIONS = "event_registrations";
    public static final String COLLECTION_SERVANT_NOTEBOOK = "servant_notebook";
    public static final String COLLECTION_ATTENDANCE = "attendance";
    public static final String COLLECTION_VISITATION = "visitation"; // الافتقاد
    public static final String COLLECTION_CHILDREN = "children";
    public static final String COLLECTION_CLASSES = "classes";
    public static final String COLLECTION_NOTIFICATIONS = "notifications";
    public static final String COLLECTION_LIBRARY = "library";
    public static final String COLLECTION_HYMNS = "hymns";
    public static final String COLLECTION_PRAYER_REQUESTS = "prayer_requests";
    public static final String COLLECTION_MESSAGES_TO_ADMIN = "messages_to_admin";
    public static final String COLLECTION_DONATIONS = "donations";
    public static final String COLLECTION_ACTIVITY_LOG = "activity_log";

    // SharedPreferences keys
    public static final String PREFS_NAME = "stgeorge_prefs";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_ROLE = "user_role";
    public static final String PREF_USER_NAME = "user_name";

    // App preferences (language / theme / font size) — separate prefs file
    // from the auth session so clearing a session never resets user settings.
    public static final String APP_PREFS_NAME = "stgeorge_app_prefs";
    public static final String PREF_LANGUAGE = "app_language";       // "ar" | "en"
    public static final String PREF_NIGHT_MODE = "app_night_mode";   // "system" | "light" | "dark"
    public static final String PREF_FONT_SCALE = "app_font_scale";   // float, applied to Agpeya/Bible readers

    public static final String LANGUAGE_ARABIC = "ar";
    public static final String LANGUAGE_ENGLISH = "en";

    public static final String NIGHT_MODE_SYSTEM = "system";
    public static final String NIGHT_MODE_LIGHT = "light";
    public static final String NIGHT_MODE_DARK = "dark";

    // Fixed Super Admin account (المدير العام) — see AuthHelper#loginAsSuperAdmin.
    // Either password logs in with full manager rights; both map to the same
    // real Firebase Auth account behind the scenes.
    public static final String SUPER_ADMIN_USERNAME = "manager";
    public static final String[] SUPER_ADMIN_PASSWORDS = {"manager123", "manager143"};
    public static final String SUPER_ADMIN_CANONICAL_PASSWORD = "manager123";

    // مدارس الأحد — fixed list of stages/classes from the spec
    public static final String[] SUNDAY_SCHOOL_STAGES = {
            "أولى ابتدائي", "ثانية ابتدائي", "ثالثة ابتدائي",
            "رابعة ابتدائي", "خامسة ابتدائي", "سادسة ابتدائي",
            "إعدادي", "ثانوي", "شباب"
    };

    private Constants() {
        // no instances
    }
}
