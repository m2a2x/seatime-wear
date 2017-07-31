package com.maks.seatimewear.network;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maks.seatimewear.MainActivity;
import com.maks.seatimewear.R;
import java.util.concurrent.TimeUnit;


public class NetworkActivity extends WearableActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Intent action for sending the user directly to the add Wi-Fi network activity.
    private static final String ACTION_ADD_NETWORK_SETTINGS =
            "com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS";

    // Message to notify the network request timout handler that too much time has passed.
    private static final int MESSAGE_CONNECTIVITY_TIMEOUT = 1;

    // How long the app should wait trying to connect to a sufficient high-bandwidth network before
    // asking the user to add a new Wi-Fi nTABLE_USEROPTIONSetwork.
    private static final long NETWORK_CONNECTIVITY_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(10);
    private static final long NETWORK_REQUEST_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(5);
    private ConnectivityManager mConnectivityManager;
    private Handler mHandler;
    private ConnectivityManager.NetworkCallback mNetworkCallback;



    private ImageView mConnectivityIcon;
    private TextView mConnectivityText;

    private View mButton;
    private ImageView mButtonIcon;
    private TextView mButtonText;
    private TextView mInfoText;
    private TextView mSmallInfoText;
    private View mProgressBar;


    // Tags added to the button in the UI to detect what operation the user has requested.
    // These are required since the app reuses the button for different states of the app/UI.
    // See onButtonClick() for how these tags are used.
    static final String TAG_REQUEST_NETWORK = "REQUEST_NETWORK";
    static final String TAG_RELEASE_NETWORK = "RELEASE_NETWORK";
    static final String TAG_ADD_WIFI = "ADD_WIFI";



    // These constants are used by setUiState() to determine what information to display in the UI,
    // as this app reuses UI components for the various states of the app, which is dependent on
    // the state of the network.
    static final int UI_STATE_REQUEST_NETWORK = 1;
    static final int UI_STATE_REQUESTING_NETWORK = 2;
    static final int UI_STATE_NETWORK_CONNECTED = 3;
    static final int UI_STATE_CONNECTION_TIMEOUT = 4;
    static final int UI_STATE_REQUESTING_DATA = 5;
    static final int UI_STATE_PAIR = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);


        mConnectivityIcon = (ImageView) findViewById(R.id.connectivity_icon);
        mConnectivityText = (TextView) findViewById(R.id.connectivity_text);

        mProgressBar = findViewById(R.id.progress_bar);

        mButton = findViewById(R.id.button);
        mButton.setTag(TAG_REQUEST_NETWORK);
        mButtonIcon = (ImageView) findViewById(R.id.button_icon);
        mButtonText = (TextView) findViewById(R.id.button_label);

        mInfoText = (TextView) findViewById(R.id.info_text);
        mSmallInfoText = (TextView) findViewById(R.id.smallinfo_text);



        /*
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_CONNECTIVITY_TIMEOUT:
                        Log.d(LOG_TAG, "Network connection timeout");
                        setUiState(UI_STATE_CONNECTION_TIMEOUT);
                        unregisterNetworkCallback();
                        break;
                }
            }
        }; */
    }


    @Override
    public void onStop() {
        // releaseHighBandwidthNetwork();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        if (isNetwork()) {
            setUiState(UI_STATE_NETWORK_CONNECTED);
        } else {
            setUiState(UI_STATE_REQUEST_NETWORK);
        } */
    }

    private void unregisterNetworkCallback() {
        if (mNetworkCallback != null) {
            Log.d(LOG_TAG, "Unregistering network callback");
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            mNetworkCallback = null;
        }
    }

    // Determine if there is a high-bandwidth network exists. Checks both the active
    // and bound networks. Returns false if no network is available (low or high-bandwidth).
    private boolean isNetwork() {
        Network network = mConnectivityManager.getBoundNetworkForProcess();
        network = network == null ? mConnectivityManager.getActiveNetwork() : network;
        if (network == null) {
            return false;
        }

        return true;
    }

    private void requestHighBandwidthNetwork() {
        // Before requesting a high-bandwidth network, ensure prior requests are invalidated.
        unregisterNetworkCallback();

        Log.d(LOG_TAG, "Requesting high-bandwidth network");

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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // requires android.permission.INTERNET
                        if (!mConnectivityManager.bindProcessToNetwork(network)) {
                            Log.e(LOG_TAG, "ConnectivityManager.bindProcessToNetwork()"
                                    + " requires android.permission.INTERNET");
                            setUiState(UI_STATE_REQUEST_NETWORK);
                        } else {
                            Log.d(LOG_TAG, "Network available");
                            setUiState(UI_STATE_NETWORK_CONNECTED);
                        }
                    }
                });
            }

            @Override
            public void onCapabilitiesChanged(Network network,
                                              NetworkCapabilities networkCapabilities) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_TAG, "Network capabilities changed");
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                Log.d(LOG_TAG, "Network lost");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUiState(UI_STATE_REQUEST_NETWORK);
                    }
                });
            }
        };

        // requires android.permission.CHANGE_NETWORK_STATE
        mConnectivityManager.requestNetwork(request, mNetworkCallback);

        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_CONNECTIVITY_TIMEOUT),
                NETWORK_CONNECTIVITY_TIMEOUT_MS);
    }

    private void releaseHighBandwidthNetwork() {
        mConnectivityManager.bindProcessToNetwork(null);
        unregisterNetworkCallback();
    }

    private void addWifiNetwork() {
        // requires android.permission.CHANGE_WIFI_STATE
        startActivity(new Intent(ACTION_ADD_NETWORK_SETTINGS));
    }

    /**
     * Click handler for the button in the UI. The view tag is used to determine the specific
     * function of the button.
     *
     * @param view The view that was clicked
     */
    public void onButtonClick(View view) {
        switch (view.getTag().toString()) {
            case TAG_REQUEST_NETWORK:
                requestHighBandwidthNetwork();
                setUiState(UI_STATE_REQUESTING_NETWORK);
                break;

            case TAG_RELEASE_NETWORK:
                releaseHighBandwidthNetwork();
                setUiState(UI_STATE_REQUEST_NETWORK);
                break;

            case TAG_ADD_WIFI:
                addWifiNetwork();
                break;
        }
    }

    // Sets the text and icons the connectivity indicator, button, and info text in the app UI,
    // which are all reused for the various states of the app and network connectivity. Also,
    // will show/hide a progress bar, which is dependent on the state of the network connectivity
    // request.
    private void setUiState(int uiState) {
        mConnectivityIcon.setVisibility(View.VISIBLE);
        mInfoText.setVisibility(View.GONE);
        mSmallInfoText.setVisibility(View.GONE);
        // timerHandler.removeCallbacks(timerRunnable);

        switch (uiState) {
            case UI_STATE_REQUEST_NETWORK:
                if (isNetwork()) {
                    mConnectivityIcon.setImageResource(R.drawable.ic_cloud_happy);
                } else {
                    mConnectivityIcon.setImageResource(R.drawable.ic_cloud_sad);
                }

                mButton.setTag(TAG_REQUEST_NETWORK);
                mButtonIcon.setImageResource(R.drawable.ic_fast_network);
                mButtonText.setText(R.string.button_request_network);

                break;

            case UI_STATE_REQUESTING_NETWORK:
            case UI_STATE_REQUESTING_DATA:
                mConnectivityIcon.setImageResource(R.drawable.ic_cloud_disconnected);
                mConnectivityText.setText(R.string.network_connecting);

                mProgressBar.setVisibility(View.VISIBLE);
                mButton.setVisibility(View.GONE);

                break;

            case UI_STATE_PAIR:
                mConnectivityIcon.setVisibility(View.GONE);
                mConnectivityText.setText(R.string.data_pair);

                mProgressBar.setVisibility(View.GONE);
                // mInfoText.setText(pairNumber);
                mInfoText.setVisibility(View.VISIBLE);
                mButton.setVisibility(View.GONE);

                // timerHandler.postDelayed(timerRunnable, 1000);

                break;

            case UI_STATE_NETWORK_CONNECTED:
                mConnectivityIcon.setImageResource(R.drawable.ic_cloud_happy);
                mConnectivityText.setText(R.string.network);

                mProgressBar.setVisibility(View.GONE);
                mButton.setVisibility(View.VISIBLE);

                mButton.setTag(TAG_RELEASE_NETWORK);
                mButtonIcon.setImageResource(R.drawable.ic_no_network);
                mButtonText.setText(R.string.button_release_network);

                setUiState(UI_STATE_REQUESTING_DATA);
                // timerHandler.postDelayed(timerRunnable, 0);

                break;

            case UI_STATE_CONNECTION_TIMEOUT:
                mConnectivityIcon.setImageResource(R.drawable.ic_cloud_disconnected);
                mConnectivityText.setText(R.string.network_disconnected);

                mProgressBar.setVisibility(View.GONE);
                mButton.setVisibility(View.VISIBLE);

                mButton.setTag(TAG_ADD_WIFI);
                mButtonIcon.setImageResource(R.drawable.ic_wifi_network);
                mButtonText.setText(R.string.button_add_wifi);
                mSmallInfoText.setVisibility(View.GONE);
                mSmallInfoText.setText(R.string.info_add_wifi);

                break;
        }
    }
}
