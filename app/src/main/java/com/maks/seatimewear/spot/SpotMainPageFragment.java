package com.maks.seatimewear.spot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maks.seatimewear.R;
import com.maks.seatimewear.model.Spot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpotMainPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpotMainPageFragment extends Fragment {
    private static final String ARG_PAGE = "Page";
    private static final String ARG_SPOT = "Spot";

    private int mPageNumber;
    private Spot mSpot;
    TextView mName;


    public SpotMainPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param page Page number.
     * @return A new instance of fragment SpotMainPageFragment.
     */
    public static SpotMainPageFragment newInstance(int page, Spot spot) {
        SpotMainPageFragment fragment = new SpotMainPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putSerializable(ARG_SPOT, spot);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPageNumber = getArguments().getInt(ARG_PAGE);
            mSpot = (Spot) getArguments().getSerializable(ARG_SPOT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_spot_main_page, container, false);

        mName = (TextView) rootView.findViewById(R.id.spot_name);
        mName.setText(mSpot.getValue());
        return rootView;
    }
}
