package com.maks.seatimewear.sql;

import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

public class SeaSQLiteHelper extends SQLiteOpenHelper {
    // Common
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SPOT_ID = "spot_id";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_SHIFT = "shift";
    public static final String COLUMN_TIME = "timestamp";

    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_OPTION = "option";
    public static final String COLUMN_NAME = "name";



    /*
     * UserOptions
     * [_id, key, option]
     * */
    public static final String TABLE_USEROPTIONS = "UserOptions";

    private static final String TABLE_USEROPTIONS_CREATE =
            "create table " + TABLE_USEROPTIONS + "( "
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_KEY + " text not null, "
                    + COLUMN_OPTION + " text not null);";

    /*
    * Spot
    * [_id, name]
    * */
    public static final String TABLE_SPOTS = "Spots";

    private static final String TABLE_SPOTS_CREATE =
            "create table " + TABLE_SPOTS + "( "
                    + COLUMN_ID + " integer primary key, "
                    + COLUMN_NAME + " text not null);";

    /*
    * Condition
    * [_id, spot_id]
    * */
    public static final String TABLE_CONDITION = "Conditions";

    private static final String TABLE_CONDITION_CREATE =
            "create table " + TABLE_CONDITION + "( "
                    + COLUMN_ID + " text primary key, "
                    + COLUMN_SPOT_ID + " integer not null);";


    /*
    * Tide
    * [_id, spot_id, state, shift, timestamp]
    * */
    public static final String TABLE_TIDE = "Tides";

    private static final String TABLE_TIDE_CREATE =
            "create table " + TABLE_TIDE + "( "
                    + COLUMN_ID + " text primary key, "
                    + COLUMN_SPOT_ID + " integer not null, "
                    + COLUMN_STATE + " text not null, "
                    + COLUMN_SHIFT + " text not null, "
                    + COLUMN_TIME + " integer not null);";

    /*
    * Database options
    * */
    private static final String DATABASE_NAME = "Seatime.db";
    private static final int DATABASE_VERSION = 9;




    public SeaSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_USEROPTIONS_CREATE);
        database.execSQL(TABLE_SPOTS_CREATE);
        database.execSQL(TABLE_CONDITION_CREATE);
        database.execSQL(TABLE_TIDE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SeaSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USEROPTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPOTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONDITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIDE);
        onCreate(db);
    }

}