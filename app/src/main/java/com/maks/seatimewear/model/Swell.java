package com.maks.seatimewear.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.maks.seatimewear.model.i.forecastI;

import static com.maks.seatimewear.sql.DatabaseHelper.SPOT_ID;
import static com.maks.seatimewear.sql.DatabaseHelper.TIMESTAMP;

/**
 * Created by maks on 08/07/2017.
 * [_id, spot_id, state, shift, timestamp]
 */
@DatabaseTable
public class Swell implements forecastI {

    @DatabaseField(generatedId = true)
    private long id;


    @DatabaseField(canBeNull = false, columnName = SPOT_ID, foreign = true)
    private Spot spot;

    @DatabaseField(columnName = TIMESTAMP)
    private long timestamp;

    @DatabaseField
    private String compassDirection;

    @DatabaseField
    private long height;

    @DatabaseField
    private long period;

    @DatabaseField
    private long power;

    @DatabaseField
    private String unit;

    public Swell() {}

    public long getId() {
        return id;
    }

    public void setId(long v) {
        id = v;
    }


    public void setSpot(Spot v) {
        spot = v;
    }

    public void setTimestamp(long v) {
        timestamp = v;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
