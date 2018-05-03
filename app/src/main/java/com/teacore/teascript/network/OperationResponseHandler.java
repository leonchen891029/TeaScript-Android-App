package com.teacore.teascript.network;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.ByteArrayInputStream;

import cz.msebera.android.httpclient.Header;

/**操作结果返回处理类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-8
 */

public class OperationResponseHandler extends AsyncHttpResponseHandler{

    private Object[] args;

    public OperationResponseHandler(Looper looper,Object... args){
        super(looper);
        this.args=args;
    }

    public OperationResponseHandler(Object... args){
        this.args=args;
    }

    @Override
    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
        onFailureOperation(arg0, arg3.getMessage(), args);
    }

    @Override
    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
        try {
            onSuccessOperation(arg0, new ByteArrayInputStream(arg2), args);
        } catch (Exception e) {
            e.printStackTrace();
            onFailureOperation(arg0, e.getMessage(), args);
        }
    }

    public void onSuccessOperation(int code, ByteArrayInputStream is, Object[] args) throws Exception{

    }


    public void onFailureOperation(int code, String errorMessage, Object[] args){
    }



}
