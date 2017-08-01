package com.maks.seatimewear.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.maks.seatimewear.model.i.forecastI;

import java.io.Serializable;

import static com.maks.seatimewear.sql.DatabaseHelper.SPOT_ID;
import static com.maks.seatimewear.sql.DatabaseHelper.TIMESTAMP;
import static com.maks.seatimewear.utils.Utils.timestampToTime;

/**
 * Created by maks on 08/07/2017.
 * [_id, spot_id, state, shift, timestamp]
 */
@DatabaseTable
public class Tide implements Serializable, forecastI {

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

    public void setSpot(Spot _spot) {
        spot = _spot;
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

    public String getState() {
        return state;
    }


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return state + ' ' + getTime();
    }
}
