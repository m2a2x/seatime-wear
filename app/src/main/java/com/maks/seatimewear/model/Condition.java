package com.maks.seatimewear.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.maks.seatimewear.model.i.forecastI;

import static com.maks.seatimewear.sql.DatabaseHelper.SPOT_ID;
import static com.maks.seatimewear.sql.DatabaseHelper.TIMESTAMP;

@DatabaseTable
public class Condition implements forecastI {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, columnName = SPOT_ID, foreign = true)
    private Spot spot;

    @DatabaseField(columnName = TIMESTAMP)
    private long timestamp;

    @DatabaseField
    private long temperature;

    @DatabaseField
    private long weather;

    @DatabaseField
    private String unit;

    public Condition() {}

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
