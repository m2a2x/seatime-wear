package com.maks.seatimewear.network;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.maks.seatimewear.model.ConditionCollection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.maks.seatimewear.SystemConfiguration.getService;
import static com.maks.seatimewear.SystemConfiguration.NETWORK_REPEAT_REQUEST_DELAY;
import static com.maks.seatimewear.network.PairDataFragment.forecastDataMapping;
import static com.maks.seatimewear.utils.Utils.getDayAfterTodayUnix;

/**
 * {@link Fragment} subclass.
 * For pair data between device and web service
 */
public class PairConditionFragment extends Fragment {
    public static final String TAG = "PairConditionHelper";

    OnPairConditionListener mCallback;

    public interface OnPairConditionListener {
        void onSpotDataUpdated(ConditionCollection conditions);
    }

    private static final String url= getService() + "load";

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
                ConditionCollection conditions = forecastDataMapping(response);
                mCallback.onSpotDataUpdated(conditions);
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

        request.setRetryPolicy(new DefaultRetryPolicy(NETWORK_REPEAT_REQUEST_DELAY, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyRequestController.getInstance(getActivity()).addToRequestQueue(request);
    }

    public void onPause() {
        super.onPause();
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
}
