package com.maks.seatimewear.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.maks.seatimewear.sql.SeaSQLiteHelper;

import static com.maks.seatimewear.utils.Utils.timestampToTime;

/**
 * Created by maks on 08/07/2017.
 * [_id, spot_id, state, shift, timestamp]
 */

public class Tide {
    private String _id;
    private long spot_id;
    private String state;
    private String shift;
    private String time;
    private long timestamp;

    public String getId() {
        return _id;
    }

    public void setId(String v) {
        _id = v;
    }

    public long getSpot_id() {
        return spot_id;
    }

    public void setSpot_id(long v) {
        spot_id = v;
    }

    public String getState() {
        return state;
    }

    public void setState(String v) {
        state = v;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String v) {
        shift = v;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String v) {
        time = v;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long v) {
        timestamp = v;
    }

    public void update(Cursor cursor) {
        setId(cursor.getString(0));
        setSpot_id(cursor.getInt(1));
        setState(cursor.getString(2));
        setShift(cursor.getString(3));
        setTimestamp(cursor.getInt(4));
        setTime(timestampToTime(timestamp));
    }

    public void putContentValues(ContentValues values) {
        values.put(SeaSQLiteHelper.COLUMN_ID, _id);
        values.put(SeaSQLiteHelper.COLUMN_SPOT_ID, spot_id);
        values.put(SeaSQLiteHelper.COLUMN_STATE, state);
        values.put(SeaSQLiteHelper.COLUMN_SHIFT, shift);
        values.put(SeaSQLiteHelper.COLUMN_TIME, timestamp);
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return state + ' ' + time;
    }
}
