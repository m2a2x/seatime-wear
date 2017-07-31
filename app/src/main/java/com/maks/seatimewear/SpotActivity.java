package com.maks.seatimewear;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.maks.seatimewear.components.PagerCountView;
import com.maks.seatimewear.components.SpotMainPageFragment;
import com.maks.seatimewear.components.SpotTidePageFragment;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.network.PairConditionFragment;
import com.maks.seatimewear.sql.DatabaseHelper;
import com.maks.seatimewear.utils.Utils;

import java.sql.SQLException;
import java.util.ArrayList;

public class SpotActivity extends FragmentActivity implements PairConditionFragment.OnPairConditionListener {
    static final int SWELL_PAGE = 0;
    static final int TIDE_PAGE = 1;

    private DatabaseHelper databaseHelper = null;
    int id;
    private PagerAdapter mPagerAdapter;
    private PagerCountView mPagerCountView;
    private ViewPager mPager;

    private Spot currentSpot;

    /*TODO: Update selected page according async data from server*/

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
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
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

    public void onSpotDataUpdated(ArrayList<PairConditionFragment.ForecastItem> forecasts,
                                  ArrayList<PairConditionFragment.ConditionItem> conditions) {

        try {
            Dao<Tide, Integer> dao = getHelper().getTideDao();
            for (PairConditionFragment.ConditionItem c: conditions) {
                for (Tide tide : c.tide) {
                    tide.setSpot(currentSpot);
                    Tide t = getTideByTimestamp(tide.getTimestamp());
                    if (t != null) {
                        dao.update(t);
                    } else {
                        dao.create(tide);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * A simple pager adapter that represents N objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SWELL_PAGE: return SpotMainPageFragment.newInstance(position);
                case TIDE_PAGE: {
                    return SpotTidePageFragment.newInstance(position, getTodayTides(), currentSpot);
                }
                default: return SpotMainPageFragment.newInstance(position);
            }

        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private ArrayList<Tide> getTodayTides() {
        ArrayList<Tide> tides;
        try {
            QueryBuilder<Tide, Integer> queryBuilder =
                    getHelper().getTideDao().queryBuilder();


            queryBuilder.where()
                    .eq(Tide.SPOT_ID, id)
                    .and()
                    .between(Tide.TIMESTAMP, Utils.currentTimeUnix(), Utils.getEndOfDayUnix());


            tides = new ArrayList<>(queryBuilder.query());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tides;
    }

    private Tide getTideByTimestamp (long timestamp) throws SQLException {
        ArrayList<Tide> tides;
        QueryBuilder<Tide, Integer> queryBuilder =
                getHelper().getTideDao().queryBuilder();


        queryBuilder.where()
                .eq(Tide.SPOT_ID, id)
                .and()
                .eq(Tide.TIMESTAMP, timestamp);

        tides = new ArrayList<>(queryBuilder.query());
        if (tides.size() > 0) {
            return tides.get(0);
        }
        return null;
    }
}
