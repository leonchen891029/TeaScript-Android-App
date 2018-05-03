package com.teacore.teascript.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.Map;

/**
 * ViewPagerFragment的适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */
@SuppressLint("Recycle")
public class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter{

    //ViewPagerInfo列表
    public ArrayList<ViewPagerInfo> mTabsList=new ArrayList<ViewPagerInfo>();

    //滑动选项卡
    protected PagerSlidingTabStrip mPagerStrip;

    private final Context mContext;

    private final ViewPager mViewPager;

    private Map<String,Fragment> mFragmentsList=new ArrayMap<>();

    public ViewPagerFragmentAdapter(FragmentManager fragmentManager,PagerSlidingTabStrip pageStrip,ViewPager viewPager){
        super(fragmentManager);
        mContext=viewPager.getContext();
        mPagerStrip=pageStrip;
        mViewPager=viewPager;
        mViewPager.setAdapter(this);
        //滑动选项卡加载ViewPager
        mPagerStrip.setViewPager(mViewPager);
    }

    //添加相应的Tab
    public void addTab(String title, String tag, Class<?> clss, Bundle args) {
        //根据传入的标题值，tag值，Fragment类型，Bundle值创建ViewPagerInfo实体类
        ViewPagerInfo viewPageInfo = new ViewPagerInfo(title, tag, clss, args);

        addFragment(viewPageInfo);
    }

    public void addAllTab(ArrayList<ViewPagerInfo> mTabs) {
        for (ViewPagerInfo viewPagerInfo : mTabs) {
            addFragment(viewPagerInfo);
        }
    }

    //添加到ViewPagerInfo列表
    private void addFragment(ViewPagerInfo info) {

        if (info == null) {
            return;
        }

        // 加入tab title
        View view= LayoutInflater.from(mContext).inflate(R.layout.viewpager_fragment_tab_item, null, false);
        TextView titleTV = (TextView) view.findViewById(R.id.tab_title_tv);
        titleTV.setText(info.title);
        titleTV.setTextColor(ContextCompat.getColor(mContext,R.color.teascript_text));

        //添加到导航条中
        mPagerStrip.addTab(view);

        //添加到TabList中
        mTabsList.add(info);

        //刷新ViewPager
        notifyDataSetChanged();
    }

    //移除第一个tab
    public void remove() {
        remove(0);
    }

    //移除一个tab
    public void remove(int index){

        if (mTabsList.isEmpty()) {
            return;
        }

        if (index < 0) {
            index = 0;
        }

        if (index >= mTabsList.size()) {
            index = mTabsList.size() - 1;
        }

        ViewPagerInfo info = mTabsList.get(index);

        if (mFragmentsList.containsKey(info.tag))
            mFragmentsList.remove(info.tag);

        mTabsList.remove(index);

        mPagerStrip.removeTab(index, 1);

        notifyDataSetChanged();
    }

    //移除所有的tab
    public void removeAll() {
        if (mTabsList.isEmpty()) {
            return;
        }
        mFragmentsList.clear();
        mPagerStrip.removeAllTab();
        mTabsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return mTabsList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    //创建相应的Fragment
    @Override
    public Fragment getItem(int position) {
        ViewPagerInfo info = mTabsList.get(position);

        Fragment fragment = mFragmentsList.get(info.tag);

        if (fragment == null) {
            fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
            // 避免重复创建而进行缓存
            mFragmentsList.put(info.tag, fragment);
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabsList.get(position).title;
    }


}
