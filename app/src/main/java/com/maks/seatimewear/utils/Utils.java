package com.maks.seatimewear.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


final public class Utils {
    private static String FORMAT = "HH:mm";


    public static String timestampToTime(long tmp) {
        Timestamp timestamp = new Timestamp(tmp * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT);
        return simpleDateFormat.format(timestamp);
    }

    public static long currentTimeUnix() {
        return getNowUtcMs() / 1000L;
    }

    public static String timeTimezone(String timezone, long time) {
        TimeZone tz = TimeZone.getTimeZone(timezone);
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
        dateFormat.setTimeZone(tz);
        return dateFormat.format(new Date(time * 1000L));


    }

    public static long getEndOfDayUnix() {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds  = 1 day
        return currentTimeUnix() + (24 * 60 * 60);
    }

    public static long getDayAfterTodayUnix(int n) {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return currentTimeUnix() + (n * 24 * 60 * 60);
    }

    private static long getNowUtcMs() {
        Date cals = Calendar.getInstance(TimeZone.getTimeZone("gmt")).getTime();
        return  cals.getTime();
    }
}
