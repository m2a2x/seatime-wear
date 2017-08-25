package com.maks.seatimewear.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.maks.seatimewear.R;
import com.maks.seatimewear.model.Tide;
import com.maks.seatimewear.utils.Utils;

import java.util.ArrayList;


public class TideChart extends View {
    private ArrayList<Tide> mTides;
    private TextPaint mTextPaint;
    Paint paint;
    private static final float PADDING_TOP = 4;
    private String mTimezone;

    float mLineSize = 1.0f;
    float mDimensionLineSize = 0.0f;
    float mTextSize = 8.0f;
    float mTextHeight = 5.0f;

    int mChartHeight = 25;

    int mLineColor = Color.WHITE;
    int mTextColor = Color.WHITE;
    int mProgressColor = Color.WHITE;
    int mDimensionLineColor = Color.TRANSPARENT;
    int mCurrentPositionColor = Color.RED;


    public TideChart(Context context) {
        super(context);
        init(null);
    }

    public TideChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        // Load attributes

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TideChart, 0, 0);

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);


        try {
            // Retrieve the values from the TypedArray and store into

            mTextSize = a.getDimension(R.styleable.TideChart_textSize, mTextSize);
            mDimensionLineSize = a.getDimension(R.styleable.TideChart_dimensionSize, mDimensionLineSize);
            mLineSize = a.getDimension(R.styleable.TideChart_lineSize, mLineSize);
            mTextHeight = a.getDimension(R.styleable.TideChart_labelHeight, mTextHeight);

            mLineColor = a.getColor(R.styleable.TideChart_lineColor, mLineColor);
            mDimensionLineColor = a.getColor(R.styleable.TideChart_dimensionLineColor, mDimensionLineColor);
            mTextColor = a.getColor(R.styleable.TideChart_textColor, mTextColor);
            mProgressColor = a.getColor(R.styleable.TideChart_progressColor, mProgressColor);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    private boolean isFirstLow() {
        Tide tide = this.mTides.get(0);
        return !tide.getState().equals("High");
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        if (this.mTides == null || this.mTides.isEmpty()) {
            return;
        }

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        float contentWidth = getWidth() - paddingLeft - paddingRight;
        float pikes = this.mTides.size() + 1;
        float partSize = contentWidth / pikes;
        float progress = this.getTideProgress(partSize);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mDimensionLineColor);
        paint.setStrokeWidth(mDimensionLineSize);
        c.drawLine(0, mChartHeight + 5, contentWidth, mChartHeight + 5, paint);

        paint.setStrokeWidth(mLineSize);
        paint.setColor(mLineColor);
        this.drawCurveLine(
                c,
                paint,
                progress,
                contentWidth,
                partSize,
                mChartHeight,
                this.isFirstLow()
        );

        paint.setColor(mProgressColor);
        this.drawCurveLine(
                c,
                paint,
                0,
                progress,
                partSize,
                mChartHeight,
                this.isFirstLow()
        );

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        paint.setColor(mCurrentPositionColor);
        this.drawCurrentPosition(
                c,
                paint,
                progress,
                partSize,
                mChartHeight,
                this.isFirstLow()
        );

        this.drawTime(
            c,
            partSize,
            partSize,
            28 + mChartHeight,
            this.mTides,
            mTextPaint
        );
    }

    private void drawCurrentPosition (Canvas c, Paint p, float x, float partSize, float h, boolean isRevert) {
        float y = this.getWaveYPosition(isRevert, x, partSize);
        float width = 3;
        float height = 8;

        h /= 2;
        y = h + h * y;
        x = Math.max(0, x - width);
        c.drawRect(x, y, x + width, y + height, p);
    }

    private void drawCurveLine(
            Canvas c,
            Paint p,
            float startPointX,
            float lastPointX,
            float partSize,
            float height,
            boolean isRevert
    ) {
        float new_x;
        float old_x = startPointX;
        float old_y = this.getWaveYPosition(isRevert, old_x, partSize);
        height /= 2;

        for (new_x = startPointX; new_x < lastPointX; new_x = new_x + 2) {
            float new_y = this.getWaveYPosition(isRevert, new_x, partSize);

            c.drawLine (
                old_x,
                height + height * old_y + PADDING_TOP,
                new_x,
                height + height * new_y + PADDING_TOP,
                p
            );

            old_x = new_x;
            old_y = new_y;
        }
    }

    private void drawTime(Canvas c, float startPointX, float partSize, int y, ArrayList<Tide> tides, Paint p) {
        int i = 0;
        while (i < tides.size()) {
            Tide tide = tides.get(i);
            c.drawText(
                Utils.timeTimezone(mTimezone, tide.getTimestamp()),
                startPointX + i * partSize,
                y,
                p
            );
            i++;
        }
    }

    private float getWaveYPosition(boolean isRevert, float x, float partSize) {
        float cof = isRevert ? partSize : 0;
        return (float) Math.cos((x + cof) / partSize * Math.PI);
    }

    private float getTideProgress(float partSize) {
        long prevInt = 0;
        long sum = 0;
        float interval;
        for(Tide t: this.mTides) {
            if (prevInt > 0) {
                sum += t.getTimestamp() - prevInt;
            }
            prevInt = t.getTimestamp();
        }
        interval = sum / (this.mTides.size() - 1);


        float intervalSize = partSize / interval;

        float timeLeft = Math.min(interval, this.mTides.get(0).getTimestamp() - Utils.currentTimeUnix());
        float progress = partSize - timeLeft * intervalSize;
        return progress;
    }


    /**
     * Sets the view's tides attribute value. This tides
     * is the tides to draw.
     *
     * @param tides Tides attribute value to use.
     */
    public void setTides(ArrayList<Tide> tides, String timezone) {
        mTides = tides;
        mTimezone = timezone;
    }
}
