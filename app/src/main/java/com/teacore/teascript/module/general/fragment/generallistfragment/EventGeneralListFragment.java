package com.teacore.teascript.module.general.fragment.generallistfragment;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Banner;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.module.general.adapter.generaladapter.EventAdapter;
import com.teacore.teascript.module.general.app.AppOperator;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.base.basebean.PageBean;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.base.basefragment.BaseGeneralListFragment;
import com.teacore.teascript.module.general.bean.Event;
import com.teacore.teascript.module.general.detail.activity.EventDetailActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.widget.EventHeaderView;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 综合下的活动界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-14
 */

public class EventGeneralListFragment extends BaseGeneralListFragment<Event>{

    public static final String HISTORY_EVENT="history_event";
    private boolean isFirst=true;
    private static final String EVENT_BANNER="event_banner";
    private EventHeaderView mHeaderView;
    private Handler handler=new Handler();

    @Override
    protected void initView(View view){

        super.initView(view);

        mHeaderView=new EventHeaderView(getActivity());

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                final PageBean<Banner> pageBean = (PageBean<Banner>) CacheManager.readObject(getActivity(), EVENT_BANNER);
                if (pageBean != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHeaderView.initData(getImageLoader(), pageBean.getItems());
                        }
                    });
                }
            }
        });

        mHeaderView.setRefreshLayout(mRefreshLayout);

        mListView.addHeaderView(mHeaderView);

        getBannerList();
    }


    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (!isFirst)
            getBannerList();
    }

    @Override
    protected void requestData(){
        super.requestData();
        mPageBean.setNextPageToken("eventteascriptofficial2018");
        TeaScriptApi.getEventList(mIsRefresh?mPageBean.getNextPageToken():mPageBean.getPrevPageToken(),mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent,View view,int position,long id){
        Event event=mAdapter.getItem(position-1);
        if(event!=null){
            EventDetailActivity.show(getActivity(), event.getId());
            TextView title = (TextView) view.findViewById(R.id.event_title_tv);
            updateTextColor(title, null);
            saveToReadedList(HISTORY_EVENT, event.getId() + "");
        }
    }

    @Override
    protected BaseListAdapter<Event> getListAdapter(){
        return new EventAdapter(this);
    }

    @Override
    protected Type getType(){
        return new TypeToken<ResultBean<PageBean<Event>>>(){}.getType();
    }

    @Override
    protected void onRequestFinish(){
        super.onRequestFinish();
        isFirst=false;
    }

    private void getBannerList(){

        String stringToken="eventbannersteascriptofficial";

        TeaScriptApi.getBannerList(stringToken, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {

                    final ResultBean<PageBean<Banner>> resultBean = AppContext.createGson().fromJson(responseString, new TypeToken<ResultBean<PageBean<Banner>>>() {
                    }.getType());

                    if (resultBean != null && resultBean.isSuccess()) {

                        AppOperator.runOnThread(new Runnable() {
                            @Override
                            public void run() {
                                CacheManager.saveObject(getActivity(), resultBean.getResult(), EVENT_BANNER);
                            }
                        });

                        mHeaderView.initData(getImageLoader(), resultBean.getResult().getItems());
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        });

    }

}
