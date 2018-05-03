package com.teacore.teascript.module.detail.fragment;

import com.teacore.teascript.base.BaseDetailFragment;
import com.teacore.teascript.bean.CommentList;
import com.teacore.teascript.bean.FavoriteList;
import com.teacore.teascript.bean.News;
import com.teacore.teascript.bean.NewsDetail;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;

/**新闻详情Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-22
 */

public class NewsDetailFragment extends BaseDetailFragment<News>{

    @Override
    protected String getCacheKey(){
        return "news_"+mId;
    }

    //从网络中读取数据，得到的是NewsDetail这个类
    @Override
    protected void sendRequestDataForNet(){
        TeaScriptApi.getNewsDetail(mId,mDetailHandler);
    }

    @Override
    protected News parseData(InputStream inputStream) {
        return XmlUtils.toBean(NewsDetail.class, inputStream).getNews();
    }

    //获取WebView Body
    @Override
    protected String getWebViewBody(News detail) {

        StringBuffer body = new StringBuffer();

        //script
        body.append(UiUtils.WEB_STYLE).append(UiUtils.WEB_LOAD_IMAGES);

        //根据是否是夜间模式改变body class
        body.append(ThemeSwitchUtils.getWebViewBodyString());

        // 添加title
        body.append(String.format("<div class='title'>%s</div>", mDetail.getTitle()));

        // 添加时间
        String time = TimeUtils.friendly_time(mDetail.getPubDate());

        //添加作者
        String author = String.format("<a class='author' href='http://my.oschina.net/u/%s'>%s</a>", mDetail.getAuthorId(), mDetail.getAuthor());
        body.append(String.format("<div class='authortime'>%s&nbsp;&nbsp;&nbsp;&nbsp;%s</div>", author, time));

        // 添加图片点击放大支持
        body.append(UiUtils.setHtmlCotentImagePreview(mDetail.getBody()));


        // 更多关于***软件的信息
        String softwareName = mDetail.getSoftwareName();
        String softwareLink = mDetail.getSoftwareLink();

        if (!StringUtils.isEmpty(softwareName)
                && !StringUtils.isEmpty(softwareLink))

            body.append(String
                    .format("<div class='oschina_software' style='margin-top:8px;font-weight:bold'>更多关于:&nbsp;<a href='%s'>%s</a>&nbsp;的详细信息</div>",
                            softwareLink, softwareName));

        // 相关新闻
        if (mDetail != null && mDetail.getRelatives() != null
                && mDetail.getRelatives().size() > 0) {

            String strRelative = "";

            for (News.Relative relative : mDetail.getRelatives()) {
                strRelative += String.format(
                        "<li><a href='%s' style='text-decoration:none'>%s</a></li>",
                        relative.url, relative.title);
            }

            body.append("<p/><div style=\"height:1px;width:100%;background:#DADADA;margin-bottom:10px;\"/>"
                    + String.format("<br/> <b>相关资讯</b><ul class='about'>%s</ul>",
                    strRelative));
        }

        body.append("<br/>");
        // 封尾
        body.append("</div></body>");
        return  body.toString();

    }

    @Override
    protected void showCommentView(){
        if(mDetail!=null){
            UiUtils.showComment(getActivity(),mId, CommentList.CATALOG_NEWS);
        }
    }

    @Override
    protected int getCommentType() {
        return CommentList.CATALOG_NEWS;
    }

    @Override
    protected int getFavoriteType() {
        return FavoriteList.TYPE_NEWS;
    }

    @Override
    protected int getFavoriteState() {
        return mDetail.getFavorite();
    }

    @Override
    protected String getShareTitle() {
        return mDetail.getTitle();
    }

    @Override
    protected String getShareContent() {
        return StringUtils.getSubstring(0, 55,
                getFilterHtmlBody(mDetail.getBody()));
    }

    @Override
    protected String getShareUrl() {
        return mDetail.getUrl().replace("http://www", "http://m");
    }

    @Override
    protected void updateFavoriteChanged(int newFavoritedState) {
        mDetail.setFavorite(newFavoritedState);
        saveCache(mDetail);
    }

    @Override
    protected int getCommentCount() {
        return mDetail.getCommentCount();
    }

}
