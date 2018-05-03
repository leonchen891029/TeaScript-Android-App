package com.teacore.teascript.module.quickoption.fragment;

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
import com.teacore.teascript.bean.Event;
import com.teacore.teascript.bean.EventList;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.adapter.EventAdapter;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 同城活动列表类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */

public class SameCityFragment extends BaseListFragment<Event> {

    public static final String BUNDLE_KEY_EVENT_TYPE = "BUNDLE_KEY_EVENT_TYPE";

    private static final String CACHE_KEY_PREFIX = "event_list_";

    private int event_type;

    @Override
    protected EventAdapter getListAdapter() {
        EventAdapter adapter = new EventAdapter();
        adapter.setEventType(event_type);
        return adapter;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            requestData(true);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            event_type = args.getInt(BUNDLE_KEY_EVENT_TYPE);
        }

        if (event_type == EventList.EVENT_LIST_TYPE_MY_EVENT) {
            IntentFilter filter = new IntentFilter(
                    Constants.INTENT_ACTION_USER_CHANGE);
            filter.addAction(Constants.INTENT_ACTION_LOGOUT);
            getActivity().registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (event_type == EventList.EVENT_LIST_TYPE_MY_EVENT) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEmptyLayout();
            }
        });
    }

    private void clickEmptyLayout() {
        if (event_type == EventList.EVENT_LIST_TYPE_NEW_EVENT) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
            requestData(true);
        } else {
            if (AppContext.getInstance().isLogin()) {
                mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            } else {
                UiUtils.showLoginActivity(getActivity());
            }
        }
    }

    @Override
    protected void requestData(boolean refresh) {

        if (event_type == EventList.EVENT_LIST_TYPE_NEW_EVENT) {
            mCatalog = -1;
            super.requestData(refresh);
            return;
        }
        if (AppContext.getInstance().isLogin()) {
            mCatalog = AppContext.getInstance().getLoginUid();
            super.requestData(refresh);
        } else {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
            mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mCatalog;
    }

    @Override
    protected EventList parseList(InputStream is) throws Exception {
        return XmlUtils.toBean(EventList.class, is);
    }

    @Override
    protected EventList readList(Serializable seri) {
        return ((EventList) seri);
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getEventList(mCurrentPage, mCatalog, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Event event = mAdapter.getItem(position);
        if (event != null)
            UiUtils.showEventDetailActivity(view.getContext(), event.getId());
    }

}
