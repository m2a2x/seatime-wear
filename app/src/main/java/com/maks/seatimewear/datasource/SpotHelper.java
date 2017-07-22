package com.maks.seatimewear.datasource;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.sql.SeaSQLiteHelper;
import java.util.ArrayList;

public class SpotHelper {
    private String[] allColumns = { SeaSQLiteHelper.COLUMN_ID, SeaSQLiteHelper.COLUMN_NAME };
    private SQLiteDatabase database;
    private String TABLE = SeaSQLiteHelper.TABLE_SPOTS;

    public SpotHelper(SQLiteDatabase db) {
        database = db;
    }

    public ArrayList<Spot> getAllSpots() {
        ArrayList<Spot> items = new ArrayList();

        Cursor cursor = database.query(TABLE, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Spot item = cursorToSpot(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    public Spot findById(long id) {
        String where =  SeaSQLiteHelper.COLUMN_ID + "=" + id;
        Cursor cursor = database.query(TABLE, null, where, null, null, null, null, null);
        cursor.moveToFirst();
        Spot spot = cursorToSpot(cursor);
        cursor.close();
        return spot;
    }

    public void createItems(ArrayList<Spot> spots) {
        database.beginTransaction();
        database.execSQL("delete from "+ TABLE);
        try {
            ContentValues values = new ContentValues();
            for (Spot spot: spots) {
                values.put(SeaSQLiteHelper.COLUMN_ID, spot.getId());
                values.put(SeaSQLiteHelper.COLUMN_NAME, spot.getValue());
                database.insert(TABLE, null, values);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private Spot cursorToSpot(Cursor cursor) {
        Spot item = new Spot();
        item.setId(cursor.getInt(0));
        item.setValue(cursor.getString(1));
        return item;
    }
}
