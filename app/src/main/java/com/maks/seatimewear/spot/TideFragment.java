package com.maks.seatimewear.spot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maks.seatimewear.R;
import com.maks.seatimewear.components.TideChart;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Tide;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpotMainData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TideFragment extends Fragment {
    private static final String ARG_PAGE = "Page";
    private static final String ARG_TIDES = "Tides";
    private static final String ARG_SPOT = "Spot";

    private ArrayList<Tide> mTides;
    private Spot mSpot;
    int mPageNumber;
    TideChart mTc;

    TextView mName;


    public TideFragment() {}

    public static TideFragment newInstance(int page, ArrayList<Tide> tides, Spot spot) {
        TideFragment fragment = new TideFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putSerializable(ARG_TIDES, tides);
        args.putSerializable(ARG_SPOT, spot);

        fragment.setArguments(args);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPageNumber = getArguments().getInt(ARG_PAGE);
            mTides = (ArrayList<Tide>) getArguments().getSerializable(ARG_TIDES);
            mSpot = (Spot) getArguments().getSerializable(ARG_SPOT);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.spot_additional_data, container, false);
        this.mTc = (TideChart) rootView.findViewById(R.id.TideChart);
        // this.mTc.setTides(mTides);

        mName = (TextView) rootView.findViewById(R.id.spot_name);
        mName.setText(mSpot.getValue());

        return rootView;
    }
}
