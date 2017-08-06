package com.maks.seatimewear.spot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Swell;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.model.Wind;

import java.util.ArrayList;


/**
 * A simple pager adapter that represents N objects, in
 * sequence.
 */

public class SpotScreenAdapter  extends FragmentStatePagerAdapter {

    static final int SWELL_PAGE = 0;
    static final int TIDE_PAGE = 1;
    private static  Spot currentSpot;
    private ScreenDataCallback mCallback;

    public interface ScreenDataCallback {
        ArrayList<Tide> getTodayTides();
        Swell getNowSwell();
        Wind getNowWind();
    }

    public SpotScreenAdapter(FragmentManager fm, Spot cSpot, final ScreenDataCallback callback) {
        super(fm);
        currentSpot = cSpot;
        mCallback = callback;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case SWELL_PAGE:
                return SpotMainPageFragment.newInstance(
                        position,
                        mCallback.getNowSwell(),
                        mCallback.getNowWind(),
                        currentSpot);
            case TIDE_PAGE: {
                return SpotTidePageFragment.newInstance(position, mCallback.getTodayTides(), currentSpot);
            }
            default: return SpotMainPageFragment.newInstance(
                    position,
                    mCallback.getNowSwell(),
                    mCallback.getNowWind(),
                    currentSpot);
        }

    }

    @Override
    public int getCount() {
        return 2;
    }
}