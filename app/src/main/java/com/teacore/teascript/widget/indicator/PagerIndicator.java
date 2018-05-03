package com.teacore.teascript.widget.indicator;

import android.support.v4.view.ViewPager;

/**
 * 指示器的接口
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-22
 */

public interface PagerIndicator extends ViewPager.OnPageChangeListener{

    void bindViewPager(ViewPager viewPager);

    void bindViewPager(ViewPager viewPager,int initialPosition);

    void setCurrentItem(int currentItem);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    void notifyDataSetChanged();


}
