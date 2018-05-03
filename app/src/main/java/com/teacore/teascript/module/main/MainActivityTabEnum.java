package com.teacore.teascript.module.main;

import com.teacore.teascript.R;
import com.teacore.teascript.module.general.fragment.GeneralViewPagerFragment;
import com.teacore.teascript.module.myinformation.MyInformationFragment;
import com.teacore.teascript.module.teascript.TeaScriptViewPagerFragment;
import com.teacore.teascript.module.teatime.TeatimesViewPagerFragment;

/**
 * 主界面的FragmentTabHost的枚举类
 *@author 陈晓帆
 *@version 1.0
 * Created 2017-4-17
 */

public enum MainActivityTabEnum {

    NEWS(0, R.string.main_tab_name_news,R.drawable.icon_tab_new_selector,GeneralViewPagerFragment.class),

    TeaScript(1,R.string.main_tab_name_teascript, R.drawable.icon_tab_teascript_selector, TeaScriptViewPagerFragment.class),

    QUICK(2,R.string.main_tab_name_quick,R.drawable.icon_nav_add_selector,null),

    Teatime(3,R.string.main_tab_name_teatime,R.drawable.icon_tab_teatime_selector,TeatimesViewPagerFragment.class),

    /*
    发现模块已经整合入quick中
    EXPLORE(3,R.string.main_tab_name_explore, R.drawable.icon_tab_explore_selector, ExploreFragment.class),
     */

    ME(4, R.string.main_tab_name_my, R.drawable.icon_tab_my_selector,
            MyInformationFragment.class);

    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    private MainActivityTabEnum(int idx, int resName, int resIcon, Class<?> clz) {
        this.idx = idx;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }

}
