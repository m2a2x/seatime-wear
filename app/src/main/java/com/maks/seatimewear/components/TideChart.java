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

    public TideChart(Context context) {
        super(context);
        init(null, 0);
    }

    public TideChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TideChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TideChart, defStyle, 0);
        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        /*mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom; */
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



        Path wave = drawCurve(contentWidth, this.mTides.size(), this.isFirstLow(), 10);
        Path lines = drawDemensions(contentWidth, this.mTides.size(), 10);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2.0f);
        paint.setColor(Color.BLACK);

        c.drawPath(wave, paint);

        paint.setStrokeWidth(1.0f);
        c.drawPath(lines, paint);

        mTextPaint.setTextSize(12);
        mTextPaint.setColor(Color.BLACK);
        this.drawTime(c, contentWidth, this.mTides.size(), this.mTides, mTextPaint);
        this.drawTide(c, contentWidth, this.mTides.size(), this.mTides, mTextPaint);
    }
    private Path drawCurve(int w, int pikes, boolean startLow, int paddingTop) {
        Path graph = new Path();
        int i = 0;
        boolean low = startLow;

        int x = Math.round(w / pikes / 2);
        int x2 = 2 * x;

        int y = 40;

        graph.moveTo(0, y / 2 + paddingTop);

        while (i < pikes) {
            graph.rQuadTo(x, (low ? 1 : -1) * y, x2, 0);
            low = !low;
            i++;
        }

        return graph;
    }

    private Path drawDemensions(int w, int pikes, int paddingTop) {
        Path line = new Path();
        int i = 0;
        int x = Math.round(w / pikes);
        int y = 40;

        while (i < pikes) {
            line.moveTo(x * i + x / 2, paddingTop);
            line.lineTo(x * i + x / 2, y + paddingTop);
            i++;
        }

        return line;
    }

    private void drawTime(Canvas c, int w, int pikes, ArrayList<Tide> tides, Paint p) {
        int i = 0;
        int x = Math.round(w / pikes);

        while (i < pikes) {
            Tide tide = tides.get(i);
            c.drawText(tide.getTime(), x * i + x / 2, 10, p);
            i++;
        }
    }

    private void drawTide(Canvas c, int w, int pikes, ArrayList<Tide> tides, Paint p) {
        int i = 0;
        int x = Math.round(w / pikes);
        int y = 40;

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
        // invalidateTextPaintAndMeasurements();
    }
}
