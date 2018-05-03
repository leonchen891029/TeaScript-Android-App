package com.teacore.teascript.module.myinformation;

import android.os.Bundle;
import android.view.View;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.bean.Favorite;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;

/**
 * 用户收藏ViewPager界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-17
 */

public class UserFavoriteViewPagerFragment extends BaseViewPagerFragment{

    public static UserFavoriteViewPagerFragment newInstance(){
        return new UserFavoriteViewPagerFragment();
    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter){
        String[] title = getResources().getStringArray(R.array.userfavorite_viewpager_arrays);
        adapter.addTab(title[0], "favorite_software", UserFavoriteListFragment.class, getBundle(Favorite.CATALOG_SOFTWARE));
        adapter.addTab(title[1], "favorite_topic", UserFavoriteListFragment.class, getBundle(Favorite.CATALOG_TOPIC));
        adapter.addTab(title[2], "favorite_code", UserFavoriteListFragment.class, getBundle(Favorite.CATALOG_CODE));
        adapter.addTab(title[3], "favorite_blogs", UserFavoriteListFragment.class, getBundle(Favorite.CATALOG_BLOGS));
        adapter.addTab(title[4], "favorite_news", UserFavoriteListFragment.class, getBundle(Favorite.CATALOG_NEWS));
    }

    private Bundle getBundle(int favoriteType) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, favoriteType);
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

}
