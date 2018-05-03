package com.teacore.teascript.module.general.base.basefragment;

import com.teacore.teascript.interfaces.OnTabReselectListener;

/**
 * 用于GeneralListFragment的基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-28
 */

public abstract class BaseGeneralListFragment<T> extends BaseListFragment<T>  implements OnTabReselectListener{

    @Override
    public void onTabReselect(){
        mListView.setSelection(0);
        mRefreshLayout.setRefreshing(true);
        onRefreshing();
    }


}
