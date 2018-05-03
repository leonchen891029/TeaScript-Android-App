package com.teacore.teascript.adapter;

import android.os.Bundle;

/**
 * ViewPager信息类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */

public final class ViewPagerInfo {

    public final String tag;
    public final Class<?> clss;
    public final Bundle args;
    public final String title;

    public ViewPagerInfo(String _title, String _tag, Class<?> _class, Bundle _args) {
        title = _title;
        tag = _tag;
        clss = _class;
        args = _args;
    }



}
