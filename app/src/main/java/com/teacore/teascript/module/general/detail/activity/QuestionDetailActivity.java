package com.teacore.teascript.module.general.detail.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Report;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.bean.CommentQ;
import com.teacore.teascript.module.general.bean.QuestionDetail;
import com.teacore.teascript.module.general.detail.constract.QuestionDetailContract;
import com.teacore.teascript.module.general.detail.fragment.DetailFragment;
import com.teacore.teascript.module.general.detail.fragment.QuestionDetailFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UrlUtils;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * QuestionDetailActivity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-25
 */

public class QuestionDetailActivity extends DetailActivity<QuestionDetail,QuestionDetailContract.QuestionView>
             implements QuestionDetailContract.QuestionPresenter{

    public static void show(Context context,long id){
        Intent intent=new Intent(context,QuestionDetailActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }

    @Override
    protected int getOptionsMenuId(){
        return R.menu.menu_detail_report;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==R.id.menu_item_report){
            toReport();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void requestData(){
        TeaScriptApi.getQuestionDetail(getDataId(),getRequestHandler());
    }

    protected int getType(){
        return 2;
    }

    @Override
    protected Class<? extends DetailFragment> getDataViewFragment(){
        return QuestionDetailFragment.class;
    }

    @Override
    public void toFavorite(){

        int uId=requestCheck();

        if(uId==0){
            return;
        }

        showWaitDialog(R.string.progress_submit);

        final QuestionDetail questionDetail=getData();

        TeaScriptApi.getFavReverse(getDataId(), getType(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                hideWaitDialog();
                if (questionDetail == null)
                    return;
                if (questionDetail.isFavorite()) {
                    AppContext.showToast(R.string.del_favorite_fail);
                }else{
                    AppContext.showToast(R.string.add_favorite_fail);
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    Type type = new TypeToken<ResultBean<QuestionDetail>>() {
                    }.getType();

                    ResultBean<QuestionDetail> resultBean = AppContext.createGson().fromJson(s, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        questionDetail.setFavorite(!questionDetail.isFavorite());
                        mView.toFavoriteOk(questionDetail);
                        if (questionDetail.isFavorite())
                            AppContext.showToast(R.string.add_favorite_success);
                        else
                            AppContext.showToast(R.string.del_favorite_success);
                    }
                    hideWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(i, headers, s, e);
                }
            }
        });
    }

    @Override
    protected Type getDataType(){
        return new TypeToken<ResultBean<QuestionDetail>>(){}.getType();
    }

    @Override
    public void toShare() {
        if (getDataId() != 0 && getData() != null) {
            String content;

            String url = String.format(UrlUtils.URL_MOBILE + "question/%s", getDataId());
            final QuestionDetail blogDetail = getData();
            if (blogDetail.getBody().length() > 55) {
                content = HtmlUtils.delHTMLTag(blogDetail.getBody().trim());
                if (content.length() > 55)
                    content = StringUtils.getSubstring(0, 55, content);
            } else {
                content = HtmlUtils.delHTMLTag(blogDetail.getBody().trim());
            }
            String title = blogDetail.getTitle();

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

        TeaScriptApi.publishQuestionComment(id, commentId, commentAuthorId, comment, new TextHttpResponseHandler() {

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
                    Type type = new TypeToken<ResultBean<CommentQ>>() {
                    }.getType();

                    ResultBean<CommentQ> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        CommentQ commentQ = resultBean.getResult();
                        if (commentQ != null) {
                            if (mView != null) {
                                mView.toSendCommentOk(commentQ);
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

    @Override
    public void toReport() {
        int uid = requestCheck();
        if (uid == 0)
            return;
        toReport(getDataId(), getData().getHref(), Report.TYPE_QUESTION);
    }

}
