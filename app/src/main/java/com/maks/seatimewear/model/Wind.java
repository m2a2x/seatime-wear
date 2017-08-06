package com.maks.seatimewear.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.maks.seatimewear.model.i.ForecastI;

import java.io.Serializable;

import static com.maks.seatimewear.sql.DatabaseHelper.SPOT_ID;
import static com.maks.seatimewear.sql.DatabaseHelper.TIMESTAMP;

/**
 * Created by maks on 08/07/2017.
 * [_id, spot_id, state, shift, timestamp]
 */
@DatabaseTable
public class Wind implements Serializable, ForecastI {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long spot_id;

    @DatabaseField
    private String compass;

    @DatabaseField
    private long speed;

    @DatabaseField(columnName = TIMESTAMP)
    private long timestamp;

    @DatabaseField
    private String unit;

    public Wind() { }
    
    public void setSpot(long id) {
        spot_id = id;
    }

    public void setTimestamp(long v) {
        timestamp = v;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getSpeed() {
        return speed;
    }

    public String getDirection() {
        return compass;
    }
}
