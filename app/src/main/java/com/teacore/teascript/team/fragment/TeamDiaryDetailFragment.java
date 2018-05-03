package com.teacore.teascript.team.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseHeaderListFragment;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.adapter.TeamReplyAdapter;
import com.teacore.teascript.team.bean.TeamDiary;
import com.teacore.teascript.team.bean.TeamDiaryDetail;
import com.teacore.teascript.team.bean.TeamReply;
import com.teacore.teascript.team.bean.TeamReplyList;
import com.teacore.teascript.team.fragment.viewpagerfragment.TeamDiaryViewPagerFragment;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.emoji.OnSendClickListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

/**
 * 周报详情:
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */

public class TeamDiaryDetailFragment extends BaseHeaderListFragment<TeamReply,TeamDiary> implements OnSendClickListener {

    private AvatarView mUserAV;
    private TextView mNameTV;
    private TextView mDateTV;
    private WebView mWebView;


    private TeamDiary mTeamDiary;
    private int mTeamId;

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getDiaryComment(mTeamId, mTeamDiary.getId(), mHandler);
    }

    @Override
    protected void requestDetailData(boolean isRefresh){
        TeaScriptApi.getDiaryDetail(mTeamId,mTeamDiary.getId(),mDetailHander);
    }

    @Override
    protected View initHeaderView() {

        Bundle bundle = activity.getIntent().getBundleExtra("diary");
        if (bundle != null) {
            mTeamId = bundle.getInt(TeamDiaryViewPagerFragment.TEAM_ID_KEY);
            mTeamDiary = (TeamDiary) bundle.getSerializable(TeamDiaryViewPagerFragment.TEAM_DIARY_KEY);
        } else {
            mTeamDiary = new TeamDiary();
        }

        View headView=inflateView(R.layout.fragment_team_diary_detail);
        mUserAV=findHeaderView(headView,R.id.user_av);
        mNameTV=findHeaderView(headView,R.id.name_tv);
        mDateTV=findHeaderView(headView,R.id.date_tv);
        mWebView=findHeaderView(headView,R.id.webview);

        mUserAV.setAvatarUrl(mTeamDiary.getAuthor().getPortrait());
        mNameTV.setText(mTeamDiary.getAuthor().getName());
        mDateTV.setText(TimeUtils.friendly_time(mTeamDiary.getCreateTime()));

        return headView;
    }

    @Override
    public void onResume(){
        super.onResume();
        ((DetailActivity) getActivity()).emojiFragment.hideFlagButton();
    }

    @Override
    protected String getDetailCacheKey(){
        return "team_diary_detail_"+mTeamId+mTeamDiary.getId();
    }

    @Override
    protected void executeOnLoadDetailSuccess(TeamDiary detailBean){
        UiUtils.initWebView(mWebView);
        UiUtils.addWebImageShow(getActivity(),mWebView);

        StringBuffer body = new StringBuffer();
        body.append(UiUtils.WEB_STYLE).append(UiUtils.WEB_LOAD_IMAGES);
        body.append(ThemeSwitchUtils.getWebViewBodyString());
        // 添加图片点击放大支持
        body.append(UiUtils.setHtmlCotentImagePreview(mTeamDiary.getTitle()));
        // 封尾
        body.append("</div></body>");
        mWebView.loadDataWithBaseURL(null,
                UiUtils.WEB_STYLE + body.toString(), "text/html",
                "utf-8", null);
    }

    @Override
    protected TeamDiary getDetailBean(ByteArrayInputStream inputStream){
        return XmlUtils.toBean(TeamDiaryDetail.class,inputStream).getTeamDiary();
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
        return "team_diary_reply" + mTeamId + "_" + mTeamDiary.getId();
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
        TeaScriptTeamApi.pubTeamTeatimeComment(mTeamId, 118, mTeamDiary.getId(),
                str.toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {}
                });
    }

    @Override
    public void onClickFlagButton() {

    }



}
