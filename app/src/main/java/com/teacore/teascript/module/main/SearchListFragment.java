package com.teacore.teascript.module.main;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.SearchResult;
import com.teacore.teascript.bean.SearchResultList;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.adapter.SearchResultAdapter;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 搜索结果列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-28
 */

public class SearchListFragment extends BaseListFragment<SearchResult>{

    protected static final String TAG = SearchListFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "search_list_";
    private String mCatalog;
    private String mSearch;
    private boolean mRquestDataIfCreated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args != null) {
            mCatalog = args.getString(BUNDLE_KEY_CATALOG);
        }

        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
        getActivity().getWindow().setSoftInputMode(mode);
    }

    public void search(String search) {
        mSearch = search;
        if (mEmptyLayout != null) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
            mState = STATE_REFRESH;
            requestData(true);
        } else {
            mRquestDataIfCreated = true;
        }
    }

    @Override
    protected boolean requestDataIfViewCreated() {
        return mRquestDataIfCreated;
    }

    @Override
    protected SearchResultAdapter getListAdapter() {
        return new SearchResultAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mCatalog + mSearch;
    }

    @Override
    protected SearchResultList parseList(InputStream inputStream) throws Exception {
        SearchResultList list = XmlUtils.toBean(SearchResultList.class, inputStream);
        return list;
    }

    @Override
    protected SearchResultList readList(Serializable seri) {
        return ((SearchResultList) seri);
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getSearchList(mCatalog, mSearch, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        SearchResult res = mAdapter.getItem(position);
        if (res != null) {
            if (res.getType().equalsIgnoreCase(SearchResultList.CATALOG_SOFTWARE)) {
                UiUtils.showSoftwareDetailById(getActivity(), res.getId());
            } else {
                UiUtils.showUrlRedirect(getActivity(), res.getUrl());
            }
        }
    }
}
