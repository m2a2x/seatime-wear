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
import com.maks.seatimewear.model.Condition;
import com.maks.seatimewear.model.Option;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Swell;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.model.Wind;

/**
 * Database helper which creates and upgrades the database and provides the DAOs for the app.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "seatime.db";
    private static final int DATABASE_VERSION = 7;


    public static final String TIMESTAMP = "timestamp";
    public static final String SPOT_ID = "spot_id";

    private Dao<Option, Integer> optionDao;
    private Dao<Spot, Integer> spotDao;
    private Dao<Tide, Integer> tideDao;
    private Dao<Swell, Integer> swellDao;
    private Dao<Condition, Integer> conditionDao;
    private Dao<Wind, Integer> windDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Option.class);
            TableUtils.createTable(connectionSource, Spot.class);
            TableUtils.createTable(connectionSource, Tide.class);
            TableUtils.createTable(connectionSource, Swell.class);
            TableUtils.createTable(connectionSource, Condition.class);
            TableUtils.createTable(connectionSource, Wind.class);
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
            TableUtils.dropTable(connectionSource, Swell.class, true);
            TableUtils.dropTable(connectionSource, Condition.class, true);
            TableUtils.dropTable(connectionSource, Wind.class, true);
            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
                    + newVer, e);
        }
    }

    public void dropData() {
        try {
            TableUtils.dropTable(connectionSource, Spot.class, true);
            TableUtils.dropTable(connectionSource, Tide.class, true);
            TableUtils.dropTable(connectionSource, Swell.class, true);
            TableUtils.dropTable(connectionSource, Condition.class, true);
            TableUtils.dropTable(connectionSource, Wind.class, true);

            TableUtils.createTable(connectionSource, Spot.class);
            TableUtils.createTable(connectionSource, Tide.class);
            TableUtils.createTable(connectionSource, Swell.class);
            TableUtils.createTable(connectionSource, Condition.class);
            TableUtils.createTable(connectionSource, Wind.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to drop database", e);
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

    public Dao<Swell, Integer> getSwellDao() throws SQLException {
        if (swellDao == null) {
            swellDao = getDao(Swell.class);
        }
        return swellDao;
    }

    public Dao<Condition, Integer> getConditionDao() throws SQLException {
        if (conditionDao == null) {
            conditionDao = getDao(Condition.class);
        }
        return conditionDao;
    }

    public Dao<Wind, Integer> getWindDao() throws SQLException {
        if (windDao == null) {
            windDao = getDao(Wind.class);
        }
        return windDao;
    }
}
