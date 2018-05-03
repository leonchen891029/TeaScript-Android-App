package com.teacore.teascript.module.general.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.interfaces.OnWebViewImageListener;
import com.teacore.teascript.module.general.activity.PreviewImageActivity;
import com.teacore.teascript.module.general.app.AppOperator;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**自定义WebView
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-14
 */

public class TWebView extends WebView{

    public TWebView(Context context){
        super(context);
        init();
    }


    public TWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void init(){
        setClickable(false);
        setFocusable(false);
        setHorizontalScrollBarEnabled(false);

        //获取WebSetting对象进行设置
        WebSettings settings=getSettings();
        settings.setDefaultFontSize(14);
        //禁止缩放操作
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        //与JavaScript交互
        settings.setJavaScriptEnabled(true);

        /*
        添加可以被JavaScript文件调用的方法
        1.被调用的方法上面必须加上@JavascriptInterface注解
        2."mWebViewImageListener"是js的对象名
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addJavascriptInterface(new OnWebViewImageListener() {

                @Override
                @JavascriptInterface
                public void showImagePreview(String bigImageUrl) {

                    if (bigImageUrl != null && !StringUtils.isEmpty(bigImageUrl)) {

                        PreviewImageActivity.showImagePreview(getContext(), bigImageUrl);



                    }
                }
            }, "mWebViewImageListener");
        }


    }

    //加载详情数据时调用的异步方法
    public void loadDetailDataAsync(final String content,Runnable finishCallback){

        //设置TWebClient对象
        this.setWebViewClient(new TWebViewClient(finishCallback));

        //因为WebView是这样创建的WebView webView=new WebView(Context context);
        Context context=getContext();

        //如果context不是null且为activity
        if(context !=null && context instanceof Activity){

            final Activity activity=(Activity) context;

            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {

                    //初始化具体的web内容
                    final String body=setupWebContent(content,true,true,"");
                    //加载数据
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDataWithBaseURL("",body,"text/html","UTF-8","");
                        }
                    });

                }
            });

        }else {
            Log.e(TWebView.class.getName(),"Context Error");
        }

    }

    //加载Teatime信息的异步方法
    public void loadTeatimeDataAsync(final String content, Runnable finishCallback) {

        this.setWebViewClient(new TWebViewClient(finishCallback));

        Context context = getContext();

        if (context != null && context instanceof Activity) {

            final Activity activity = (Activity) context;

            AppOperator.runOnThread(new Runnable() {

                @Override
                public void run() {

                    final String body = setupWebContent(content, "");

                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            //loadData(body, "text/html; charset=UTF-8", null);
                            loadDataWithBaseURL("", body, "text/html", "UTF-8", "");
                        }
                    });
                }
            });
        } else {
            Log.e(TWebView.class.getName(),"Context error");
        }
    }

    //私有函数，初始化web内容
    private static String setupWebContent(String content, String style) {

        //判断内容是否为空
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim()))
            return "";

        if (AppContext.get(AppConfig.KEY_LOAD_IMAGE, true)
                || TDevice.isWifiOpen()) {

            Pattern pattern = Pattern.compile("<img[^>]+src\\s*=\\s*[\"\']([^\"\']*)[\"\'](\\s*data-url\\s*=\\s*[\"\']([^\"\']*)[\"\'])*");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String snippet = String.format(
                        "<img src=\"%s\" onClick=\"javascript:mWebViewImageListener.showImagePreview('%s')\"",
                        matcher.group(1),
                        matcher.group(3) == null ? matcher.group(1) : matcher.group(3));
                content = content.replace(matcher.group(0), snippet);
            }

        } else {
            // 过滤掉img标签
            content = content.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        }

        return String.format(
                "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/detail.css\">"
                        + "</head>"
                        + "<body>"
                        + "<div class='contentstyle' id='article_id' style='%s'>"
                        + "%s"
                        + "</div>"
                        + "</body>"
                        + "</html>"
                , style == null ? "" : style, content);
    }

    private static String setupWebContent(String content, boolean isShowHighlight, boolean isShowImagePreview, String css) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim()))
            return "";

        // 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
        if (AppContext.get(AppConfig.KEY_LOAD_IMAGE, true)
                || TDevice.isWifiOpen()) {

            // 过滤掉 img标签的width,height属性
            content = content.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
            content = content.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

            // 添加点击图片放大支持
            if (isShowImagePreview) {
                content = content.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                        "$1$2\" onClick=\"javascript:mWebViewImageListener.showImagePreview('$2')\"");
            }
        } else {
            // 过滤掉 img标签
            content = content.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        }

        // 过滤table的内部属性
        content = content.replaceAll("(<table[^>]*?)\\s+border\\s*=\\s*\\S+", "$1");
        content = content.replaceAll("(<table[^>]*?)\\s+cellspacing\\s*=\\s*\\S+", "$1");
        content = content.replaceAll("(<table[^>]*?)\\s+cellpadding\\s*=\\s*\\S+", "$1");


        return String.format(
                "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + (isShowHighlight ? "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/shCore.css\">" : "")
                        + (isShowHighlight ? "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/shThemeDefault.css\">" : "")
                        + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/teatime.css\">"
                        + "%s"
                        + "</head>"
                        + "<body>"
                        + "<div class='body-content'>"
                        + "%s"
                        + "</div>"
                        + (isShowHighlight ? "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>" : "")
                        + (isShowHighlight ? "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>" : "")
                        + (isShowHighlight ? "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>" : "")
                        + "</body>"
                        + "</html>"
                , (css == null ? "" : css), content);
    }

    private static class TWebViewClient extends WebViewClient implements Runnable {

        private Runnable mFinishCallback;

        private boolean mDone = false;

        TWebViewClient(Runnable finishCallback) {
            super();
            mFinishCallback = finishCallback;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mDone = false;
            // 当webview加载2秒后强制回馈完成
            view.postDelayed(this, 2800);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            run();
        }

        @Override
        public synchronized void run() {
            if (!mDone) {
                mDone = true;
                if (mFinishCallback != null) mFinishCallback.run();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            UiUtils.showUrlRedirect(view.getContext(), url);
            return true;
        }
    }

    @Override
    public void destroy() {
        setWebViewClient(null);

        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(false);

        removeJavascriptInterface("mWebViewImageListener");
        removeAllViewsInLayout();

        removeAllViews();
        //clearCache(true);

        super.destroy();
    }

}
