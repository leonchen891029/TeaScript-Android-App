package com.teacore.teascript.module.general.detail.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.bean.SoftwareDetail;
import com.teacore.teascript.module.general.detail.constract.SoftwareDetailContract;
import com.teacore.teascript.module.general.detail.fragment.DetailFragment;
import com.teacore.teascript.module.general.detail.fragment.SoftwareDetailFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UrlUtils;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * softwaredetailactivity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-25
 */

public class SoftwareDetailActivity extends DetailActivity<SoftwareDetail,SoftwareDetailContract.SoftwareView>
        implements SoftwareDetailContract.SoftwarePresenter{

    public static void show(Context context,long id){
        Intent intent=new Intent(context,SoftwareDetailActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }

    @Override
    protected void requestData(){
        TeaScriptApi.getNewsDetail(getDataId(), TeaScriptApi.CATALOG_SOFTWARE_DETAIL,getRequestHandler());
    }

    @Override
    protected Class<? extends DetailFragment> getDataViewFragment(){
        return SoftwareDetailFragment.class;
    }

    @Override
    protected Type getDataType(){
        return new TypeToken<ResultBean<SoftwareDetail>>(){}.getType();
    }

    @Override
    public void toFavorite() {
        int uid = requestCheck();
        if (uid == 0)
            return;
        showWaitDialog(R.string.progress_submit);
        final SoftwareDetail softwareDetail = getData();

        TeaScriptApi.getFavReverse(getDataId(), 5, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                hideWaitDialog();
                if (softwareDetail.isFavorite())
                    AppContext.showToast(R.string.del_favorite_fail);
                else
                    AppContext.showToast(R.string.add_favorite_fail);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<SoftwareDetail>>() {
                    }.getType();

                    ResultBean<SoftwareDetail> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        softwareDetail.setFavorite(!softwareDetail.isFavorite());
                        mView.toFavoriteOk(softwareDetail);
                        if (softwareDetail.isFavorite())
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

            String url = String.format(UrlUtils.URL_MOBILE + "software/%s", getDataId());
            final SoftwareDetail softwareDetail = getData();
            if (softwareDetail.getBody().length() > 55) {
                content = HtmlUtils.delHTMLTag(softwareDetail.getBody().trim());
                if (content.length() > 55)
                    content = StringUtils.getSubstring(0, 55, content);
            } else {
                content = HtmlUtils.delHTMLTag(softwareDetail.getBody().trim());
            }
            String title = softwareDetail.getName();

            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(content) || TextUtils.isEmpty(title)) {
                AppContext.showToast("内容加载失败...");
                return;
            }
            toShare(title, content, url);
        } else {
            AppContext.showToast("内容加载失败...");
        }
    }


}
