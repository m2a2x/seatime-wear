package com.maks.seatimewear.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.maks.seatimewear.sql.SeaSQLiteHelper;

import java.io.Serializable;

import static com.maks.seatimewear.utils.Utils.timestampToTime;

/**
 * Created by maks on 08/07/2017.
 * [_id, spot_id, state, shift, timestamp]
 */

public class Wind {
    private String _id;
    private long spot_id;
    private String compass;
    private long speed;
    private long timestamp;
    private String unit;

    public String getId() {
        return _id;
    }

    public void setId(String v) {
        _id = v;
    }

    public void setSpot_id(long v) {
        spot_id = v;
    }


    public void setCompass(String v) {
        compass = v;
    }

    public void setSpeed(long v) {
        speed = v;
    }


    public void setUnit(String v) {
        unit = v;
    }


    public void setTimestamp(long v) {
        timestamp = v;
    }
}
