package com.maks.seatimewear.spot;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SnapHelper;
import android.support.wearable.view.WearableRecyclerView;
import android.view.Gravity;
import android.view.View;
import com.maks.seatimewear.R;
import com.maks.seatimewear.utils.GravitySnapHelper;
import com.maks.seatimewear.utils.RecyclerFixedHeader;
import com.maks.seatimewear.utils.RecyclerTouchListener;

class NavigationControl {
    private RecyclerFixedHeader sectionItemDecoration;
    private WearableRecyclerView recyclerView;
    private ViewerAdapter mViewAdapter;

    NavigationControl(final WearableRecyclerView rv, PagerAdapter mPagerAdapter, Context context) {
        SnapHelper snapHelper = new GravitySnapHelper(Gravity.TOP);
        recyclerView = rv;

        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(
            new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        );
        recyclerView.setHasFixedSize(false);

        sectionItemDecoration =
            new RecyclerFixedHeader(
                context
                    .getResources()
                    .getDimensionPixelSize(R.dimen.recycler_section_header_height));

        recyclerView.addItemDecoration(sectionItemDecoration);

        mViewAdapter = new ViewerAdapter(context, mPagerAdapter);
        recyclerView.setAdapter(mViewAdapter);
        recyclerView.addOnItemTouchListener(
            new RecyclerTouchListener(context, recyclerView,
                new RecyclerTouchListener.OnTouchActionListener() {
                    @Override
                    public void onClick(View view, int position, float y) {
                        if (sectionItemDecoration.isItem(y)) {
                            recyclerView.getLayoutManager().scrollToPosition(0);
                        }
                    }
                    @Override
                    public void onRightSwipe(View view, int position) {}

                    @Override
                    public void onLeftSwipe(View view, int position) {}
                }));

        hideMenu();
        this.setDeprecated(false);
    }

    void hideMenu() {
        recyclerView.getLayoutManager().scrollToPosition(1);
    }

    void setDeprecated(boolean isDeprecated) {
        int color;
        if (isDeprecated) {
            color = R.color.attention_color;
        } else {
            color = R.color.spot_update_panel_color;
        }
        this.sectionItemDecoration.disable(!isDeprecated);
        this.mViewAdapter.setColor(color);
    }
}
