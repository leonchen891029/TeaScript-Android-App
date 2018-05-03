package com.teacore.teascript.module.detail.fragment;

import android.text.Editable;
import android.text.TextUtils;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseDetailFragment;
import com.teacore.teascript.bean.FavoriteList;
import com.teacore.teascript.bean.Software;
import com.teacore.teascript.bean.SoftwareDetail;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.UrlUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 软件详情
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-20
 */

public class SoftwareDetailFragment extends BaseDetailFragment<Software> {

    private String mIdentity;

    @Override
    protected String getCacheKey() {
        if (TextUtils.isEmpty(mIdentity)) {
            return "software_" + mId;
        }
        return "software_" + mIdentity;
    }

    @Override
    protected void sendRequestDataForNet() {
        // 通过id来获取软件详情
        if (mId > 0) {
            TeaScriptApi.getSoftwareDetail(mId, mDetailHandler);
            return;
        }

        if (TextUtils.isEmpty(mIdentity)) {
            executeOnLoadDataError();
            return;
        }

        TeaScriptApi.getSoftwareDetail(mIdentity, mDetailHandler);
    }

    @Override
    public void initData() {
        super.initData();
        mIdentity = getActivity().getIntent().getStringExtra("ident");
        if (TextUtils.isEmpty(mIdentity)) {
            return;
        }
        try {
            mIdentity = URLEncoder.encode(mIdentity, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Software parseData(InputStream is) {
        return XmlUtils.toBean(SoftwareDetail.class, is).getSoftware();
    }

    @Override
    protected String getWebViewBody(Software detail) {
        mId = detail.getId();
        if (TextUtils.isEmpty(detail.getBody())) {
            return "";
        }
        StringBuffer body = new StringBuffer();
        body.append(ThemeSwitchUtils.getWebViewBodyString());
        body.append(UiUtils.WEB_STYLE).append(UiUtils.WEB_LOAD_IMAGES);
        // 添加title
        String title = "";
        // 判断是否推荐
        if (mDetail.getRecommended() == 4) {
            title = String.format("<div class='title'><img src=\"%s\"/>%s %s <img class='recommend' src=\"%s\"/></div>", mDetail.getLogo(), mDetail.getExtensionTitle(), mDetail.getTitle(), "file:///android_asset/ic_soft_recommend.png");
        } else {
            title = String.format("<div class='title'><img src=\"%s\"/>%s %s</div>", mDetail.getLogo(), mDetail.getExtensionTitle(), mDetail.getTitle());
        }
        body.append(title);
        // 添加图片点击放大支持
        body.append(UiUtils.setHtmlCotentImagePreview(mDetail.getBody()));

        // 软件信息
        body.append("<div class='software_attr'>");
        if (!TextUtils.isEmpty(mDetail.getAuthor())) {
            String author = String.format("<a class='author' href='http://my.oschina.net/u/%s'>%s</a>", mDetail.getAuthorId(), mDetail.getAuthor());
            body.append(String.format("<li class='software'>软件作者:&nbsp;&nbsp;%s</li>", author));
        }
        body.append(String.format("<li class='software'>开源协议:&nbsp;&nbsp;%s</li>", mDetail.getLicense()));
        body.append(String.format("<li class='software'>开发语言:&nbsp;&nbsp;%s</li>", mDetail.getLanguage()));
        body.append(String.format("<li class='software'>操作系统:&nbsp;&nbsp;%s</li>", mDetail.getOs()));
        body.append(String.format("<li class='software'>收录时间:&nbsp;&nbsp;%s</li>", mDetail.getRecordtime()));
        body.append("</div>");

        // 软件的首页、文档、下载
        body.append("<div class='software_urls'>");
        if (!TextUtils.isEmpty(mDetail.getHomepage())) {
            body.append(String.format("<li class='software'><a href='%s'>软件首页</a></li>", mDetail.getHomepage()));
        }
        if (!TextUtils.isEmpty(mDetail.getDocument())) {
            body.append(String.format("<li class='software'><a href='%s'>软件文档</a></li>", mDetail.getDocument()));
        }
        if (!TextUtils.isEmpty(mDetail.getDownload())) {
            body.append(String.format("<li class='software'><a href='%s'>软件下载</a></li>", mDetail.getDownload()));
        }
        body.append("</div>");
        // 封尾
        body.append("</div></body>");
        return body.toString();
    }

    @Override
    protected void showCommentView() {
        if (mDetail != null)
            UiUtils.showSoftWareTeatimes(getActivity(), mDetail.getId());
    }

    @Override
    public void onClickSendButton(Editable str) {
        if (mDetail.getId() == 0) {
            AppContext.showToast("无法获取该软件~");
            return;
        }
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
        Teatime teatime = new Teatime();
        teatime.setAuthorId(AppContext.getInstance().getLoginUid());
        teatime.setBody(str.toString());
        showWaitDialog(R.string.progress_submit);
        TeaScriptApi.pubSoftWareTeatime(teatime, mDetail.getId(), mCommentHandler);
    }

    @Override
    protected int getCommentType() {
        return 0;
    }

    @Override
    protected int getFavoriteType() {
        return FavoriteList.TYPE_SOFTWARE;
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
        return mDetail.getTeatimeCount();
    }

    @Override
    protected String getShareTitle() {
        return String.format("%s %s", mDetail.getExtensionTitle(), mDetail.getTitle());
    }

    @Override
    protected String getShareContent() {
        return StringUtils.getSubstring(0, 55,
                getFilterHtmlBody(mDetail.getBody()));
    }

    @Override
    protected String getShareUrl() {
        return String.format(UrlUtils.URL_MOBILE + "p/%s", mIdentity);
    }






}
