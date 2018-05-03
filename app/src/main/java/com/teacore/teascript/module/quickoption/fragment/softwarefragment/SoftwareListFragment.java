package com.teacore.teascript.module.quickoption.fragment.softwarefragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.SoftwareIntro;
import com.teacore.teascript.bean.SoftwareIntroList;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.adapter.SoftwareAdapter;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 软件介绍列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-2
 */

public class SoftwareListFragment extends BaseListFragment<SoftwareIntro>{

    public static final String BUNDLE_SOFTWARE = "BUNDLE_SOFTWARE";

    protected static final String TAG = SoftwareListFragment.class
            .getSimpleName();

    private static final String CACHE_KEY_PREFIX = "softwarelist_";

    private String softwareType = "recommend";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            softwareType = args.getString(BUNDLE_SOFTWARE);
        }
    }

    @Override
    protected SoftwareAdapter getListAdapter() {
        return new SoftwareAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + softwareType;
    }

    @Override
    protected SoftwareIntroList parseList(InputStream is) throws Exception {
        SoftwareIntroList list = XmlUtils.toBean(SoftwareIntroList.class, is);
        return list;
    }

    @Override
    protected SoftwareIntroList readList(Serializable seri) {
        return (SoftwareIntroList) seri;
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getSoftwareList(softwareType, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        SoftwareIntro softwaredec = (SoftwareIntro) mAdapter.getItem(position);
        if (softwaredec != null) {
            String ident = softwaredec.getUrl().substring(softwaredec.getUrl().lastIndexOf("/") + 1);
            UiUtils.showSoftwareDetail(getActivity(), ident);
            // 放入已读列表
            saveToReadedList(view, SoftwareIntroList.PREF_READED_SOFTWARE_LIST,
                    softwaredec.getName());
        }
    }

    @Override
    protected boolean compareTo(List<? extends Entity> data, Entity enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (((SoftwareIntro) enity).getName().equals(
                        ((SoftwareIntro) data.get(i)).getName())) {
                    return true;
                }
            }
        }
        return false;
    }



}
