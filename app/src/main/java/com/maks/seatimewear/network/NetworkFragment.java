package com.maks.seatimewear.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.util.Log;
import com.maks.seatimewear.BuildConfig;
import java.util.concurrent.TimeUnit;


public class NetworkFragment extends Fragment {

    public interface OnNetworkCheckCompleted{
        void OnNetworkCheckCompleted(boolean isAvailable);
    }

    // These constants are used by setUiState() to determine what information to display in the UI,
    // as this app reuses UI components for the various states of the app, which is dependent on
    // the state of the network.
    static final int UI_STATE_REQUEST_NETWORK = 1;
    static final int UI_STATE_NETWORK_CONNECTED = 2;
    static final int UI_STATE_CONNECTION_TIMEOUT = 3;

    private OnNetworkCheckCompleted pendingCallback;

    public static final String TAG = "NetworkFragment";

    // Intent action for sending the user directly to the add Wi-Fi network activity.
    private static final String ACTION_ADD_NETWORK_SETTINGS =
            "com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS";

    // Message to notify the network request timout handler that too much time has passed.
    private static final int MESSAGE_CONNECTIVITY_TIMEOUT = 1;

    // How long the app should wait trying to connect to a sufficient high-bandwidth network before
    // asking the user to add a new Wi-Fi nTABLE_USEROPTIONSetwork.
    private static final long NETWORK_CONNECTIVITY_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(10);
    // private static final long NETWORK_REQUEST_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(5);
    private ConnectivityManager mConnectivityManager;
    private Handler mHandler;
    private ConnectivityManager.NetworkCallback mNetworkCallback;



    public static NetworkFragment newInstance() {
        return new NetworkFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_CONNECTIVITY_TIMEOUT:
                        Log.d(TAG, "Network connection timeout");
                        setState(UI_STATE_CONNECTION_TIMEOUT);
                        unregisterNetworkCallback();
                        break;
                }
            }
        };
    }


    @Override
    public void onStop() {
        releaseHighBandwidthNetwork();
        super.onStop();
    }

    private void unregisterNetworkCallback() {
        if (mNetworkCallback != null) {
            Log.d(TAG, "Unregistering network callback");
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            mNetworkCallback = null;
        }
    }

    // Determine if there is a high-bandwidth network exists. Checks both the active
    // and bound networks. Returns false if no network is available (low or high-bandwidth).
    public boolean isNetwork() {
        if (BuildConfig.DIRECT_NETWORK) {
            return  true;
        }

        Network network = mConnectivityManager.getBoundNetworkForProcess();
        network = network == null ? mConnectivityManager.getActiveNetwork() : network;
        if (network == null) {
            return false;
        }

        return true;
    }

    public void requireNetwork(OnNetworkCheckCompleted networkCheckCallback) {
        if (!isNetwork()) {
            pendingCallback = networkCheckCallback;
            requestHighBandwidthNetwork();
            return;
        }
        networkCheckCallback.OnNetworkCheckCompleted(true);
    }

    public void requestHighBandwidthNetwork() {
        // Before requesting a high-bandwidth network, ensure prior requests are invalidated.
        unregisterNetworkCallback();

        Log.d(TAG, "Requesting high-bandwidth network");

        // Requesting an unmetered network may prevent you from connecting to the cellular
        // network on the user's watch or phone; however, unless you explicitly ask for permission
        // to a access the user's cellular network, you should request an unmetered network.
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        mNetworkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(final Network network) {
                mHandler.removeMessages(MESSAGE_CONNECTIVITY_TIMEOUT);

                // requires android.permission.INTERNET
                if (!mConnectivityManager.bindProcessToNetwork(network)) {
                    Log.e(TAG, "ConnectivityManager.bindProcessToNetwork()"
                            + " requires android.permission.INTERNET");
                    setState(UI_STATE_REQUEST_NETWORK);
                } else {
                    Log.d(TAG, "Network available");
                    setState(UI_STATE_NETWORK_CONNECTED);
                }
            }

            @Override
            public void onCapabilitiesChanged(Network network,
                                              NetworkCapabilities networkCapabilities) {
                Log.d(TAG, "Network capabilities changed");
            }

            @Override
            public void onLost(Network network) {
                Log.d(TAG, "Network lost");

                setState(UI_STATE_REQUEST_NETWORK);
            }
        };

        // requires android.permission.CHANGE_NETWORK_STATE
        mConnectivityManager.requestNetwork(request, mNetworkCallback);

        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_CONNECTIVITY_TIMEOUT),
                NETWORK_CONNECTIVITY_TIMEOUT_MS);
    }

    public void releaseHighBandwidthNetwork() {
        mConnectivityManager.bindProcessToNetwork(null);
        unregisterNetworkCallback();
    }

    public void addWifiNetwork() {
        // requires android.permission.CHANGE_WIFI_STATE
        startActivity(new Intent(ACTION_ADD_NETWORK_SETTINGS));
    }


    public void setState(int state) {
        switch (state) {
            case UI_STATE_REQUEST_NETWORK:
                addWifiNetwork();
                break;

            case UI_STATE_NETWORK_CONNECTED:
                if (pendingCallback != null) {
                    pendingCallback.OnNetworkCheckCompleted(true);
                }
                pendingCallback = null;
                break;

            case UI_STATE_CONNECTION_TIMEOUT:
                addWifiNetwork();
                break;
        }
    }
}
