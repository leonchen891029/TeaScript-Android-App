package com.teacore.teascript.module.general.comment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.adapter.teatimeadapter.TeatimeCommentAdapter;
import com.teacore.teascript.module.general.base.baseactivity.BaseBackActivity;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.bean.CommentQ;
import com.teacore.teascript.module.general.behavior.KeyboardInputDelegation;
import com.teacore.teascript.module.general.widget.TWebView;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 问答评论详情Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-16
 */

public class QuestionCommentDetailActivity extends BaseBackActivity {

    public static final String BUNDLE_KEY="BUNDLE_KEY";
    public static final String BUNDLE_ARTICLE_KEY="BUNDLE_ARTICLE_KEY";

    private CoordinatorLayout mCoordinatorLayout;
    private AvatarView mUserAV;
    private TextView mNameTV;
    private TextView mPubDateTV;
    private LinearLayout mVoteLL;
    private ImageView mVoteUpIV;
    private ImageView mVoteDownIV;
    private TextView mUpCountTV;
    private NestedScrollView mContentNSV;
    private TWebView mWebView;
    private TextView mCommentCountTV;
    private LinearLayout mContainerLL;

    //文章Id
    private long mId;
    private Dialog mVoteDialog;
    private Dialog mWaitingDialog;
    private CommentQ mComment;
    private CommentQ.Reply reply;
    private View mVoteView;
    private List<CommentQ.Reply> replies=new ArrayList<>();
    private KeyboardInputDelegation mDelegation;
    private TextHttpResponseHandler onSendCommentHandler;
    private View.OnClickListener onCommentButtonClickListener;

    public static void show(Context context, CommentQ comment, long sid) {
        Intent intent = new Intent(context, QuestionCommentDetailActivity.class);
        intent.putExtra(BUNDLE_KEY, comment);
        intent.putExtra(BUNDLE_ARTICLE_KEY, sid);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mComment = (CommentQ) getIntent().getSerializableExtra(BUNDLE_KEY);
        mId = getIntent().getLongExtra(BUNDLE_ARTICLE_KEY, 0);
        return !(mComment == null || mComment.getId() <= 0) && super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId(){
        return R.layout.general_activity_question_comment_detail;
    }

    @Override
    protected void initWindow() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setTitle("返回");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initView() {

        mCoordinatorLayout=(CoordinatorLayout) findViewById(R.id.improve_activity_question_comment_detail);
        mUserAV=(AvatarView) findViewById(R.id.user_av);
        mNameTV=(TextView) findViewById(R.id.name_tv);
        mPubDateTV=(TextView) findViewById(R.id.pub_date_tv);
        mVoteLL=(LinearLayout) findViewById(R.id.vote_ll);
        mVoteUpIV=(ImageView) findViewById(R.id.vote_up_iv);
        mVoteDownIV=(ImageView) findViewById(R.id.vote_down_iv);
        mUpCountTV=(TextView) findViewById(R.id.up_count_tv);
        mContentNSV=(NestedScrollView) findViewById(R.id.content_nsv);
        mWebView=(TWebView) findViewById(R.id.webview);
        mCommentCountTV=(TextView) findViewById(R.id.comment_count_tv);
        mContainerLL=(LinearLayout) findViewById(R.id.container_ll);

        if (TextUtils.isEmpty(mComment.getAuthorPortrait())) {
            mUserAV.setImageResource(R.drawable.widget_dface);
        } else {
            getImageLoader().load(mComment.getAuthorPortrait()).asBitmap()
                    .placeholder(getResources().getDrawable(R.drawable.widget_dface))
                    .error(getResources().getDrawable(R.drawable.widget_dface))
                    .into(mUserAV);
        }

        mNameTV.setText(mComment.getAuthor());

        if (!TextUtils.isEmpty(mComment.getPubDate()))
            mPubDateTV.setText(TimeUtils.friendly_time(mComment.getPubDate()));

        mVoteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVoteDialog = DialogUtils.getDialog(QuestionCommentDetailActivity.this)
                        .setView(getVoteView())
                        .create();
                mVoteDialog.show();
                WindowManager.LayoutParams params = mVoteDialog.getWindow().getAttributes();
                params.width = (int) TDevice.dpToPixels(260f);
                mVoteDialog.getWindow().setAttributes(params);
            }
        });

        switch (mComment.getVoteState()) {
            case CommentQ.VOTE_STATE_UP:
                mVoteUpIV.setSelected(true);
                break;
            case CommentQ.VOTE_STATE_DOWN:
                mVoteDownIV.setSelected(true);
        }

        mUpCountTV.setText(String.valueOf(mComment.getVoteCount()));

        mCommentCountTV.setText("评论 (" + (mComment.getReply() == null ? 0 : mComment.getReply().length) + ")");

        mDelegation = KeyboardInputDelegation.delegate(this, mCoordinatorLayout, mContentNSV);

        mDelegation.setAdapter(new KeyboardInputDelegation.KeyboardInputAdapter() {
            @Override
            public void onSubmit(TextView v, String content) {

                if (TextUtils.isEmpty(content.replaceAll("[ \\s\\n]+", ""))) {
                    Toast.makeText(QuestionCommentDetailActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!AppContext.getInstance().isLogin()) {
                    UiUtils.showLoginActivity(QuestionCommentDetailActivity.this);
                    return;
                }

                mWaitingDialog = DialogUtils.getProgressDialog(QuestionCommentDetailActivity.this, "正在发表评论...");
                mWaitingDialog.show();

                TeaScriptApi.pubComment(mId, -1, mComment.getId(), mComment.getAuthorId(), 2, content, onSendCommentHandler);
            }

            @Override
            public void onFinalBackSpace(View v) {
                if (reply == null) return;
                reply = null;
                mDelegation.getInputView().setHint("发表评论");
            }
        });

        if (mComment.getReply() != null) {
            mContainerLL.removeAllViews();
            replies.clear();

            // 反转集合, 最新的评论在集合后面
            Collections.addAll(replies, mComment.getReply());
            Collections.reverse(replies);

            for (int i = 0; i < replies.size(); i++) {
                appendComment(i, replies.get(i));
            }
        }

        fillWebView();
    }

    private void fillWebView() {
        if (TextUtils.isEmpty(mComment.getContent())) {
            return;
        }
        mWebView.loadDetailDataAsync(mComment.getContent(), null);
    }

    @SuppressWarnings("deprecation")
    private void appendComment(int i, CommentQ.Reply reply) {

        View view = LayoutInflater.from(this).inflate(R.layout.view_teatime_comment, mContainerLL, false);

        TeatimeCommentAdapter.TeatimeCommentViewHolder viewHolder = new TeatimeCommentAdapter.TeatimeCommentViewHolder(view);

        viewHolder.nameTV.setText(reply.getAuthor());

        if (TextUtils.isEmpty(reply.getAuthorPortrait())) {
           viewHolder.userAV.setImageResource(R.drawable.widget_dface);
        } else {
            getImageLoader()
                    .load(reply.getAuthorPortrait())
                    .asBitmap()
                    .placeholder(getResources().getDrawable(R.drawable.widget_dface))
                    .error(getResources().getDrawable(R.drawable.widget_dface))
                    .into(viewHolder.userAV);
        }

        viewHolder.pubDateTV.setText(String.format("%s楼  %s", i + 1, TimeUtils.friendly_time(reply.getPubDate())));
        CommentUtils.formatHtml(getResources(), viewHolder.contentTTV, reply.getContent());
        viewHolder.commentBtn.setTag(reply);
        viewHolder.commentBtn.setOnClickListener(getOnCommentButtonClickListener());
        mContainerLL.addView(view, 0);

    }

    private View.OnClickListener getOnCommentButtonClickListener() {
        if (onCommentButtonClickListener == null) {
            onCommentButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommentQ.Reply reply = (CommentQ.Reply) view.getTag();
                    mDelegation.notifyWrapper();
                    mDelegation.getInputView().setText("回复 @" + reply.getAuthor() + " : ");
                    mDelegation.getInputView().setSelection(mDelegation.getInputView().getText().length());
                    QuestionCommentDetailActivity.this.reply = reply;
                    TDevice.showSoftKeyboard(mDelegation.getInputView());
                }
            };
        }
        return onCommentButtonClickListener;
    }

    @Override
    protected void initData(){

        onSendCommentHandler=new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast(R.string.comment_publish_faile);
                if(mWaitingDialog!=null){
                    mWaitingDialog.dismiss();
                    mWaitingDialog=null;
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<CommentQ.Reply> resultBean=AppContext.createGson().fromJson(responseString
                        ,new TypeToken<ResultBean<CommentQ.Reply>>(){}.getType());

                if(resultBean.isSuccess()){
                    replies.add(resultBean.getResult());
                    mCommentCountTV.setText("评论 ("+replies.size()+")");
                    reply=null;
                    mDelegation.getInputView().setHint("发表评论");
                    mDelegation.getInputView().setText(null);
                    appendComment(replies.size() - 1, resultBean.getResult());
                }else{
                    AppContext.showToast(resultBean.getMessage());
                }

                if (mWaitingDialog != null){
                    mWaitingDialog.dismiss();
                    mWaitingDialog = null;
                }

                TDevice.hideSoftKeyboard(mDelegation.getInputView());
            }
        };

        TeaScriptApi.getComment(mComment.getId(), mComment.getAuthorId(), 2, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast(R.string.request_fail);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<CommentQ> resultBean = AppContext.createGson().fromJson(responseString,
                        new TypeToken<ResultBean<CommentQ>>() {
                        }.getType());
                if (resultBean.isSuccess()) {
                    CommentQ comment = resultBean.getResult();
                    if (comment != null && comment.getId() > 0) {
                        mComment= comment;
                        initView();
                        return;
                    }
                }
                AppContext.showToast(R.string.request_fail);
            }

        });
    }

    private View getVoteView() {

        if (mVoteView == null) {

            mVoteView = LayoutInflater.from(this).inflate(R.layout.dialog_question_comment_detail_vote, null, false);

            final VoteViewHolder holder = new VoteViewHolder(mVoteView);

            View.OnClickListener listener = new View.OnClickListener() {

                @Override
                public void onClick(final View view) {
                    if (!AppContext.getInstance().isLogin()){
                        UiUtils.showLoginActivity(QuestionCommentDetailActivity.this);
                        return;
                    }

                    final int opt = (int) view.getTag();

                    switch (opt){

                        case CommentQ.VOTE_STATE_UP:
                            if (mVoteDownIV.isSelected()){
                                Toast.makeText(QuestionCommentDetailActivity.this, "你已经踩过了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            holder.mVoteUpBtn.setVisibility(View.GONE);
                            holder.mVotePB.setVisibility(View.VISIBLE);
                            break;
                        case CommentQ.VOTE_STATE_DOWN:
                            if (mVoteUpIV.isSelected()){
                                Toast.makeText(QuestionCommentDetailActivity.this, "你已经顶过了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            holder.mVoteDownBtn.setVisibility(View.GONE);
                            holder.mVotePB.setVisibility(View.VISIBLE);
                            break;
                    }

                    TeaScriptApi.questionVote(mId, mComment.getId(), opt, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(QuestionCommentDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                            if (mVoteDialog != null && mVoteDialog.isShowing()){
                                switch (opt){
                                    case CommentQ.VOTE_STATE_UP:
                                        holder.mVoteUpBtn.setVisibility(View.VISIBLE);
                                        holder.mVotePB.setVisibility(View.GONE);
                                        break;
                                    case CommentQ.VOTE_STATE_DOWN:
                                        holder.mVoteDownBtn.setVisibility(View.VISIBLE);
                                        holder.mVotePB.setVisibility(View.GONE);
                                        break;
                                }
                            }
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {

                            ResultBean<CommentQ> resultBean = AppContext.createGson().fromJson(
                                    responseString, new TypeToken<ResultBean<CommentQ>>(){}.getType());
                            if (resultBean.isSuccess()){
                                mComment.setVoteState(resultBean.getResult().getVoteState());
                                mComment.setVoteCount(resultBean.getResult().getVoteCount());
                                mCommentCountTV.setText(String.valueOf(resultBean.getResult().getVoteCount()));
                                view.setSelected(!view.isSelected());
                                switch (opt){
                                    case CommentQ.VOTE_STATE_UP:
                                        mVoteUpIV.setSelected(!mVoteUpIV.isSelected());
                                        break;
                                    case CommentQ.VOTE_STATE_DOWN:
                                        mVoteDownIV.setSelected(!mVoteDownIV.isSelected());
                                        break;
                                }
                                Toast.makeText(QuestionCommentDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(QuestionCommentDetailActivity.this, TextUtils.isEmpty(resultBean.getMessage())
                                        ? "操作失败" : resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (mVoteDialog != null) mVoteDialog.dismiss();
                        }
                    });
                }
            };
            holder.mVoteUpBtn.setTag(CommentQ.VOTE_STATE_UP);
            holder.mVoteDownBtn.setTag(CommentQ.VOTE_STATE_DOWN);
            holder.mVoteUpBtn.setOnClickListener(listener);
            holder.mVoteDownBtn.setOnClickListener(listener);
            mVoteView.setTag(holder);
        }else{
            ViewGroup view = (ViewGroup) mVoteView.getParent();
            view.removeView(mVoteView);
        }

        VoteViewHolder holder = (VoteViewHolder) mVoteView.getTag();
        holder.mVoteUpBtn.setVisibility(View.VISIBLE);
        holder.mVoteDownBtn.setVisibility(View.VISIBLE);
        holder.mVotePB.setVisibility(View.GONE);

        switch (mComment.getVoteState()){
            default:
                holder.mVoteUpBtn.setSelected(false);
                holder.mVoteDownBtn.setSelected(false);
                holder.mVoteUpBtn.setText("顶");
                holder.mVoteDownBtn.setText("踩");
                break;
            case CommentQ.VOTE_STATE_UP:
                holder.mVoteUpBtn.setSelected(true);
                holder.mVoteDownBtn.setSelected(false);
                holder.mVoteUpBtn.setText("已顶");
                holder.mVoteDownBtn.setText("踩");
                break;
            case CommentQ.VOTE_STATE_DOWN:
                holder.mVoteUpBtn.setSelected(false);
                holder.mVoteDownBtn.setSelected(true);
                holder.mVoteUpBtn.setText("顶");
                holder.mVoteDownBtn.setText("已踩");
                break;
        }
        return mVoteView;
    }

    public static class VoteViewHolder{

        TextView mVoteUpBtn;
        TextView mVoteDownBtn;
        ProgressBar mVotePB;

        public VoteViewHolder(View view) {
            mVoteUpBtn=(TextView) view.findViewById(R.id.vote_up_btn);
            mVoteDownBtn=(TextView) view.findViewById(R.id.vote_down_btn);
            mVotePB=(ProgressBar) view.findViewById(R.id.vote_pb);
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView = null;
            mWebView.destroy();
        }
        super.onDestroy();
    }

}
