package com.teacore.teascript.module.general.fragment.generallistfragment;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Banner;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.module.general.adapter.generaladapter.NewsAdapter;
import com.teacore.teascript.module.general.app.AppOperator;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.base.basebean.PageBean;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.base.basefragment.BaseGeneralListFragment;
import com.teacore.teascript.module.general.bean.News;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.NewsHeaderView;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 综合里面的资讯界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-28
 */

public class NewsGeneralListFragment extends BaseGeneralListFragment<News>{

    public static final String HISTORY_NEWS="history_news";
    public static final String NEWS_SYSTEM_TIME="news_system_time";

    private static final String NEWS_BANNER="news_banner";

    private boolean isFirst=true;

    private NewsHeaderView mHeaderView;

    private Handler handler=new Handler();

    @Override
    protected void initView(View rootView){
        super.initView(rootView);
        mHeaderView=new NewsHeaderView(getActivity());

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                final PageBean<Banner> pageBean=(PageBean<Banner>) CacheManager.readObject(getActivity(),NEWS_BANNER);

                //从缓存中读取PageBean<Banner>
                if(pageBean!=null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ((NewsAdapter) mAdapter).setSystemTime(AppContext.get(NEWS_SYSTEM_TIME,null));
                            mHeaderView.initData(getImageLoader(),pageBean.getItems());
                        }
                    });
                }

            }
        });

        mHeaderView.setRefreshLayout(mRefreshLayout);

        //添加ListView的HeadView,OnItemClick就需要-1
        mListView.addHeaderView(mHeaderView);
        getBannerList();

    }

    //重写刷新方法
    @Override
    public void onRefreshing(){
        super.onRefreshing();
        if(!isFirst)
            getBannerList();
    }

    //请求ListView中的数据
    @Override
    protected void requestData(){

        super.requestData();

        //下面一段为测试代码，模拟加载一个PageBean类
        mPageBean.setNextPageToken("igferrari430zhouyyfchuanfaith");

        TeaScriptApi.getNewsList(mIsRefresh?mPageBean.getNextPageToken():mPageBean.getPrevPageToken(),mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent,View view,int position,long id){

        News news=mAdapter.getItem(position-1);

        if(news!=null){

            int type=news.getType();

            long newsId=news.getId();

            UiUtils.showDetail(getActivity(),type,newsId,news.getHref());

            TextView titleTV=(TextView) view.findViewById(R.id.title_tv);
            TextView contentTV=(TextView) view.findViewById(R.id.description_tv);
            updateTextColor(titleTV,contentTV);

            saveToReadedList(HISTORY_NEWS,news.getId()+"");
        }

    }

    @Override
    protected BaseListAdapter<News> getListAdapter(){
        return new NewsAdapter(this);
    }

    @Override
    protected Type getType(){
        return new TypeToken<ResultBean<PageBean<News>>>(){}.getType();
    }

    @Override
    protected void onRequestFinish(){
        super.onRequestFinish();
        isFirst=false;
    }

    //ListView中加载返回的数据
    @Override
    protected void setListData(ResultBean<PageBean<News>> resultBean) {
        ((NewsAdapter) mAdapter).setSystemTime(resultBean.getTime());
        AppContext.set(NEWS_SYSTEM_TIME, resultBean.getTime());
        super.setListData(resultBean);
    }

    private void getBannerList(){

        //下面一段为测试代码，模拟设置一个pageToken
        String pageToken="newsbannersteascriptofficial";

        TeaScriptApi.getBannerList(pageToken, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {

                try{

                    final ResultBean<PageBean<Banner>> resultBean =AppContext.createGson().fromJson(s,new TypeToken<ResultBean<PageBean<Banner>>>(){
                    }.getType());

                    if(resultBean!=null && resultBean.isSuccess()){

                        AppOperator.runOnThread(new Runnable() {
                            @Override
                            public void run() {
                                CacheManager.saveObject(getActivity(), resultBean.getResult(), NEWS_BANNER);
                            }
                        });

                        mHeaderView.initData(getImageLoader(), resultBean.getResult().getItems());
                    }
                }catch(Exception e){

                    Log.e("Bannner", Log.getStackTraceString(e));

                }
            }
        });
    }

}
