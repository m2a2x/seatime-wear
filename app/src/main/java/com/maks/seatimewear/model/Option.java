package com.maks.seatimewear.model;

import java.io.Serializable;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;


@DatabaseTable
public class Option implements Serializable {

    public static final String STATUS_NOTPAIRED = "NotPaired";
    public static final String STATUS_PAIRED = "Paired";
    public static final String STATUS_AWAITING = "Awaiting";


    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String option;

    @DatabaseField
    private String key;

    public Option() {}

    public Option(String _key, String _option) {
        setValue(_option);
        setKey(_key);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public String getValue() {
        return option;
    }

    public void setValue(String option) {
        this.option = option;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return option;
    }
}
