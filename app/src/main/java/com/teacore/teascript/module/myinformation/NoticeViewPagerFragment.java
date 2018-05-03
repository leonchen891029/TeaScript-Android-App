package com.teacore.teascript.module.myinformation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.bean.ActiveList;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.FriendsList;
import com.teacore.teascript.bean.Notice;
import com.teacore.teascript.module.main.MainActivity;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;
import com.teacore.teascript.module.back.currencyfragment.ActiveListFragment;
import com.teacore.teascript.widget.BadgeView;
import com.teacore.teascript.widget.PagerSlidingTabStrip;

/**
 * 消息中心页面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-20
 */

public class NoticeViewPagerFragment extends BaseViewPagerFragment{

    public BadgeView mAtmeBV,mCommentBV,mMsgBV,mFansBV,mLikeBV;
    public static int noticeCurrentPage=0;
    public static int[] noticeShowCount=new int[]{0,0,0,0,0};

    private BroadcastReceiver mNoticeReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setNoticeTip();
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        IntentFilter filter=new IntentFilter(Constants.INTENT_ACTION_NOTICE);
        getActivity().registerReceiver(mNoticeReceiver,filter);

        mAtmeBV=new BadgeView(getActivity(),mTabStrip.getBadgeView(0));
        mAtmeBV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mAtmeBV.setBadgePosition(BadgeView.POSITION_CENTER);
        mAtmeBV.setGravity(Gravity.CENTER);
        mAtmeBV.setBackgroundResource(R.drawable.notification_bg);

        mCommentBV = new BadgeView(getActivity(), mTabStrip.getBadgeView(1));
        mCommentBV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mCommentBV.setBadgePosition(BadgeView.POSITION_CENTER);
        mCommentBV.setGravity(Gravity.CENTER);
        mCommentBV.setBackgroundResource(R.drawable.notification_bg);

        mMsgBV = new BadgeView(getActivity(), mTabStrip.getBadgeView(2));
        mMsgBV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mMsgBV.setBadgePosition(BadgeView.POSITION_CENTER);
        mMsgBV.setGravity(Gravity.CENTER);
        mMsgBV.setBackgroundResource(R.drawable.notification_bg);

        mFansBV = new BadgeView(getActivity(), mTabStrip.getBadgeView(3));
        mFansBV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mFansBV.setBadgePosition(BadgeView.POSITION_CENTER);
        mFansBV.setGravity(Gravity.CENTER);
        mFansBV.setBackgroundResource(R.drawable.notification_bg);

        mLikeBV = new BadgeView(getActivity(), mTabStrip.getBadgeView(4));
        mLikeBV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        mLikeBV.setBadgePosition(BadgeView.POSITION_CENTER);
        mLikeBV.setGravity(Gravity.CENTER);
        mLikeBV.setBackgroundResource(R.drawable.notification_bg);

        mTabStrip.getBadgeView(0).setVisibility(View.GONE);
        mTabStrip.getBadgeView(1).setVisibility(View.VISIBLE);
        mTabStrip.getBadgeView(2).setVisibility(View.VISIBLE);
        mTabStrip.getBadgeView(3).setVisibility(View.VISIBLE);
        mTabStrip.getBadgeView(4).setVisibility(View.VISIBLE);

        initView(view);

        initData();
    }

    @Override
    public void initView(View view) {
        changePage();
        mViewPager.setOffscreenPageLimit(3);
        mTabStrip.setOnPagerChange(new PagerSlidingTabStrip.TSOnPageChangeListener() {
            @Override
            public void onChanged(int page) {
                refreshPage(page);
                noticeShowCount[page]++;
                noticeCurrentPage = page;
            }
        });
    }

    @Override
    public void initData() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mNoticeReceiver);
        mNoticeReceiver = null;
    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(
                R.array.mymessage_viewpager_arrays);
        adapter.addTab(title[0], "active_me", ActiveListFragment.class,
                getBundle(ActiveList.CATALOG_ATME));
        adapter.addTab(title[1], "active_comment", ActiveListFragment.class,
                getBundle(ActiveList.CATALOG_COMMENT));
        adapter.addTab(title[2], "active_mes", MessageListFragment.class, null);
        Bundle bundle = getBundle(FriendsList.TYPE_FANS);
        bundle.putInt(FriendsListFragment.BUNDLE_KEY_UID, AppContext.getInstance()
                .getLoginUid());
        adapter.addTab(title[3], "active_fans", FriendsListFragment.class, bundle);
        adapter.addTab(title[4], "my_tweet", TeatimeLikeListFragment.class, null);
    }

    private Bundle getBundle(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, catalog);
        return bundle;
    }


    //界面每次显示时，重置tip的显示
    @Override
    public void onResume() {
        super.onResume();
        setNoticeTip();
        mViewPager.setOffscreenPageLimit(2);
    }

    //设置消息tip
    private void setNoticeTip(){
        Notice notice= MainActivity.mNotice;

        if(notice!=null){
            changeTip(mAtmeBV,notice.getAtmeCount());
            changeTip(mCommentBV,notice.getCommentCount());
            changeTip(mMsgBV,notice.getMsgCount());
            changeTip(mFansBV,notice.getNewFansCount());
            changeTip(mLikeBV,notice.getNewLikeCount());
        }else{
            switch(mViewPager.getCurrentItem()){
                case 0:
                    changeTip(mAtmeBV,-1);
                    break;
                case 1:
                    changeTip(mCommentBV,-1);
                    break;
                case 2:
                    changeTip(mMsgBV,-1);
                    break;
                case 3:
                    changeTip(mFansBV,-1);
                    break;
                case 4:
                    changeTip(mLikeBV,-1);
                    break;
            }
        }
    }

    private void changeTip(BadgeView view, int count) {
        if (count > 0) {
            view.setText(count + "");
            view.show();
        } else {
            view.hide();
        }
    }

    //判断指定位置的BadgeView是否显示
    private boolean tipIsShow(int position){
        switch (position) {
            case 0:
                return mAtmeBV.isShown();
            case 1:
                return mCommentBV.isShown();
            case 2:
                return mMsgBV.isShown();
            case 3:
                return mFansBV.isShown();
            case 4:
                return mLikeBV.isShown();
            default:
                return false;
        }
    }

    //首次进入，切换到相应的page
    private void changePage(){
        Notice notice = MainActivity.mNotice;
        if (notice == null) {
            return;
        }
        if (notice.getAtmeCount() != 0) {
            mViewPager.setCurrentItem(0);
            noticeCurrentPage = 0;
            refreshPage(0);
            noticeShowCount[0] = 1;
        } else if (notice.getCommentCount() != 0) {
            mViewPager.setCurrentItem(1);
            noticeCurrentPage = 1;
            refreshPage(1);
            noticeShowCount[1] = 1;
        } else if (notice.getMsgCount() != 0) {
            mViewPager.setCurrentItem(2);
            noticeCurrentPage = 2;
            refreshPage(2);
            noticeShowCount[2] = 1;
        } else if (notice.getNewFansCount() != 0) {
            mViewPager.setCurrentItem(3);
            noticeCurrentPage = 3;
            refreshPage(3);
            noticeShowCount[3] = 1;
        } else if (notice.getNewLikeCount() != 0) {
            mViewPager.setCurrentItem(4);
            noticeCurrentPage = 4;
            refreshPage(4);
            noticeShowCount[4] = 1;
        }
    }

    private void refreshPage(int index) {
        if (tipIsShow(index)) {
            try {
                ((BaseListFragment) getChildFragmentManager().getFragments()
                        .get(index)).onRefresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void setScreenPageLimit() {
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void onClick(View v) {}


}
