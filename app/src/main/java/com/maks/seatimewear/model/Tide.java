package com.maks.seatimewear.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.maks.seatimewear.model.i.ForecastI;
import com.maks.seatimewear.utils.Utils;

import java.io.Serializable;

import static com.maks.seatimewear.sql.DatabaseHelper.SPOT_ID;
import static com.maks.seatimewear.sql.DatabaseHelper.TIMESTAMP;
import static com.maks.seatimewear.utils.Utils.timestampToTime;

/**
 * Created by maks on 08/07/2017.
 * [_id, spot_id, state, shift, timestamp]
 */
@DatabaseTable
public class Tide implements Serializable, ForecastI {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long spot_id;

    @DatabaseField
    private String state;

    @DatabaseField
    private String shift;

    @DatabaseField(columnName = TIMESTAMP)
    private long timestamp;

    public void setSpot(long id) {
        spot_id = id;
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
