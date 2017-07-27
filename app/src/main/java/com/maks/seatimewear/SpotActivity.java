package com.maks.seatimewear;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.maks.seatimewear.components.SpotMainPageFragment;
import com.maks.seatimewear.components.SpotTidePageFragment;
import com.maks.seatimewear.datasource.UserDS;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Tide;

import java.util.ArrayList;

public class SpotActivity extends FragmentActivity {
    long id;
    private PagerAdapter mPagerAdapter;
    private ViewPager mPager;

    // DataSource
    private UserDS dataSource;
    private Spot currentSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = this.getIntent().getExtras().getLong("id");
        final Context currentContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);

        dataSource = new UserDS(currentContext);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
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


    /**
     * A simple pager adapter that represents 5  objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0: return SpotMainPageFragment.newInstance(position);
                case 1: {
                    dataSource.open();
                    currentSpot = dataSource.findSpotById(id);
                    ArrayList<Tide> tides = dataSource.getTidesTodayBySpot(id);
                    dataSource.close();
                    return SpotTidePageFragment.newInstance(position, tides, currentSpot);
                }
                default: return SpotMainPageFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
