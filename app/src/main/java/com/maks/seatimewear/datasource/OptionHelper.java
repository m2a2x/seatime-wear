package com.maks.seatimewear.datasource;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maks.seatimewear.model.Option;
import com.maks.seatimewear.sql.SeaSQLiteHelper;

import java.util.HashMap;
import java.util.Map;


public class OptionHelper {
    private String[] allColumns = { SeaSQLiteHelper.COLUMN_ID, SeaSQLiteHelper.COLUMN_KEY, SeaSQLiteHelper.COLUMN_OPTION };
    SQLiteDatabase database;

    public OptionHelper(SQLiteDatabase db) {
        database = db;
    }

    public Map<String, String> getAllOptions() {
        Map<String, String> options = new HashMap<>();

        Cursor cursor = database.query(SeaSQLiteHelper.TABLE_USEROPTIONS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Option option = cursorToOption(cursor);
            options.put(option.getKey(), option.getValue());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return options;
    }

    public Option createOption(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(SeaSQLiteHelper.COLUMN_KEY, key);
        values.put(SeaSQLiteHelper.COLUMN_OPTION, value);

        long insertId = database.insert(SeaSQLiteHelper.TABLE_USEROPTIONS, null, values);
        Cursor cursor = database.query(SeaSQLiteHelper.TABLE_USEROPTIONS,
                allColumns, SeaSQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Option newOption = cursorToOption(cursor);
        cursor.close();
        return newOption;
    }

    private Option cursorToOption(Cursor cursor) {
        Option option = new Option("k", "c");
        option.setId(cursor.getInt(0));
        option.setKey(cursor.getString(1));
        option.setValue(cursor.getString(2));
        return option;
    }
}
