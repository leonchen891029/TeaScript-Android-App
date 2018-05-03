package com.teacore.teascript.module.myinformation;

import android.os.Bundle;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.bean.FriendsList;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;

/**
 * 关注|粉丝ViewPagerFragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-13
 */

public class FriendsViewPagerFragment extends BaseViewPagerFragment{

    public static final String BUNDLE_KEY_TABIDX = "BUNDLE_KEY_TABIDX";

    private int mInitTabIdx;

    private int mUid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mInitTabIdx = args.getInt(BUNDLE_KEY_TABIDX, 0);
        mUid = args.getInt(FriendsListFragment.BUNDLE_KEY_UID, 0);
    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(R.array.friends_viewpager_arrays);
        adapter.addTab(title[0], "follower", FriendsListFragment.class, getBundle(FriendsList.TYPE_FOLLOWER));
        adapter.addTab(title[1], "following", FriendsListFragment.class, getBundle(FriendsList.TYPE_FANS));

        mViewPager.setCurrentItem(mInitTabIdx);
    }

    private Bundle getBundle(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, catalog);
        bundle.putInt(FriendsListFragment.BUNDLE_KEY_UID, mUid);
        return bundle;
    }

}
