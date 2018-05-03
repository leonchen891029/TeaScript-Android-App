package com.teacore.teascript.module.general.base.basefragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.module.general.app.AppOperator;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.base.basebean.PageBean;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.TSRefreshLayout;
import com.teacore.teascript.widget.TSRefreshLayout.TSRefreshLayoutListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


/**
 * 综合下的BaseListFragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-22
 */

public abstract class BaseListFragment<T> extends BaseFragment implements TSRefreshLayoutListener,OnItemClickListener,
        BaseListAdapter.Callback,View.OnClickListener{

    public static final int TYPE_NORMAL=0;
    public static final int TYPE_LOADING=1;
    public static final int TYPE_NO_MORE=2;
    public static final int TYPE_ERROR=3;
    public static final int TYPE_NET_ERROR=4;

    protected String CACHE_NAME=getClass().getName();
    protected ListView mListView;
    protected TSRefreshLayout mRefreshLayout;
    protected EmptyLayout mEmptyLayout;
    protected BaseListAdapter<T> mAdapter;
    protected boolean mIsRefresh;
    protected TextHttpResponseHandler mHandler;
    protected PageBean<T> mPageBean;

    private String mTime;
    private View mFooterView;
    private ProgressBar mFooterPB;
    private TextView mFooterTV;

    @Override
    protected int getLayoutId(){
        return R.layout.general_fragment_base_list;
    }

    @Override
    protected void initView(View rootView){
        super.initView(rootView);

        mListView=(ListView) rootView.findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);

        mRefreshLayout=(TSRefreshLayout) rootView.findViewById(R.id.ts_refreshlayout);
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1,R.color.swiperefresh_color2,
                R.color.swiperefresh_color3,R.color.swiperefresh_color3);
        mRefreshLayout.setTSRefreshLayoutListener(this);

        mEmptyLayout=(EmptyLayout) rootView.findViewById(R.id.empty_layout);
        mEmptyLayout.setOnLayoutClickListener(this);

        mFooterView= LayoutInflater.from(getContext()).inflate(R.layout.view_list_footer,null);
        mFooterPB=(ProgressBar) mFooterView.findViewById(R.id.footer_pb);
        mFooterTV=(TextView) mFooterView.findViewById(R.id.footer_tv);

        setFooterType(TYPE_LOADING);

        if(isNeedFooterView()){
            mListView.addFooterView(mFooterView);
        }

    }

    @Override
    protected void initData(){

        super.initData();

        mAdapter=getListAdapter();
        mListView.setAdapter(mAdapter);

        mHandler=new TextHttpResponseHandler() {

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                onRequestError(i);
                onRequestFinish();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String responseString) {

                try{
                    ResultBean<PageBean<T>> resultBean= AppContext.createGson().fromJson(responseString,getType());

                    if(resultBean!=null && resultBean.isSuccess() && resultBean.getResult().getItems()!=null){

                        onRequestSuccess(resultBean.getCode());

                        setListData(resultBean);

                    }else{
                        setFooterType(TYPE_NO_MORE);
                    }

                    onRequestFinish();

                }catch(Exception e){
                    e.printStackTrace();
                    onFailure(i,headers,responseString,e);
                }
            }
        };

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {

                mPageBean=(PageBean<T>) CacheManager.readObject(getActivity(),CACHE_NAME);

                //如果是第一次，mPageBean是为null的
                if (mPageBean == null) {

                    mPageBean = new PageBean<T>();

                    mPageBean.setItems(new ArrayList<T>());

                    onRefreshing();

                }else{

                    mRootView.post(new Runnable() {
                        @Override
                        public void run() {

                            //如果mPageBean不为null,取出其中的元素填充mAdapter
                            mAdapter.addItems(mPageBean.getItems());

                            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);

                            mRefreshLayout.setVisibility(View.VISIBLE);

                            onRefreshing();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View view){

        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);

        onRefreshing();

    }

    @Override
    public void onRefreshing(){

        mIsRefresh=true;

        requestData();

    }

    @Override
    public void onLoadMore() {
        requestData();
    }

    //请求网络数据
    protected void requestData(){
        onRequestStart();
        setFooterType(TYPE_LOADING);
    }

    protected void onRequestStart() {

    }

    protected void onRequestSuccess(int code) {

    }

    protected void onRequestError(int code) {
        setFooterType(TYPE_NET_ERROR);
        if (mAdapter.getDatas().size() == 0)
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
    }

    protected void onRequestFinish() {
        onComplete();
    }

    protected void onComplete() {
        mRefreshLayout.onLoadComplete();
        mIsRefresh = false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    //放入已读列表
    protected void saveToReadedList(String fileName, String key) {

        // 放入已读列表
        AppContext.putReadedPostList(fileName, key, "true");

    }

    //更新字体的颜色
    protected void updateTextColor(TextView title, TextView content) {
        if (title != null) {
            title.setTextColor(getResources().getColor(R.color.count_text_color_light));
        }
        if (content != null) {
            content.setTextColor(getResources().getColor(R.color.count_text_color_light));
        }
    }

    //List加载ResultBean数据
    protected void setListData(ResultBean<PageBean<T>> resultBean) {

        mPageBean.setNextPageToken(resultBean.getResult().getNextPageToken());

        if (mIsRefresh) {
            //cache the time
            mTime = resultBean.getTime();
            mPageBean.setItems(resultBean.getResult().getItems());
            mAdapter.clear();
            mAdapter.addItems(mPageBean.getItems());
            mPageBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());

            mRefreshLayout.setCanLoadMore();

            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    CacheManager.saveObject(getActivity(), mPageBean, CACHE_NAME);
                }
            });

        } else {

            mAdapter.addItems(resultBean.getResult().getItems());

        }

        if (resultBean.getResult().getItems().size() < 20) {
            setFooterType(TYPE_NO_MORE);
            //mRefreshLayout.setNoMoreData();
        }

        if (mAdapter.getDatas().size() > 0) {
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        }
    }

    @Override
    public Date getSystemTime() {
        return new Date();
    }

    protected abstract BaseListAdapter<T> getListAdapter();

    protected abstract Type getType();

    protected boolean isNeedFooterView() {
        return true;
    }

    protected void setFooterType(int type) {

        switch (type) {
            case TYPE_NORMAL:
            case TYPE_LOADING:
                mFooterTV.setText(getResources().getString(R.string.footer_type_loading));
                mFooterPB.setVisibility(View.VISIBLE);
                break;
            case TYPE_NET_ERROR:
                mFooterTV.setText(getResources().getString(R.string.footer_type_net_error));
                mFooterPB.setVisibility(View.GONE);
                break;
            case TYPE_ERROR:
                mFooterTV.setText(getResources().getString(R.string.footer_type_error));
                mFooterPB.setVisibility(View.GONE);
                break;
            case TYPE_NO_MORE:
                mFooterTV.setText(getResources().getString(R.string.footer_type_not_more));
                mFooterPB.setVisibility(View.GONE);
                break;
        }
    }


}
