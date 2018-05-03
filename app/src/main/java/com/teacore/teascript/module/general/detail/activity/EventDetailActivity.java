package com.teacore.teascript.module.general.detail.activity;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.EventApplyData;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.bean.EventDetail;
import com.teacore.teascript.module.general.detail.constract.EventDetailContract;
import com.teacore.teascript.module.general.detail.fragment.DetailFragment;
import com.teacore.teascript.module.general.detail.fragment.EventDetailFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * EventDetailActivity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-25
 */

public class EventDetailActivity extends DetailActivity<EventDetail,EventDetailContract.BaseView> implements EventDetailContract.Operator{

    public static void show(Context context,long id){
        Intent intent=new Intent(context,EventDetailActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }

    protected int getType(){return 3;}

    @Override
    protected void requestData(){
        TeaScriptApi.getEventDetail(mDataId,getRequestHandler());
    }

    @Override
    protected Class<? extends DetailFragment> getDataViewFragment(){
        return EventDetailFragment.class;
    }

    @Override
    protected Type getDataType(){
        return new TypeToken<ResultBean<EventDetail>>(){}.getType();
    }

    @Override
    protected int getOptionsMenuId(){
        return R.menu.menu_detail_share;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.menu_item_share){
            final EventDetail eventDetail=getData();
            if(eventDetail!=null){
                toShare(eventDetail.getTitle(),eventDetail.getBody(),eventDetail.getHref());
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void toFavorite(){
        int uId=requestCheck();
        if(uId==0){
            return;
        }
        showWaitDialog(R.string.progress_submit);
        final EventDetail eventDetail=getData();
        TeaScriptApi.getFavReverse(mDataId, getType(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                hideWaitDialog();;
                if(eventDetail==null){
                    return;
                }
                if(eventDetail.isFavorite()){
                    AppContext.showToast(R.string.del_favorite_fail);
                }else{
                    AppContext.showToast(R.string.add_favorite_fail);
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    Type type = new TypeToken<ResultBean<EventDetail>>() {
                    }.getType();

                    ResultBean<EventDetail> resultBean = AppContext.createGson().fromJson(s, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        eventDetail.setFavorite(!eventDetail.isFavorite());
                        mView.toFavoriteOk(eventDetail);
                        if (eventDetail.isFavorite())
                            AppContext.showToast(R.string.add_favorite_success);
                        else
                            AppContext.showToast(R.string.del_favorite_success);
                    }
                    hideWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(i, headers, s,e);
                }
            }
        });
    }

    @Override
    public void toSignUp(EventApplyData data){
        int uId=requestCheck();
        if(uId==0){
            return;
        }
        final EventDetail eventDetail=getData();
        showWaitDialog(R.string.progress_submit);
        TeaScriptApi.eventApply(data, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Result result= XmlUtils.toBean(ResultData.class,new ByteArrayInputStream(bytes)).getResult();
                if(result.OK()){
                    AppContext.showToast(R.string.apply_success);
                    eventDetail.setApplyStatus(0);
                    mView.toSignUpOk(eventDetail);
                }else{
                    AppContext.showToast(result.getMessage());
                }
                hideWaitDialog();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                hideWaitDialog();
            }
        });

    }



}
