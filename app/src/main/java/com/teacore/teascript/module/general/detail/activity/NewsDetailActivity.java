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
import com.teacore.teascript.module.general.bean.NewsDetail;
import com.teacore.teascript.module.general.detail.constract.NewsDetailContract;
import com.teacore.teascript.module.general.detail.fragment.DetailFragment;
import com.teacore.teascript.module.general.detail.fragment.NewsDetailFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UrlUtils;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * NewsDetailActivity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-25
 */

public class NewsDetailActivity extends DetailActivity<NewsDetail,NewsDetailContract.NewsView>
        implements NewsDetailContract.NewsPresenter{

    //显示NewsDetail
    public static void show(Context context,long id){
        Intent intent=new Intent(context,NewsDetailActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }

    protected int getType(){
        return 0;
    }

    @Override
    protected void requestData(){
        TeaScriptApi.getNewsDetail(getDataId(),getRequestHandler());
    }

    @Override
    protected Class<? extends DetailFragment> getDataViewFragment(){
        return NewsDetailFragment.class;
    }

    @Override
    protected Type getDataType(){
        return new TypeToken<ResultBean<NewsDetail>>(){}.getType();
    }

    @Override
    public void toFavorite(){

        int uId=requestCheck();

        if(uId==0){
            return;
        }

        showWaitDialog(R.string.progress_submit);

        final NewsDetail newsDetail=getData();

        TeaScriptApi.getFavReverse(getDataId(), getType(), new TextHttpResponseHandler() {

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                hideWaitDialog();
                if(newsDetail.isFavorite()){
                    AppContext.showToast(R.string.del_favorite_fail);
                }else{
                    AppContext.showToast(R.string.add_favorite_fail);
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, String responseString) {

                try {

                    Type type = new TypeToken<ResultBean<NewsDetail>>(){}.getType();

                    ResultBean<NewsDetail> resultBean = AppContext.createGson().fromJson(responseString, type);

                    if (resultBean != null && resultBean.isSuccess()) {

                        newsDetail.setFavorite(!newsDetail.isFavorite());

                        mView.toFavoriteOk(newsDetail);

                        if (newsDetail.isFavorite())
                            AppContext.showToast(R.string.add_favorite_success);
                        else
                            AppContext.showToast(R.string.del_favorite_success);

                    }

                    hideWaitDialog();

                } catch (Exception e) {

                    e.printStackTrace();

                    onFailure(i, headers, responseString, e);

                }
            }
        });

    }

    @Override
    public void toShare() {

        if (getDataId() != 0 && getData() != null) {

            String content;

            String url = String.format(UrlUtils.URL_MOBILE + "news/%s", getDataId());

            final NewsDetail newsDetail = getData();

            if (newsDetail.getBody().length() > 55) {

                content = HtmlUtils.delHTMLTag(newsDetail.getBody().trim());

                if (content.length() > 55)
                    content = StringUtils.getSubstring(0, 55, content);

            } else {

                content = HtmlUtils.delHTMLTag(newsDetail.getBody().trim());

            }

            String title = newsDetail.getTitle();

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
    public void toSendComment(long id,long commentId,long commentAuthorId,String comment){

        int uId=requestCheck();

        if(uId==0){
            return;
        }

        if (TextUtils.isEmpty(comment)) {
            AppContext.showToast(R.string.tip_comment_content_empty);
            return;
        }

        TeaScriptApi.pubNewsComment(id, commentId, commentAuthorId, comment, new TextHttpResponseHandler() {

            @Override
            public void onStart(){
                super.onStart();
                showWaitDialog(R.string.progress_submit);
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                AppContext.showToast(R.string.comment_publish_faile);
                hideWaitDialog();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String responseString) {

                try{

                    Type type=new TypeToken<ResultBean<Comment>>(){}.getType();

                    ResultBean<Comment> resultBean=AppContext.createGson().fromJson(responseString,type);

                    if(resultBean.isSuccess()){

                        Comment comment=resultBean.getResult();

                        if(comment!=null){

                            if(mView!=null){
                                mView.toSendCommentOk(comment);
                            }

                        }

                    }

                    hideWaitDialog();

                }catch(Exception e){
                    e.printStackTrace();
                    onFailure(i,headers,responseString,e);
                }

            }
        });
    }

}
