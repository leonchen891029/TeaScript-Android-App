package com.teacore.teascript.module.general.base.basefragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.base.baseadapter.BaseRecyclerAdapter;
import com.teacore.teascript.module.general.base.basebean.PageBean;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.widget.RecyclerRefreshLayout;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 基于RecyclerView的列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-12
 */

public abstract class BaseRecyclerViewFragment<T> extends BaseFragment implements RecyclerRefreshLayout.RecyclerRefreshLayoutListener,
       BaseRecyclerAdapter.OnItemClickListener{

    protected BaseRecyclerAdapter<T> mAdapter;
    protected RecyclerView mRecyclerView;
    protected RecyclerRefreshLayout mRefreshLayout;
    protected boolean mIsRefresh;

    protected PageBean<T> mPageBean;

    protected TextHttpResponseHandler mHandler=new TextHttpResponseHandler() {
        @Override
        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            onRequestFinish();
        }

        @Override
        public void onSuccess(int i, Header[] headers, String responseString) {
            try {
                ResultBean<PageBean<T>> resultBean = AppContext.createGson().fromJson(responseString, getType());

                if (resultBean != null && resultBean.isSuccess() && resultBean.getResult().getItems() != null) {
                    onRequestSuccess(resultBean.getCode());
                    setListData(resultBean);
                } else {
                    mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                }

                onRequestFinish();

            } catch (Exception e) {
                e.printStackTrace();
                onFailure(i, headers, responseString, e);
            }
        }
    };


    @Override
    public int getLayoutId(){
        return R.layout.general_fragment_base_recycler_view;
    }

    @Override
    protected void initView(View view){
        mRefreshLayout=(RecyclerRefreshLayout) view.findViewById(R.id.refreshlayout);
        mRecyclerView=(RecyclerView) view.findViewById(R.id.recyclerview);
    }

    @Override
    public void initData(){

        mAdapter=getRecyclerAdapter();
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE,false);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mRefreshLayout.setOnRecyclerRefreshLayoutListener(this);
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE,false);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRefreshLayout.setColorSchemeResources(R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                onRefreshing();
            }
        });

    }

    @Override
    public void onItemClick(int position, long itemId) {

    }

    @Override
    public void onRefreshing() {
        mIsRefresh = true;
        requestData();
    }

    protected void requestData() {
    }

    protected void onRequestStart() {

    }

    protected void onRequestSuccess(int code) {

    }

    protected void onRequestFinish() {
        onComplete();
    }

    protected void onComplete() {
        mRefreshLayout.onComplete();
        mIsRefresh = false;
    }

    protected void setListData(ResultBean<PageBean<T>> resultBean) {

        mPageBean.setNextPageToken(resultBean.getResult().getNextPageToken());

        if (mIsRefresh) {
            mPageBean.setItems(resultBean.getResult().getItems());
            mAdapter.clear();
            mAdapter.addAll(mPageBean.getItems());
            mPageBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());
            mRefreshLayout.setCanLoadMore(true);
        } else {
            mAdapter.addAll(resultBean.getResult().getItems());
        }
        if (resultBean.getResult().getItems().size() < 20) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected abstract BaseRecyclerAdapter<T> getRecyclerAdapter();

    protected abstract Type getType();


}
