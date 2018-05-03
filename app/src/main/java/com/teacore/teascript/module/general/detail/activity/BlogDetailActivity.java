package com.teacore.teascript.module.general.detail.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.bean.BlogDetail;
import com.teacore.teascript.module.general.bean.Comment;
import com.teacore.teascript.module.general.bean.UserRelation;
import com.teacore.teascript.module.general.detail.constract.BlogDetailContract;
import com.teacore.teascript.module.general.detail.fragment.BlogDetailFragment;
import com.teacore.teascript.module.general.detail.fragment.DetailFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UrlUtils;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * blogdetailactivity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-25
 */

public class BlogDetailActivity extends DetailActivity<BlogDetail,BlogDetailContract.BlogView> implements BlogDetailContract.BlogPresenter {

    public static void show(Context context,long id){
        Intent intent=new Intent(context,BlogDetailActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }

    protected int getType(){
        return 1;
    }

    @Override
    protected void requestData(){
        TeaScriptApi.getBlogDetail(getDataId(),getRequestHandler());
    }

    @Override
    protected Class<? extends DetailFragment> getDataViewFragment(){
        return BlogDetailFragment.class;
    }

    @Override
    protected Type getDataType(){
        return new TypeToken<ResultBean<BlogDetail>>(){}.getType();
    }

    @Override
    public void toFavorite(){

        int uId=requestCheck();

        if(uId==0){
            return;
        }
        showWaitDialog(R.string.progress_submit);

        final BlogDetail blogDetail=getData();

        TeaScriptApi.getFavReverse(getDataId(), getType(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                hideWaitDialog();;
                if(blogDetail==null){
                    return;
                }
                if(blogDetail.isFavorite()){
                    AppContext.showToast(R.string.del_favorite_fail);
                }else{
                    AppContext.showToast(R.string.add_favorite_fail);
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try{
                    Type type=new TypeToken<ResultBean<BlogDetail>>(){}.getType();

                    ResultBean<BlogDetail> resultBean=AppContext.createGson().fromJson(s,type);

                    if(resultBean!=null && resultBean.isSuccess()){
                        blogDetail.setFavorite(!blogDetail.isFavorite());
                        mView.toFavoriteOk(blogDetail);
                        if (blogDetail.isFavorite())
                            AppContext.showToast(R.string.add_favorite_success);
                        else
                            AppContext.showToast(R.string.del_favorite_success);
                    }
                    hideWaitDialog();
                }catch(Exception e){
                    e.printStackTrace();
                    onFailure(i,headers,s,e);
                }

            }
        });

    }

    @Override
    public void toShare() {

        if (getDataId() != 0 && getData() != null) {
            String content;

            String url = String.format(UrlUtils.URL_MOBILE + "blog/%s", getDataId());
            final BlogDetail blogDetail = getData();
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
    public void toFollow(){
        int uId=requestCheck();
        if(uId==0){
            return;
        }
        showWaitDialog(R.string.progress_submit);

        final BlogDetail blogDetail=getData();
        TeaScriptApi.getUserRelationReverse(blogDetail.getAuthorId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                AppContext.showToast(R.string.follow_fail);
                hideWaitDialog();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try{
                    Type type=new TypeToken<ResultBean<UserRelation>>(){}.getType();

                    ResultBean<UserRelation> resultBean=AppContext.createGson().fromJson(s,type);

                    if(resultBean!=null && resultBean.isSuccess()){
                        blogDetail.setAuthorRelation(resultBean.getResult().getRelation());
                        mView.toFollowOk(blogDetail);
                        if (blogDetail.getAuthorRelation() >= 3) {
                            AppContext.showToast(R.string.del_follow_success);
                        } else {
                            AppContext.showToast(R.string.add_follow_success);
                        }
                    }
                    hideWaitDialog();
                }catch (Exception e){
                    e.printStackTrace();
                    onFailure(i,headers,s,e);
                }
            }
        });

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

        TeaScriptApi.pubBlogComment(id, commentId, commentAuthorId, comment, new TextHttpResponseHandler() {

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
                        Comment comment = resultBean.getResult();
                        if (comment != null) {
                            if (mView != null) {
                                mView.toSendCommentOk(comment);
                            }
                        }
                    }
                    hideWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

}
