package com.maks.seatimewear.model;

public class ForecastItem {
    public long timestamp;
    public long rating;
    public long spot_id;
    public SwellItem swell;
    public Wind wind;
    public Condition condition;

    public void set() {
        swell.combined.setSpot(spot_id);
        swell.combined.setTimestamp(timestamp);

        swell.primary.setSpot(spot_id);
        swell.primary.setTimestamp(timestamp);

        swell.secondary.setSpot(spot_id);
        swell.secondary.setTimestamp(timestamp);

        wind.setSpot(spot_id);
        wind.setTimestamp(timestamp);
        condition.setSpot(spot_id);
        condition.setTimestamp(timestamp);
    }
}
