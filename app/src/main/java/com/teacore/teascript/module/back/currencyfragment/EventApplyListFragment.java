package com.teacore.teascript.module.back.currencyfragment;

import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.EventAppliesList;
import com.teacore.teascript.bean.EventApply;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.adapter.EventApplyAdapter;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 活动报名者列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */

public class EventApplyListFragment extends BaseListFragment<EventApply>{

    protected static final String TAG = EventApplyListFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "event_apply_list";

    @Override
    protected EventApplyAdapter getListAdapter() {
        return new EventApplyAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCatalog;
    }

    @Override
    protected EventAppliesList parseList(InputStream is) throws Exception {
        return XmlUtils.toBean(EventAppliesList.class, is);
    }

    @Override
    protected EventAppliesList readList(Serializable seri) {
        return ((EventAppliesList) seri);
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getEventApplies(mCatalog, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        EventApply item = (EventApply) mAdapter.getItem(position);

        if (item != null) {
            //如果已经登录，显示与该好友的聊天记录
           if (AppContext.getInstance().isLogin()) {
                UiUtils.showMessageDetail(getActivity(), item.getId(), item.getName());
                return;
            }
            //如果没有登录，则显示该用户的用户中心
            UiUtils.showUserCenter(getActivity(), item.getId(),item.getName());
        }

    }

}
