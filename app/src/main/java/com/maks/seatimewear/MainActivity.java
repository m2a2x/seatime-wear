package com.maks.seatimewear;
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

import com.maks.seatimewear.datasource.UserDS;
import com.maks.seatimewear.model.Spot;

import java.util.ArrayList;

public class MainActivity extends WearableActivity {

    // private static final SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);
    BoxInsetLayout mContainerView;
    TextView mTextView;


    // DataSource
    private UserDS dataSource;

    private ListView mListView;
    private ArrayAdapter mListAdapter;

    // private TextView mClockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context currentContext = this;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setAmbientEnabled();
        dataSource = new UserDS(this);
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Spot item = (Spot) parent.getItemAtPosition(position);
                Intent intent = new Intent(currentContext, SpotActivity.class);
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });
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
        dataSource.open();
        ArrayList<Spot> spots = dataSource.getAllSpots();
        mListAdapter.clear();
        mListAdapter.addAll(spots);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent(this, NetworkActivity.class);
        startActivity(intent);
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
}
