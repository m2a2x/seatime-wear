package com.maks.seatimewear.datasource;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.maks.seatimewear.model.Option;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.sql.SeaSQLiteHelper;
import com.maks.seatimewear.utils.Utils;

import java.util.ArrayList;
import java.util.Map;

public class UserDS {

    // Database fields
    SQLiteDatabase database;
    OptionHelper optionHelper;
    SpotHelper spotHelper;
    TideHelper tideHelper;

    private SeaSQLiteHelper dbHelper;

    public UserDS(Context context) {
        dbHelper = new SeaSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        optionHelper = new OptionHelper(database);
        spotHelper = new SpotHelper(database);
        tideHelper = new TideHelper(database);
    }

    public Map<String, String> getAllOptions() {
        return optionHelper.getAllOptions();
    }

    public Option createOption(String key, String value) {
        return optionHelper.createOption(key, value);
    }

    public ArrayList<Spot> getAllSpots() {
        return spotHelper.getAllSpots();
    }

    public void createSpots(ArrayList<Spot> items) {
        spotHelper.createItems(items);
    }

    public Spot findSpotById(long id) {
        return spotHelper.findById(id);
    }


    public void createTides(ArrayList<Tide> items) {
        tideHelper.createItems(items);
    }

    public ArrayList<Tide> getTidesBySpot(long id) {
        return tideHelper.getBySpot(id);
    }


    public ArrayList<Tide> getTidesTodayBySpot(long id) {
        return tideHelper.getBySpot(id, Utils.getEndOfDayUnix());
    }

    public void close() {
        dbHelper.close();
    }



    /*
    public User createComment(String comment) {
        ContentValues values = new ContentValues();
        values.put(SeaSQLiteHelper.COLUMN_COMMENT, comment);
        long insertId = database.insert(SeaSQLiteHelper.TABLE_COMMENTS, null,
                values);
        Cursor cursor = database.query(SeaSQLiteHelper.TABLE_COMMENTS,
                allColumns, SeaSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        User newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }
    public void deleteComment(User comment) {
        long id = comment.getId();
        System.out.println("User deleted with id: " + id);
        database.delete(SeaSQLiteHelper.TABLE_COMMENTS, SeaSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<User> getAllComments() {
        List<User> comments = new ArrayList<User>();

        Cursor cursor = database.query(SeaSQLiteHelper.TABLE_COMMENTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private User cursorToComment(Cursor cursor) {
        User comment = new User();
        comment.setId(cursor.getLong(0));
        comment.setComment(cursor.getString(1));
        return comment;
    }*/
}
