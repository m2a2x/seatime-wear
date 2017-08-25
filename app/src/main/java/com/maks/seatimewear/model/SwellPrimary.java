package com.maks.seatimewear.model;


import com.j256.ormlite.table.DatabaseTable;
import com.maks.seatimewear.model.i.ForecastI;
import java.io.Serializable;


/**
 * Created by maks on 08/07/2017.
 * [_id, spot_id, state, shift, timestamp]
 */
@DatabaseTable
public class SwellPrimary extends Swell implements Serializable, ForecastI {
    public SwellPrimary() {}
}
