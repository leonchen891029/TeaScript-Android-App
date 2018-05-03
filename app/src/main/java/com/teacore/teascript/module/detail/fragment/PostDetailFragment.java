package com.teacore.teascript.module.detail.fragment;

import com.teacore.teascript.base.BaseDetailFragment;
import com.teacore.teascript.bean.CommentList;
import com.teacore.teascript.bean.FavoriteList;
import com.teacore.teascript.bean.Post;
import com.teacore.teascript.bean.PostDetail;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.UrlUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.net.URLEncoder;

/**帖子的Detail类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-8
 */

public class PostDetailFragment extends BaseDetailFragment<Post>{

    @Override
    protected String getCacheKey() {
        return "post_" + mId;
    }

    @Override
    protected void sendRequestDataForNet() {
        TeaScriptApi.getPostDetail(mId, mDetailHandler);
    }

    @Override
    protected Post parseData(InputStream is) {
        return XmlUtils.toBean(PostDetail.class, is).getPost();
    }

    @Override
    protected String getWebViewBody(Post detail) {

        StringBuffer body = new StringBuffer();

        body.append(UiUtils.WEB_STYLE).append(UiUtils.WEB_LOAD_IMAGES);

        body.append(ThemeSwitchUtils.getWebViewBodyString());

        // 添加title
        body.append(String.format("<div class='title'>%s</div>", mDetail.getTitle()));

        // 添加作者和时间
        String time = TimeUtils.friendly_time(mDetail.getPubDate());
        String author = String.format("<a class='author' href='http://my.oschina.net/u/%s'>%s</a>", mDetail.getAuthorId(), mDetail.getAuthor());
        body.append(String.format("<div class='authortime'>%s&nbsp;&nbsp;&nbsp;&nbsp;%s</div>", author, time));

        // 添加图片点击放大支持
        body.append(UiUtils.setHtmlCotentImagePreview(mDetail.getBody()));
        body.append(getPostTags(mDetail.getTags()));

        // 封尾
        body.append("</div></body>");
        return body.toString();
    }

    @SuppressWarnings("deprecation")
    private String getPostTags(Post.Tags taglist) {
        if (taglist == null)
            return "";
        StringBuffer tags = new StringBuffer();
        for (String tag : taglist.getTags()) {
            tags.append(String
                    .format("<a class='tag' href='http://www.oschina.net/question/tag/%s' >&nbsp;%s&nbsp;</a>&nbsp;&nbsp;",
                            URLEncoder.encode(tag), tag));
        }
        return String.format("<div style='margin-top:10px;'>%s</div>", tags);
    }

    @Override
    protected void showCommentView() {
        if (mDetail != null) {
            UiUtils.showComment(getActivity(), mId, CommentList.CATALOG_POST);
        }
    }

    @Override
    protected int getCommentType() {
        return CommentList.CATALOG_POST;
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
        return  String.format(UrlUtils.URL_MOBILE + "question/%s_%s", mDetail.getAuthorId(), mId);
    }

    @Override
    protected int getFavoriteType() {
        return FavoriteList.TYPE_POST;
    }

    @Override
    protected int getFavoriteState() {
        return mDetail.getFavorite();
    }

    @Override
    protected void updateFavoriteChanged(int newFavoritedState) {
        mDetail.setFavorite(newFavoritedState);
        saveCache(mDetail);
    }

    @Override
    protected int getCommentCount() {
        return mDetail.getAnswerCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DetailActivity) getActivity()).toolbarFragment.showReportButton();
    }

    @Override
    protected String getReportUrl() {
        return mDetail.getUrl();
    }



}
