package com.teacore.teascript.module.detail.fragment;

import android.text.Editable;
import android.text.TextUtils;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseDetailFragment;
import com.teacore.teascript.bean.Blog;
import com.teacore.teascript.bean.BlogDetail;
import com.teacore.teascript.bean.CommentList;
import com.teacore.teascript.bean.FavoriteList;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.UrlUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;

/**Blog详情fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-8
 */
public class BlogDetailFragment extends BaseDetailFragment<Blog> {

    @Override
    protected String getCacheKey(){
        return "blog_"+mId;
    }

    @Override
    protected void sendRequestDataForNet() {
        TeaScriptApi.getBlogDetail(mId, mDetailHandler);
    }

    @Override
    protected Blog parseData(InputStream inputStream) {
        return XmlUtils.toBean(BlogDetail.class, inputStream).getBlog();
    }

    @Override
    protected String getWebViewBody(Blog detail) {

        StringBuilder body = new StringBuilder();

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

        // 封尾
        body.append("</div></body>");
        return body.toString();
    }

    @Override
    protected void showCommentView() {

        if (mDetail != null) {
            UiUtils.showBlogComment(getActivity(), mId,
                    mDetail.getAuthorId());
        }
    }

    @Override
    protected int getCommentType() {
        return CommentList.CATALOG_MESSAGE;
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
        return String.format(UrlUtils.URL_MOBILE + "blog/%s", mId);
    }

    @Override
    protected int getFavoriteType() {
        return FavoriteList.TYPE_BLOG;
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
        return mDetail.getCommentCount();
    }

    @Override
    public void onClickSendButton(Editable str) {
        if (!TDevice.hasInternet()) {
            AppContext.showToast(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {

            UiUtils.showLoginActivity(getActivity());

            return;
        }
        if (TextUtils.isEmpty(str)) {
            AppContext.showToast(R.string.tip_comment_content_empty);
            return;
        }

        showWaitDialog(R.string.progress_submit);

        TeaScriptApi.pubBlogComment(mId, AppContext.getInstance()
                .getLoginUid(), str.toString(), mCommentHandler);
    }

    @Override
    protected String getReportUrl() {
        return mDetail.getUrl();
    }


}
