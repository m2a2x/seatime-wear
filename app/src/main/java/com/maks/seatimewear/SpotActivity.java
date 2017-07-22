package com.maks.seatimewear;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maks.seatimewear.datasource.UserDS;
import com.maks.seatimewear.model.Spot;
import com.maks.seatimewear.model.Tide;

import java.util.ArrayList;

public class SpotActivity extends WearableActivity {
    private Spot currentSpot;


    // DataSource
    private UserDS dataSource;

    private TextView mSpotText;
    private ArrayAdapter mListAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context currentContext = this;
        setContentView(R.layout.activity_spot);
        long id = this.getIntent().getExtras().getLong("id");

        dataSource = new UserDS(this);
        dataSource.open();
        currentSpot = dataSource.findSpotById(id);

        ArrayList<Tide> tides = dataSource.getTidesBySpot(id);
        dataSource.close();

        mSpotText = (TextView) findViewById(R.id.spot_text);
        mSpotText.setText(currentSpot.getValue());

        mListView = (ListView) findViewById(R.id.listview);

        mListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        mListAdapter.addAll(tides);
        mListView.setAdapter(mListAdapter);

    }
}
