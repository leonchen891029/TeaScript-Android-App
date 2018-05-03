package com.teacore.teascript.base;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.teacore.teascript.R;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.PagerSlidingTabStrip;

/**带有导航条的基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-5
 */
public abstract class BaseViewPagerFragment extends BaseFragment{

    protected PagerSlidingTabStrip mTabStrip;
    protected ViewPager mViewPager;
    protected ViewPagerFragmentAdapter mViewPagerAdapter;
    protected EmptyLayout mEmptyLayout;
    protected View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){

        if (mRootView==null){

            View view=inflater.inflate(R.layout.fragment_base_viewpager,null);

            mTabStrip=(PagerSlidingTabStrip) view.findViewById(R.id.pager_tabstrip);

            mViewPager=(ViewPager) view.findViewById(R.id.viewpager);

            mEmptyLayout=(EmptyLayout) view.findViewById(R.id.empty_layout);

            mViewPagerAdapter=new ViewPagerFragmentAdapter(getChildFragmentManager(),mTabStrip,mViewPager);

            setScreenPageLimit();

            onSetupTabAdapter(mViewPagerAdapter);

            mRootView =view;
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        if(savedInstanceState!=null){
           int pos=savedInstanceState.getInt("position");
           mViewPager.setCurrentItem(pos,true);
        }
    }

    protected void setScreenPageLimit(){

    }

    protected abstract void onSetupTabAdapter(ViewPagerFragmentAdapter mViewPagerAdapter);



}
