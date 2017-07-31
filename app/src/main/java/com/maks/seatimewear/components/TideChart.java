package com.maks.seatimewear.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.maks.seatimewear.R;
import com.maks.seatimewear.model.Tide;

import java.util.ArrayList;


public class TideChart extends View {
    private ArrayList<Tide> mTides;

    private TextPaint mTextPaint;

    float mLineSize = 1.0f;
    float mDimensionLineSize = 0.0f;
    float mTextSize = 5.0f;
    float mTextHeight = 5.0f;

    int mChartHeight = 40;

    int mLineColor = Color.WHITE;
    int mTextColor = Color.WHITE;
    int mDimensionLineColor = Color.TRANSPARENT;

    boolean mHasTideDescription = false;

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


        try {
            // Retrieve the values from the TypedArray and store into

            mTextSize = a.getDimension(R.styleable.TideChart_textSize, mTextSize);
            mDimensionLineSize = a.getDimension(R.styleable.TideChart_dimensionSize, mDimensionLineSize);
            mLineSize = a.getDimension(R.styleable.TideChart_lineSize, mLineSize);
            mTextHeight = a.getDimension(R.styleable.TideChart_labelHeight, mTextHeight);

            mLineColor = a.getColor(R.styleable.TideChart_lineColor, mLineColor);
            mDimensionLineColor = a.getColor(R.styleable.TideChart_dimensionLineColor, mDimensionLineColor);
            mTextColor = a.getColor(R.styleable.TideChart_textColor, mTextColor);
            mHasTideDescription = a.getBoolean(R.styleable.TideChart_tideDescription, mHasTideDescription);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }

    private boolean isFirstLow() {
        Tide tide = this.mTides.get(0);
        if (tide.getState().equals("High")) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        if (this.mTides == null || this.mTides.isEmpty()) {
            return;
        }

        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        /*
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int contentHeight = getHeight() - paddingTop - paddingBottom;
        // Draw the text.
        /* canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        } */



        Path wave = drawCurve(contentWidth, this.mTides.size(), this.isFirstLow(), mTextHeight, mChartHeight);
        Path lines = drawDimensions(contentWidth, this.mTides.size(), mTextHeight, mChartHeight);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mLineSize);
        paint.setColor(mLineColor);

        c.drawPath(wave, paint);

        // Dimension
        paint.setColor(mDimensionLineColor);
        paint.setStrokeWidth(mDimensionLineSize);
        c.drawPath(lines, paint);

        this.drawTime(c, contentWidth, this.mTides.size(), this.mTides, mTextPaint, mChartHeight);
        if (mHasTideDescription) {
            this.drawTide(c, contentWidth, this.mTides.size(), this.mTides, mTextPaint, mChartHeight);
        }
    }
    private Path drawCurve(int w, int pikes, boolean startLow, float paddingTop, int y) {
        Path graph = new Path();
        int i = 0;
        boolean low = startLow;

        int x = Math.round(w / pikes / 2);
        int x2 = 2 * x;

        graph.moveTo(0, y / 2 + paddingTop);

        while (i < pikes) {
            graph.rQuadTo(x, (low ? 1 : -1) * y, x2, 0);
            low = !low;
            i++;
        }

        return graph;
    }

    private Path drawDimensions(int w, int pikes, float paddingTop, int y) {
        Path line = new Path();
        int i = 0;
        int x = Math.round(w / pikes);

        while (i < pikes) {
            line.moveTo(x * i + x / 2, paddingTop);
            line.lineTo(x * i + x / 2, y + paddingTop);
            i++;
        }

        return line;
    }

    private void drawTime(Canvas c, int w, int pikes, ArrayList<Tide> tides, Paint p, int y) {
        int i = 0;
        int x = Math.round(w / pikes);

        while (i < pikes) {
            Tide tide = tides.get(i);
            c.drawText(tide.getTime(), x * i + x / 2, 10, p);
            i++;
        }
    }

    private void drawTide(Canvas c, int w, int pikes, ArrayList<Tide> tides, Paint p, int y) {
        int i = 0;
        int x = Math.round(w / pikes);

        while (i < pikes) {
            Tide tide = tides.get(i);
            c.drawText(tide.getState(), x * i + x / 2, y, p);
            i++;
        }
    }

    /**
     * Sets the view's tides attribute value. This tides
     * is the tides to draw.
     *
     * @param tides Tides attribute value to use.
     */
    public void setTides(ArrayList<Tide> tides) {
        mTides = tides;
    }
}
