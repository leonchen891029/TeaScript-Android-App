package com.teacore.teascript.network;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.util.TLog;

import java.util.Locale;

/**HttpClient类
 * @auth 陈晓帆
 * @version 1.0
 * Created by apple on 17/10/6.
 */
public class TSHttpClient {

    public  static final String HOST="www.teascript.cn";
    private static String BASE_URL="http://www.teascript.cn/%s";

    //HttpClient操作
    public static final String GET="get";
    public static final String POST="post";
    public static final String PUT="put";
    public static final String DELETE="delete";

    public static AsyncHttpClient client;

    public TSHttpClient(){

    }

    //获取AsyncHttpClient实例
    public static AsyncHttpClient getHttpClient(){
        return client;
    }

    //取消所有的请求
    public static void cancelAll(Context context){
        client.cancelRequests(context,true);
    }

    //清除用户的Cookies
    public static void cleanUserCookies(Context context){

    }

    //获取BaseUrl
    public static String getBaseUrl(){
        return BASE_URL;
    }

    //设置BaseUrl
    public static void setBaseUrl(String url){
        BASE_URL=url;
    }

    //获取绝对url
    public static String getAbsoluteApiUrl(String partUrl){
        String url = partUrl;

        //先判定partUrl是不是相对路径
        if (!partUrl.startsWith("http:") && !partUrl.startsWith("https:")) {
            url = String.format(BASE_URL, partUrl);
        }

        return url;
    }

    //封装AsyncHttpClient的get方法
    public static void get(String partUrl,AsyncHttpResponseHandler handler){
        client.get(getAbsoluteApiUrl(partUrl),handler);
        log(new StringBuilder("get ").append(partUrl).toString());
    }

    public static void get(String partUrl, RequestParams params,
                           AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("get ").append(partUrl).append("&")
                .append(params).toString());
    }

    //url为AbsoluteUrl时调用的get方法
    public static void getDirect(String url,AsyncHttpResponseHandler handler){
        client.get(url,handler);
        log(new StringBuilder("get ").append(url).toString());
    }

    //封装AsyncHttpClient的post方法
    public static void post(String partUrl, AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("post ").append(partUrl).toString());
    }

    public static void post(String partUrl, RequestParams params,
                            AsyncHttpResponseHandler handler) {
        client.post(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("post ").append(partUrl).append("&")
                .append(params).toString());
    }

    //url为AbsoluteUrl时调用的post方法
    public static void postDirect(String url, RequestParams params,
                                  AsyncHttpResponseHandler handler) {
        client.post(url, params, handler);
        log(new StringBuilder("post ").append(url).append("&").append(params)
                .toString());
    }

    //封装AsyncHttpClient的put方法
    public static void put(String partUrl, AsyncHttpResponseHandler handler) {
        client.put(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("put ").append(partUrl).toString());
    }

    public static void put(String partUrl, RequestParams params,
                           AsyncHttpResponseHandler handler) {
        client.put(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("put ").append(partUrl).append("&")
                .append(params).toString());
    }

    //封装AsyncHttpClient的delete方法
    public static void delete(String partUrl, AsyncHttpResponseHandler handler){
        client.delete(getAbsoluteApiUrl(partUrl),handler);
        log(new StringBuilder("delete ").append(partUrl).toString());
    }

    //设置新的AsyncHttp
    public static void setHttpClient(AsyncHttpClient asyncHttpClient){
        client=asyncHttpClient;
        client.addHeader("Accept-Language", Locale.getDefault().toString());
        client.addHeader("Host",HOST);
        client.addHeader("Connection","Keep-Alive");

        setUserAgent(TSHttpHelper.getUserAgent(AppContext.getInstance()));
    }

    //设置AsyncHttpClient的user-agent
    public static void setUserAgent(String userAgent){
        client.setUserAgent(userAgent);
    }

    //设置AsyncHttpClient的cookie
    public static void setCookie(String cookie){
        client.addHeader("Cookie",cookie);
    }

    //应用程序的cookie
    private  static String appCookie;

    public static void cleanCookie(){
        appCookie="";
    }

    public static String getCookie(AppContext appContext) {
        if (appCookie == null || appCookie == "") {
            appCookie = appContext.getProperty("cookie");
        }
        return appCookie;
    }

    public static void log(String string){
        Log.d("BaseApi",string);
        TLog.log("Test",string);
    }


}
