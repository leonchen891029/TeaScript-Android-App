package com.teacore.teascript.module.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.bean.SearchResultList;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;
import com.teacore.teascript.util.TDevice;

/**
 * 搜索的ViewPagerFragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-26
 */

public class SearchViewPagerFragment extends BaseViewPagerFragment {

    private SearchView mSearchView;

    public static SearchViewPagerFragment newInstance(){
        return new SearchViewPagerFragment();
    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(R.array.search_viewpager_arrays);
        adapter.addTab(title[0], "search_soft", SearchListFragment.class, getBundle(SearchResultList.CATALOG_SOFTWARE));
        adapter.addTab(title[1], "search_quest", SearchListFragment.class, getBundle(SearchResultList.CATALOG_POST));
        adapter.addTab(title[2], "search_blog", SearchListFragment.class, getBundle(SearchResultList.CATALOG_BLOG));
        adapter.addTab(title[3], "search_news", SearchListFragment.class, getBundle(SearchResultList.CATALOG_NEWS));
    }

    private Bundle getBundle(String catalog) {
        Bundle bundle = new Bundle();
        bundle.putString(BaseListFragment.BUNDLE_KEY_CATALOG, catalog);
        return bundle;
    }


    @Override
    protected void setScreenPageLimit() {
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem search = menu.findItem(R.id.search_content);
        mSearchView = (SearchView) search.getActionView();
        mSearchView.setIconifiedByDefault(false);
        setSearch();
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setSearch() {
        mSearchView.setQueryHint("搜索");
        TextView textView = (TextView) mSearchView.findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(0x90ffffff);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                mSearchView.clearFocus();
                TDevice.hideSoftKeyboard(mSearchView);
                search(arg0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                return false;
            }
        });

        mSearchView.requestFocus();
    }

    private void search(String content) {
        int index = mViewPager.getChildCount();
        for (int i = 0; i < index; i++) {
            SearchListFragment fragment = (SearchListFragment) getChildFragmentManager().getFragments().get(i);
            if (fragment != null) {
                fragment.search(content);
            }
        }
    }


}
