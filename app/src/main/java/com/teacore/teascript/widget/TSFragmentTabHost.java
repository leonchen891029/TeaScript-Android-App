package com.teacore.teascript.widget;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

/**
 * 自定义FragmentTabHost
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-14
 */

public class TSFragmentTabHost extends FragmentTabHost{

    private String mCurrentTag;

    private String mNoTabChangedTag;

    public TSFragmentTabHost(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    public void onTabChanged(String tag){

        if (tag.equals(mNoTabChangedTag)) {
            setCurrentTabByTag(mCurrentTag);
        } else {
            super.onTabChanged(tag);
            mCurrentTag = tag;
        }

    }

    public void setNoTabChangedTag(String tag) {
        this.mNoTabChangedTag = tag;
    }
}
