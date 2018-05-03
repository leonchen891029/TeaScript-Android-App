package com.teacore.teascript.module.general.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.teacore.teascript.R;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.bean.BlogList;
import com.teacore.teascript.bean.EventList;
import com.teacore.teascript.bean.NewsList;
import com.teacore.teascript.interfaces.OnTabReselectListener;
import com.teacore.teascript.module.general.base.basefragment.BaseGeneralListFragment;
import com.teacore.teascript.module.general.bean.Question;
import com.teacore.teascript.module.general.fragment.generallistfragment.BlogGeneralListFragment;
import com.teacore.teascript.module.general.fragment.generallistfragment.EventGeneralListFragment;
import com.teacore.teascript.module.general.fragment.generallistfragment.NewsGeneralListFragment;
import com.teacore.teascript.module.general.fragment.generallistfragment.QuestionGeneralListFragment;

/**
 * 综合Tab界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */

public class GeneralViewPagerFragment extends BaseViewPagerFragment implements OnTabReselectListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(
                R.array.general_viewpager_arrays);

        adapter.addTab(title[0], "news", NewsGeneralListFragment.class,
                getBundle(NewsList.CATALOG_ALL));
        adapter.addTab(title[1], "blog", BlogGeneralListFragment.class,
                getBundle(BlogList.CATALOG_LATEST));
        adapter.addTab(title[2], "question", QuestionGeneralListFragment.class,
                getBundle(Question.QUESTION_TYPE_ASK));
        adapter.addTab(title[3], "event", EventGeneralListFragment.class,
                getBundle(EventList.EVENT_LIST_TYPE_NEW_EVENT));

    }

    private Bundle getBundle(int newType) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, newType);
        return bundle;
    }

    private Bundle getBundle(String catalog) {
        Bundle bundle = new Bundle();
        bundle.putString(BlogGeneralListFragment.BUNDLE_BLOG_TYPE, catalog);
        return bundle;
    }

    @Override
    protected void setScreenPageLimit() {
        mViewPager.setOffscreenPageLimit(3);
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
    public void onTabReselect() {
        Fragment fragment = mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
        if (fragment != null && fragment instanceof BaseGeneralListFragment) {
            ((BaseGeneralListFragment) fragment).onTabReselect();
        }
    }
}
