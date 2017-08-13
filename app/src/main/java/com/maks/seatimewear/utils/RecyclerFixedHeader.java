package com.maks.seatimewear.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.maks.seatimewear.R;

public class RecyclerFixedHeader extends RecyclerView.ItemDecoration {

    private final int headerOffset;
    private View headerView;

    public RecyclerFixedHeader(int headerHeight) {
        headerOffset = headerHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (pos == 0) {
            outRect.top = headerOffset;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (headerView == null) {
            headerView = inflateHeaderView(parent);
            fixLayoutSize(headerView, parent);
        }

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(child);
            if (position == 0 || i == 0) {
                drawHeader(c, child, headerView);
            }
        }
    }

    private void drawHeader(Canvas c, View child, View headerView) {
        c.save();
        c.translate(0, Math.max(0, child.getTop() - headerView.getHeight()));
        headerView.draw(c);
        c.restore();
    }

    private View inflateHeaderView(RecyclerView parent) {
        return LayoutInflater.from(parent.getContext())
            .inflate(R.layout.recycler_section_header, parent, false);
    }

    public boolean isItem(float y) {
        return  y < headerView.getHeight();
    }

    private void fixLayoutSize(View view, ViewGroup parent) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(),
            View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),
            View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
           parent.getPaddingLeft() + parent.getPaddingRight(),
           view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
            parent.getPaddingTop() + parent.getPaddingBottom(),
            view.getLayoutParams().height);

        view.measure(childWidth,childHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }
}


