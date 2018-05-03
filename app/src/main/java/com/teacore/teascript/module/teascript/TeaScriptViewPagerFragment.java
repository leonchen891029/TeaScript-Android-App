package com.teacore.teascript.module.teascript;

import android.os.Bundle;
import android.view.View;

import com.teacore.teascript.R;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.base.BaseViewPagerFragment;

/**
 * TeaScriptViewPager界面(包括TeaScript语言、TVM虚拟机、Tea3)
 * @author 陈晓帆
 * @version 1.0
 * Created 2018-2-1
 */

public class TeaScriptViewPagerFragment extends BaseViewPagerFragment{

    public final static int CATALOG_TEASCRIPT=0;
    public final static int CATALOG_TVM=-1;
    public final static int CATALOG_TEA3D=0;

    //加载三个Tab:最新Teatime，热门Teatime，我的Teatime
    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {

        String[] title = getResources().getStringArray(
                R.array.teascript_viewpager_arrays);
        adapter.addTab(title[0], "teascript", TeaScriptFragment.class,
                getBundle(CATALOG_TEASCRIPT));
        adapter.addTab(title[1], "tvm", TVMFragment.class,
                getBundle(CATALOG_TVM));
        adapter.addTab(title[2], "tea3d", Tea3DFragment.class,
                getBundle(CATALOG_TEA3D));

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

}
