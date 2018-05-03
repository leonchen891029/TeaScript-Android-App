package com.teacore.teascript.module.general.teatime;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teacore.teascript.R;
import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.module.general.detail.constract.TeatimeDetailContract;

/**
 * TeamtimeDetailActivity页面下的 赞与评论的ViewPager
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-21
 */

public class TeatimeDetailViewPagerFragment extends Fragment implements TeatimeDetailContract.ICommentView,TeatimeDetailContract.IThumbupView,TeatimeDetailContract.IAgencyView{

    protected FragmentStatePagerAdapter mAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TeatimeDetailContract.ICommentView mCommentViewImp;
    private TeatimeDetailContract.IThumbupView mThumbupView;
    private TeatimeDetailContract.IAgencyView mAgencyView;
    private TeatimeDetailContract.Presenter mPresenter;

    public static TeatimeDetailViewPagerFragment instantiate(TeatimeDetailContract.Presenter operator){
        TeatimeDetailViewPagerFragment fragment=new TeatimeDetailViewPagerFragment();
        fragment.mPresenter=operator;
        return fragment;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mPresenter=(TeatimeDetailContract.Presenter) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_teatime_view_pager,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        mTabLayout=(TabLayout) view.findViewById(R.id.tablayout);
        mViewPager=(ViewPager) view.findViewById(R.id.viewpager);

        if(mAdapter==null){
            final TeatimeLikeUsersFragment mThumbupFragment = TeatimeLikeUsersFragment.instantiate(mPresenter, this);
            mThumbupView = mThumbupFragment;

            final TeatimeCommentFragment mCommentFragment = TeatimeCommentFragment.instantiate(mPresenter, this);
            mCommentViewImp = mCommentFragment;

            mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    switch (position){
                        case 0:
                            return mThumbupFragment;

                        case 1:
                            return mCommentFragment;

                    }
                    return null;
                }

                @Override
                public int getCount() {
                    return 2;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    switch (position){
                        case 0:
                            return String.format("赞(%s)", mPresenter.getTeatime().getLikeCount());
                        case 1:
                            return String.format("评论(%s)",
                                     mPresenter.getTeatime().getCommentCount());
                    }
                    return null;
                }
            };

            mViewPager.setAdapter(mAdapter);
            mTabLayout.setupWithViewPager(mViewPager);
            mViewPager.setCurrentItem(1);
        }else{
            mViewPager.setAdapter(mAdapter);
        }

    }

    @Override
    public void onCommentSuccess(Comment comment) {
        mPresenter.getTeatime().setCommentCount(mPresenter.getTeatime().getCommentCount() + 1); // Bean的事,真不是我想这样干
        if (mCommentViewImp != null) mCommentViewImp.onCommentSuccess(comment);
        TabLayout.Tab tab = mTabLayout.getTabAt(1);
        if (tab != null) tab.setText(String.format("评论(%s)", mPresenter.getTeatime().getCommentCount()));
    }

    @Override
    public void onLikeSuccess(boolean isUp, User user) {
        mPresenter.getTeatime().setLikeCount(mPresenter.getTeatime().getLikeCount() + (isUp ? 1 : -1));
        if (mThumbupView != null) mThumbupView.onLikeSuccess(isUp, user);
        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if (tab != null) tab.setText(String.format("赞(%s)", mPresenter.getTeatime().getLikeCount()));
    }

    @Override
    public void resetLikeCount(int count) {
        mPresenter.getTeatime().setLikeCount(count);
        TabLayout.Tab tab = mTabLayout.getTabAt(0);
        if (tab != null) tab.setText(String.format("赞(%s)", count));
    }

    @Override
    public void resetCmnCount(int count) {
        mPresenter.getTeatime().setCommentCount(count);
        TabLayout.Tab tab = mTabLayout.getTabAt(1);
        if (tab != null) tab.setText(String.format("评论(%s)", count));
    }


}
