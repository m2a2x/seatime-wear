package com.maks.seatimewear.network;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maks.seatimewear.model.Spot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * {@link Fragment} subclass.
 * For pair data between device and web service
 */
public class PairDataFragment extends Fragment {
    public static final String TAG = "PairHelper";
    public String STATUS_PAIRED = "PAIRED";
    private int REQUEST_DELAY = 5000;

    OnPairDataListener mCallback;
    Handler mHandler = new Handler();

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            pairData();
        }
    };

    public interface OnPairDataListener {
        /** Called by PairDataFragment when Pair updated */
        void onGlobalDataUpdate(ArrayList<Spot> spots);
        void onPairAwait(String pair);
        void onDataPaired(String status);
        void onPairFinished();
    }

    private static final String url="http://10.0.2.2:3000/apiDevice/pair";
    //String url = "https://seatime.herokuapp.com/apiDevice/";

    private String pairNumber;
    private String uuidKey;

    public PairDataFragment() {}

    public static PairDataFragment newInstance(String uuid) {
        PairDataFragment f = new PairDataFragment();
        Bundle args = new Bundle();
        args.putString("uuid", uuid);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        uuidKey = getArguments().getString("uuid");
    }

    public void startPair() {
        mRunnable.run();
    }

    public void onPause() {
        super.onPause();
        stopPair();
    }

    public void stopPair() {
        mHandler.removeCallbacks(mRunnable);
        mCallback.onPairFinished();
    }

    private void pairData() {
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pairNumber = response.optString("Pair");
                if (pairNumber.isEmpty()) {
                    mCallback.onDataPaired(STATUS_PAIRED);
                    dataMapping(response);
                    stopPair();
                    return;
                }
                mCallback.onPairAwait(pairNumber);
                mHandler.postDelayed(mRunnable, REQUEST_DELAY);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put("uuid", uuidKey);
        params.put("device", android.os.Build.BRAND);

        RequestHelper request = new RequestHelper(
            Request.Method.POST,
            url,
            params,
            responseListener,
            errorListener
        );

        request.setRetryPolicy(new DefaultRetryPolicy(REQUEST_DELAY, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRequestController.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void dataMapping(JSONObject response) {
        if (response != null) {
            JSONObject data = response.optJSONObject("PairedData");

            JSONArray dataSpot = data.optJSONArray("spots");
            Type listSpotType = new TypeToken<ArrayList<Spot>>() {}.getType();
            ArrayList<Spot> spots = new Gson().fromJson(dataSpot.toString(), listSpotType);
            mCallback.onGlobalDataUpdate(spots);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPairDataListener) {
            mCallback = (OnPairDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPairDataListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopPair();
        mCallback = null;
    }
}
