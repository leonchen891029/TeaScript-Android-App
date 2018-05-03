package com.teacore.teascript.module.myinformation;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.bean.EventList;
import com.teacore.teascript.module.back.BackActivity;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;

/**
 * 我的活动ViewPagerFragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */

public class EventViewPagerFragment extends BaseViewPagerFragment{

    private int position = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        position = bundle.getInt(BackActivity.BUNDLE_KEY_ARGS, 0);
    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {
        String[] title = getResources().getStringArray(R.array.events_viewpager_arrays);

        if (position == 0) {
            adapter.addTab(title[0], "new_event", EventListFragment.class, getBundle(EventList.EVENT_LIST_TYPE_NEW_EVENT));
            adapter.addTab(title[1], "my_event", EventListFragment.class, getBundle(EventList.EVENT_LIST_TYPE_MY_EVENT));
            mTabStrip.setVisibility(View.VISIBLE);
        } else {
            adapter.addTab(title[1], "my_event", EventListFragment.class, getBundle(EventList.EVENT_LIST_TYPE_MY_EVENT));
            mTabStrip.setVisibility(View.GONE);
        }

        mViewPager.setCurrentItem(position, true);
    }

    private Bundle getBundle(int event_type) {
        Bundle bundle = new Bundle();
        bundle.putInt(EventListFragment.BUNDLE_KEY_EVENT_TYPE, event_type);
        return bundle;
    }

}
