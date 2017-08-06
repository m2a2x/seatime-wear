package com.maks.seatimewear.model.i;

/**
 * Created by maks on 01/08/2017.
 */

public interface ForecastI {
    long spot_id = 0;

    long getTimestamp();

    void setTimestamp(long v);
}