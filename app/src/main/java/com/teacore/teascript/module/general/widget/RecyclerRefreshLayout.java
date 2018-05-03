package com.teacore.teascript.module.general.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 *与RecyclerView相互结合的SwipeRefreshLayout
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-2
 */

public class RecyclerRefreshLayout extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;

    private int mTouchSlop;

    private RecyclerRefreshLayoutListener mListener;

    private boolean mIsLoading=false;
    private boolean mCanLoadMore=true;
    private boolean mHasMore=true;

    private int mYDown;
    private int mLastY;

    public RecyclerRefreshLayout(Context context){
        this(context,null);
    }

    public RecyclerRefreshLayout(Context context, AttributeSet attrs){
        super(context,attrs);
        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (mListener != null && !mIsLoading) {
            mListener.onRefreshing();
        } else
            setRefreshing(false);
    }

    @Override
    protected void onLayout(boolean changed,int left,int top,int right,int bottom){
        super.onLayout(changed,left,top,right,bottom);
        //初始化recycleview对象
        if(mRecyclerView==null){
            getRecyclerView();
        }
    }

    private void getRecyclerView(){
        int childCount=getChildCount();
        if(childCount>0){
            View childView=getChildAt(0);
            if(childView instanceof RecyclerView){
                mRecyclerView=(RecyclerView) childView;
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if(canLoad() && mCanLoadMore){
                            loadData();
                        }
                    }
                });
            }
        }
    }

    public void setNoMoreData(){
        this.mHasMore=false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastY = (int) event.getRawY();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    //是否可以加载更多
    private boolean canLoad(){
        return isScrollBottom() && mHasMore && isPullUp() && !mIsLoading;
    }

    //如果到了最底部，而且上拉操作
    private void loadData() {
        if (mListener != null) {
            setOnLoading(true);
            mListener.onLoadMore();
        }
    }

    //是否是上拉操作
    private boolean isPullUp(){
        return (mYDown - mLastY) >= mTouchSlop;
    }

    public void setOnLoading(boolean loading) {
        mIsLoading = loading;
        if (!mIsLoading) {
            mYDown = 0;
            mLastY = 0;
        }
    }

    //判断是否到了最底部
    private boolean isScrollBottom() {
        return (mRecyclerView != null && mRecyclerView.getAdapter() != null)
                && getLastVisiblePosition() == (mRecyclerView.getAdapter().getItemCount() - 1);
    }

    public void onComplete() {
        setOnLoading(false);
        setRefreshing(false);
        mHasMore = true;
    }

    public void setCanLoadMore(boolean mCanLoadMore) {
        this.mCanLoadMore = mCanLoadMore;
    }

    public boolean isHasMore() {
        return mHasMore;
    }

    public void setHasMore(boolean mHasMore) {
        this.mHasMore = mHasMore;
    }

    public int getLastVisiblePosition() {
        int position;
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        } else if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = mRecyclerView.getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }

    public void setOnRecyclerRefreshLayoutListener(RecyclerRefreshLayoutListener listener) {
        this.mListener = listener;
    }


    public interface RecyclerRefreshLayoutListener{
        void onRefreshing();

        void onLoadMore();
    }


}
