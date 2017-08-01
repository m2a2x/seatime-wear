package com.maks.seatimewear.spot;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.maks.seatimewear.R;
import com.maks.seatimewear.components.PagerCountView;
import com.maks.seatimewear.model.Condition;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Swell;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.model.Wind;
import com.maks.seatimewear.model.i.forecastI;
import com.maks.seatimewear.network.PairConditionFragment;
import com.maks.seatimewear.sql.DatabaseHelper;
import com.maks.seatimewear.utils.Utils;

import java.sql.SQLException;
import java.util.ArrayList;

import static com.maks.seatimewear.sql.DatabaseHelper.SPOT_ID;
import static com.maks.seatimewear.sql.DatabaseHelper.TIMESTAMP;

public class SpotActivity extends FragmentActivity
        implements PairConditionFragment.OnPairConditionListener, SpotScreenAdapter.TodayTidesCallback {

    private DatabaseHelper databaseHelper = null;
    private int id;
    private PagerAdapter mPagerAdapter;
    private PagerCountView mPagerCountView;
    private ViewPager mPager;

    private Spot currentSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String UUID = this.getIntent().getExtras().getString("uuid");
        id = (int) this.getIntent().getExtras().getLong("id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);

        try {
            Dao<Spot, Integer> dao = getHelper().getSpotDao();
            currentSpot = dao.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        PairConditionFragment conditionFragment =
                (PairConditionFragment) getFragmentManager().findFragmentByTag(PairConditionFragment.TAG);

        if (conditionFragment == null) {
            conditionFragment = PairConditionFragment.newInstance(UUID, currentSpot.getId());
            getFragmentManager()
                    .beginTransaction()
                    .add(conditionFragment, PairConditionFragment.TAG)
                    .commit();
        }


        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerCountView = (PagerCountView) findViewById(R.id.pagerCount);
        mPagerCountView.setPage(1);
        mPagerAdapter = new SpotScreenAdapter(getSupportFragmentManager(), currentSpot, this);
        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mPagerCountView.setPage(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    public void onSpotDataUpdated(ArrayList<PairConditionFragment.ForecastItem> forecasts,
                                  ArrayList<PairConditionFragment.ConditionItem> conditions) {
        try {
            Dao<Tide, Integer> daoTide = getHelper().getTideDao();
            Dao<Swell, Integer> daoSwell= getHelper().getSwellDao();
            Dao<Wind, Integer> daoWind = getHelper().getWindDao();
            Dao<Condition, Integer> daoCondition= getHelper().getConditionDao();

            for (PairConditionFragment.ConditionItem c: conditions) {
                for (Tide tide : c.tide) {
                    tide.setSpot(currentSpot);
                    daoUpdate(daoTide, tide);
                }
            }

            for (PairConditionFragment.ForecastItem f: forecasts) {
                f.swell.setSpot(currentSpot);
                f.swell.setTimestamp(f.timestamp);

                daoUpdate(daoSwell, f.swell);


                f.wind.setSpot(currentSpot);
                f.wind.setTimestamp(f.timestamp);

                daoUpdate(daoWind, f.wind);

                f.condition.setSpot(currentSpot);
                f.condition.setTimestamp(f.timestamp);

                daoUpdate(daoCondition, f.condition);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        mPager.setAdapter(mPagerAdapter);
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

    private <T extends forecastI> void
        daoUpdate(Dao<T, Integer> dao, T sItem)
            throws SQLException  {
        T dbItem = getByTimestamp(
                sItem.getTimestamp(),
                dao.queryBuilder()
        );

        if (dbItem != null) {
            dao.update(dbItem);
        } else {
            dao.create(sItem);
        }
    }

    private <T>T getByTimestamp (long timestamp, QueryBuilder<T, Integer> queryBuilder) throws SQLException {
        ArrayList<T> items;


        queryBuilder.where()
                .eq("spot_id", id)
                .and()
                .eq("timestamp", timestamp);

        items = new ArrayList<>(queryBuilder.query());
        if (items.size() > 0) {
            return items.get(0);
        }
        return null;
    }
}
