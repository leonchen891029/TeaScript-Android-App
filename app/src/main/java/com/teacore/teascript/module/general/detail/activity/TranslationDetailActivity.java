package com.teacore.teascript.module.general.detail.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.bean.Comment;
import com.teacore.teascript.module.general.bean.TranslationDetail;
import com.teacore.teascript.module.general.detail.constract.TranslationDetailContract;
import com.teacore.teascript.module.general.detail.fragment.DetailFragment;
import com.teacore.teascript.module.general.detail.fragment.TranslationDetailFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UrlUtils;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * TranslationDetailActivity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-27
 */

public class TranslationDetailActivity extends DetailActivity<TranslationDetail,TranslationDetailContract.TransView>
       implements TranslationDetailContract.TransPresenter{

    public static void show(Context context,long id){
        Intent intent=new Intent(context,TranslationDetailActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }

    protected int getType(){
        return 4;
    }

    @Override
    protected Type getDataType(){
        return new TypeToken<ResultBean<TranslationDetail>>(){}.getType();
    }

    @Override
    protected void requestData(){
        TeaScriptApi.getNewsDetail(getDataId(), TeaScriptApi.CATALOG_TRANSLATE_DETAIL,getRequestHandler());
    }

    @Override
    protected Class<? extends DetailFragment> getDataViewFragment(){
        return TranslationDetailFragment.class;
    }

    @Override
    public void toFavorite() {
        int uid = requestCheck();
        if (uid == 0)
            return;
        showWaitDialog(R.string.progress_submit);
        final TranslationDetail translationDetail = getData();
        TeaScriptApi.getFavReverse(getDataId(), getType(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                hideWaitDialog();
                if (translationDetail.isFavorite())
                    AppContext.showToast(R.string.del_favorite_fail);
                else
                    AppContext.showToast(R.string.add_favorite_fail);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<TranslationDetail>>() {
                    }.getType();

                    ResultBean<TranslationDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        translationDetail.setFavorite(!translationDetail.isFavorite());
                        mView.toFavoriteOk(translationDetail);
                        if (translationDetail.isFavorite())
                            AppContext.showToast(R.string.add_favorite_success);
                        else
                            AppContext.showToast(R.string.del_favorite_success);
                    }
                    hideWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    @Override
    public void toShare() {
        if (getDataId() != 0 && getData() != null) {
            String content;

            String url = String.format(UrlUtils.URL_MOBILE + "translation/%s", getDataId());
            final TranslationDetail translationDetail = getData();
            if (translationDetail.getBody().length() > 55) {
                content = HtmlUtils.delHTMLTag(translationDetail.getBody().trim());
                if (content.length() > 55)
                    content = StringUtils.getSubstring(0, 55, content);
            } else {
                content = HtmlUtils.delHTMLTag(translationDetail.getBody().trim());
            }
            String title = translationDetail.getTitle();

            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(content) || TextUtils.isEmpty(title)) {
                AppContext.showToast("内容加载失败...");
                return;
            }
            toShare(title, content, url);
        } else {
            AppContext.showToast("内容加载失败...");
        }
    }


    @Override
    public void toSendComment(long id, long commentId, long commentAuthorId, String comment) {
        int uid = requestCheck();
        if (uid == 0)
            return;

        if (TextUtils.isEmpty(comment)) {
            AppContext.showToast(R.string.tip_comment_content_empty);
            return;
        }
        TeaScriptApi.pubTranslateComment(id, commentId, commentAuthorId, comment, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaitDialog(R.string.progress_submit);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast("评论失败!");
                hideWaitDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Comment>>() {
                    }.getType();

                    ResultBean<Comment> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        Comment respComment = resultBean.getResult();
                        if (respComment != null) {
                            if (mView != null) {
                                mView.toSendCommentOk(respComment);
                            }
                        }
                    }
                    hideWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
                hideWaitDialog();
            }
        });

    }

}



