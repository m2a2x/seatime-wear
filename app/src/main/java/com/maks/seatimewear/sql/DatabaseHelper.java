package com.maks.seatimewear.sql;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.maks.seatimewear.model.Option;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Tide;

/**
 * Database helper which creates and upgrades the database and provides the DAOs for the app.
 *
 * @author kevingalligan
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    /************************************************
     * Suggested Copy/Paste code. Everything from here to the done block.
     ************************************************/

    private static final String DATABASE_NAME = "seatime.db";
    private static final int DATABASE_VERSION = 2;

    private Dao<Option, Integer> optionDao;
    private Dao<Spot, Integer> spotDao;
    private Dao<Tide, Integer> tideDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /************************************************
     * Suggested Copy/Paste Done
     ************************************************/

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Option.class);
            TableUtils.createTable(connectionSource, Spot.class);
            TableUtils.createTable(connectionSource, Tide.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            TableUtils.dropTable(connectionSource, Option.class, true);
            TableUtils.dropTable(connectionSource, Spot.class, true);
            TableUtils.dropTable(connectionSource, Tide.class, true);
            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
                    + newVer, e);
        }
    }

    public Dao<Option, Integer> getOptionDao() throws SQLException {
        if (optionDao == null) {
            optionDao = getDao(Option.class);
        }
        return optionDao;
    }

    @Nullable
    public Option getOptionByKey(String value) throws SQLException {
        optionDao = getOptionDao();
        List<Option> options = optionDao.queryForEq("key", value);
        if (options.size() > 0) {
            return options.get(0);
        }
        return null;
    }

    public Dao<Spot, Integer> getSpotDao() throws SQLException {
        if (spotDao == null) {
            spotDao = getDao(Spot.class);
        }
        return spotDao;
    }

    public Dao<Tide, Integer> getTideDao() throws SQLException {
        if (tideDao == null) {
            tideDao = getDao(Tide.class);
        }
        return tideDao;
    }
}
