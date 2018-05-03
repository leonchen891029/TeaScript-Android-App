package com.teacore.teascript.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Notebook圆形ImageView 重写了onTouchEvent
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-5
 */

public class NotebookCircleView extends CircleImageView{


    public NotebookCircleView(Context context) {
        super(context);
    }

    public NotebookCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotebookCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }

}
