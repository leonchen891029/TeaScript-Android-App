package com.teacore.teascript.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 左右切换屏幕的控件
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-30
 */

public class SwitchLayout extends ViewGroup{


    private static final String TAG=SwitchLayout.class.getSimpleName();
    private Scroller mScroller;
    //是否可以滑动
    private boolean mIsScroll=true;
    private VelocityTracker mVelocityTracker;
    private int mCurrentScreen;
    private int mDefaultScreen=0;
    private static final int TOUCH_STATE_REST=0;
    private static final int TOUCH_STATE_SCROLLING=1;
    private static final int SWITCH_VELOCITY=600;
    private int mTouchState=TOUCH_STATE_REST;
    private int mTouchSlop;
    private float mLastX;
    private float mLastY;
    private OnViewChangeListener mOnViewChangeListener;

    public SwitchLayout(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public SwitchLayout(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        mScroller=new Scroller(context);
        mCurrentScreen=mDefaultScreen;
        mTouchSlop= ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        final int width=MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode=MeasureSpec.getMode(widthMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only canmCurScreen run at EXACTLY mode!");
        }
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only can run at EXACTLY mode!");
        }

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(mCurrentScreen * width, 0);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childWidth = childView.getMeasuredWidth();
                childView.layout(childLeft, 0, childLeft + childWidth,
                        childView.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    public void switchToDestination() {
        final int screenWidth = getWidth();
        final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        switchToScreen(destScreen);
    }

    public void switchToScreen(int whichScreen) {
        //是否可滑动
        if(!mIsScroll) {
            this.setToScreen(whichScreen);
            return;
        }

        scrollToScreen(whichScreen);
    }

    public void scrollToScreen(int whichScreen) {
        // get the valid layout page
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollX() != (whichScreen * getWidth())) {
            final int delta = whichScreen * getWidth() - getScrollX();
            mScroller.startScroll(getScrollX(), 0, delta, 0,
                    Math.abs(delta) * 1);//持续滚动时间 以毫秒为单位
            mCurrentScreen = whichScreen;
            invalidate(); // Redraw the layout

            if (mOnViewChangeListener != null)
            {
                mOnViewChangeListener.OnViewChange(mCurrentScreen);
            }
        }
    }

    public void setToScreen(int whichScreen) {
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        mCurrentScreen = whichScreen;
        scrollTo(whichScreen * getWidth(), 0);

        if (mOnViewChangeListener != null)
        {
            mOnViewChangeListener.OnViewChange(mCurrentScreen);
        }
    }

    public int getCurrentScreen() {
        return mCurrentScreen;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //是否可滑动
        if(!mIsScroll) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastX - x);
                int deltaY = (int) (mLastY - y);
                if(Math.abs(deltaX) < 200 && Math.abs(deltaY) > 10)
                    break;
                mLastY = y;
                mLastX = x;
                scrollBy(deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();
                if (velocityX > SWITCH_VELOCITY && mCurrentScreen > 0) {
                    switchToScreen(mCurrentScreen - 1);
                } else if (velocityX < -SWITCH_VELOCITY
                        && mCurrentScreen < getChildCount() - 1) {
                    switchToScreen(mCurrentScreen + 1);
                } else {
                    switchToDestination();
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!mIsScroll) {
            return super.onTouchEvent(ev);
        }
        //Log.e(TAG, "onInterceptTouchEvent-slop:" + mTouchSlop);
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(mLastX - x);
                if (xDiff > mTouchSlop) {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
                        : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return mTouchState != TOUCH_STATE_REST;
    }

    //设置屏幕切换监听器
    public void SetOnViewChangeListener(OnViewChangeListener listener)
    {
        mOnViewChangeListener = listener;
    }

    //屏幕切换监听器
    public interface OnViewChangeListener {
        public void OnViewChange(int view);
    }

    public void setIsScroll(boolean isScroll){
        this.mIsScroll=isScroll;
    }


}
