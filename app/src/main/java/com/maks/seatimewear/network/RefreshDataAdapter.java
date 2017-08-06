package com.maks.seatimewear.network;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Handler;

import com.maks.seatimewear.model.Option;

import static com.maks.seatimewear.SystemConfiguration.NETWORK_REQUEST_DELAY;
import static com.maks.seatimewear.model.Option.STATUS_NOTPAIRED;
import static com.maks.seatimewear.model.Option.STATUS_PAIRED;

public class RefreshDataAdapter {
    private NetworkFragment networkFragment;
    private PairDataFragment pairDataFragment;
    private Handler mRequestHandler = new Handler();

    private String status;
    private String timestamp;



    private Runnable mRequestRunnable = new Runnable() {
        @Override
        public void run() {
        if (networkFragment.isNetwork()) {
            pairDataFragment.startPair(timestamp);
        }
        }
    };

    public RefreshDataAdapter(Activity activity, String UUID, NetworkFragment network) {
        networkFragment = network;

        pairDataFragment =
                (PairDataFragment) activity.getFragmentManager().findFragmentByTag(PairDataFragment.TAG);

        if (pairDataFragment == null) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            pairDataFragment = PairDataFragment.newInstance(UUID);
            ft.add(pairDataFragment, PairDataFragment.TAG);
            ft.commit();
        }
    }

    public boolean isNeedPair(Option statusOption, Option timestampOption) {
        status = statusOption != null ? statusOption.getValue() : STATUS_NOTPAIRED;
        timestamp = timestampOption != null ? timestampOption.getValue() : "";

        switch (status) {
            case STATUS_NOTPAIRED:
                networkFragment.requireNetwork(new NetworkFragment.OnNetworkCheckCompleted() {
                    @Override
                    public void OnNetworkCheckCompleted(boolean isAvailable) {
                    pairDataFragment.startPair(timestamp);
                    }
                });
                return true;
            case STATUS_PAIRED:
            default:
                mRequestHandler.removeCallbacks(mRequestRunnable);
                mRequestHandler.postDelayed(mRequestRunnable, NETWORK_REQUEST_DELAY);
                return false;
        }
    }

    public void onDestroy() {
        mRequestHandler.removeCallbacks(mRequestRunnable);
    }

    public void onStop() {
        mRequestHandler.removeCallbacks(mRequestRunnable);
    }

}
