package com.teacore.teascript.module.general.comment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.base.basebean.PageBean;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.bean.CommentQ;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.TeatimeTextView;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 问答模块CommentView
 * @author 陈晓帆
 * @version 1.0
 * Created 2017--7-15
 */

public class CommentQView extends LinearLayout implements View.OnClickListener{

    private long mId;
    private int mType;
    private TextView mTitleTV;
    private TextView mSeeMoreTV;
    private LinearLayout mCommentsLL;

    public CommentQView(Context context){
        super(context);
        init();
    }

    public CommentQView(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
        init();
    }

    public CommentQView(Context context, @Nullable AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
        init();
    }

    private void init(){
        setOrientation(VERTICAL);
        LayoutInflater inflater=LayoutInflater.from(getContext());
        inflater.inflate(R.layout.view_comments,this,true);

        mTitleTV=(TextView) findViewById(R.id.comments_title_tv);
        mCommentsLL=(LinearLayout) findViewById(R.id.comments_ll);
        mSeeMoreTV=(TextView) findViewById(R.id.see_more_tv);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleTV.setText(title);
        }
    }

    public void initComments(long id, int type, final int commentsTotal, final RequestManager imageLoader, final OnCommentClickListener onCommentClickListener){
        this.mId=id;
        this.mType=type;

        mSeeMoreTV.setVisibility(GONE);
        setVisibility(GONE);

        TeaScriptApi.getCommentList(id, type, "refer,reply", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PageBean<CommentQ>>>() {
                    }.getType();

                    ResultBean<PageBean<CommentQ>> resultBean = AppContext.createGson().fromJson(responseString, type);

                    if (resultBean != null && resultBean.isSuccess()) {
                        addComments(resultBean.getResult().getItems(), commentsTotal, imageLoader, onCommentClickListener);
                        return;
                    }

                    onFailure(statusCode, headers, responseString, null);
                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });

    }

    private void addComments(List<CommentQ> comments, int commentTotal, RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        if (comments != null && comments.size() > 0) {
            if (comments.size() < commentTotal) {
                mSeeMoreTV.setVisibility(VISIBLE);
                mSeeMoreTV.setOnClickListener(this);
            }

            if (getVisibility() != VISIBLE) {
                setVisibility(VISIBLE);
            }

            int clearLineNumber = comments.size() - 1;
            for (final CommentQ comment : comments) {
                if (comment == null || comment.getId() == 0 || TextUtils.isEmpty(comment.getAuthor()))
                    continue;
                ViewGroup lay = addComment(false, comment, imageLoader, onCommentClickListener);
                if (clearLineNumber <= 0) {
                    lay.findViewById(R.id.line).setVisibility(View.INVISIBLE);
                } else {
                    clearLineNumber--;
                }
            }
        } else {
            setVisibility(GONE);
        }
    }

    public ViewGroup addComment(final CommentQ comment, RequestManager imageLoader, final OnCommentClickListener onCommentClickListener) {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }

        return addComment(true, comment, imageLoader, onCommentClickListener);
    }

    private ViewGroup addComment(boolean first,final CommentQ comment,RequestManager imageLoader,final OnCommentClickListener onCommentClickListener){
        LayoutInflater inflater=LayoutInflater.from(getContext());

        ViewGroup viewGroup=(ViewGroup) inflater.inflate(R.layout.item_comment,null,false);

        imageLoader.load(comment.getAuthorPortrait()).error(R.drawable.widget_dface)
                .into((AvatarView) viewGroup.findViewById(R.id.user_av));

        ((TextView) viewGroup.findViewById(R.id.name_tv)).setText(comment.getAuthor());

        ((TextView) viewGroup.findViewById(R.id.pub_date_tv)).setText(TimeUtils.friendly_time(comment.getPubDate()));

        TeatimeTextView contentTTV=(TeatimeTextView) viewGroup.findViewById(R.id.content_ttv);
        CommentUtils.formatHtml(getResources(),contentTTV,comment.getContent());

        if(comment.getRefer()!=null){
            //refer最多6层
            View view=CommentUtils.getReferLayout(inflater,comment.getRefer(),5);
            viewGroup.addView(view,viewGroup.indexOfChild(contentTTV));
        }

        viewGroup.findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCommentClickListener.onClick(view,comment);
            }
        });

        if (first)
            mCommentsLL.addView(viewGroup, 0);
        else
            mCommentsLL.addView(viewGroup);

        return viewGroup;
    }

    void onItemClick(View view,CommentQ comment){
        QuestionCommentDetailActivity.show(getContext(),comment,mId);
    }

    //查看更多comment的Activity
    @Override
    public void onClick(View v) {
        if (mId != 0 && mType != 0)
            CommentsActivity.show(getContext(), mId, mType);
    }

}
