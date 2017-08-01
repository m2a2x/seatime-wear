package com.maks.seatimewear.network;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maks.seatimewear.model.Condition;
import com.maks.seatimewear.model.Swell;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.model.Wind;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.maks.seatimewear.utils.Utils.getDayAfterTodayUnix;

/**
 * {@link Fragment} subclass.
 * For pair data between device and web service
 */
public class PairConditionFragment extends Fragment {
    public static final String TAG = "PairConditionHelper";
    private int REQUEST_DELAY = 5000;

    OnPairConditionListener mCallback;

    public interface OnPairConditionListener {
        void onSpotDataUpdated(ArrayList<ForecastItem> forecasts,
                              ArrayList<ConditionItem> conditions);
    }

    private static final String url="http://10.0.2.2:3000/apiDevice/load";
    //String url = "https://seatime.herokuapp.com/apiDevice/";

    private String uuidKey;
    private static int spotId;

    public PairConditionFragment() {}

    public static PairConditionFragment newInstance(String uuid, long id) {
        PairConditionFragment f = new PairConditionFragment();
        Bundle args = new Bundle();
        args.putString("uuid", uuid);
        args.putLong("spotId", id);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uuidKey = getArguments().getString("uuid");
        spotId = (int)getArguments().getLong("spotId");

        if (uuidKey == null || uuidKey.isEmpty()) {
            throw new RuntimeException("uuidKey expected");
        }
        startPair();
    }

    public void startPair() {
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            try {
                dataMapping(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        params.put("spot", Integer.toString(spotId));
        params.put("end", Long.toString(getDayAfterTodayUnix(4)));

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

    public void onPause() {
        super.onPause();
        //stopPair();
    }



    private void dataMapping(JSONObject response)  throws JSONException {
        if (response != null) {

            String pairNumber = response.optString("Pair");
            if (!pairNumber.isEmpty()) {
                return;
            }

            JSONArray forecast = response.getJSONArray("forecast");
            Type listFType = new TypeToken<ArrayList<ForecastItem>>() {}.getType();
            ArrayList<ForecastItem> forecasts = new Gson().fromJson(forecast.toString(), listFType);


            JSONArray condition = response.getJSONArray("condition");
            Type listCType = new TypeToken<ArrayList<ConditionItem>>() {}.getType();
            ArrayList<ConditionItem> conditions = new Gson().fromJson(condition.toString(), listCType);
            mCallback.onSpotDataUpdated(forecasts, conditions);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPairConditionListener) {
            mCallback = (OnPairConditionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPairDataListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // stopPair();
        mCallback = null;
    }


    public class ForecastItem {
        public long timestamp;
        public int rating;
        public Swell swell;
        public Wind wind;
        public Condition condition;
    }

    public class ConditionItem {
        int _id;
        public ArrayList<Tide> tide;
    }
}
