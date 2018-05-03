package com.teacore.teascript.module.quickoption.fragment.softwarefragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.SoftwareCatalogList;
import com.teacore.teascript.bean.SoftwareCatalogList.SoftwareType;
import com.teacore.teascript.bean.SoftwareIntro;
import com.teacore.teascript.bean.SoftwareIntroList;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.adapter.SoftwareAdapter;
import com.teacore.teascript.adapter.SoftwareCatalogAdapter;
import com.teacore.teascript.widget.SwitchLayout;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 开源软件分类页面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-20
 */

public class SoftwareCatalogListFragment extends BaseFragment implements OnItemClickListener,OnScrollListener {

    private static final int STATE_NONE=0;
    private static final int STATE_REFRESH=1;
    private static final int STATE_LOADMORE=2;

    private static final int SCREEN_CATALOG=0;
    private static final int SCREEN_TAG=1;
    private static final int SCREEN_SOFTWARE=2;

    private static SwitchLayout mSwitchLayout;
    private static ListView mCatalogLV,mTagLV,mSoftwareLV;
    private static EmptyLayout mEmptyLayout;
    private static SoftwareCatalogAdapter mCatalogAdapter,mTagAdapter;
    private static SoftwareAdapter mSoftwareAdapter;

    private static int mState=STATE_NONE;
    private static int mCurrentScreen=SCREEN_CATALOG;
    private static int mCurrentTag;
    private static int mCurrentPage;

    //获取目录信息
    private AsyncHttpResponseHandler mCatalogHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                SoftwareCatalogList list = XmlUtils.toBean(SoftwareCatalogList.class, new ByteArrayInputStream(arg2));
                if (mState == STATE_REFRESH)
                    mCatalogAdapter.clear();
                List<SoftwareType> data = list.getSoftwareCatalogList();
                mCatalogAdapter.addDatas(data);
                mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
                if (data.size() == 0 && mState == STATE_REFRESH) {
                    mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
                } else {
                    mCatalogAdapter.setState(BaseListAdapter.STATE_LESS_ONE_PAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        }

        public void onFinish() {
            mState = STATE_NONE;
        }
    };

    //获取标签信息
    private AsyncHttpResponseHandler mTagHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                SoftwareCatalogList list = XmlUtils.toBean(SoftwareCatalogList.class, new ByteArrayInputStream(arg2));
                if (mState == STATE_REFRESH)
                    mTagAdapter.clear();
                List<SoftwareType> data = list.getSoftwareCatalogList();
                mTagAdapter.addDatas(data);
                mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
                if (data.size() == 0 && mState == STATE_REFRESH) {
                    mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
                } else {
                    mTagAdapter.setState(BaseListAdapter.STATE_LESS_ONE_PAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        }

        public void onFinish() {
            mState = STATE_NONE;
        }
    };

    //获取相关软件信息
    private AsyncHttpResponseHandler mSoftwareHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              byte[] responseBytes) {
            try {
                SoftwareIntroList list = XmlUtils.toBean(SoftwareIntroList.class,
                        new ByteArrayInputStream(responseBytes));

                executeOnLoadDataSuccess(list.getSoftwareList());

            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseBytes, null);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        }

        public void onFinish() {
            mState = STATE_NONE;
        }
    };

    private OnItemClickListener mCatalogOnItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            SoftwareType type = (SoftwareType) mCatalogAdapter
                    .getItem(position);
            if (type != null && type.getTag() > 0) {
                // 加载二级分类
                mCurrentScreen = SCREEN_TAG;
                mSwitchLayout.scrollToScreen(mCurrentScreen);
                mCurrentTag = type.getTag();
                sendRequestCatalogData(mTagHandler);
            }
        }
    };

    private OnItemClickListener mTagOnItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            SoftwareType type = (SoftwareType) mTagAdapter.getItem(position);
            if (type != null && type.getTag() > 0) {
                // 加载二级分类里面的软件列表
                mCurrentScreen = SCREEN_SOFTWARE;
                mSwitchLayout.scrollToScreen(mCurrentScreen);
                mCurrentTag = type.getTag();
                mState = STATE_REFRESH;
                sendRequestSoftwareData();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_software_catalog_list, container,
                false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mSwitchLayout = (SwitchLayout) view.findViewById(R.id.switchlayout);
        mSwitchLayout.setIsScroll(false);

        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);
        mCatalogLV = (ListView) view.findViewById(R.id.catalog_lv);
        mCatalogLV.setOnItemClickListener(mCatalogOnItemClick);
        mTagLV = (ListView) view.findViewById(R.id.tag_lv);
        mTagLV.setOnItemClickListener(mTagOnItemClick);
        mSoftwareLV = (ListView) view.findViewById(R.id.software_lv);
        mSoftwareLV.setOnItemClickListener(this);
        mSoftwareLV.setOnScrollListener(this);

        if (mCatalogAdapter == null) {
            mCatalogAdapter = new SoftwareCatalogAdapter();
            sendRequestCatalogData(mCatalogHandler);
        }
        mCatalogLV.setAdapter(mCatalogAdapter);

        if (mTagAdapter == null) {
            mTagAdapter = new SoftwareCatalogAdapter();
        }
        mTagLV.setAdapter(mTagAdapter);

        if (mSoftwareAdapter == null) {
            mSoftwareAdapter = new SoftwareAdapter();
        }
        mSoftwareLV.setAdapter(mSoftwareAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        SoftwareIntro software = (SoftwareIntro) mSoftwareAdapter.getItem(position);
        if (software != null)
            UiUtils.showUrlRedirect(view.getContext(), software.getUrl());
    }

    @Override
    public boolean onBackPressed() {
        mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
        mCurrentPage = 0;
        switch (mCurrentScreen) {
            case SCREEN_SOFTWARE:
                mCurrentScreen = SCREEN_TAG;
                mSwitchLayout.scrollToScreen(SCREEN_TAG);
                return true;
            case SCREEN_TAG:
                mCurrentScreen = SCREEN_CATALOG;
                mSwitchLayout.scrollToScreen(SCREEN_CATALOG);
                return true;
            case SCREEN_CATALOG:
                return false;
        }
        return super.onBackPressed();
    }

    private void sendRequestCatalogData(AsyncHttpResponseHandler handler) {
        mState = STATE_REFRESH;
        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        TeaScriptApi.getSoftwareCatalogList(mCurrentTag, handler);
    }

    private void sendRequestSoftwareData() {
        TeaScriptApi.getSoftwareTagList(mCurrentTag, mCurrentPage,
                mSoftwareHandler);
    }

    private void executeOnLoadDataSuccess(List<SoftwareIntro> data) {
        if (data == null) {
            return;
        }
        mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);

        if (mCurrentPage == 0) {
            mSoftwareAdapter.clear();
        }

        for (int i = 0; i < data.size(); i++) {
            if (compareTo(mSoftwareAdapter.getDatas(), data.get(i))) {
                data.remove(i);
            }
        }
        int adapterState = BaseListAdapter.STATE_EMPTY_ITEM;
        if (mSoftwareAdapter.getCount() == 0 && mState == STATE_NONE) {
            mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        } else if (data.size() == 0 || (data.size() < 20 && mCurrentPage == 0)) {
            adapterState = BaseListAdapter.STATE_NO_MORE;
        } else {
            adapterState = BaseListAdapter.STATE_LOAD_MORE;
        }
        mSoftwareAdapter.setState(adapterState);
        mSoftwareAdapter.addDatas(data);
    }

    private boolean compareTo(List<? extends Entity> data, SoftwareIntro enity) {
        int s = data.size();
        if (enity != null) {
            for (int i = 0; i < s; i++) {
                if (enity.getName().equals(
                        ((SoftwareIntro) data.get(i)).getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
        if (mState == STATE_NOMORE || mState == STATE_LOADMORE
                || mState == STATE_REFRESH) {
            return;
        }
        if (mSoftwareAdapter != null
                && mSoftwareAdapter.getDataSize() > 0
                && mSoftwareLV.getLastVisiblePosition() == (mSoftwareLV
                .getCount() - 1)) {
            if (mState == STATE_NONE
                    && mSoftwareAdapter.getState() == BaseListAdapter.STATE_LOAD_MORE) {
                mState = STATE_LOADMORE;
                mCurrentPage++;
                sendRequestSoftwareData();
            }
        }
    }


}
