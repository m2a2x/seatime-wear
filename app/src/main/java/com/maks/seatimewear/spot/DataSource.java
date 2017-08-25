package com.maks.seatimewear.spot;


import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.maks.seatimewear.model.Condition;
import com.maks.seatimewear.model.ConditionCollection;
import com.maks.seatimewear.model.ConditionItem;
import com.maks.seatimewear.model.ForecastItem;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Swell;
import com.maks.seatimewear.model.SwellPrimary;
import com.maks.seatimewear.model.SwellSecondary;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.model.Wind;
import com.maks.seatimewear.model.i.ForecastI;
import com.maks.seatimewear.sql.DatabaseHelper;
import com.maks.seatimewear.utils.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.maks.seatimewear.sql.DatabaseHelper.SPOT_ID;
import static com.maks.seatimewear.sql.DatabaseHelper.TIMESTAMP;

class DataSource implements ScreenAdapter.ScreenDataCallback {

    private DatabaseHelper databaseHelper = null;
    private Activity mActivity;
    private int currentSpotId;

    DataSource(Activity activity, int id) {
        this.mActivity = activity;
        this.currentSpotId = id;
    }

    public Spot getSpot() {
        try {
            com.j256.ormlite.dao.Dao<Spot, Integer> dao = getHelper().getSpotDao();
            return dao.queryForId(this.currentSpotId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this.mActivity, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    void setConditions(ConditionCollection conditions) {
        final SQLiteDatabase db = getHelper().getWritableDatabase();
        db.beginTransaction();
        try {
            Dao<Tide, Integer> daoTide = getHelper().getTideDao();

            Dao<Swell, Integer> daoSwell= getHelper().getSwellDao();
            Dao<SwellPrimary, Integer> daoPrimSwell= getHelper().getSwellPrimDao();
            Dao<SwellSecondary, Integer> daoSecSwell= getHelper().getSwellSecDao();

            Dao<Wind, Integer> daoWind = getHelper().getWindDao();
            Dao<Condition, Integer> daoCondition= getHelper().getConditionDao();

            daoRemove(daoTide);

            daoRemove(daoSwell);
            daoRemove(daoPrimSwell);
            daoRemove(daoSecSwell);

            daoRemove(daoWind);
            daoRemove(daoCondition);

            for (ConditionItem c: conditions.conditions) {
                for (Tide tide : c.tide) {
                    daoTide.create(tide);
                }
            }

            for (ForecastItem f: conditions.forecasts) {
                daoSwell.create(f.swell.combined);
                daoPrimSwell.create(f.swell.primary);
                daoSecSwell.create(f.swell.secondary);
                daoWind.create(f.wind);
                daoCondition.create(f.condition);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<Tide> getTodayTides() {
        ArrayList<Tide> tides;
        try {
            QueryBuilder<Tide, Integer> queryBuilder =
                    getHelper().getTideDao().queryBuilder();


            queryBuilder
                .limit(3L)
                .where()
                .eq(SPOT_ID, this.currentSpotId)
                .and()
                .between(TIMESTAMP, Utils.currentTimeUnix(), Utils.getDayAfterTodayUnix(1));

            tides = new ArrayList<>(queryBuilder.query());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tides;
    }

    public Swell getNowSwell() {
        Swell item;
        try {
            item = getNowForecast(getHelper().getSwellDao().queryBuilder());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return item;
    }

    public Wind getNowWind() {
        Wind item;
        try {
            item = getNowForecast(getHelper().getWindDao().queryBuilder());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return item;
    }

    public <T extends ForecastI> T getNowForecast(QueryBuilder<T, Integer> queryBuilder) throws SQLException {
        List<T> items;
        T item = null;
        queryBuilder.where()
            .eq(SPOT_ID, this.currentSpotId)
            .and()
            .between(TIMESTAMP, Utils.currentTimeUnix(), Utils.getEndOfDayUnix());
        items = queryBuilder.limit((long) 1).query();
        if (items.size() > 0) {
            item = items.get(0);
        }

        return  item;
    }

    private <T> void daoRemove(Dao<T, Integer> dao) throws SQLException {
        DeleteBuilder<T, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.where().eq("spot_id", this.currentSpotId);
        deleteBuilder.delete();
    }

    protected void onDestroy() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
