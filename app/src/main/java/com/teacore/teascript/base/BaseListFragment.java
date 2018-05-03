package com.teacore.teascript.base;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.EmptyLayout;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.teacore.teascript.base.BaseListAdapter.STATE_EMPTY_ITEM;

/**
 * BaseListFragment
 *
 * @author 陈晓帆
 * @version 1.0
 *          Created 2017-1-5
 */
public abstract class BaseListFragment<T extends Entity> extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    public static final String BUNDLE_KEY_CATALOG = "BUNDLE_KEY_CATALOG";

    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected ListView mListView;

    protected BaseListAdapter<T> mAdapter;

    protected EmptyLayout mEmptyLayout;
    //存储的EmptyLayout的状态
    protected int mStoreEmptyState = -1;
    //当前的页码
    protected int mCurrentPage = 0;
    //listfragment的类型
    protected int mCatalog = 1;
    //获取的数据结果
    protected Result mResult;

    private AsyncTask<String, Void, EntityList<T>> mCacheTask;

    private ParserTask mParserTask;

    //加载list数据的AsyncHttpResponseHandler
    protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {

            //如果当前的页面为第一页并且需要自动刷新为true
            if (mCurrentPage == 0 && needAutoRefresh()) {
                AppContext.putLastRefreshTime(getCacheKey(), TimeUtils.getCurrentTimeStr());
            }

            //判断该Fragment是否被添加到相应的Activity中
            if (isAdded()) {
                //如果当前Fragment的状态为REFRESH
                if (mState == STATE_REFRESH) {
                    //网络刷新成功
                    onRefreshNetworkSuccess();
                }
                //解析返回的数据
                executeParserTask(responseBytes);
            } else {
                //完成加载
                executeOnLoadFinish();
            }

        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

            if (isAdded()) {
                readCacheData(getCacheKey());
            } else {
                executeOnLoadFinish();
            }
        }
    };

    protected void onRefreshNetworkSuccess() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_list;
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        //获取listfragment类型
        if (args != null) {
            mCatalog = args.getInt(BUNDLE_KEY_CATALOG, 0);
        }
    }

    //创建fragment的view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    //绑定控件初始化控件
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout =(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mListView = (ListView) view.findViewById(R.id.listview);
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);

        initView(view);
    }

    @Override
    public void initView(View view) {

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.swiperefresh_color1), ContextCompat.getColor(getContext(), R.color.swiperefresh_color2),
                ContextCompat.getColor(getContext(), R.color.swiperefresh_color3), ContextCompat.getColor(getContext(), R.color.swiperefresh_color4));

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPage = 0;
                mState = STATE_REFRESH;
                mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                //刷新为true
                requestData(true);
            }
        });

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        //如果相应的适配器不为null
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
        } else {
            //获取相应的BaseListAdapter<T>
            mAdapter = getListAdapter();

            mListView.setAdapter(mAdapter);

            //如果是view创建时加载数据
            if (requestDataIfViewCreated()) {
                mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                mState = STATE_NONE;
                //刷新为false
                requestData(false);
            } else {
                mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            }

        }

        if (mStoreEmptyState != -1) {
            mEmptyLayout.setEmptyType(mStoreEmptyState);
        }
    }

    protected abstract BaseListAdapter<T> getListAdapter();

    @Override
    public void onDestroyView() {
        mStoreEmptyState = mEmptyLayout.getEmptyState();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        cancelReadCacheTask();
        cancelParserTask();
        super.onDestroy();
    }

    //下拉刷新数据(实现OnRefreshListener接口的方法)
    @Override
    public void onRefresh() {
        //正在刷新直接返回
        if (mState == STATE_REFRESH) {
            return;
        }
        //设置顶部正在刷新
        mListView.setSelection(0);
        setSwipeRefreshLoadingState();
        mCurrentPage = 0;
        mState = STATE_REFRESH;
        requestData(true);
    }

    //View创建时请求数据
    protected boolean requestDataIfViewCreated() {
        return true;
    }

    protected String getCacheKeyPrefix() {
        return null;
    }

    //解析List(inputstream)
    protected EntityList<T> parseList(InputStream is) throws Exception {
        return null;
    }

    //读取List(serializable)
    protected EntityList<T> readList(Serializable seri) {
        return null;
    }

    //list item click事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    //获取缓存的key
    private String getCacheKey() {
        return getCacheKeyPrefix() + "_" + mCurrentPage;
    }

    //是否需要自动刷新
    protected boolean needAutoRefresh() {
        return true;
    }

    //获取列表数据
    protected void requestData(boolean refresh) {

        String key = getCacheKey();
        //是否从缓存中读取数据 refresh代表是否主动刷新
        if (isReadCacheData(refresh)) {
            //读取缓存中的数据
            readCacheData(key);
        } else {
            //更新数据
            sendRequestData();
        }

    }

    protected void sendRequestData() {

    }

    //判断是否需要读取缓存中的数据
    protected boolean isReadCacheData(boolean refresh) {
        String key = getCacheKey();
        //如果设备没有网络连接
        if (!TDevice.hasInternet()) {
            return true;
        }
        //(1)缓存存在(2)不主动刷新(3)第一页:优先取缓存
        if (CacheManager.isCacheExist(getActivity(), key) && !refresh && mCurrentPage == 0) {
            return true;
        }
        //(1)缓存存在(2)缓存没有失效(3)其他页数:优先取缓存
        if (CacheManager.isCacheExist(getActivity(), key) && !CacheManager.isCacheInvalid(getActivity(), key)
                && mCurrentPage != 0) {
            return true;
        }
        return false;
    }

    //是否到时间去刷新数据了
    private boolean onTimeRefresh() {

        String lastRefreshTime = AppContext.getLastRefreshTime(getCacheKey());

        String currTime = TimeUtils.getCurrentTimeStr();

        long diff = TimeUtils.dateDifferent(lastRefreshTime, currTime);

        return needAutoRefresh() && diff > getAutoRefreshTime();
    }

    //获取自动刷新时间(12小时)
    protected long getAutoRefreshTime() {
        return 12 * 60 * 60;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onTimeRefresh()) {
            onRefresh();
        }
    }

    private void readCacheData(String cacheKey) {
        cancelReadCacheTask();
        mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

    //取消读取缓存的Task
    private void cancelReadCacheTask() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;

        }
    }

    //加载数据成功
    protected void executeOnLoadDataSuccess(List<T> data) {
        if (data == null) {
            data = new ArrayList<T>();
        }

        //注销登录，密码已经修改，cookie，失效了
        if (mResult != null && !mResult.OK()) {
            AppContext.showToast(mResult.getMessage());
            AppContext.getInstance().logout();
        }

        mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);

        if (mCurrentPage == 0) {
            mAdapter.clear();
        }

        for (int i = 0; i < data.size(); i++) {
            if (compareTo(mAdapter.getDatas(), data.get(i))) {
                //去掉id一样的data
                data.remove(i);
                i--;
            }
        }

        int adapterState = BaseListAdapter.STATE_EMPTY_ITEM;

        if ((mAdapter.getCount() + data.size()) == 0) {
            adapterState = STATE_EMPTY_ITEM;
        } else if (data.size() == 0 || (data.size() < getPageSize()
                && mCurrentPage == 0)) {
            //data数据为0 或者 data的大小小于页面大小且为第一页
            adapterState = BaseListAdapter.STATE_NO_MORE;
            mAdapter.notifyDataSetChanged();
        } else {
            adapterState = BaseListAdapter.STATE_LOAD_MORE;
        }

        //设置mAdapter状态并添加数据
        mAdapter.setState(adapterState);
        mAdapter.addDatas(data);

        if (mAdapter.getCount() == 1) {
            if (needShowEmptyNoData()) {
                mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
            } else {
                mAdapter.setState(STATE_EMPTY_ITEM);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    //将得到的数据与mAdapter中的数据进行比较
    protected boolean compareTo(List<? extends Entity> data, Entity entity) {
        int s = data.size();
        if (entity != null) {
            for (int i = 0; i < s; i++) {
                if (entity.getId() == data.get(i).getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    //是否需要隐藏listview，显示无数据状态
    protected boolean needShowEmptyNoData() {
        return true;
    }

    protected int getPageSize() {
        return AppContext.PAGE_SIZE;
    }

    //加载数据错误
    protected void executeOnLoadDataError(String error) {

        if (mCurrentPage == 0 && !CacheManager.isCacheExist(getActivity(), getCacheKey())) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        } else {
                /*
                在没有网络的情况下，滚动到底部，mCurrentPage先自增加了，然而失败时却没有
                减回来，如果刻意在无网络的情况下上拉，会出现漏页的问题
                */
            mCurrentPage--;
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            mAdapter.setState(BaseListAdapter.STATE_NETWORK_ERROR);
            mAdapter.notifyDataSetChanged();
        }
    }

    //完成刷新
    protected void executeOnLoadFinish() {
        setSwipeRefreshLoadedState();
        mState = STATE_NONE;
    }

    //设置顶部正在加载的状态
    protected void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            //防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    //设置顶部加载完毕的状态
    protected void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    private void executeParserTask(byte[] data) {
        cancelParserTask();
        mParserTask = new ParserTask(data);
        mParserTask.execute();
    }

    private void cancelParserTask() {
        if (mParserTask != null) {
            mParserTask.cancel(true);
            mParserTask = null;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //数据为空，数据正在加载，或者全部加载完毕，不处理滚动事件
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }
        if (mState == STATE_LOADMORE || mState == STATE_REFRESH) {
            return;
        }

        //判断是否滚动到了底部
        boolean scrollEnd = false;
        try {
            if (view.getPositionForView(mAdapter.getFooterView())
                    == view.getLastVisiblePosition())
                scrollEnd = true;
        } catch (Exception e) {
            scrollEnd = false;
        }

        if (mState == STATE_NONE && scrollEnd) {
            if (mAdapter.getState() == BaseListAdapter.STATE_LOAD_MORE
                    || mAdapter.getState() == BaseListAdapter.STATE_NETWORK_ERROR) {
                mCurrentPage++;
                mState = STATE_LOADMORE;
                requestData(false);
                mAdapter.setFooterViewLoading();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    protected void saveToReadedList(final View view, final String prefFileName, final String key) {
        //放入已读列表
        AppContext.putReadedPostList(prefFileName, key, "true");
        TextView titleTV = (TextView) view.findViewById(R.id.title_tv);
        if (titleTV != null) {
            titleTV.setTextColor(ContextCompat.getColor(getContext(),ThemeSwitchUtils.getTitleReadedColor()));
        }
    }

    private class CacheTask extends AsyncTask<String, Void, EntityList<T>> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected EntityList<T> doInBackground(String... params) {

            Serializable seri = CacheManager.readObject(mContext.get(), params[0]);

            if (seri == null) {
                return null;
            } else {
                return readList(seri);
            }

        }

        @Override
        protected void onPostExecute(EntityList<T> list) {

            super.onPostExecute(list);

            if (list != null) {
                executeOnLoadDataSuccess(list.getList());
            } else {
                executeOnLoadDataError(null);
            }

            executeOnLoadFinish();
        }
    }

    class ParserTask extends AsyncTask<Void, Void, String> {

        private final byte[] responseData;

        private boolean parserError;

        public List<T> list;

        public ParserTask(byte[] data) {
            this.responseData = data;

            String responseString=new String(responseData);
            Log.i("noone",responseString);
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                final EntityList<T> data = parseList(new ByteArrayInputStream(responseData));

                CacheManager.saveObject(getActivity(), data, getCacheKey());

                list = data.getList();

                if (list == null) {

                    ResultData resultData = XmlUtils.toBean(ResultData.class, responseData);

                    if (resultData != null) {
                        mResult = resultData.getResult();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                parserError = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (parserError) {
                readCacheData(getCacheKey());
            } else {
                executeOnLoadDataSuccess(list);
                executeOnLoadFinish();
            }
        }
    }

}

