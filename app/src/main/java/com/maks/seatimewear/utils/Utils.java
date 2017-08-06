package com.maks.seatimewear.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


final public class Utils {


    public static String timestampToTime(long tmp) {
        Timestamp timestamp = new Timestamp(tmp * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(timestamp);
    }

    public static long currentTimeUnix() {
        return unixTime(System.currentTimeMillis());
    }

    public static long getStartOfDayInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getEndOfDayUnix() {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return unixTime(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
    }

    public static long getDayAfterTodayUnix(int n) {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return unixTime(System.currentTimeMillis() + (n * 24 * 60 * 60 * 1000));
    }

    private static long unixTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);
        return Math.round(calendar.getTimeInMillis() / 1000L);
    }
}
