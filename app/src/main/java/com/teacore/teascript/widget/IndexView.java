package com.teacore.teascript.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 索引View
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-30
 */

public class IndexView extends View {

    private static final String DEFAULT_INDEX="☆ABCDEFGHIJKLMNOPQRSTUVWXYZ#";

    private static final int[] ATTRS=new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor,
    };

    private char[] indexLetters=DEFAULT_INDEX.toCharArray();

    private Paint mPaint;
    private int lastIndex=-1;
    private int itemHeight;
    private int offsetY;

    //是否初始化高度
    private boolean isInitItemHeight=false;

    private OnIndexTouchListener mListener;

    public IndexView(Context context){
        this(context,null);
    }

    public IndexView(Context context,AttributeSet attrs){
        this(context,attrs,0);
    }

    public IndexView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);

        TypedArray typedArray=context.obtainStyledAttributes(attrs,ATTRS);

        mPaint.setAntiAlias(true);
        mPaint.setTextSize(typedArray.getDimensionPixelSize(0,12));
        mPaint.setColor(typedArray.getColor(1,0xff000000));

        typedArray.recycle();

        setClickable(true);
    }

    @Override
    protected void onSizeChanged(int w,int h,int oldW,int oldH){
        super.onSizeChanged(w,h,oldW,oldH);
        isInitItemHeight=false;
    }

    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);

        if(indexLetters==null||indexLetters.length==0){
            return;
        }

        final int width=getWidth();

        if(!isInitItemHeight){
            final int height=getHeight()-getPaddingTop()-getPaddingBottom();
            itemHeight=height/indexLetters.length;
            final Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            offsetY = (int) ((itemHeight - (fontMetrics.bottom - fontMetrics.top)) * 0.5
                    - fontMetrics.top + getPaddingTop());
            isInitItemHeight = true;
        }

        int x;
        for (int i = 0; i < indexLetters.length; i++) {
            String indexStr = String.valueOf(indexLetters[i]);
            x = (int) ((width - mPaint.measureText(indexStr)) * 0.5);
            canvas.drawText(indexStr, x, offsetY + itemHeight * i, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (itemHeight == 0) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(0x50000000);
            case MotionEvent.ACTION_MOVE:
                final float y = event.getY();
                final int newIndex = (int) (y / itemHeight);
                if (lastIndex != newIndex && 0 <= newIndex && newIndex < indexLetters.length) {
                    lastIndex = newIndex;
                    if (mListener != null) {
                        mListener.onIndexTouchMove(indexLetters[newIndex]);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setBackgroundColor(0);
                if (mListener != null) {
                    mListener.onIndexTouchUp();
                }
                break;
        }
        return true;
    }

    public void setOnIndexTouchListener(OnIndexTouchListener listener) {
        mListener = listener;
    }

    public interface OnIndexTouchListener {
        public void onIndexTouchMove(char indexLeter);

        public void onIndexTouchUp();
    }

}
