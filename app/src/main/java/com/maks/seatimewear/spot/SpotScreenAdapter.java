package com.maks.seatimewear.spot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Tide;

import java.util.ArrayList;


/**
 * A simple pager adapter that represents N objects, in
 * sequence.
 */

public class SpotScreenAdapter  extends FragmentStatePagerAdapter {

    static final int SWELL_PAGE = 0;
    static final int TIDE_PAGE = 1;
    private static  Spot currentSpot;
    private TodayTidesCallback mCallback;

    public interface TodayTidesCallback {
        ArrayList<Tide> getTodayTides();
    }

    public SpotScreenAdapter(FragmentManager fm, Spot cSpot, final TodayTidesCallback callback) {
        super(fm);
        currentSpot = cSpot;
        mCallback = callback;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case SWELL_PAGE: return SpotMainPageFragment.newInstance(position, currentSpot);
            case TIDE_PAGE: {
                return SpotTidePageFragment.newInstance(position, mCallback.getTodayTides(), currentSpot);
            }
            default: return SpotMainPageFragment.newInstance(position, currentSpot);
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}