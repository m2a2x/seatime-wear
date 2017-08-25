package com.maks.seatimewear.model;

import java.util.ArrayList;


public class ConditionItem {
    long _id;
    public long spot_id;
    public ArrayList<Tide> tide;

    public void set() {
        for (Tide tide : this.tide) {
            tide.setSpot(spot_id);
        }
    }
}
