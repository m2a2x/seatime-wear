package com.maks.seatimewear.spot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maks.seatimewear.R;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Swell;
import com.maks.seatimewear.model.Wind;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpotMainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpotMainPageFragment extends Fragment {
    private static final String ARG_PAGE = "Page";
    private static final String ARG_SPOT = "Spot";
    private static final String ARG_SWELL = "Swell";
    private static final String ARG_WIND = "Wind";

    private int mPageNumber;
    private Spot mSpot;
    private Swell mSwell;
    private Wind mWind;


    public SpotMainPageFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param page Page number.
     * @return A new instance of fragment SpotMainPageFragment.
     */
    public static SpotMainPageFragment newInstance(int page, Swell swell, Wind wind, Spot spot) {
        SpotMainPageFragment fragment = new SpotMainPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putSerializable(ARG_SPOT, spot);
        args.putSerializable(ARG_SWELL, swell);
        args.putSerializable(ARG_WIND, wind);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPageNumber = getArguments().getInt(ARG_PAGE);
            mSpot = (Spot) getArguments().getSerializable(ARG_SPOT);
            mSwell = (Swell) getArguments().getSerializable(ARG_SWELL);
            mWind = (Wind) getArguments().getSerializable(ARG_WIND);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_spot_main_page, container, false);

        TextView mName = (TextView) rootView.findViewById(R.id.spot_name);
        mName.setText(mSpot.getValue());

        if (mSwell != null) {
            TextView swellHeight = (TextView) rootView.findViewById(R.id.swell_height);
            swellHeight.setText(Long.toString(mSwell.getHeight()));

            TextView swellUnit = (TextView) rootView.findViewById(R.id.swell_unit);
            swellUnit.setText(mSwell.getUnit());

            TextView swellPeriod = (TextView) rootView.findViewById(R.id.swell_period);
            swellPeriod.setText(Long.toString(mSwell.getPeriod()));
        }

        if (mWind != null) {
            TextView windDir = (TextView) rootView.findViewById(R.id.wind_direction);
            windDir.setText(mWind.getDirection());

            TextView windSpeed = (TextView) rootView.findViewById(R.id.wind_speed);
            windSpeed.setText(Long.toString(mWind.getSpeed()));
        }


        return rootView;
    }
}
