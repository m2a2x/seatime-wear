package com.maks.seatimewear;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.maks.seatimewear.components.PairDialogFragment;
import com.maks.seatimewear.model.Condition;
import com.maks.seatimewear.model.ConditionCollection;
import com.maks.seatimewear.model.Option;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Swell;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.model.Wind;
import com.maks.seatimewear.model.i.ForecastI;
import com.maks.seatimewear.network.NetworkFragment;
import com.maks.seatimewear.network.PairDataFragment;
import com.maks.seatimewear.network.RefreshDataAdapter;
import com.maks.seatimewear.spot.CustomCurvedChildLayoutManager;
import com.maks.seatimewear.spot.SpotActivity;
import com.maks.seatimewear.sql.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends WearableActivity
        implements PairDataFragment.OnPairDataListener,
        SpotListAdapter.ItemSelectedListener {

    private DatabaseHelper databaseHelper = null;

    // private static final SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);

    NetworkFragment networkFragment;

    private SpotListAdapter mListAdapter;
    String UUID = null;

    private RelativeLayout mProgress;
    WearableRecyclerView wearableRecyclerView;
    RefreshDataAdapter refreshDataAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        wearableRecyclerView = (WearableRecyclerView) findViewById(R.id.recycler_launcher_view);
        wearableRecyclerView.setHasFixedSize(true);
        wearableRecyclerView.setCenterEdgeItems(true);
        wearableRecyclerView.setLayoutManager(new CustomCurvedChildLayoutManager(this));



        mProgress = (RelativeLayout) findViewById(R.id.progress_bar);

        networkFragment =
                (NetworkFragment) getFragmentManager().findFragmentByTag(NetworkFragment.TAG);

        if (networkFragment == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            networkFragment = NetworkFragment.newInstance();
            ft.add(networkFragment, NetworkFragment.TAG);
            ft.commit();
        }

        try {
            Option op = getHelper().getOptionByKey("uuid");
            if (op != null) {
                UUID = op.getValue();
            } else {
                UUID = java.util.UUID.randomUUID().toString();
                UUIDSet(UUID);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        mListAdapter = new SpotListAdapter(MainActivity.this, new ArrayList<Spot>());
        wearableRecyclerView.setAdapter(mListAdapter);
        mListAdapter.setListener(this);
        refreshDataAdapter = new RefreshDataAdapter(this, UUID, networkFragment);
    }

    @Override
    public void onItemSelected(int position) {
        Intent intent = new Intent(this, SpotActivity.class);
        intent.putExtra("id", mListAdapter.getItemId(position));
        intent.putExtra("uuid", UUID);
        startActivity(intent);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void setLoading(boolean isBusy) {
        if (isBusy) {
            mProgress.setVisibility(View.VISIBLE);
            wearableRecyclerView.setVisibility(View.GONE);
        } else {
            mProgress.setVisibility(View.GONE);
            wearableRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setLoading(true);

        try {
            Option status = getHelper().getOptionByKey("status");
            Option timestamp = getHelper().getOptionByKey("timestamp");
            if (refreshDataAdapter.isNeedPair(status, timestamp)) {
                return;
            }

            mListAdapter.clear();

            Dao<Spot, Integer> dao = getHelper().getSpotDao();
            ArrayList<Spot> spots = new ArrayList<>(dao.queryForAll());
            mListAdapter.addAll(spots);
            mListAdapter.notifyDataSetChanged();

            setLoading(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refreshDataAdapter.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshDataAdapter.onStop();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            // mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            // mTextView.setTextColor(getResources().getColor(android.R.color.white));
            // mClockView.setVisibility(View.VISIBLE);

            // mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            // mContainerView.setBackground(null);
            // mTextView.setTextColor(getResources().getColor(android.R.color.black));
            // mClockView.setVisibility(View.GONE);
        }
    }

    public void UUIDSet(String uuid) {
        try {
            Option op = getHelper().getOptionByKey("uuid");
            Dao<Option, Integer> dao = getHelper().getOptionDao();
            if (op != null) {
                op.setValue(uuid);
                dao.update(op);
            } else {
                op = new Option("uuid", uuid);
                dao.create(op);
            }
            UUID = uuid;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDataPaired(String status) {
        setLoading(true);
        try {
            Option op = getHelper().getOptionByKey("status");
            Dao<Option, Integer> dao = getHelper().getOptionDao();
            if (op != null) {
                if (op.getValue() != status) {
                    op.setValue(status);
                    dao.update(op);
                }
            } else {
                op = new Option("status", status);
                dao.create(op);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setLoading(false);
    }

    public void onPairAwait(String pair) {
        PairDialogFragment dialog;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(PairDialogFragment.TAG);
        if (prev != null) {
            dialog = (PairDialogFragment) prev;
            dialog.onRefresh(pair);
            ft.commit();
            return;
        }
        ft.addToBackStack(null);

        dialog = PairDialogFragment.newInstance(pair);
        dialog.show(ft, PairDialogFragment.TAG);
    }

    public void onPairFinished() {
        Fragment dialog = getFragmentManager().findFragmentByTag(PairDialogFragment.TAG);
        if (dialog != null) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(dialog)
                    .commit();
        }
    }

    public void onGlobalDataUpdate() {
        try {
            Option status = getHelper().getOptionByKey("status");
            Option timestamp = getHelper().getOptionByKey("timestamp");
            refreshDataAdapter.isNeedPair(status, timestamp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onGlobalDataUpdate(ArrayList<Spot> spots, ConditionCollection conditions, String timestamp) {
        getHelper().dropData();

        try {
            Dao<Spot, Integer> daoSpot = getHelper().getSpotDao();
            Dao<Option, Integer> daoOption = getHelper().getOptionDao();
            Option option = getHelper().getOptionByKey("timestamp");

            Dao<Tide, Integer> daoTide = getHelper().getTideDao();
            Dao<Swell, Integer> daoSwell= getHelper().getSwellDao();
            Dao<Wind, Integer> daoWind = getHelper().getWindDao();
            Dao<Condition, Integer> daoCondition= getHelper().getConditionDao();

            for (Spot spot : spots) {
                daoSpot.create(spot);
            }

            for (PairDataFragment.ConditionItem c: conditions.conditions) {
                for (Tide tide : c.tide) {
                    daoUpdate(daoTide, tide);
                }
            }

            for (PairDataFragment.ForecastItem f: conditions.forecasts) {
                daoUpdate(daoSwell, f.swell);
                daoUpdate(daoWind, f.wind);
                daoUpdate(daoCondition, f.condition);
            }

            if (option != null) {
                option.setValue(timestamp);
                daoOption.update(option);
            } else {
                option = new Option("timestamp", timestamp);
                daoOption.create(option);
            }

            onResume();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private <T extends ForecastI> void
    daoUpdate(Dao<T, Integer> dao, T sItem)
            throws SQLException  {
        T dbItem = getByTimestamp(
                sItem.getTimestamp(),
                dao.queryBuilder(),
                sItem.spot_id
        );

        if (dbItem != null) {
            dao.update(dbItem);
        } else {
            dao.create(sItem);
        }
    }

    private <T>T getByTimestamp (long timestamp, QueryBuilder<T, Integer> queryBuilder, long spot_id) throws SQLException {
        ArrayList<T> items;


        queryBuilder.where()
                .eq("spot_id", spot_id)
                .and()
                .eq("timestamp", timestamp);

        items = new ArrayList<>(queryBuilder.query());
        if (items.size() > 0) {
            return items.get(0);
        }
        return null;
    }
}
