package com.maks.seatimewear.model;

/**
 * Created by maks on 07/07/2017.
 */

public class Spot {
    private long _id;
    private String name;
    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public String getValue() {
        return name;
    }

    public void setValue(String name) {
        this.name = name;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return name;
    }
}
