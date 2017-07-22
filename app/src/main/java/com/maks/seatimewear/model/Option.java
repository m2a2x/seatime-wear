package com.maks.seatimewear.model;


public class Option {
    private long id;
    private String option;
    private String key;

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
