package com.maks.seatimewear;

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
import com.maks.seatimewear.model.ConditionCollection;
import com.maks.seatimewear.model.ConditionItem;
import com.maks.seatimewear.model.ForecastItem;
import com.maks.seatimewear.model.Option;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.network.NetworkFragment;
import com.maks.seatimewear.network.RequestHelper;
import com.maks.seatimewear.network.VolleyRequestController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.maks.seatimewear.model.Option.STATUS_NOTPAIRED;
import static com.maks.seatimewear.model.Option.STATUS_PAIRED;
import static com.maks.seatimewear.utils.Utils.getDayAfterTodayUnix;

/**
 * {@link Fragment} subclass.
 * For pair data between device and web service
 */

public class PairDataFragment extends Fragment {
    public static final String TAG = "PairHelper";

    OnPairDataListener mCallback;

    private String mStatus;
    private String mTimestamp;



    private Handler mRequestRefreshHandler = new Handler();
    private Runnable mRequestRefreshRunnable = new Runnable() {
        @Override
        public void run() {
        if (getNetwork().isNetwork()) {
            startPair(mTimestamp);
        }
        }
    };

    private Handler mRequestHandler = new Handler();
    private Runnable mRequestRunnable = new Runnable() {
        @Override
        public void run() {
            pairData();
        }
    };


    public interface OnPairDataListener {
        /** Called by PairDataFragment when Pair updated */
        void onGlobalDataUpdate(ArrayList<Spot> spots, ConditionCollection conditions, String timestamp);
        void onGlobalDataUpdate();

        void onPairAwait(String pair);
        void onDataPaired(String status);
        void onPairFinished();
    }

    private static final String url= BuildConfig.URL + "pair";

    private String pairNumber;
    private String uuidKey;
    private String timestamp;

    public PairDataFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        uuidKey = getArguments().getString("uuid");

        if (uuidKey == null || uuidKey.isEmpty()) {
            throw new RuntimeException("uuid key expected");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPair();
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

    public static PairDataFragment newInstance(String uuid) {
        PairDataFragment f = new PairDataFragment();
        Bundle args = new Bundle();
        args.putString("uuid", uuid);
        f.setArguments(args);
        return f;
    }

    public void startPair(String _timestamp) {
        timestamp = _timestamp;
        mRequestRunnable.run();
    }

    public void stopPair() {
        mRequestHandler.removeCallbacks(mRequestRunnable);
        mRequestRefreshHandler.removeCallbacks(mRequestRefreshRunnable);
        mCallback.onPairFinished();
    }

    private void pairData() {
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            pairNumber = response.optString("pair");
            if (pairNumber.isEmpty()) {
                mCallback.onDataPaired(STATUS_PAIRED);
                globalDataMapping(response);
                stopPair();
                return;
            }
            mCallback.onPairAwait(pairNumber);
            mRequestHandler.postDelayed(mRequestRunnable, BuildConfig.NETWORK_REPEAT_REQUEST_DELAY);
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
        params.put("end", Long.toString(getDayAfterTodayUnix(BuildConfig.INIT_PRELOAD_DAYS)));
        params.put("device", android.os.Build.BRAND);
        if (!timestamp.isEmpty()) {
            params.put("timestamp", timestamp);
        }

        RequestHelper request = new RequestHelper(
            Request.Method.POST,
            url,
            params,
            responseListener,
            errorListener
        );

        request.setRetryPolicy(new DefaultRetryPolicy(BuildConfig.NETWORK_REPEAT_REQUEST_DELAY, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRequestController.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void globalDataMapping(JSONObject response) {
        if (response != null) {
            ConditionCollection conditions = null;
            JSONObject data = response.optJSONObject("result");

            if (data == null) {
                mCallback.onGlobalDataUpdate();
                return;
            }

            JSONArray dataSpot = data.optJSONArray("spots");
            String _timestamp = data.optString("timestamp");
            Type listSpotType = new TypeToken<ArrayList<Spot>>() {}.getType();
            ArrayList<Spot> spots = new Gson().fromJson(dataSpot.toString(), listSpotType);
            try {
                conditions = forecastDataMapping(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mCallback.onGlobalDataUpdate(spots, conditions, _timestamp);
        }
    }

    public static ConditionCollection forecastDataMapping(JSONObject response) throws JSONException {
        if (response != null) {

            String pairNumber = response.optString("Pair");
            if (!pairNumber.isEmpty()) {
                return null;
            }

            JSONArray forecast = response.getJSONArray("forecast");
            Type listFType = new TypeToken<ArrayList<ForecastItem>>() {}.getType();
            ArrayList<ForecastItem> forecasts = new Gson().fromJson(forecast.toString(), listFType);

            for (ForecastItem f: forecasts) {
                f.set();
            }

            JSONArray condition = response.getJSONArray("condition");
            Type listCType = new TypeToken<ArrayList<ConditionItem>>() {}.getType();
            ArrayList<ConditionItem> conditions = new Gson().fromJson(condition.toString(), listCType);


            for (ConditionItem c: conditions) {
                c.set();
            }
            return new ConditionCollection(conditions, forecasts);
        }
        return null;
    }


    public boolean isNeedPair(Option statusOption, Option timestampOption) {
        mStatus = statusOption != null ? statusOption.getValue() : STATUS_NOTPAIRED;
        mTimestamp = timestampOption != null ? timestampOption.getValue() : "";

        switch (mStatus) {
            case STATUS_NOTPAIRED:
                getNetwork().requireNetwork(new NetworkFragment.OnNetworkCheckCompleted() {
                    @Override
                    public void OnNetworkCheckCompleted(boolean isAvailable) {
                    startPair(mTimestamp);
                    }
                });
                return true;
            case STATUS_PAIRED:
            default:
                mRequestRefreshHandler.removeCallbacks(mRequestRefreshRunnable);
                mRequestRefreshHandler.postDelayed(mRequestRefreshRunnable, BuildConfig.NETWORK_REQUEST_DELAY);
                return false;
        }
    }

    private NetworkFragment getNetwork() {
        return (NetworkFragment) getFragmentManager().findFragmentByTag(NetworkFragment.TAG);
    }
}