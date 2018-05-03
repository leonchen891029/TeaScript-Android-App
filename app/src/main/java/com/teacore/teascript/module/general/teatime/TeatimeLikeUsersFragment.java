package com.teacore.teascript.module.general.teatime;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.bean.TeatimeLikeUserList;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.module.general.adapter.teatimeadapter.TeatimeLikeUserAdapter;
import com.teacore.teascript.module.general.base.baseadapter.BaseRecyclerAdapter;
import com.teacore.teascript.module.general.base.basefragment.BaseRecyclerViewFragment;
import com.teacore.teascript.module.general.detail.constract.TeatimeDetailContract;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Teatime点赞用户列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-21
 */

public class TeatimeLikeUsersFragment extends BaseRecyclerViewFragment<User> implements TeatimeDetailContract.IThumbupView{

    private int pageNum=0;
    private TeatimeDetailContract.Presenter mPresenter;
    private TeatimeDetailContract.IAgencyView mAgencyView;

    private AsyncHttpResponseHandler mRequestHandler=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            TeatimeLikeUserList data = XmlUtils.toBean(TeatimeLikeUserList.class, responseBody);
            setLikeUsersData(data.getList());
            onRequestSuccess(1);
            onRequestFinish();
            if (mAdapter.getCount() < 20 && mAgencyView != null)
                mAgencyView.resetLikeCount(mAdapter.getCount());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            mRefreshLayout.onComplete();
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
        }
    };

    public static TeatimeLikeUsersFragment instantiate(TeatimeDetailContract.Presenter presenter,TeatimeDetailContract.IAgencyView iAgencyView){
        TeatimeLikeUsersFragment fragment=new TeatimeLikeUsersFragment();
        fragment.mPresenter=presenter;
        fragment.mAgencyView=iAgencyView;
        return fragment;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = (TeatimeDetailContract.Presenter) activity;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mPresenter.onScroll();
                }
            }
        });

    }

    @Override
    protected BaseRecyclerAdapter<User> getRecyclerAdapter() {
        return new TeatimeLikeUserAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<TeatimeLikeUserAdapter>() {
        }.getType();
    }

    @Override
    public void onLoadMore() {
        requestData(pageNum);
    }

    @Override
    protected void requestData() {
        requestData(0);
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        if(mIsRefresh) pageNum = 0;
        ++pageNum;
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        User user = mAdapter.getItem(position);
        UiUtils.showUserCenter(getContext(), user.getId(), user.getName());
    }

    private void requestData(int pageNum) {
        TeaScriptApi.getTeatimeLikeList(mPresenter.getTeatime().getId(), pageNum, mRequestHandler);
    }

    private void setLikeUsersData(List<User> users) {
        if (mIsRefresh) {
            //cache the time
            mAdapter.clear();
            mAdapter.addAll(users);
            mRefreshLayout.setCanLoadMore(true);
        } else {
            mAdapter.addAll(users);
        }
        if (users.size() < 20) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
        }
    }

    @Override
    public void onLikeSuccess(boolean isUp, User user) {
        onRefreshing();
    }

}
