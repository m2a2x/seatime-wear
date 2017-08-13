package com.maks.seatimewear.spot;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.maks.seatimewear.R;
import com.maks.seatimewear.components.PagerCountView;

public class ViewerAdapter extends WearableRecyclerView.Adapter<WearableRecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private PagerAdapter mPagerAdapter;
    Context context;
    boolean mDeprecated = false;

    public ViewerAdapter(Context context, PagerAdapter pagerAdapter) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        mPagerAdapter = pagerAdapter;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return position;
        }
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public WearableRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item;
        switch (viewType) {
            case 0:
                item = layoutInflater.inflate(R.layout.item_recycler_menu, parent, false);
                return new Menu(item);
            case 1:
            default:
                item = layoutInflater.inflate(R.layout.item_recycler_view, parent, false);
                return new ViewHolderData(item);
        }
    }

    @Override
    public void onBindViewHolder(final WearableRecyclerView.ViewHolder holder, int position) {
        //Item item = items.get(position);


        switch (holder.getItemViewType()) {
            case 0:
                // Menu menu = (Menu)holder;
                // viewHolder0.image.setImageResource(item.getDrawable());
                // viewHolder0.appName.setText(item.getName());
                break;

            case 1:
            default:
                ViewHolderData viewHolder1 = (ViewHolderData)holder;
                viewHolder1.setAdapter(mPagerAdapter);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void setDepracated(boolean isDeprecated) {
        mDeprecated = isDeprecated;
    }

    class ViewHolderData extends WearableRecyclerView.ViewHolder {
        ViewPager mPager;

        public ViewHolderData(final View v) {
            super(v);

            final PagerCountView mPagerCountView = (PagerCountView) v.findViewById(R.id.pagerCount);
            mPagerCountView.setPage(1);

            mPager = (ViewPager) v.findViewById(R.id.pager);
            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    mPagerCountView.setPage(position + 1);
                }

                @Override
                public void onPageScrollStateChanged(int state) {}
            });
        }

        public void setAdapter(PagerAdapter adapter) {
            mPager.setAdapter(adapter);
        }
    }

    class Menu extends WearableRecyclerView.ViewHolder {
        public Menu(final View v) {
            super(v);
        }
    }
}
