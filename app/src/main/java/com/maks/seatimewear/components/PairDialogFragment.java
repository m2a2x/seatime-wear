package com.maks.seatimewear.components;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maks.seatimewear.R;

public class PairDialogFragment extends DialogFragment {
    public static final String TAG = "PairDialog";
    String mPair;
    TextView mText;


    public static PairDialogFragment newInstance(String pair) {
        PairDialogFragment f = new PairDialogFragment();
        Bundle args = new Bundle();
        args.putString("pair", pair);
        f.setArguments(args);

        return f;
    }

    public void onRefresh(String pair) {
        mPair = pair;
        mText.setText(mPair);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPair = getArguments().getString("pair");
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pair_dialog, container, false);
        mText = (TextView)v.findViewById(R.id.info_text);
        mText.setText(mPair);

        return v;
    }
}
