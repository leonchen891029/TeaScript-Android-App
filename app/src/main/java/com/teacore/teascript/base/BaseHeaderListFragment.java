package com.teacore.teascript.base;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.widget.EmptyLayout;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import cz.msebera.android.httpclient.Header;

/**加入了header的BaseListFragment，头部显示详情，然后下面显示评论列表的
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-7
 */
public abstract class BaseHeaderListFragment<T1 extends Entity,T2 extends Serializable> extends BaseListFragment<T1>{

    //list头部的详情实体类
    protected T2 detailBean;

    protected Activity activity;

    //加载HeaderView数据的AsyncHttpResponseHandler
    protected final AsyncHttpResponseHandler mDetailHander=new AsyncHttpResponseHandler(){

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2){
            try{
                if(arg2 != null){
                    //获取detai相应的bean类
                    T2 detail=getDetailBean(new ByteArrayInputStream(arg2));
                    if(detail!=null){
                        //请求列表数据
                        requestListData();
                        executeOnLoadDetailSuccess(detail);
                        //保存detai到缓存
                        new SaveCacheTask(getActivity(),detail,
                                getDetailCacheKey()).execute();
                    }else{
                        onFailure(arg0,arg1,arg2,null);
                    }
                }else{
                 throw new RuntimeException("load detail error");
                }
            }catch(Exception e){
                e.printStackTrace();
                onFailure(arg0,arg1,arg2,e);
            }
        }

        @Override
        public void onFailure(int arg0,Header[] arg1,byte[] arg2,Throwable arg3){
            readDetailCacheData( getDetailCacheKey() );
        };
    };

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        activity=getActivity();
        //mistView添加HeaderView
        mListView.addHeaderView( initHeaderView() );
        requestDetailData(isRefresh());
    }

    //是否需要刷新
    protected boolean isRefresh(){
        return false;
    }

    private void requestListData(){
        mState=STATE_REFRESH;
        mAdapter.setState(BaseListAdapter.STATE_LOAD_MORE);
        //请求列表数据
        sendRequestData();
    }

    //请求HeaderView数据
    protected abstract void requestDetailData(boolean isRefresh);
    //初始化HeaderView
    protected abstract View initHeaderView();
    //获取HeaderView数据的Cachekey
    protected abstract String getDetailCacheKey();
    //HeaderView数据加载成功
    protected abstract void executeOnLoadDetailSuccess(T2 detailBean);
    //获取HeaderView数据(ByteArrayInputStream)
    protected abstract T2 getDetailBean(ByteArrayInputStream is);

    @Override
    protected  boolean requestDataIfViewCreated(){
        return false;
    }

    //带有Header view的ListFragment不需要显示数据为空
    @Override
    protected boolean needShowEmptyNoData(){
        return false;
    }

    //读取HeaderView数据的缓存
    protected void readDetailCacheData(String cacheKey){
        new ReadCacheTask(getActivity()).execute(cacheKey);
    }

    private class ReadCacheTask extends AsyncTask<String,Void,T2>{
        private final WeakReference<Context> mContext;

        private ReadCacheTask(Context context){
            mContext=new WeakReference<Context>(context);
        }

        @Override
        protected T2 doInBackground(String... params){

            if(mContext.get()!=null){

                Serializable seri=CacheManager.readObject(mContext.get(),params[0]);

                if(seri==null){
                    return null;
                }else{
                    return (T2) seri;
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(T2 t){
            super.onPostExecute(t);
            if(t !=null){
                requestListData();
                executeOnLoadDetailSuccess(t);
            }
        }
    }

    private class SaveCacheTask extends AsyncTask<Void,Void,Void>{

        private final WeakReference<Context> mContext;

        private final Serializable seri;

        private final String key;

        private SaveCacheTask(Context context,Serializable seri,String key){
            mContext=new WeakReference<Context>(context);
            this.seri=seri;
            this.key=key;
        }

        @Override
        protected Void doInBackground(Void... params){
            CacheManager.saveObject(mContext.get(),seri,key);
            return null;
        }

    }

    @Override
    protected void executeOnLoadDataError(String error){
        mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
        mAdapter.setState(BaseListAdapter.STATE_NETWORK_ERROR);
        mAdapter.notifyDataSetChanged();
    }

    protected <T extends View> T findHeaderView(View view,int viewId){
        return (T) view.findViewById(viewId);
    }


}

















