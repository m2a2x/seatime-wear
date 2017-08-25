package com.maks.seatimewear.model;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

@DatabaseTable
public class Spot implements Serializable {
    @DatabaseField(id = true)
    private long _id;

    @DatabaseField
    private String name;

    @DatabaseField
    private long updatedAt;

    @DatabaseField
    private String timezone;

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public String getValue() {
        return name;
    }

    public String getTimezone() {
        return timezone;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return name;
    }
}
