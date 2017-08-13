package com.maks.seatimewear.spot;

import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SnapHelper;
import android.support.wearable.view.WearableRecyclerView;
import android.view.Gravity;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.maks.seatimewear.R;
import com.maks.seatimewear.utils.GravitySnapHelper;
import com.maks.seatimewear.model.Condition;
import com.maks.seatimewear.model.ConditionCollection;
import com.maks.seatimewear.model.ConditionItem;
import com.maks.seatimewear.model.ForecastItem;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Swell;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.model.Wind;
import com.maks.seatimewear.model.i.ForecastI;
import com.maks.seatimewear.network.NetworkFragment;
import com.maks.seatimewear.network.PairConditionFragment;
import com.maks.seatimewear.sql.DatabaseHelper;
import com.maks.seatimewear.utils.RecyclerFixedHeader;
import com.maks.seatimewear.utils.RecyclerTouchListener;
import com.maks.seatimewear.utils.Utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.maks.seatimewear.sql.DatabaseHelper.SPOT_ID;
import static com.maks.seatimewear.sql.DatabaseHelper.TIMESTAMP;

public class SpotActivity
        extends FragmentActivity
        implements PairConditionFragment.OnPairConditionListener,
        ScreenAdapter.ScreenDataCallback {

    private DatabaseHelper databaseHelper = null;
    private int id;


    NetworkFragment networkFragment;
    WearableRecyclerView recyclerView;
    ViewerAdapter mViewAdapter;
    Spot currentSpot;
    RecyclerFixedHeader sectionItemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);

        String UUID = this.getIntent().getExtras().getString("uuid");
        id = (int) this.getIntent().getExtras().getLong("id");
        try {
            Dao<Spot, Integer> dao = getHelper().getSpotDao();
            currentSpot = dao.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        networkFragment =
            (NetworkFragment) getFragmentManager().findFragmentByTag(NetworkFragment.TAG);

        if (networkFragment == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            networkFragment = NetworkFragment.newInstance();
            ft.add(networkFragment, NetworkFragment.TAG);
            ft.commit();
        }


        PairConditionFragment conditionFragment =
                (PairConditionFragment) getFragmentManager().findFragmentByTag(PairConditionFragment.TAG);

        if (conditionFragment == null) {
            conditionFragment = PairConditionFragment.newInstance(UUID, currentSpot);
            getFragmentManager()
                    .beginTransaction()
                    .add(conditionFragment, PairConditionFragment.TAG)
                    .commit();
        }


        recyclerView = (WearableRecyclerView) findViewById(R.id.recycler_view);
        SnapHelper snapHelper = new GravitySnapHelper(Gravity.TOP);
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(false);

        sectionItemDecoration =
                new RecyclerFixedHeader(
                    getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height));
        recyclerView.addItemDecoration(sectionItemDecoration);

        PagerAdapter mPagerAdapter = new ScreenAdapter(getSupportFragmentManager(), currentSpot, this);
        mViewAdapter = new ViewerAdapter(this, mPagerAdapter);
        recyclerView.setAdapter(mViewAdapter);
        recyclerView.getLayoutManager().scrollToPosition(1);
        recyclerView.addOnItemTouchListener(
            new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.OnTouchActionListener() {
                    @Override
                    public void onClick(View view, int position, float y) {
                        if (sectionItemDecoration.isItem(y)) {
                            recyclerView.getLayoutManager().scrollToPosition(0);
                        }
                    }
                    @Override
                    public void onRightSwipe(View view, int position) {
                    }
                    @Override
                    public void onLeftSwipe(View view, int position) {
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void onDataDeprecated(boolean isDeprecated) {
        mViewAdapter.setDepracated(isDeprecated);
    }

    public void onSpotDataUpdated(ConditionCollection conditions) {
        final SQLiteDatabase db = getHelper().getWritableDatabase();
        db.beginTransaction();
        try {
            Dao<Tide, Integer> daoTide = getHelper().getTideDao();
            Dao<Swell, Integer> daoSwell= getHelper().getSwellDao();
            Dao<Wind, Integer> daoWind = getHelper().getWindDao();
            Dao<Condition, Integer> daoCondition= getHelper().getConditionDao();

            daoRemove(daoTide);
            daoRemove(daoSwell);
            daoRemove(daoWind);
            daoRemove(daoCondition);

            for (ConditionItem c: conditions.conditions) {
                for (Tide tide : c.tide) {
                    daoTide.create(tide);
                }
            }

            for (ForecastItem f: conditions.forecasts) {
                daoSwell.create(f.swell);
                daoWind.create(f.wind);
                daoCondition.create(f.condition);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            db.endTransaction();
        }

        //mPager.setAdapter(mPagerAdapter);
    }

    public ArrayList<Tide> getTodayTides() {
        ArrayList<Tide> tides;
        try {
            QueryBuilder<Tide, Integer> queryBuilder =
                    getHelper().getTideDao().queryBuilder();


            queryBuilder.where()
                    .eq(SPOT_ID, id)
                    .and()
                    .between(TIMESTAMP, Utils.currentTimeUnix(), Utils.getEndOfDayUnix());


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
                .eq(SPOT_ID, id)
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
        deleteBuilder.where().eq("spot_id", id);
        deleteBuilder.delete();
    }
}
