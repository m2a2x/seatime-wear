package com.maks.seatimewear.spot;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.maks.seatimewear.R;
import com.maks.seatimewear.model.ConditionCollection;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.network.NetworkFragment;


public class SpotActivity
        extends FragmentActivity
        implements PairConditionFragment.OnPairConditionListener,
        ViewerAdapter.NavigationMenuListener {

    private DataSource dataSource;
    private NavigationControl mNavigation;

    NetworkFragment networkFragment;
    PairConditionFragment conditionFragment;
    Spot currentSpot;

    private RelativeLayout mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spot_activity);
        mProgress = (RelativeLayout) findViewById(R.id.progress_bar);
        dataSource = new DataSource(this, (int) this.getIntent().getExtras().getLong("id"));

        String UUID = this.getIntent().getExtras().getString("uuid");
        currentSpot = dataSource.getSpot();

        networkFragment =
            (NetworkFragment) getFragmentManager().findFragmentByTag(NetworkFragment.TAG);

        if (networkFragment == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            networkFragment = NetworkFragment.newInstance();
            ft.add(networkFragment, NetworkFragment.TAG);
            ft.commit();
        }

        conditionFragment =
                (PairConditionFragment) getFragmentManager().findFragmentByTag(PairConditionFragment.TAG);

        if (conditionFragment == null) {
            conditionFragment = PairConditionFragment.newInstance(UUID, currentSpot);
            getFragmentManager()
                    .beginTransaction()
                    .add(conditionFragment, PairConditionFragment.TAG)
                    .commit();
        }
        mNavigation = new NavigationControl(
            (WearableRecyclerView) findViewById(R.id.recycler_view),
            new ScreenAdapter(getSupportFragmentManager(), currentSpot, dataSource),
            this
        );
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.refresh: {
                setLoading(true);
                conditionFragment.requestedUpdate(this);
                mNavigation.hideMenu();
                break;
            }
        }
    }

    public void onUpdateFinished(boolean isSuccessful) {
        setLoading(false);
    }

    private void setLoading(boolean isBusy) {
        if (isBusy) {
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mProgress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.dataSource.onDestroy();
    }

    public void onDataDeprecated(boolean isDeprecated) {
        this.mNavigation.setDeprecated(isDeprecated);
    }

    public void onSpotDataUpdated(ConditionCollection conditions) {
        this.dataSource.setConditions(conditions);
    }
}
