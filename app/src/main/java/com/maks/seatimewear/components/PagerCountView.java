package com.maks.seatimewear.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.maks.seatimewear.R;

/**
 * TODO: document your custom view class.
 */
public class PagerCountView extends View {
    private float mPointR = 5.0f;
    private float mPointMargin = 5.0f;
    private int mSelectedColor = Color.RED;
    private int mInactiveColor = Color.GRAY;


    private int mPageCount = 0;
    private int mCurrentPageNumber = 0;

    Paint paint;
    Paint paintSelected;

    public PagerCountView(Context context) {
        super(context);
        init(null, 0);
    }

    public PagerCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PagerCountView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        paint = new Paint();

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PagerCountView, defStyle, 0);

        try {
            mSelectedColor = a.getColor(
                    R.styleable.PagerCountView_selectedColor,
                    mSelectedColor);

            mInactiveColor = a.getColor(
                    R.styleable.PagerCountView_inactiveColor,
                    mInactiveColor);
            mPointR = a.getDimension(R.styleable.PagerCountView_radius, mPointR);



            mPageCount = a.getInteger(
                    R.styleable.PagerCountView_pageCount,
                    mPageCount);
        } finally {
            a.recycle();
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mInactiveColor);

        paintSelected = new Paint(paint);
        paintSelected.setColor(mSelectedColor);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.round(mPageCount * ((mPointR * 2) + mPointMargin + mPointR));
        int height = this.getLayoutParams().height;
        this.setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mPageCount; i++) {
            float x = i * ((mPointR * 2) + mPointMargin) + mPointR;
            Paint p = paint;
            if (i + 1 == mCurrentPageNumber) {
                p = paintSelected;
            }
            canvas.drawCircle(x, mPointR, mPointR, p);
        }
    }
    /**
     * Sets page number for view switch
     *
     * @param page Page number in collection.
     */
    public void setPage(int page) {
        mCurrentPageNumber = page;
        invalidate();
    }
}
