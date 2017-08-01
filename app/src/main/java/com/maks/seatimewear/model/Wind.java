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
public class Wind implements forecastI {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, columnName = SPOT_ID, foreign = true)
    private Spot spot;

    @DatabaseField
    private String compass;

    @DatabaseField
    private long speed;

    @DatabaseField(columnName = TIMESTAMP)
    private long timestamp;

    @DatabaseField
    private String unit;

    public Wind() { }


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
