package com.teacore.teascript.module.general.behavior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.teacore.teascript.util.TDevice;

/**
 * 滚动时隐藏的behavior
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-4
 */

public class ScrollingAutoHideBehavior extends CoordinatorLayout.Behavior<View>{

    private static final Interpolator interpolator=new DecelerateInterpolator();
    private boolean mIsAnimatingOut=false;
    private boolean mIsScrollToBottom=false;

    public ScrollingAutoHideBehavior(Context context, AttributeSet attrs)
    {
        super();
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);

        if (!mIsScrollToBottom) {
            float mPreTranslationY = dy + child.getTranslationY();
            if (mPreTranslationY <= 0) {
                child.setTranslationY(0);
                mIsAnimatingOut = true;
            }
            if (mPreTranslationY >= child.getHeight()) {
                child.setTranslationY(child.getHeight());
                mIsAnimatingOut = false;
            }
            if (mPreTranslationY > 0 && mPreTranslationY < child.getHeight()) {
                child.setTranslationY(mPreTranslationY);
                mIsAnimatingOut = dy > 0;
            }
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent,final View child,View dependency){

        if(child!=null && dependency !=null && dependency instanceof NestedScrollView){
            NestedScrollView scrollView=(NestedScrollView) dependency;

            if(scrollView.getChildCount()>0){
                View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
                view.setPadding(view.getPaddingLeft(),
                        view.getPaddingTop(),
                        view.getPaddingRight(),
                        view.getPaddingBottom() + child.getHeight());
            }
            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if(view.getChildCount()>0){
                        View childView=view.getChildAt(view.getChildCount()-1);
                        int diff=(childView.getBottom()-(view.getHeight()+scrollY));
                        //如果diff等于0，代表以及到了bottom
                        if(diff==0){
                            animateIn(child);
                            mIsScrollToBottom = true;
                        } else {
                            mIsScrollToBottom = false;
                        }
                    }
                }
            });
        }
        return super.layoutDependsOn(parent,child,dependency);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final View child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }


    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final View child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        //滑动时隐藏软键盘
        TDevice.hideSoftKeyboard(coordinatorLayout);

        //我们需要关心的是竖直方向的滑动
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);

        if (child.getTranslationY() == 0 || child.getTranslationY() == child.getHeight()) return;

        if (mIsAnimatingOut) {
            animateOut(child);
        } else {
            animateIn(child);
        }

    }

    private void animateOut(final View button) {
        button.animate()
                .translationY(button.getHeight())
                .setInterpolator(interpolator)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        button.setTranslationY(button.getHeight());
                    }
                });
    }

    private void animateIn(final View button) {
        button.animate()
                .translationY(0)
                .setInterpolator(interpolator)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        button.setTranslationY(0);
                    }
                });
    }


    /**
     * 点击内容栏唤起底部操作区域
     * @param coordinatorLayout 外部CoordinatorLayout
     * @param contentView       滚动区域
     * @param bottomView        滚动时隐藏底部区域
     */
    public static void showBottomLayout(CoordinatorLayout coordinatorLayout, View contentView, final View bottomView) {

        bottomView.animate()
                .translationY(0)
                .setInterpolator(interpolator)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bottomView.setTranslationY(0);
                    }
                });

    }

}
