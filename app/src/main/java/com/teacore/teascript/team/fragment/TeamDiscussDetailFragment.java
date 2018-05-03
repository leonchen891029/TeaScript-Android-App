package com.teacore.teascript.team.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseHeaderListFragment;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.adapter.TeamReplyAdapter;
import com.teacore.teascript.team.bean.TeamDiscuss;
import com.teacore.teascript.team.bean.TeamDiscussDetail;
import com.teacore.teascript.team.bean.TeamReply;
import com.teacore.teascript.team.bean.TeamReplyList;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.emoji.OnSendClickListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

/**团队讨论详情
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-8
 */
public class TeamDiscussDetailFragment extends BaseHeaderListFragment<TeamReply,TeamDiscuss> implements OnSendClickListener{

    private int mTeamId;
    private int mDiscussId;
    private WebView mWebView;
    private DetailActivity detailActivity;

    @Override
    protected void sendRequestData() {
        TeaScriptTeamApi.getTeamReplyList(mTeamId, mDiscussId,
                TeamReply.REPLY_TYPE_DISCUSS, mCurrentPage, mHandler);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        detailActivity=(DetailActivity) getActivity();
        super.onViewCreated(view,savedInstanceState);
    }

    private final AsyncHttpResponseHandler mReplyHandler=new AsyncHttpResponseHandler() {
        @Override
        public void onStart(){
            super.onStart();
            showWaitDialog();
        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            Result result= XmlUtils.toBean(ResultData.class,bytes).getResult();
            if(result.OK()) {
                AppContext.showToast("评论成功");
                onRefresh();
            }else{
                AppContext.showToast(result.getMessage());
            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            AppContext.showToast(new String(bytes));
        }

        @Override
        public void onFinish(){
            super.onFinish();
            hideWaitDialog();
        }
    };

    @Override
    protected void requestDetailData(boolean isRefresh){
        TeaScriptTeamApi.getTeamDiscussDetail(mTeamId,mDiscussId,mDetailHander);
    }

    @Override
    protected View initHeaderView(){
        Intent args=getActivity().getIntent();
        if(args!=null){
            mTeamId=args.getIntExtra("teamid",0);
            mDiscussId=args.getIntExtra("discussid",0);
        }
        View headView= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_team_discuss_detail,null);

        mWebView=findHeaderView(headView,R.id.dicuss_wb);

        return headView;
    }

    @Override
    public void onResume(){
        super.onResume();
        detailActivity.emojiFragment.hideFlagButton();
    }

    @Override
    protected String getDetailCacheKey(){
        return "team_discuss_detail_"+mTeamId+mDiscussId;
    }

    @Override
    protected void executeOnLoadDetailSuccess(TeamDiscuss detailBean){
        UiUtils.initWebView(mWebView);
        UiUtils.addWebImageShow(getActivity(),mWebView);

        StringBuffer body = new StringBuffer();
        body.append(UiUtils.WEB_STYLE).append(UiUtils.WEB_LOAD_IMAGES);
        body.append(ThemeSwitchUtils.getWebViewBodyString());
        // 添加title
        body.append(String.format("<div class='title'>%s</div>", detailBean.getTitle()));
        // 添加作者和时间
        String time = TimeUtils.friendly_time(detailBean.getCreateTime());
        String author = String.format("<a class='author' href='http://my.teascript.net/u/%s'>%s</a>", detailBean.getAuthor().getId(), detailBean.getAuthor().getName());
        String answerCountAndVoteup = detailBean.getVoteUp() + "赞/"
                + detailBean.getReplyCount() + "回";
        body.append(String.format("<div class='authortime'>%s&nbsp;&nbsp;&nbsp;&nbsp;%s&nbsp;&nbsp;&nbsp;&nbsp;%s</div>", author, time, answerCountAndVoteup));
        // 添加图片点击放大支持
        body.append(UiUtils.setHtmlCotentImagePreview(detailBean.getBody()));
        // 封尾
        body.append("</div></body>");
        mWebView.loadDataWithBaseURL(null,
                UiUtils.WEB_STYLE + body.toString(), "text/html",
                "utf-8", null);
        mAdapter.setNoDataText(R.string.comment_empty);

    }

    @Override
    protected TeamDiscuss getDetailBean(ByteArrayInputStream inputStream){
        return XmlUtils.toBean(TeamDiscussDetail.class,inputStream).getDiscuss();
    }

    @Override
    protected TeamReplyAdapter getListAdapter(){
        return new TeamReplyAdapter();
    }

    @Override
    protected TeamReplyList parseList(InputStream inputStream) throws Exception{
        return XmlUtils.toBean(TeamReplyList.class,inputStream);
    }


    @Override
    protected TeamReplyList readList(Serializable seri) {
        return (TeamReplyList) seri;
    }

    @Override
    protected String getCacheKeyPrefix() {
        return "team_discuss_reply" + mTeamId + "_" + mDiscussId;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeamReply reply = mAdapter.getItem(position - 1);
        if (reply == null)
            return;
    }

    @Override
    public void onClickSendButton(Editable str) {
        if (TextUtils.isEmpty(str)) {
            AppContext.showToast("请先输入评论内容...");
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        int uid = AppContext.getInstance().getLoginUid();
        TeaScriptTeamApi.pubTeamDiscussReply(uid, mTeamId, mDiscussId,
                str.toString(), mReplyHandler);
    }

    @Override
    public void onClickFlagButton() {

    }





}
