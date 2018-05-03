package com.teacore.teascript.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 *自定义的RefreshLayout
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-22
 */

public class TSRefreshLayout extends SwipeRefreshLayout implements OnScrollListener,OnRefreshListener{

    private ListView mListView;
    //touchSlop是一个滑动距离的常量，只有我们在屏幕上面滑动的距离超过touchSlop时，系统才会认为我们做了滑动操作
    private int mTouchSlop;

    private TSRefreshLayoutListener mListener;

    private boolean mIsOnLoading=false;
    private boolean mCanLoadMore=false;

    private int mDownY;
    private int mLastY;

    private int mTextColor;
    private int mFooterBackground;
    private boolean mIsMoving=false;

    public TSRefreshLayout(Context context){
        this(context,null);
    }

    public TSRefreshLayout(Context context, AttributeSet attrs){
        super(context,attrs);
        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        setOnRefreshListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (canLoad()) {
            loadData();
        }
    }

    @Override
    public void onRefresh() {
        if (mListener != null && !mIsOnLoading) {
            mListener.onRefreshing();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mListView == null) {
            getListView();
        }
    }

    //获取listview
    private void getListView(){
        int child = getChildCount();
        if (child > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                mListView.setOnScrollListener(this);
            }
        }
    }

    public void setCanLoadMore() {
        this.mCanLoadMore = true;
    }

    public void setNoMoreData() {
        this.mCanLoadMore = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mDownY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动
                mIsMoving = true;
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                mIsMoving = false;
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /*
    *是否可以加载更多
    *条件 1.到了最底部 2.listview不在加载中 3.上拉操作
    */
    private boolean canLoad() {
        return isInBottom() && !mIsOnLoading && isPullUp() && mCanLoadMore;
    }

    //加载数据
    private void loadData(){
        if(mListener!=null){
            setIsOnLoading(true);
            mListener.onLoadMore();
        }
    }

    //是否是上拉操作
    private boolean isPullUp(){
        return (mDownY-mLastY)>=mTouchSlop;
    }


    //设置正在加载
    public void setIsOnLoading(boolean loading) {
        mIsOnLoading = loading;
        if (!mIsOnLoading) {
            mDownY = 0;
            mLastY = 0;
        }
    }

    //判断是否到了最底部
    private boolean isInBottom() {
        return (mListView != null && mListView.getAdapter() != null)
                && mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
    }

    public interface TSRefreshLayoutListener {

        void onRefreshing();

        void onLoadMore();

    }


    //加载结束记得调用
    public void onLoadComplete() {
        setIsOnLoading(false);
        setRefreshing(false);
    }

    public void setTSRefreshLayoutListener(TSRefreshLayoutListener loadListener) {
        mListener = loadListener;
    }

    public boolean isMoving() {
        return mIsMoving;
    }



}
