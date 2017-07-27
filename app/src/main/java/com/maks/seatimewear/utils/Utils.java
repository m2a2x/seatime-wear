package com.maks.seatimewear.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by maks on 08/07/2017.
 */

final public class Utils {


    public static String timestampToTime(long tmp) {
        Timestamp timestamp = new Timestamp(tmp * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(timestamp);
    }

    public static long currentTimeUnix() {
        return timeToUnix(System.currentTimeMillis());
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
        return timeToUnix(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
    }

    public static long timeToUnix(long time) {
        return time / 1000L;
    }
}
