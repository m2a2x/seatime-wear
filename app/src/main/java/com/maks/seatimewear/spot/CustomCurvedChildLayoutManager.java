package com.maks.seatimewear.spot;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.wearable.view.CurvedChildLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;

public class CustomCurvedChildLayoutManager extends CurvedChildLayoutManager {
    private final Path mCurvePath = new Path();
    private final PathMeasure mPathMeasure = new PathMeasure();
    private int mCurvePathHeight;
    private int mXCurveOffset;
    private float mPathLength;
    private float mCurveBottom;
    private float mCurveTop;
    private float mLineGradient;
    private final float[] mPathPoints = new float[2];
    private final float[] mPathTangent = new float[2];
    private final float[] mAnchorOffsetXY = new float[2];
    private WearableRecyclerView mParentView;
    private boolean mIsScreenRound;
    private int mLayoutWidth;
    private int mLayoutHeight;

    private static final float MAX_ICON_PROGRESS = 0.8f;

    public CustomCurvedChildLayoutManager(Context context) {
        super(context);
        this.mIsScreenRound = context.getResources().getConfiguration().isScreenRound();
        this.mXCurveOffset = context.getResources().getDimensionPixelSize(android.support.wearable.R.dimen.wrv_curve_default_x_offset);

    }

    public void updateChild(View child, WearableRecyclerView parent) {
        if(this.mParentView != parent) {
            this.mParentView = parent;
            this.mLayoutWidth = this.mParentView.getWidth();
            this.mLayoutHeight = this.mParentView.getHeight();
        }

        if(this.mIsScreenRound) {
            this.maybeSetUpCircularInitialLayout(this.mLayoutWidth, this.mLayoutHeight);

            this.mAnchorOffsetXY[0] = (float)this.mXCurveOffset;
            this.mAnchorOffsetXY[1] = (float)child.getHeight() / 2.0F;

            this.adjustAnchorOffsetXY(child, this.mAnchorOffsetXY);

            float minCenter = -((float)child.getHeight()) / 2.0F;
            float maxCenter = (float)this.mLayoutHeight + (float)child.getHeight() / 2.0F;
            float range = maxCenter - minCenter;

            float verticalAnchor = (float)child.getTop() + this.mAnchorOffsetXY[1];
            float mYScrollProgress = (verticalAnchor + Math.abs(minCenter)) / range;

            this.mPathMeasure.getPosTan(mYScrollProgress * this.mPathLength, this.mPathPoints, this.mPathTangent);
            boolean topClusterRisk = Math.abs(this.mPathPoints[1] - this.mCurveBottom) < 0.001F && minCenter < this.mPathPoints[1];
            boolean bottomClusterRisk = Math.abs(this.mPathPoints[1] - this.mCurveTop) < 0.001F && maxCenter > this.mPathPoints[1];

            if(topClusterRisk || bottomClusterRisk) {
                this.mPathPoints[1] = verticalAnchor;
                this.mPathPoints[0] = Math.abs(verticalAnchor) * this.mLineGradient;
            }

            int newLeft = (int)(this.mPathPoints[0] - this.mAnchorOffsetXY[0]);
            child.offsetLeftAndRight(newLeft - child.getLeft());

            float verticalTranslation = this.mPathPoints[1] - verticalAnchor;
            child.setTranslationY(verticalTranslation);

            // Normalize for center
            float mProgressToCenter = Math.abs(0.5f - mYScrollProgress);
            // Adjust to the maximum scale
            mProgressToCenter = Math.min(mProgressToCenter, MAX_ICON_PROGRESS);
            child.setScaleX(1 - mProgressToCenter);
            child.setScaleY(1 - mProgressToCenter);
        }
    }

    private void maybeSetUpCircularInitialLayout(int width, int height) {
        if(this.mCurvePathHeight != height) {
            this.mCurvePathHeight = height;
            this.mCurveBottom = -0.048F * (float)height;
            this.mCurveTop = 1.048F * (float)height;
            this.mLineGradient = 10.416667F;
            this.mCurvePath.reset();
            this.mCurvePath.moveTo(0.5F * (float)width, this.mCurveBottom);
            this.mCurvePath.lineTo(0.34F * (float)width, 0.075F * (float)height);
            this.mCurvePath.cubicTo(0.22F * (float)width, 0.17F * (float)height, 0.13F * (float)width, 0.32F * (float)height, 0.13F * (float)width, (float)(height / 2));
            this.mCurvePath.cubicTo(0.13F * (float)width, 0.68F * (float)height, 0.22F * (float)width, 0.83F * (float)height, 0.34F * (float)width, 0.925F * (float)height);
            this.mCurvePath.lineTo((float)(width / 2), this.mCurveTop);
            this.mPathMeasure.setPath(this.mCurvePath, false);
            this.mPathLength = this.mPathMeasure.getLength();
        }

    }
}
