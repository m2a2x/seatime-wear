package com.maks.seatimewear.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by maks on 08/07/2017.
 */

final public class Utils {
    public static String timestampToTime(long tmp) {
        Timestamp timestamp = new Timestamp(tmp * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm");
        return simpleDateFormat.format(timestamp);
    }
}
