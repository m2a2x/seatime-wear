package com.maks.seatimewear.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public static long getDayAfterTodayUnix(int n) {
        // Add one day's time to the beginning of the day.
        // 24 hours * 60 minutes * 60 seconds * 1000 milliseconds = 1 day
        return timeToUnix(System.currentTimeMillis() + (n * 24 * 60 * 60 * 1000));
    }

    public static long timeToUnix(long time) {
        return Math.round(time / 1000L);
    }


    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
