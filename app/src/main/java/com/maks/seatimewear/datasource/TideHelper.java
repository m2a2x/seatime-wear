package com.maks.seatimewear.datasource;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.sql.SeaSQLiteHelper;
import com.maks.seatimewear.utils.Utils;

import java.util.ArrayList;

/**
 * Created by maks on 08/07/2017.
 * [_id, spot_id, state, shift, timestamp]
 */

public class TideHelper {
    private String[] allColumns = {
            SeaSQLiteHelper.COLUMN_ID,
            SeaSQLiteHelper.COLUMN_STATE,
            SeaSQLiteHelper.COLUMN_TIME
    };

    private SQLiteDatabase database;
    private String TABLE = SeaSQLiteHelper.TABLE_TIDE;

    public TideHelper(SQLiteDatabase db) {
        database = db;
    }

    public ArrayList<Tide> getBySpot(long spot_id, long time) {

        ArrayList<Tide> items = new ArrayList();

        String where =  SeaSQLiteHelper.COLUMN_SPOT_ID + "=" + spot_id;
        where += " AND " + SeaSQLiteHelper.COLUMN_TIME + ">=" + Utils.currentTimeUnix();
        if (time != 0) {
            where += " AND " + SeaSQLiteHelper.COLUMN_TIME + "<=" + time;
        }

        Cursor cursor = database.query(TABLE, null, where, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tide item = cursorToTide(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    public ArrayList<Tide> getBySpot(long spot_id) {
        return this.getBySpot(spot_id, 0);
    }

    public void createItems(ArrayList<Tide> items) {
        database.beginTransaction();
        database.execSQL("delete from "+ TABLE);
        try {
            ContentValues values = new ContentValues();
            for (Tide item: items) {
                //item.putContentValues(values);
                database.insert(TABLE, null, values);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private Tide cursorToTide(Cursor cursor) {
        Tide item = new Tide();
        //item.update(cursor);
        return item;
    }
}
