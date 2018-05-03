package com.teacore.teascript.module.general.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.base.baseactivity.BaseBackActivity;
import com.teacore.teascript.module.general.base.baseadapter.BaseRecyclerAdapter;
import com.teacore.teascript.module.general.base.basebean.PageBean;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.bean.CommentQ;
import com.teacore.teascript.module.general.behavior.KeyboardInputDelegation;
import com.teacore.teascript.module.general.widget.RecyclerRefreshLayout;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.TeatimeTextView;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 问答评论Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-2
 */

public class CommentQActivity extends BaseBackActivity {

    private long mId;
    private int mType;

    private PageBean<CommentQ>  mPageBean;
    private CommentQAdapter mAdapter;
    private CommentQ mComment;

    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerRefreshLayout mRefreshLayout;
    private RecyclerView mCommentsRV;


    private KeyboardInputDelegation mDelegation;
    private View.OnClickListener onCommentBtnClickListener;

    public static void show(Context context,long id,int type){
        Intent intent=new Intent(context,CommentQActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId(){
        return R.layout.general_activity_comments;
    }

    @Override
    protected boolean initBundle(Bundle bundle){
        mId=bundle.getLong("id");
        mType=bundle.getInt("type");
        return super.initBundle(bundle);
    }

    @Override
    protected void initView(){
        mCoordinatorLayout=(CoordinatorLayout) findViewById(R.id.general_activity_comments_cl);
        mRefreshLayout=(RecyclerRefreshLayout) findViewById(R.id.comments_rrl);
        mCommentsRV=(RecyclerView) findViewById(R.id.comments_rv);

        LinearLayoutManager manager=new LinearLayoutManager(this);
        mCommentsRV.setLayoutManager(manager);

        mAdapter=new CommentQAdapter(this);
        mCommentsRV.setAdapter(mAdapter);

        mDelegation=KeyboardInputDelegation.delegate(this,mCoordinatorLayout,mRefreshLayout);

        mDelegation.setAdapter(new KeyboardInputDelegation.KeyboardInputAdapter() {
            @Override
            public void onSubmit(TextView v, String content) {

            }

            @Override
            public void onFinalBackSpace(View v) {
                if(mComment==null) return;
                mComment=null;
                mDelegation.getInputView().setHint("发表评论");
            }
        });

    }

    @Override
    protected  void initData(){

        mRefreshLayout.setOnRecyclerRefreshLayoutListener(new RecyclerRefreshLayout.RecyclerRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                getData(true,null);
            }

            @Override
            public void onLoadMore() {
                String token=null;
                if(mPageBean!=null){
                    token=mPageBean.getNextPageToken();
                }
                getData(true,token);
            }
        });

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mRefreshLayout.onRefresh();
            }
        });

    }

    private void getData(final boolean clearData,String token){

        TeaScriptApi.getCommentList(mId, mType, "comment", token, new TextHttpResponseHandler() {

            @Override
            public void onFinish(){
                super.onFinish();
                mRefreshLayout.onComplete();
            }


            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {

                try{

                    Type type=new TypeToken<ResultBean<PageBean<CommentQ>>>(){}.getType();

                    ResultBean<PageBean<CommentQ>> resultBean= AppContext.createGson().fromJson(s,type);

                    if(resultBean!=null&&resultBean.isSuccess()){

                        if(resultBean.getResult()!=null&&resultBean.getResult().getItems()!=null&&resultBean.getResult().getItems().size()>0){
                            mPageBean=resultBean.getResult();
                            handleData(mPageBean.getItems(),clearData);
                            return;
                        }

                    }

                    mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE,true);

                }catch(Exception e){
                    e.printStackTrace();
                    onFailure(i,headers,s,e);
                }

            }
        });
    }

    private void handleData(List<CommentQ> comments,boolean clearData){
        if(clearData){
            mAdapter.clear();
        }

        mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING,false);

        mAdapter.addAll(comments);

        mAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener getCommentBtnClickListener(){
        if(onCommentBtnClickListener==null){
            onCommentBtnClickListener=new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CommentQ comment=(CommentQ) view.getTag();
                    mDelegation.getInputView().setHint("@"+comment.getAuthor()+":");
                    mComment=comment;
                    mDelegation.notifyWrapper();
                }
            };
        }

        return onCommentBtnClickListener;
    }

    private static class CommentQHolder extends RecyclerView.ViewHolder{

        private AvatarView userAV;
        private TextView  nameTV;
        private TextView  dateTV;
        private ImageView commentBtn,bestAnswerIV;
        private LinearLayout commentsLL;
        private TeatimeTextView contentTTV;

        CommentQHolder(View view){

            super(view);

            userAV=(AvatarView) view.findViewById(R.id.user_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            dateTV=(TextView) view.findViewById(R.id.date_tv);
            commentBtn=(ImageView) view.findViewById(R.id.comment_btn);
            bestAnswerIV=(ImageView) view.findViewById(R.id.best_answer_iv);
            commentsLL=(LinearLayout) view.findViewById(R.id.comments_ll);
            contentTTV=(TeatimeTextView) view.findViewById(R.id.content_ttv);

        }

        void setData(CommentQ comment, RequestManager imageLoader, View.OnClickListener listener){

            if(comment.getAuthorPortrait()!=null){
                imageLoader.load(comment.getAuthorPortrait()).error(R.drawable.widget_dface).into(userAV);
            }else{
                userAV.setImageResource(R.drawable.widget_dface);
            }

            nameTV.setText(comment.getAuthor());
            dateTV.setText(comment.getPubDate());
            CommentUtils.formatHtml(contentTTV.getResources(),contentTTV,comment.getContent());

            commentsLL.removeAllViews();
            if(comment.getRefer()!=null){
                View view=CommentUtils.getReferLayout(LayoutInflater.from(commentsLL.getContext()),comment.getRefer(),5);
                commentsLL.addView(view);
            }

            commentBtn.setTag(comment);

            if(listener!=null){
                commentBtn.setOnClickListener(listener);
            }

        }

    }

    private class CommentQAdapter extends BaseRecyclerAdapter<CommentQ>{

        CommentQAdapter(Context context){
            super(context,ONLY_FOOTER);
            mState=STATE_LOADING;
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, long itemId) {
                    CommentQActivity.this.onItemClick(getItem(position));
                }
            });
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent,int type){
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());
            View view=inflater.inflate(R.layout.recycler_view_comment_q,parent,false);
            return new CommentQHolder(view);
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder,CommentQ comment,int position){
            if(holder instanceof  CommentQHolder){
                CommentQHolder commentQHolder=(CommentQHolder) holder;
                RequestManager requestManager=getImageLoader();
                if(requestManager!=null){
                    commentQHolder.setData(comment,requestManager,null);
                }
                if(comment.isBest()){
                    commentQHolder.commentBtn.setVisibility(View.GONE);
                    commentQHolder.bestAnswerIV.setVisibility(View.VISIBLE);
                }else{
                    commentQHolder.commentBtn.setVisibility(View.VISIBLE);
                    commentQHolder.bestAnswerIV.setVisibility(View.GONE);
                }
            }

        }


    }

    private void onItemClick(CommentQ comment){
        QuestionCommentDetailActivity.show(this,comment,mId);
    }


}
