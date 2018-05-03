package com.teacore.teascript.module.myinformation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.Notice;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.bean.TeatimeLike;
import com.teacore.teascript.bean.TeatimeLikesList;
import com.teacore.teascript.bean.TeatimesList;
import com.teacore.teascript.module.general.teatime.TeatimeDetailActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.service.NoticeUtils;
import com.teacore.teascript.adapter.TeatimeLikeAdapter;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 被赞过的Teatime的列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-10
 */

public class TeatimeLikeListFragment extends BaseListFragment<TeatimeLike>{

    private static final String CACHE_KEY_PREFIX = "teatime_like_list_";

    private boolean mIsWatingLogin;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            setupContent();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCatalog > 0) {
            IntentFilter filter = new IntentFilter(
                    Constants.INTENT_ACTION_USER_CHANGE);
            filter.addAction(Constants.INTENT_ACTION_LOGOUT);
            getActivity().registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        if (mCatalog > 0) {
            getActivity().unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected TeatimeLikeAdapter getListAdapter() {
        return new TeatimeLikeAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mCatalog;
    }

    @Override
    protected TeatimeLikesList parseList(InputStream is) throws Exception {
        TeatimeLikesList list = XmlUtils.toBean(TeatimeLikesList.class, is);
        return list;
    }

    @Override
    protected TeatimeLikesList readList(Serializable seri) {
        return ((TeatimeLikesList) seri);
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getTeatimeLikeList(mCurrentPage, mHandler);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeatimeLike teatimeLike = mAdapter.getItem(position);
        if (teatimeLike != null) {
            Teatime teatime = teatimeLike.getTeatime();
            TeatimeDetailActivity.show(getActivity(), teatime);
        }
    }

    private void setupContent() {
        if (AppContext.getInstance().isLogin()) {
            mIsWatingLogin = false;
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
            requestData(true);
        } else {
            mCatalog = TeatimesList.CATALOG_ME;
            mIsWatingLogin = true;
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
            mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {
            mCatalog = AppContext.getInstance().getLoginUid();
            mIsWatingLogin = false;
            super.requestData(refresh);
        } else {
            mIsWatingLogin = true;
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
            mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                    requestData(true);
                } else {
                    UiUtils.showLoginActivity(getActivity());
                }
            }
        });
    }

    @Override
    protected long getAutoRefreshTime() {
        if (mCatalog == TeatimesList.CATALOG_LATEST) {
            return 3 * 60;
        }
        return super.getAutoRefreshTime();
    }

    @Override
    protected void onRefreshNetworkSuccess() {
        super.onRefreshNetworkSuccess();
        if (AppContext.getInstance().isLogin()
                && mCatalog == AppContext.getInstance().getLoginUid()
                && 4 == NoticeViewPagerFragment.noticeCurrentPage) {
            NoticeUtils.clearNotice(Notice.TYPE_NEWLIKE);
            UiUtils.sendBroadcastForNotice(getActivity());
        }
    }

    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();

        if (enity != null && enity instanceof TeatimeLike) {
            TeatimeLike tweetLike = (TeatimeLike) enity;
            for (int i = 0; i < s; i++) {
                if (tweetLike.getUser().getId() == ((TeatimeLike) data.get(i)).getId()) {
                    return true;
                }
            }
        }
        return false;
    }

}
