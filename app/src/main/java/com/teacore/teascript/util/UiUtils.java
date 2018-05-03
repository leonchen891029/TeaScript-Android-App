package com.teacore.teascript.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Active;
import com.teacore.teascript.bean.Banner;
import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.News;
import com.teacore.teascript.bean.Notice;
import com.teacore.teascript.bean.ShakeObject;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.interfaces.OnDownloadResultListener;
import com.teacore.teascript.interfaces.OnWebViewImageListener;
import com.teacore.teascript.module.back.BackActivity;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.module.back.currencyfragment.BrowserFragment;
import com.teacore.teascript.module.back.currencyfragment.ChatMessageListFragment;
import com.teacore.teascript.module.back.currencyfragment.CommentListFragment;
import com.teacore.teascript.module.back.currencyfragment.PostTagListFragment;
import com.teacore.teascript.module.back.currencyfragment.SoftwareTeatimesListFragment;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.module.general.activity.PreviewImageActivity;
import com.teacore.teascript.module.general.teatime.TeatimeDetailActivity;
import com.teacore.teascript.module.general.detail.activity.BlogDetailActivity;
import com.teacore.teascript.module.general.detail.activity.EventDetailActivity;
import com.teacore.teascript.module.general.detail.activity.NewsDetailActivity;
import com.teacore.teascript.module.general.detail.activity.QuestionDetailActivity;
import com.teacore.teascript.module.general.detail.activity.SoftwareDetailActivity;
import com.teacore.teascript.module.general.detail.activity.TranslationDetailActivity;
import com.teacore.teascript.module.general.fragment.UserBlogFragment;
import com.teacore.teascript.module.login.LoginActivity;
import com.teacore.teascript.module.myinformation.FriendsListFragment;
import com.teacore.teascript.module.myinformation.FriendsViewPagerFragment;
import com.teacore.teascript.module.quickoption.activity.TeatimePubActivity;
import com.teacore.teascript.service.DownloadService;
import com.teacore.teascript.service.NoticeService;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.activity.TeamNewIssueActivity;
import com.teacore.teascript.team.adapter.TeamMemberAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamActive;
import com.teacore.teascript.team.bean.TeamDiscuss;
import com.teacore.teascript.team.bean.TeamIssue;
import com.teacore.teascript.team.bean.TeamIssueCatalog;
import com.teacore.teascript.team.bean.TeamMember;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.team.fragment.TeamActiveListFragment;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.dialog.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;


import java.net.URLDecoder;

/**Ui工具
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-5
 */
public class UiUtils {

    // 链接样式文件，代码块高亮的处理
    public final static String linkCss = "<script type=\"text/javascript\" " +
            "src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/detail_page" +
            ".js\"></script>"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
            + "<script type=\"text/javascript\">function showImagePreview(var url){window" +
            ".location.url= url;}</script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" " +
            "href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore" +
            ".css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common" +
            ".css\">";

    //web的style样式
    public static final String WEB_STYLE=linkCss;

    //web加载所有的图片
    public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var " +
            "allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

    //显示图片
    private static final String SHOWIMAGE = "ima-api:action=showImage&data=";


    //显示登录界面
    public static void showLoginActivity(Context context){
        Intent intent=new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    //显示Team界面
    public static void showTeamMainActivity(Context context) {
        Intent intent = new Intent(context, TeamMainActivity.class);
        context.startActivity(intent);
    }

    //获取一个环形进度条等待窗口
    public static ProgressDialog getProgressDialog(Activity activity,String msg){

        //实例化一个ProgressDialog
        ProgressDialog progressDialog=new ProgressDialog(activity);
        progressDialog.setMessage(msg);
        progressDialog.getWindow().setLayout(DensityUtils.getScreenWidth(activity), DensityUtils.getScreenHeight(activity));
        progressDialog.setCancelable(true);
        //设置ProgressDialog的显示样式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        return progressDialog;
    }

    //显示新闻详情
    public static void showNewsDetailActivity(Context context, long newsId,
                                      int commentCount) {
        /*
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", newsId);
        intent.putExtra("comment_count", commentCount);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_NEWS);
        context.startActivity(intent);
        */
        NewsDetailActivity.show(context, newsId);
    }

    //显示博客详情
    public static void showBlogDetailActivity(Context context, long blogId) {
        BlogDetailActivity.show(context, blogId);
    }

    //显示帖子详情
    public static void showQuestionDetailActivity(Context context, long postId, int count) {

        QuestionDetailActivity.show(context, postId);

    }

    //显示活动详情
    public static void showEventDetailActivity(Context context, long eventId) {
        /*
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", eventId);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_EVENT);
        context.startActivity(intent);
        */
        EventDetailActivity.show(context, eventId);
    }

    //显示相关Tag帖子列表
    public static void showPostListByTag(Context context, String tag) {
        Bundle args = new Bundle();
        args.putString(PostTagListFragment.BUNDLE_KEY_TAG, tag);
        showSimpleBack(context, BackFragmentEnum.QUESTION_POST, args);
    }

    //显示Teatime详情
    public static void showTeatimeDetail(Context context, Teatime Teatime, long Teatimeid) {

        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("Teatime_id", (int) Teatimeid);
        bundle.putInt(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_TEATIME);

        if (Teatime != null) {
            bundle.putParcelable("Teatime", Teatime);
        }

        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //显示软件详情
    public static void showSoftwareDetail(Context context, String ident) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("ident", ident);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_SOFTWARE);
        context.startActivity(intent);
    }

    public static void showSoftwareDetailById(Context context, int id) {
        /*
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("ident", "");
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_SOFTWARE);
        context.startActivity(intent);
        */
        SoftwareDetailActivity.show(context, id);
    }

    //新闻超链接点击跳转
    public static void showNewsRedirect(Context context,News news){

        String url=news.getUrl();

        //如果是活动则直接跳转到活动详情页面
        String eventUrl=news.getNewsType().getEventUrl();
        if(!StringUtils.isEmpty(eventUrl)){
            showEventDetailActivity(context,StringUtils.toInt(news.getNewsType().getAttachment()));
            return;
        }

        //如果url为空
        if (StringUtils.isEmpty(url)) {
            int newsId = news.getId();
            int newsType = news.getNewsType().getType();
            String objId = news.getNewsType().getAttachment();
            switch (newsType) {
                case News.NEWSTYPE_NEWS:
                    showNewsDetailActivity(context, newsId, news.getCommentCount());
                    break;
                case News.NEWSTYPE_SOFTWARE:
                    showSoftwareDetail(context, objId);
                    break;
                case News.NEWSTYPE_POST:
                    showQuestionDetailActivity(context, StringUtils.toInt(objId),
                            news.getCommentCount());
                    break;
                case News.NEWSTYPE_BLOG:
                    showBlogDetailActivity(context, Long.valueOf(objId));
                    break;
                default:
                    break;
            }
        } else {
            showUrlRedirect(context, url);
        }

    }

    //显示detail的总方法
    public static void showDetail(Context context, int type, long id, String href) {
        switch (type) {
            case 0:
                //资讯
                NewsDetailActivity.show(context,id);
                break;
            case 1:
                //软件推荐
                SoftwareDetailActivity.show(context, id);
                break;
            case 2:
                //问答
                QuestionDetailActivity.show(context, id);
                break;
            case 3:
                //博客
                BlogDetailActivity.show(context, id);
                break;
            case 4:
                //4.翻译
                TranslationDetailActivity.show(context, id);
                break;
            case 5:
                //活动
                EventDetailActivity.show(context, id);
                break;
            default:
                showUrlRedirect(context, id, href);
                break;
        }
    }

    public static void showBannerDetail(Context context, Banner banner) {
        long newsId = banner.getId();
        switch (banner.getType()) {
            case Banner.BANNER_TYPE_URL:
                showNewsDetailActivity(context, Integer.parseInt(String.valueOf(newsId)), 0);
                break;
            case Banner.BANNER_TYPE_SOFTWARE:
                showSoftwareDetailById(context, Integer.parseInt(String.valueOf(newsId)));
                break;
            case Banner.BANNER_TYPE_POST:
                showQuestionDetailActivity(context, StringUtils.toInt(String.valueOf(newsId)),
                        0);
                break;
            case Banner.BANNER_TYPE_BLOG:
                showBlogDetailActivity(context, StringUtils.toLong(String.valueOf(newsId)));
                break;
            case Banner.BANNER_TYPE_EVENT:
                EventDetailActivity.show(context, newsId);
                break;
            case Banner.BANNER_TYPE_NEWS:
            default:
                showUrlRedirect(context, banner.getHref());
                break;
        }
    }

    /*
    点击跳转到具体的动态
    Active实体类 0其他 1新闻 2帖子 3动弹 4博客
     */
    public static void showActiveRedirect(Context context, Active active) {
        String url = active.getUrl();
        // url为空-旧方法
        if (StringUtils.isEmpty(url)) {
            int id = active.getObjectId();
            int catalog = active.getCatalog();
            switch (catalog) {
                case Active.CATALOG_OTHER:
                    // 其他-无跳转
                    break;
                case Active.CATALOG_NEWS:
                    showNewsDetailActivity(context, id, active.getCommentCount());
                    break;
                case Active.CATALOG_POST:
                    showQuestionDetailActivity(context, id, active.getCommentCount());
                    break;
                case Active.CATALOG_TEATIME:
                    Teatime Teatime = new Teatime();
                    Teatime.setId(id);
                    TeatimeDetailActivity.show(context, Teatime);
//                    showTeatimeDetail(context, null, id);
                    break;
                case Active.CATALOG_BLOG:
                    showBlogDetailActivity(context, id);
                    break;
                default:
                    break;
            }
        } else {
            showUrlRedirect(context, url);
        }
    }

    //对一个WebView控件进行初始化
    public static void initWebView(WebView webView){
        WebSettings settings=webView.getSettings();

        settings.setDefaultFontSize(14);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);

        int systemVersion= Build.VERSION.SDK_INT;

        if(systemVersion>=11){
            settings.setDisplayZoomControls(false);
        }else{
            ZoomButtonsController zoomButtonsController=new ZoomButtonsController(webView);
            zoomButtonsController.getZoomControls().setVisibility(View.GONE);
        }

    }

    //添加网页的点击图片展示
    @JavascriptInterface
    public static void addWebImageShow(final Context context,WebView webView){
        webView.getSettings().setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new OnWebViewImageListener(){
            @Override
            @JavascriptInterface
            public void showImagePreview(String imageUrl){
                if(imageUrl!=null && !StringUtils.isEmpty(imageUrl)){
                 PreviewImageActivity.showImagePreview(context,imageUrl);
                }
            }
        },"webViewImageListener");


    }

    //读取用户设置:是否加载文章的图片
    public static String setHtmlCotentImagePreview(String body) {

        // 默认有wifi下始终加载图片
        if (AppContext.get(AppConfig.KEY_LOAD_IMAGE, true)
                || TDevice.isWifiOpen()) {

            // 过滤掉 img标签的width,height属性
            body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
            body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");

            // 添加点击图片放大支持
            body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                    "$1$2\" onClick=\"showImagePreview('$2')\"");
        } else {

            // 过滤掉 img标签
            body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");

        }

        // 过滤table的内部属性
        body = body.replaceAll("(<table[^>]*?)\\s+border\\s*=\\s*\\S+", "$1");
        body = body.replaceAll("(<table[^>]*?)\\s+cellspacing\\s*=\\s*\\S+", "$1");
        body = body.replaceAll("(<table[^>]*?)\\s+cellpadding\\s*=\\s*\\S+", "$1");

        return body;
    }

    //摇一摇点击跳转
    public static void showUrlShake(Context context, ShakeObject obj) {

        if (StringUtils.isEmpty(obj.getUrl())) {

            if (ShakeObject.RANDOMTYPE_NEWS.equals(obj.getRandomtype())) {

                UiUtils.showNewsDetailActivity(context,
                        StringUtils.toInt(obj.getId()),
                        StringUtils.toInt(obj.getCommentCount()));

            }
        } else {
            if (!StringUtils.isEmpty(obj.getUrl())) {

                UiUtils.showUrlRedirect(context, obj.getUrl());

            }
        }
    }

    //通过url进行跳转
    public static void showUrlRedirect(Context context,long id,String url){

        if (url == null) {
            if (id != 0) {
                NewsDetailActivity.show(context, id);
            }
            return;
        }

        if (url.contains("city.teascript.net/")) {

            if (id == 0)
                id = StringUtils.toInt(url.substring(url.lastIndexOf('/') + 1));

            UiUtils.showEventDetailActivity(context, id);

            return;
        }

        if (url.startsWith(SHOWIMAGE)) {

            String realUrl = url.substring(SHOWIMAGE.length());

            try {
                JSONObject json = new JSONObject(realUrl);

                int idx = json.optInt("index");

                String[] urls = json.getString("urls").split(",");

                PreviewImageActivity.showImagePreview(context, urls[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        UrlUtils urls = UrlUtils.parseURL(url);

        if (urls != null) {

            showLinkRedirect(context, urls.getObjType(), urls.getObjId() != 0 ? urls.getObjId() : id, urls.getObjKey());

        } else {
            openBrowser(context, url);
        }

    }

    public static void showUrlRedirect(Context context, String url) {
        showUrlRedirect(context, 0, url);
    }

    //通过传入URL的类型判断跳转
    public static void showLinkRedirect(Context context, int objType,
                                        long objId, String objKey) {
        switch (objType) {
            case UrlUtils.URL_OBJ_TYPE_NEWS:
                showNewsDetailActivity(context, objId, -1);
                break;
            case UrlUtils.URL_OBJ_TYPE_QUESTION:
                showQuestionDetailActivity(context, objId, 0);
                break;
            case UrlUtils.URL_OBJ_TYPE_QUESTION_TAG:
                showPostListByTag(context, objKey);
                break;
            case UrlUtils.URL_OBJ_TYPE_SOFTWARE:
                showSoftwareDetail(context, objKey);
                break;
            case UrlUtils.URL_OBJ_TYPE_ZONE:
                showUserCenter(context, objId, objKey);
                break;
            case UrlUtils.URL_OBJ_TYPE_Teatime:
                showTeatimeDetail(context, null, objId);
                break;
            case UrlUtils.URL_OBJ_TYPE_BLOG:
                showBlogDetailActivity(context, objId);
                break;
            case UrlUtils.URL_OBJ_TYPE_OTHER:
                openBrowser(context, objKey);
                break;
            case UrlUtils.URL_OBJ_TYPE_TEAM:
                openSystemBrowser(context, objKey);
                break;
            case UrlUtils.URL_OBJ_TYPE_GIT:
                openSystemBrowser(context, objKey);
                break;
        }
    }

    //打开内置浏览器
    public static void openBrowser(Context context, String url) {

        if (StringUtils.isImgUrl(url)) {

            PreviewImageActivity.showImagePreview(context, url);

            return;
        }

        if (url.startsWith("http://www.oschina.net/Teatime-topic/")) {

            Bundle bundle = new Bundle();

            int i = url.lastIndexOf("/");

            if (i != -1) {

                bundle.putString("topic",
                        URLDecoder.decode(url.substring(i + 1)));

            }

            showSimpleBack(context, BackFragmentEnum.TEATIME_TOPIC,
                    bundle);

            return;
        }

        try {
            // 启用外部浏览器
            // Uri uri = Uri.parse(url);
            // Intent it = new Intent(Intent.ACTION_VIEW, uri);
            // context.startActivity(it);
            Bundle bundle = new Bundle();
            bundle.putString(BrowserFragment.BROWSER_KEY, url);
            showSimpleBack(context, BackFragmentEnum.BROWSER, bundle);
        } catch (Exception e) {
            e.printStackTrace();
            AppContext.showToast("无法浏览此网页");
        }
    }

    //打开系统中内置的浏览器
    public static void openSystemBrowser(Context context,String url){

        try{
            Uri uri=Uri.parse(url);
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            context.startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
            AppContext.showToast("无法浏览此网页");
        }

    }

    public static void showSimpleBackForResult(Fragment fragment,
                                               int requestCode, BackFragmentEnum page, Bundle args) {
        Intent intent = new Intent(fragment.getActivity(),
                BackActivity.class);
        intent.putExtra(BackActivity.BUNDLE_KEY_PAGE, page.getValue());
        intent.putExtra(BackActivity.BUNDLE_KEY_ARGS, args);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void showSimpleBackForResult(Activity context,
                                               int requestCode, BackFragmentEnum page, Bundle args) {
        Intent intent = new Intent(context, BackActivity.class);
        intent.putExtra(BackActivity.BUNDLE_KEY_PAGE, page.getValue());
        intent.putExtra(BackActivity.BUNDLE_KEY_ARGS, args);
        context.startActivityForResult(intent, requestCode);
    }

    public static void showSimpleBackForResult(Activity context,
                                               int requestCode, BackFragmentEnum page) {
        Intent intent = new Intent(context, BackActivity.class);
        intent.putExtra(BackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivityForResult(intent, requestCode);
    }

    public static void showSimpleBack(Context context, BackFragmentEnum page) {
        Intent intent = new Intent(context, BackActivity.class);
        intent.putExtra(BackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

    public static void showSimpleBack(Context context, BackFragmentEnum page,
                                      Bundle args) {
        Intent intent = new Intent(context, BackActivity.class);
        intent.putExtra(BackActivity.BUNDLE_KEY_ARGS, args);
        intent.putExtra(BackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

    //显示动弹页面
    public static void showTeatimeActivity(Context context, int actionType, Bundle bundle) {
        Intent intent = new Intent(context, TeatimePubActivity.class);
        intent.putExtra(TeatimePubActivity.ACTION_TYPE, actionType);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    //显示评论页面
    public static void showComment(Context context, int id, int catalog) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(CommentListFragment.BUNDLE_KEY_ID, id);
        intent.putExtra(CommentListFragment.BUNDLE_KEY_CATALOG, catalog);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_COMMENT);
        context.startActivity(intent);
    }

    //显示软件动弹
    public static void showSoftWareTeatimes(Context context, int id) {
        Bundle args = new Bundle();
        args.putInt(SoftwareTeatimesListFragment.BUNDLE_KEY_SOFTWARE_TEATIME_ID, id);
        showSimpleBack(context, BackFragmentEnum.SOFTWARE_TEATIME, args);
    }

    //显示blog评论
    public static void showBlogComment(Context context, int id, int ownerId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(CommentListFragment.BUNDLE_KEY_ID, id);
        intent.putExtra(CommentListFragment.BUNDLE_KEY_OWNER_ID, ownerId);
        intent.putExtra(CommentListFragment.BUNDLE_KEY_BLOG, true);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_COMMENT);
        context.startActivity(intent);
    }

    public static SpannableString parseActiveAction(int objecttype,
                                                    int objectcatalog, String objecttitle) {
        String title = "";
        int start = 0;
        int end = 0;
        if (objecttype == 32 && objectcatalog == 0) {
            title = "加入了开源中国";
        } else if (objecttype == 1 && objectcatalog == 0) {
            title = "添加了开源项目 " + objecttitle;
        } else if (objecttype == 2 && objectcatalog == 1) {
            title = "在讨论区提问：" + objecttitle;
        } else if (objecttype == 2 && objectcatalog == 2) {
            title = "发表了新话题：" + objecttitle;
        } else if (objecttype == 3 && objectcatalog == 0) {
            title = "发表了博客 " + objecttitle;
        } else if (objecttype == 4 && objectcatalog == 0) {
            title = "发表一篇新闻 " + objecttitle;
        } else if (objecttype == 5 && objectcatalog == 0) {
            title = "分享了一段代码 " + objecttitle;
        } else if (objecttype == 6 && objectcatalog == 0) {
            title = "发布了一个职位：" + objecttitle;
        } else if (objecttype == 16 && objectcatalog == 0) {
            title = "在新闻 " + objecttitle + " 发表评论";
        } else if (objecttype == 17 && objectcatalog == 1) {
            title = "回答了问题：" + objecttitle;
        } else if (objecttype == 17 && objectcatalog == 2) {
            title = "回复了话题：" + objecttitle;
        } else if (objecttype == 17 && objectcatalog == 3) {
            title = "在 " + objecttitle + " 对回帖发表评论";
        } else if (objecttype == 18 && objectcatalog == 0) {
            title = "在博客 " + objecttitle + " 发表评论";
        } else if (objecttype == 19 && objectcatalog == 0) {
            title = "在代码 " + objecttitle + " 发表评论";
        } else if (objecttype == 20 && objectcatalog == 0) {
            title = "在职位 " + objecttitle + " 发表评论";
        } else if (objecttype == 101 && objectcatalog == 0) {
            title = "回复了动态：" + objecttitle;
        } else if (objecttype == 100) {
            title = "更新了动态";
        }
        SpannableString sp = new SpannableString(title);
        // 设置标题字体大小、高亮
        if (!StringUtils.isEmpty(objecttitle)) {
            start = title.indexOf(objecttitle);
            if (objecttitle.length() > 0 && start > 0) {
                end = start + objecttitle.length();
                sp.setSpan(new AbsoluteSizeSpan(14, true), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sp.setSpan(
                        new ForegroundColorSpan(Color.parseColor("#0e5986")),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return sp;
    }

    //动态的回复文本
    public static SpannableStringBuilder parseActiveReply(String name,
                                                          String body) {
        Spanned span = Html.fromHtml(body.trim());
        SpannableStringBuilder sp = new SpannableStringBuilder(name + "：");
        sp.append(span);
        // 设置用户名字体加粗、高亮
        // sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
        // name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0,
                name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sp;
    }

    //发送app异常崩溃的报告
    public static void sendAppCrashReport(final Context context) {

        DialogUtils.getConfirmDialog(context, "程序发生异常", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 退出
                //   System.exit(-1);
            }
        }).show();
    }

    //发送通知广播
    public static void sendBroadcastForNotice(Context context,Notice notice){

        if(!((AppContext) context.getApplicationContext()).isLogin() || notice==null){
            return;
        }

        Intent intent=new Intent(Constants.INTENT_ACTION_NOTICE);
        Bundle bundle=new Bundle();
        bundle.putSerializable("notice_bean",notice);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

    public static void sendBroadcastForNotice(Context context){
        Intent intent=new Intent(NoticeService.INTENT_ACTION_BROADCAST);
        context.sendBroadcast(intent);
    }

    //显示用户中心页面
    public static void showUserCenter(Context context, long uid,
                                      String username) {
        if (uid == 0 && username.equalsIgnoreCase("匿名")) {
            AppContext.showToast("提醒你，该用户为非会员");
            return;
        }
        Bundle args = new Bundle();
        args.putInt("his_id", (int) uid);
        args.putString("his_name", username);
        showSimpleBack(context, BackFragmentEnum.USER_CENTER, args);
    }

    //显示用户的博客列表
    public static void showUserBlog(Context context,int uid){
        Bundle args=new Bundle();
        args.putInt(UserBlogFragment.USER_ID, uid);
        showSimpleBack(context, BackFragmentEnum.USER_BLOG, args);
    }

    //显示用户头像大图
    public static void showUserAvatar(Context context,String avatarUrl){
        if(StringUtils.isEmpty(avatarUrl)){
            return;
        }

        String url= AvatarView.getLargeAvatar(avatarUrl);

        PreviewImageActivity.showImagePreview(context, url);
    }

    //显示用户的个人信息页面
    public static void showMyInformation(Context context){
        showSimpleBack(context, BackFragmentEnum.MY_INFORMATION);
    }

    //显示我的所有的动态
    public static void showUserActive(Context context){
        showSimpleBack(context, BackFragmentEnum.ACTIVE);
    }

    //显示扫一扫页面
    public static void showScanActivity(Context context){
        Intent intent=new Intent(context, com.dtr.zxing.activity.CaptureActivity.class);
        context.startActivity(intent);
    }

    //显示用户收藏页面
    public static void showUserFavorite(Context context,int uid){
        Bundle bundle=new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG,uid);
        showSimpleBack(context, BackFragmentEnum.USER_FAVORITE);
    }

    //显示用户的关注/粉丝列表
    public static void showFriends(Context context, int uid, int tabIdx) {
        Bundle args = new Bundle();
        args.putInt(FriendsViewPagerFragment.BUNDLE_KEY_TABIDX, tabIdx);
        args.putInt(FriendsListFragment.BUNDLE_KEY_UID, uid);
        showSimpleBack(context, BackFragmentEnum.USER_FRIENDS, args);
    }

    //显示留言对话页面
    public static void showMessageDetail(Context context, int friendid,
                                         String friendname) {
        Bundle args = new Bundle();
        args.putInt(ChatMessageListFragment.BUNDLE_KEY_FID, friendid);
        args.putString(ChatMessageListFragment.BUNDLE_KEY_FNAME, friendname);
        showSimpleBack(context, BackFragmentEnum.CHAT_MESSAGE_DETAIL, args);
    }

    //显示关于界面
    public static void showAboutTeaScript(Context context) {
        showSimpleBack(context, BackFragmentEnum.ABOUT_TEASCRIPT);
    }

    //清除app缓存
    public static void cleanAppCache(Activity activity){

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    AppContext.showToast("缓存清除成功");
                } else {
                    AppContext.showToast("缓存清除失败");
                }
            }
        };
        new Thread() {
            @Override
            public void run() {

                Message msg = new Message();

                try {
                    AppContext.getInstance().cleanAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }

                handler.sendMessage(msg);
            }
        }.start();

    }

    //开启下载服务
    public static void openDownLoadService(Context context, String downurl,
                                           String tilte) {
        final OnDownloadResultListener callback = new OnDownloadResultListener() {

            @Override
            public void OnBackResult(Object s) {
            }
        };
        ServiceConnection conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
                binder.addCallback(callback);
                binder.start();

            }
        };

        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, downurl);
        intent.putExtra(DownloadService.BUNDLE_KEY_TITLE, tilte);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    //发送广播通知评论发送变化
    public static void sendBroadcastCommentChanged(Context context,
                                                   boolean isBlog, int id, int catalog, int
                                                           operation,
                                                   Comment replyComment) {
        Intent intent = new Intent(Constants.INTENT_ACTION_COMMENT_CHANGE);
        Bundle args = new Bundle();
        args.putInt(Comment.BUNDLE_KEY_ID, id);
        args.putInt(Comment.BUNDLE_KEY_CATALOG, catalog);
        args.putBoolean(Comment.BUNDLE_KEY_BLOG, isBlog);
        args.putInt(Comment.BUNDLE_KEY_OPERATION, operation);
        args.putParcelable(Comment.BUNDLE_KEY_COMMENT, replyComment);
        intent.putExtras(args);
        context.sendBroadcast(intent);
    }

    //创建新的主题
    public static void showCreateNewIssue(Context context, Team team,
                                          TeamProject project, TeamIssueCatalog catalog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("Team", team);
        if (project != null) {
            bundle.putSerializable("project", project);
        }
        if (catalog != null) {
            bundle.putSerializable("catalog", catalog);
        }
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(context, TeamNewIssueActivity.class);
        context.startActivity(intent);
    }

    //显示任务详情
    public static void showTeamIssueDetail(Context context, Team team,
                                           TeamIssue issue, TeamIssueCatalog catalog) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("teamid", team.getId());
        bundle.putInt("issueid", issue.getId());
        bundle.putSerializable("Team", team);
        bundle.putSerializable("issue", issue);
        bundle.putSerializable("issue_catalog", catalog);
        bundle.putInt(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_TEAM_ISSUE_DETAIL);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //显示讨论帖详情
    public static void showTeamDiscussDetail(Context context, Team team,
                                             TeamDiscuss discuss) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("teamid", team.getId());
        bundle.putInt("discussid", discuss.getId());
        bundle.putInt(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_TEAM_DISCUSS_DETAIL);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //显示周报详情
    public static void showDiaryDetail(Context context, Bundle data) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("diary", data);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_TEAM_DIARY);
        context.startActivity(intent);
    }

    //显示团队成员信息
    public static void showTeamMemberInfo(Context context, int teamId,
                                          TeamMember teamMember) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TeamMemberAdapter.TEAM_MEMBER_KEY, teamMember);
        bundle.putInt(TeamMemberAdapter.TEAM_ID_KEY, teamId);
        showSimpleBack(context, BackFragmentEnum.TEAM_USER_INFO, bundle);
    }

    //显示团队动态详情
    public static void showTeamActiveDetail(Context contex, int teamId,
                                            TeamActive active) {
        Intent intent = new Intent(contex, DetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(TeamActiveListFragment.ACTIVE_FRAGMENT_KEY, active);
        bundle.putInt(TeamActiveListFragment.ACTIVE_FRAGMENT_TEAM_KEY, teamId);
        bundle.putInt(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_TEAM_TEATIME_DETAIL);
        intent.putExtras(bundle);
        contex.startActivity(intent);
    }

}
