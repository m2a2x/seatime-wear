package com.maks.seatimewear.network;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.maks.seatimewear.BuildConfig;
import com.maks.seatimewear.model.ConditionCollection;
import com.maks.seatimewear.model.Spot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import static com.maks.seatimewear.network.PairDataFragment.forecastDataMapping;
import static com.maks.seatimewear.utils.Utils.currentTimeUnix;
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
        void onDataDeprecated(boolean isDeprecated);
    }

    private static final String url= BuildConfig.URL + "load";

    private String uuidKey;
    private static Spot spot;

    public PairConditionFragment() {}

    public static PairConditionFragment newInstance(String uuid, Spot spot) {
        PairConditionFragment f = new PairConditionFragment();
        Bundle args = new Bundle();
        args.putString("uuid", uuid);
        args.putSerializable("spot", spot);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uuidKey = getArguments().getString("uuid");
        spot = (Spot)getArguments().getSerializable("spot");

        if (uuidKey == null || uuidKey.isEmpty()) {
            throw new RuntimeException("uuidKey expected");
        }

        if (currentTimeUnix() > spot.getUpdatedAt() + getDayAfterTodayUnix(1)) {
            NetworkFragment network = getNetwork();
            if (network != null && network.isNetwork()) {
                startPair();
            } else {
                mCallback.onDataDeprecated(true);
            }
        }
    }

    private NetworkFragment getNetwork() {
        return (NetworkFragment) getFragmentManager().findFragmentByTag(NetworkFragment.TAG);
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
        params.put("spot", Long.toString(spot.getId()));
        params.put("end", Long.toString(getDayAfterTodayUnix(BuildConfig.SPOT_PAGE_PRELOAD_DAYS)));

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
