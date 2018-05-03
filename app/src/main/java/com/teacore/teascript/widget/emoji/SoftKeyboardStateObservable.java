package com.teacore.teascript.widget.emoji;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

/**软键盘状态辅助类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-18
 */

public class SoftKeyboardStateObservable implements ViewTreeObserver.OnGlobalLayoutListener{

    public interface SoftKeyboardStateListener{
        void onSoftKeyboardOpened(int keyboardHeightInPx);

        void onSoftKeyboardClosed();
    }

    private final List<SoftKeyboardStateListener> listeners=new LinkedList<SoftKeyboardStateListener>();
    private final View activityRootView;
    private int lastSoftKeyboardHeightInPx;
    private boolean isSoftKeyboardOpened;

    public SoftKeyboardStateObservable(View activityRootView) {
        this(activityRootView, false);
    }

    public SoftKeyboardStateObservable(View activityRootView,
                                       boolean isSoftKeyboardOpened) {
        this.activityRootView = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    //当全局布局改变时调用该函数
    @Override
    public void onGlobalLayout() {

        final Rect r = new Rect();

        //根据activityRootView的可视面积得到一个Rect
        activityRootView.getWindowVisibleDisplayFrame(r);

        final int heightDiff = activityRootView.getRootView().getHeight()
                - (r.bottom - r.top);

        //以高度差为判断依据，如果大于100，可能是一个键盘
        if (!isSoftKeyboardOpened && heightDiff > 100) {

            isSoftKeyboardOpened = true;
            notifyOnSoftKeyboardOpened(heightDiff);

        } else if (isSoftKeyboardOpened && heightDiff < 100) {

            isSoftKeyboardOpened = false;
            notifyOnSoftKeyboardClosed();
        }
    }

    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }

    public int getLastSoftKeyboardHeightInPx() {
        return lastSoftKeyboardHeightInPx;
    }

    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(
            SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx) {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx;

        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed() {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }

}
