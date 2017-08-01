package com.maks.seatimewear;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.maks.seatimewear.components.PairDialogFragment;
import com.maks.seatimewear.model.Option;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.network.PairDataFragment;
import com.maks.seatimewear.spot.SpotActivity;
import com.maks.seatimewear.sql.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends WearableActivity implements PairDataFragment.OnPairDataListener {
    private DatabaseHelper databaseHelper = null;

    // private static final SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);
    BoxInsetLayout mContainerView;
    TextView mTextView;

    PairDataFragment pairDataFragment;

    private ListView mListView;
    private ArrayAdapter mListAdapter;
    String UUID = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context currentContext = this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setAmbientEnabled();

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
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);

        // List adapter
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Spot item = (Spot) parent.getItemAtPosition(position);
                Intent intent = new Intent(currentContext, SpotActivity.class);
                intent.putExtra("id", item.getId());
                intent.putExtra("uuid", UUID);
                startActivity(intent);
            }
        });
        pairDataFragment =
                (PairDataFragment) getFragmentManager().findFragmentByTag(PairDataFragment.TAG);

        if (pairDataFragment == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            pairDataFragment = PairDataFragment.newInstance(UUID);
            ft.add(pairDataFragment, PairDataFragment.TAG);
            ft.commit();
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        mListAdapter.clear();
        try {
            Dao<Spot, Integer> dao = getHelper().getSpotDao();
            ArrayList<Spot> spots = new ArrayList<>(dao.queryForAll());
            mListAdapter.addAll(spots);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        mListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    public void onButtonClick(View view) {
        /*Intent intent = new Intent(this, NetworkActivity.class);
        startActivity(intent);*/
        pairDataFragment.startPair();


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
    }

    public void onPairAwait(String pair) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(PairDialogFragment.TAG);
        if (prev != null) {
            ft.commit();
            return;
        }
        ft.addToBackStack(null);

        DialogFragment newFragment = PairDialogFragment.newInstance(pair);
        newFragment.show(ft, PairDialogFragment.TAG);
    }

    public void onPairFinished() {
        Fragment dialog = getFragmentManager().findFragmentByTag(PairDialogFragment.TAG);
        if (dialog != null) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(dialog)
                    .commit();
        }

        PairDataFragment pairDataFragment =
                (PairDataFragment) getFragmentManager().findFragmentByTag(PairDataFragment.TAG);

        if (pairDataFragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(pairDataFragment)
                    .commit();
        }
    }

    public void onGlobalDataUpdate(ArrayList<Spot> spots) {
        try {
            Dao<Spot, Integer> dao = getHelper().getSpotDao();
            for (Spot spot : spots) {
                dao.create(spot);
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
}
