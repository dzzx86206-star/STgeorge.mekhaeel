package com.stgeorge.church.utils;

import android.content.Context;

import com.stgeorge.church.R;
import com.stgeorge.church.models.NavSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Single source of truth for "what does each role see in the home drawer".
 *
 * This is the app's Role-Based Access Control (RBAC) — رقم 1 في قائمة
 * "إضافات أوصي بها" في المواصفات. Every section in the spec has an entry
 * here with the roles allowed to see it; MainActivity just asks this class
 * for the current user's list and renders it, so adding a new role or
 * changing who can see a section only ever needs a change in ONE place.
 *
 * `id` values are routed to their target screen in MainActivity#openSection.
 */
public final class NavProvider {

    private static final String[] STAFF_ROLES = {
            Constants.ROLE_MANAGER, Constants.ROLE_PRIEST, Constants.ROLE_SERVANT
    };
    private static final String[] MANAGER_PRIEST = {
            Constants.ROLE_MANAGER, Constants.ROLE_PRIEST
    };
    private static final String[] MANAGER_ONLY = {
            Constants.ROLE_MANAGER
    };

    private NavProvider() {
    }

    public static List<NavSection> getSectionsFor(Context context, String role) {
        List<NavSection> all = new ArrayList<>();

        // 1. الصفحة الرئيسية
        all.add(new NavSection("home", context.getString(R.string.nav_home), R.drawable.ic_home, null));

        // 3. الإعلانات
        all.add(new NavSection("announcements", context.getString(R.string.nav_announcements), R.drawable.ic_campaign, null));

        // 4. الأخبار
        all.add(new NavSection("news", context.getString(R.string.nav_news), R.drawable.ic_newspaper, null));

        // 5. الأحداث / المناسبات
        all.add(new NavSection("events", context.getString(R.string.nav_events), R.drawable.ic_event, null));

        // 7. المكتبة
        all.add(new NavSection("library", context.getString(R.string.nav_library), R.drawable.ic_library, null));

        // 8. الأجبية (already built)
        all.add(new NavSection("agpeya", context.getString(R.string.nav_agpeya), R.drawable.ic_book, null));

        // 9. الكتاب المقدس (already built, via التفسير)
        all.add(new NavSection("bible", context.getString(R.string.nav_bible), R.drawable.ic_book, null));

        // 10. الألحان
        all.add(new NavSection("hymns", context.getString(R.string.nav_hymns), R.drawable.ic_music_note, null));

        // 11. الطقس القبطي (السنكسار / القطمارس / الأصوام / الأعياد / القراءات اليومية)
        all.add(new NavSection("coptic_rite", context.getString(R.string.nav_coptic_rite), R.drawable.ic_calendar, null));

        // مدارس الأحد — إدارة الفصول/الحضور/الدرجات (الخادم يسجل، الكاهن يراجع، المدير يدير الكل)
        all.add(new NavSection("sunday_school", context.getString(R.string.nav_sunday_school), R.drawable.ic_school, STAFF_ROLES));

        // دفتر التحضير + الافتقاد (خدمة)
        all.add(new NavSection("servant_notebook", context.getString(R.string.nav_servant_notebook), R.drawable.ic_edit_note, STAFF_ROLES));
        all.add(new NavSection("visitation", context.getString(R.string.nav_visitation), R.drawable.ic_place, STAFF_ROLES));

        // متابعة الخدام وتقارير الخدمة (الأب الكاهن + المدير)
        all.add(new NavSection("servants_follow_up", context.getString(R.string.nav_servants_follow_up), R.drawable.ic_person_group, MANAGER_PRIEST));

        // التواصل
        all.add(new NavSection("communication", context.getString(R.string.nav_communication), R.drawable.ic_call, null));

        // البث المباشر
        all.add(new NavSection("live_stream", context.getString(R.string.nav_live_stream), R.drawable.ic_videocam, null));

        // موقع الكنيسة
        all.add(new NavSection("church_location", context.getString(R.string.nav_church_location), R.drawable.ic_place, null));

        // التبرعات (اختياري)
        all.add(new NavSection("donations", context.getString(R.string.nav_donations), R.drawable.ic_volunteer, null));

        // لوحة المدير + التقارير + النسخ الاحتياطي (المدير فقط)
        all.add(new NavSection("user_management", context.getString(R.string.nav_user_management), R.drawable.ic_person_group, MANAGER_ONLY));
        all.add(new NavSection("admin_dashboard", context.getString(R.string.nav_admin_dashboard), R.drawable.ic_dashboard, MANAGER_ONLY));
        all.add(new NavSection("reports", context.getString(R.string.nav_reports), R.drawable.ic_bar_chart, MANAGER_PRIEST));

        // الإعدادات
        all.add(new NavSection("settings", context.getString(R.string.nav_settings), R.drawable.ic_settings, null));

        List<NavSection> visible = new ArrayList<>();
        for (NavSection section : all) {
            if (section.isVisibleTo(role)) {
                visible.add(section);
            }
        }
        return visible;
    }
}
