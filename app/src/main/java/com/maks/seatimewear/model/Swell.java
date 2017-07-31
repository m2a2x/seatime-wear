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

public class Swell {
    private String _id;
    private long spot_id;
    private String compassDirection;
    private long minHeight;
    private long maxHeight;
    private long period;
    private long power;
    private long timestamp;
    private String unit;

    public Swell() {}

    public String getId() {
        return _id;
    }

    public void setId(String v) {
        _id = v;
    }


    public void setSpot_id(long v) {
        spot_id = v;
    }

    public void setCompassDirection(String v) {
        compassDirection = v;
    }

    public void setUnit(String v) {
        unit = v;
    }

    public void setPeriod(long v) {
        period = v;
    }

    public void setPower(long v) {
        power = v;
    }

    public void setMinHeight(long v) {
        minHeight = v;
    }

    public void setMaxHeight(long v) {
        maxHeight = v;
    }

    public void setTimestamp(long v) {
        timestamp = v;
    }
}
