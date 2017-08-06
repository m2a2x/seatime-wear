package com.maks.seatimewear.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.maks.seatimewear.model.i.ForecastI;

import static com.maks.seatimewear.sql.DatabaseHelper.SPOT_ID;
import static com.maks.seatimewear.sql.DatabaseHelper.TIMESTAMP;

@DatabaseTable
public class Condition implements ForecastI {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long spot_id;

    @DatabaseField(columnName = TIMESTAMP)
    private long timestamp;

    @DatabaseField
    private long temperature;

    @DatabaseField
    private long weather;

    @DatabaseField
    private String unit;

    public Condition() {}

    public void setSpot(long id) {
        spot_id = id;
    }

    public void setTimestamp(long v) {
        timestamp = v;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
