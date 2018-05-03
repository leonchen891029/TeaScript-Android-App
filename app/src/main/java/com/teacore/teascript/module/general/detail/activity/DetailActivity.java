package com.teacore.teascript.module.general.detail.activity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Report;
import com.teacore.teascript.module.general.base.baseactivity.BaseBackActivity;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.detail.constract.DetailContract;
import com.teacore.teascript.module.general.detail.fragment.DetailFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.dialog.ReportDialog;
import com.teacore.teascript.widget.dialog.ShareDialog;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;


/**
 *详情Activity的基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-21
 */

public abstract class DetailActivity<Data,DataView extends DetailContract.BaseView>
        extends BaseBackActivity implements DetailContract.BasePresenter<Data,DataView>{

    //数据ID
    long mDataId;
    //数据
    Data mData;
    //绑定的视图类，这里是Fragment
    DataView mView;
    EmptyLayout mEmptyLayout;
    TextView mCommentCountTV;

    private ProgressDialog mProgressDialog;

    private ShareDialog mShareDialog;

    //获取数据的ID
    public long getDataId(){
        return mDataId;
    }
    //获取数据
    public Data getData(){
        return mData;
    }

    protected int getLayoutId(){
        return R.layout.general_activity_detail;
    }

    //绑定DataView
    @Override
    public void setDataView(DataView view){
        mView=view;
    }

    //判定dataid是否存在
    @Override
    protected boolean initBundle(Bundle bundle){
        mDataId=bundle.getLong("id",0);
        return mDataId!=0;
    }

    @Override
    protected void initView(){
        super.initView();
        mEmptyLayout=(EmptyLayout) findViewById(R.id.empty_layout);

        //加载EmptyLayout点击事件
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmptyLayout emptyLayout = mEmptyLayout;
                if (emptyLayout != null && emptyLayout.getEmptyState() != EmptyLayout.HIDE_LAYOUT) {
                    emptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                    requestData();
                }
            }
        });

    }

    //初始化数据
    @Override
    protected void initData(){
        super.initData();
        requestData();
    }

    @Override
    public void hideLoading(){

        final EmptyLayout emptyLayout=mEmptyLayout;

        if(emptyLayout==null){
            return;
        }

        Animation animation= AnimationUtils.loadAnimation(this,R.anim.anim_alpha_to_hide);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                emptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        emptyLayout.startAnimation(animation);
    }

    //请求数据
    protected abstract void requestData();

    //获取显示界面的Fragment
    protected abstract Class<? extends DetailFragment> getDataViewFragment();

    //获取JSON解析的数据Type
    protected abstract Type getDataType();

    AsyncHttpResponseHandler getRequestHandler(){
        return new TextHttpResponseHandler() {

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                throwable.printStackTrace();
                if (isDestroy())
                    return;
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {

                if (isDestroy())
                    return;

                //如果处理数据函数返回false
                if (!handleData(s))
                    showError(EmptyLayout.NODATA);
            }

        };
    }

    boolean handleData(String responseString){

        ResultBean<Data> result;

        //解析出result,这里不需要pagebean
        try{
            Type type=getDataType();
            result= AppContext.createGson().fromJson(responseString,type);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

        //如果加载成功,为mData赋值,并且加载相应的Fragment
        if(result.isSuccess()){
            mData=result.getResult();
            handleView();
            return true;
        }

        return false;
    }

    void handleView(){

        try {
            Fragment fragment = getDataViewFragment().newInstance();
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction();
            trans.replace(R.id.container_fl, fragment);
            trans.commitAllowingStateLoss();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void showError(int type){
        EmptyLayout emptyLayout=mEmptyLayout;
        if(emptyLayout!=null){
            emptyLayout.setEmptyType(type);
        }
    }

    @Override
    public void setCommentCount(int count){
        final TextView view = mCommentCountTV;
        if (view != null) {
            String str;
            if (count < 1000)
                str = String.valueOf(count);
            else if (count < 10000) {
                str = String.format("%sK", (Math.round(count * 0.01f) * 0.1f));
            } else {
                str = String.format("%sW", (Math.round(count * 0.001f) * 0.1f));
            }
            view.setText(str);
        }
    }

    protected int getOptionsMenuId(){
        return R.menu.menu_improve_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        int menuId=getOptionsMenuId();
        if(menuId<=0){
            return false;
        }
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(menuId,menu);
        MenuItem item=menu.findItem(R.id.menu_item_comment);
        if(item!=null){
            View actionView=item.getActionView();
            if(actionView!=null){
                actionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataView view=mView;
                        if(view!=null){
                            view.scrollToComment();
                        }
                    }
                });
                View tv=actionView.findViewById(R.id.comment_count_tv);
                if (tv != null)
                    mCommentCountTV = (TextView) tv;
            }

        }
        return true;
    }

    public ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtils.getProgressDialog(this, message);
        }

        mProgressDialog.setMessage(message);
        mProgressDialog.show();

        return mProgressDialog;
    }

    public void hideWaitDialog() {
        if (mProgressDialog != null) {
            mProgressDialog = null;
            try {
                mProgressDialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //分享
    protected void toShare(String title, String content, String url) {

        if (mShareDialog == null) {
            mShareDialog = new ShareDialog(this);
        }

        mShareDialog.setCancelable(true);
        mShareDialog.setCanceledOnTouchOutside(true);
        mShareDialog.setTitle(R.string.share_to);
        mShareDialog.setShareInfo(title, content, url);
        mShareDialog.show();
    }

    protected void hideShareDialog() {
        if (mShareDialog != null) {
            mShareDialog = null;
            try {
                mShareDialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //举报
    protected void toReport(long id,String href,byte reportType){
        final ReportDialog reportDialog=new ReportDialog(this,href,id,reportType);
        reportDialog.setCancelable(true);
        reportDialog.setCanceledOnTouchOutside(true);
        reportDialog.setTitle(R.string.report);
        reportDialog.setNegativeButton(R.string.cancle,null);
        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                if (TextUtils.isEmpty(arg2)) {
                    AppContext.showToast(R.string.tip_report_success);
                } else {
                    AppContext.showToast(arg2);
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2,
                                  Throwable arg3) {
                AppContext.showToast(R.string.tip_report_fail);
            }

            @Override
            public void onFinish() {
                hideWaitDialog();
            }

            @Override
            public void onStart() {
                showWaitDialog(R.string.progress_submit);
            }
        };
        reportDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface d, int which) {
                        Report report = null;
                        if ((report = reportDialog.getReport()) != null) {
                            TeaScriptApi.report(report, handler);
                        }
                        d.dismiss();
                    }
                });
        reportDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ShareDialog shareDialog = mShareDialog;
        if (shareDialog != null) {
           // UMSsoHandler ssoHandler = shareDialog.getController().getConfig().getSsoHandler(requestCode);
           // if (ssoHandler != null) {
            //    ssoHandler.authorizeCallBack(requestCode, resultCode, data);
           // }
        }
    }

    //检查当前的数据和网络状态 返回当前登录用户的用户id，未登录或者是未通过检查返回0
    public int requestCheck(){

        if (mDataId == 0 || mData == null) {
            AppContext.showToast("数据加载中...");
            return 0;
        }

        if (!TDevice.hasInternet()) {
            AppContext.showToast(R.string.tip_no_internet);
            return 0;
        }

        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(this);
            return 0;
        }

        // 返回当前登录用户ID
        return AppContext.getInstance().getLoginUid();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        hideWaitDialog();
        hideShareDialog();
        mEmptyLayout=null;
        mData=null;
    }


}
