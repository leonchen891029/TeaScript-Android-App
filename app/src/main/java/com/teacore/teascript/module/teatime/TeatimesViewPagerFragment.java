package com.teacore.teascript.module.teatime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.bean.TeatimesList;
import com.teacore.teascript.interfaces.OnTabReselectListener;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;

/**
 * TeatimeViewPager界面(包括最新Teatime、热门Teatime、我的Teatime)
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */

public class TeatimesViewPagerFragment extends BaseViewPagerFragment implements OnTabReselectListener{

    //加载三个Tab:最新Teatime，热门Teatime，我的Teatime
    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {

        String[] title = getResources().getStringArray(
                R.array.teatimes_viewpager_arrays);

        adapter.addTab(title[0], "new_teatimes", TeatimeListFragment.class,
                getBundle(TeatimesList.CATALOG_LATEST));
        adapter.addTab(title[1], "hot_teatimes", TeatimeListFragment.class,
                getBundle(TeatimesList.CATALOG_HOT));
        adapter.addTab(title[2], "my_teatimes", TeatimeListFragment.class,
                getBundle(TeatimesList.CATALOG_ME));

    }

    private Bundle getBundle(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, catalog);
        return bundle;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void initView(View view) {}

    @Override
    public void initData() {}

    @Override
    public void onTabReselect() {
        Fragment fragment = mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
        if (fragment != null && fragment instanceof OnTabReselectListener) {
            ((OnTabReselectListener) fragment).onTabReselect();
        }
    }


}
