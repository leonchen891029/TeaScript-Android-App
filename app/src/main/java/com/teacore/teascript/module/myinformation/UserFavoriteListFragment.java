package com.teacore.teascript.module.myinformation;

import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Favorite;
import com.teacore.teascript.bean.FavoriteList;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.adapter.UserFavoriteAdapter;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 用户收藏Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-17
 */

public class UserFavoriteListFragment extends BaseListFragment<Favorite> {

    protected static final String TAG=UserFavoriteListFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX="user_favorite_";

    @Override
    protected UserFavoriteAdapter getListAdapter(){
        return new UserFavoriteAdapter();
    }

    @Override
    protected String getCacheKeyPrefix(){
        return CACHE_KEY_PREFIX+mCatalog;
    }

    @Override
    protected FavoriteList parseList(InputStream inputStream) throws Exception {
        return XmlUtils.toBean(FavoriteList.class, inputStream);
    }

    @Override
    protected FavoriteList readList(Serializable seri) {
        return ((FavoriteList) seri);
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getFavoriteList(AppContext.getInstance().getLoginUid(), mCatalog, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        Favorite favorite = mAdapter.getItem(position);

        if (favorite != null) {
            switch (favorite.getType()) {

                case Favorite.CATALOG_BLOGS:
                    UiUtils.showUrlRedirect(getActivity(), favorite.getUrl());
                    break;
                case Favorite.CATALOG_CODE:
                    UiUtils.showUrlRedirect(getActivity(), favorite.getUrl());
                    break;
                case Favorite.CATALOG_NEWS:
                    UiUtils.showUrlRedirect(getActivity(), favorite.getUrl());
                    break;
                case Favorite.CATALOG_SOFTWARE:
                    UiUtils.showDetail(getActivity(), 1, favorite.getId(), favorite.getUrl());
                    //UIHelper.showUrlRedirect(getActivity(), favorite.getUrl());
                    break;
                case Favorite.CATALOG_TOPIC:
                    UiUtils.showUrlRedirect(getActivity(), favorite.getUrl());
                    break;

            }
        }

    }


}
