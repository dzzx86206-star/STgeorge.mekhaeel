package com.stgeorge.church.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * لأن اجتماع مدارس الأحد يوم الجمعة — computes "this week's Friday" so the
 * attendance screen can show it automatically the moment it opens.
 */
public final class DateUtils {

    private static final SimpleDateFormat DATE_KEY_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("ar"));

    private DateUtils() {
    }

    /** Returns the Friday that falls within the current calendar week (Sat–Fri). */
    public static Date getCurrentFriday() {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK); // SUNDAY=1 ... SATURDAY=7
        int diff;
        switch (today) {
            case Calendar.SATURDAY: diff = -1; break; // most recent Friday (meeting just happened)
            case Calendar.SUNDAY: diff = 5; break;
            case Calendar.MONDAY: diff = 4; break;
            case Calendar.TUESDAY: diff = 3; break;
            case Calendar.WEDNESDAY: diff = 2; break;
            case Calendar.THURSDAY: diff = 1; break;
            default: diff = 0; // FRIDAY
        }
        calendar.add(Calendar.DAY_OF_YEAR, diff);
        return calendar.getTime();
    }

    public static String toDateKey(Date date) {
        return DATE_KEY_FORMAT.format(date);
    }

    public static String toDisplayString(Date date) {
        return DISPLAY_FORMAT.format(date);
    }
}
