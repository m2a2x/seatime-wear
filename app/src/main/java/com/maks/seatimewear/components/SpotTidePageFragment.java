package com.maks.seatimewear.components;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maks.seatimewear.R;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Tide;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpotMainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpotTidePageFragment extends Fragment {
    private static final String ARG_PAGE = "Page";
    private static final String ARG_TIDES = "Tides";
    private int mPageNumber;
    private long spotId;
    private TideChart mTc;
    private ArrayList<Tide> mTides;


    private TextView mSpotText;
    private ArrayAdapter mListAdapter;
    private ListView mListView;


    public SpotTidePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param page Page number.
     * @return A new instance of fragment SpotMainPageFragment.
     */
    public static SpotTidePageFragment newInstance(int page, ArrayList<Tide> tides, Spot spot) {
        SpotTidePageFragment fragment = new SpotTidePageFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putSerializable(ARG_TIDES, tides);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPageNumber = getArguments().getInt(ARG_PAGE);
            mTides = (ArrayList<Tide>) getArguments().getSerializable(ARG_TIDES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_spot_tide_page, container, false);
        // mListView = (ListView) rootView.findViewById(R.id.listview);

        // mListAdapter = new ArrayAdapter(rootView.getContext(), android.R.layout.simple_list_item_1);
       //  mSpotText = (TextView) rootView.findViewById(R.id.spot_text);




        // mSpotText.setText(currentSpot.getValue());

        this.mTc = (TideChart) rootView.findViewById(R.id.TideChart);
        this.mTc.setTides(mTides);

        // mListAdapter.addAll(mTides);
        // mListView.setAdapter(mListAdapter);

        // Inflate the layout for this fragment
        return rootView;
    }
}