package com.teacore.teascript.module.myinformation;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.Friend;
import com.teacore.teascript.bean.FriendsList;
import com.teacore.teascript.bean.Notice;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.service.NoticeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.module.main.MainActivity;
import com.teacore.teascript.adapter.FriendAdapter;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 粉丝列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-8
 */

public class FriendsListFragment extends BaseListFragment<Friend>{

    private static final String TAG = FriendsListFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_UID="BUNDLE_KEY_UID";

    private static final String CACHE_KEY_PREFIX="friends_list";

    private int mUid;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle args=getArguments();
        if(args!=null){
            mUid=args.getInt(BUNDLE_KEY_UID,0);
        }
    }

    @Override
    public void onResume() {
        if (mCatalog == FriendsList.TYPE_FANS
                && mUid == AppContext.getInstance().getLoginUid()) {
            refreshNotice();
        }
        super.onResume();
    }

    private void refreshNotice() {
        Notice notice = MainActivity.mNotice;
        if (notice != null && notice.getNewFansCount() > 0) {
            onRefresh();
        }
    }

    @Override
    protected FriendAdapter getListAdapter() {
        return new FriendAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCatalog + "_" + mUid;
    }

    @Override
    protected FriendsList parseList(InputStream is) throws Exception {
        return XmlUtils.toBean(FriendsList.class, is);
    }

    @Override
    protected FriendsList readList(Serializable seri) {
        return ((FriendsList) seri);
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((Friend) enity).getUserid() == ((Friend) data.get(i))
                        .getUserid()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getFriendList(mUid, mCatalog, mCurrentPage, mHandler);
    }

    @Override
    protected void onRefreshNetworkSuccess() {
        if ((NoticeViewPagerFragment.noticeCurrentPage == 3 || NoticeViewPagerFragment.noticeShowCount[3] > 0)
                && mCatalog == FriendsList.TYPE_FANS
                && mUid == AppContext.getInstance().getLoginUid()) {
            NoticeUtils.clearNotice(Notice.TYPE_NEWFAN);
            UiUtils.sendBroadcastForNotice(getActivity());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Friend item = (Friend) mAdapter.getItem(position);
        if (item != null) {
            if (mUid == AppContext.getInstance().getLoginUid()) {
                UiUtils.showMessageDetail(getActivity(), item.getUserid(),
                        item.getName());
                return;
            }
            UiUtils.showUserCenter(getActivity(), item.getUserid(),
                    item.getName());
        }
    }

}
