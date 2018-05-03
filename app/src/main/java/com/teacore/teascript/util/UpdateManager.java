package com.teacore.teascript.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.bean.UpdateDetail;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.io.ByteArrayInputStream;

import cz.msebera.android.httpclient.Header;

/**
 * 更新管理类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */

public class UpdateManager {

    private UpdateDetail mUpdateDetail;

    private Context mContext;

    private boolean isShow=false;

    private ProgressDialog mWaitDialog;

    private AsyncHttpResponseHandler mCheckUpdateHandle = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            hideCheckDialog();

            mUpdateDetail = XmlUtils.toBean(UpdateDetail.class,
                    new ByteArrayInputStream(arg2));

            finishCheck();
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {

            hideCheckDialog();

            if (isShow) {
                showFailDialog();
            }
        }


    };

    public UpdateManager(Context context,boolean isShow){
        this.mContext=context;

        this.isShow=isShow;
    }

    public boolean haveNewVersion(){
        if(mUpdateDetail==null){
            return false;
        }

        boolean haveNew=false;

        int curVersionCode=TDevice.getVersionCode(AppContext.getInstance().getPackageName());

        if(curVersionCode<mUpdateDetail.getUpdate().getAndroid().getVersionCode()){
            haveNew=true;
        }

        return haveNew;
    }

    public void checkUpdate() {
        if (isShow) {
            showCheckDialog();
        }
        TeaScriptApi.checkUpdate(mCheckUpdateHandle);
    }

    private void finishCheck(){
        if(haveNewVersion()){
            showUpdateInfo();
        }else {
            if(isShow){
                showLatestDialog();
            }
        }
    }

    private void showCheckDialog(){
        if(mWaitDialog==null){
            mWaitDialog= DialogUtils.getProgressDialog((Activity) mContext,"正在获取新版本的信息...");
        }
        mWaitDialog.show();
    }


    private void hideCheckDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
        }
    }

    private void showUpdateInfo(){
        if(mUpdateDetail==null) {
            return;
        }
        AlertDialog.Builder dialog = DialogUtils.getConfirmDialog(mContext, mUpdateDetail.getUpdate().getAndroid().getUpdateLog(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UiUtils.openDownLoadService(mContext, mUpdateDetail.getUpdate().getAndroid().getDownloadUrl(), mUpdateDetail.getUpdate().getAndroid().getVersionName());
            }
        });
        dialog.setTitle("发现新版本");
        dialog.show();
    }

    private void showLatestDialog() {
        DialogUtils.getMessageDialog(mContext, "已经是新版本了").show();
    }

    private void showFailDialog() {
        DialogUtils.getMessageDialog(mContext, "网络异常，无法获取新版本信息").show();
    }

}
