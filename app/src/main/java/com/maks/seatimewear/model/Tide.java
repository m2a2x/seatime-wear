package com.maks.seatimewear.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import static com.maks.seatimewear.utils.Utils.timestampToTime;

/**
 * Created by maks on 08/07/2017.
 * [_id, spot_id, state, shift, timestamp]
 */
@DatabaseTable
public class Tide implements Serializable {
    public static final String SPOT_ID = "spot_id";
    public static final String TIMESTAMP = "timestamp";

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, columnName = SPOT_ID, foreign = true)
    private Spot spot;

    @DatabaseField
    private String state;

    @DatabaseField
    private String shift;

    @DatabaseField(columnName = TIMESTAMP)
    private long timestamp;


    public long getId() {
        return id;
    }

    public void setId(long v) {
        id = v;
    }


    public void setSpot(Spot _spot) {
        spot = _spot;
    }


    public String getState() {
        return state;
    }

    public void setState(String v) {
        state = v;
    }

    public void setShift(String v) {
        shift = v;
    }

    public String getTime() {
        return timestampToTime(timestamp);
    }


    public void setTimestamp(long v) {
        timestamp = v;
    }

    public long getTimestamp() {
        return timestamp;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return state + ' ' + getTime();
    }
}
