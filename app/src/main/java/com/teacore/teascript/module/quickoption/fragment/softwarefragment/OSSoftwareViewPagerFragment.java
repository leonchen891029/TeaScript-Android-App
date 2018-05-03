package com.teacore.teascript.module.quickoption.fragment.softwarefragment;

import android.os.Bundle;
import android.view.View;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.bean.SoftwareIntroList;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;

/**
 * 开源软件界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-21
 */

public class OSSoftwareViewPagerFragment extends BaseViewPagerFragment{

    public static OSSoftwareViewPagerFragment newInstance(){
        return new OSSoftwareViewPagerFragment();
    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(
                R.array.opensourcesoftware_viewpager_arrays);
        adapter.addTab(title[0], "software_catalog",
                SoftwareCatalogListFragment.class, null);
        adapter.addTab(title[1], "software_recommend",
                SoftwareListFragment.class,
                getBundle(SoftwareIntroList.CATALOG_RECOMMEND));
        adapter.addTab(title[2], "software_latest", SoftwareListFragment.class,
                getBundle(SoftwareIntroList.CATALOG_TIME));
        adapter.addTab(title[3], "software_hot", SoftwareListFragment.class,
                getBundle(SoftwareIntroList.CATALOG_VIEW));
        adapter.addTab(title[4], "software_china", SoftwareListFragment.class,
                getBundle(SoftwareIntroList.CATALOG_LIST_CN));
    }

    private Bundle getBundle(String catalog) {
        Bundle bundle = new Bundle();
        bundle.putString(SoftwareListFragment.BUNDLE_SOFTWARE, catalog);
        return bundle;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean onBackPressed() {
        BaseFragment fragment = (BaseFragment) mViewPagerAdapter.getItem(mViewPager
                .getCurrentItem());
        if (fragment instanceof SoftwareCatalogListFragment) {
            return fragment.onBackPressed();
        }
        return super.onBackPressed();
    }


}
