package com.teacore.teascript.module.general.comment;

import android.content.Context;
import android.content.DialogInterface;
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
import com.teacore.teascript.module.general.bean.Comment;
import com.teacore.teascript.module.general.behavior.KeyboardInputDelegation;
import com.teacore.teascript.module.general.widget.RecyclerRefreshLayout;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.TeatimeTextView;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.TDevice;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 评论Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-1
 */

public class CommentsActivity extends BaseBackActivity {

    private long mId;
    private int mType;

    private PageBean<Comment> mPageBean;

    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerRefreshLayout mRefreshLayout;
    private RecyclerView mCommentsRV;

    private CommentAdapter mAdapter;
    private Comment mComment;
    private KeyboardInputDelegation mInputDelegation;
    private View.OnClickListener onCommentBtnClickListener;

    public static void show(Context context, long id, int type) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.general_activity_comments;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mId = bundle.getLong("id");
        mType = bundle.getInt("type");
        return super.initBundle(bundle);
    }

    @Override
    protected void initView() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.general_activity_comments_cl);
        mRefreshLayout = (RecyclerRefreshLayout) findViewById(R.id.comments_rrl);
        mCommentsRV = (RecyclerView) findViewById(R.id.comments_rv);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mCommentsRV.setLayoutManager(manager);

        mAdapter = new CommentAdapter(this);
        mCommentsRV.setAdapter(mAdapter);

        mInputDelegation = KeyboardInputDelegation.delegate(this, mCoordinatorLayout, mRefreshLayout);
        mInputDelegation.setAdapter(new KeyboardInputDelegation.KeyboardInputAdapter() {
            @Override
            public void onSubmit(TextView view, String content) {
            }

            @Override
            public void onFinalBackSpace(View v) {
                if (mComment == null) return;
                mComment = null;
                mInputDelegation.getInputView().setHint("发表评论");
            }
        });

        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    @Override
    protected void initData() {

        mRefreshLayout.setOnRecyclerRefreshLayoutListener(new RecyclerRefreshLayout.RecyclerRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                getData(true, null);
            }

            @Override
            public void onLoadMore() {
                String token = null;
                if (mPageBean != null) {
                    token = mPageBean.getNextPageToken();
                }
                getData(false, token);
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

    private void getData(final boolean cleanData, String token) {
        TeaScriptApi.getCommentList(mId, mType, "refer", token, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String responseString) {

                try {
                    Type type = new TypeToken<ResultBean<PageBean<Comment>>>() {
                    }.getType();

                    ResultBean<PageBean<Comment>> resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        if (resultBean.getResult() != null && resultBean.getResult().getItems() != null && resultBean.getResult().getItems().size() > 0) {
                            mPageBean = resultBean.getResult();
                            handleData(mPageBean.getItems(), cleanData);
                            return;
                        }
                    }
                    mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(i, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRefreshLayout.onComplete();
            }

        });

    }

    private void handleData(List<Comment> comments, boolean cleanData) {
        if (cleanData) {
            mAdapter.clear();
        }
        mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, false);
        mAdapter.addAll(comments);
        mAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener getCommentBtnClickListener() {
        if (onCommentBtnClickListener == null) {
            onCommentBtnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Comment comment = (Comment) view.getTag();
                    mInputDelegation.getInputView().setHint("@" + comment.getAuthor() + " :");
                    mComment = comment;
                    mInputDelegation.notifyWrapper();
                }
            };
        }
        return onCommentBtnClickListener;
    }

    private static class CommentHolder extends RecyclerView.ViewHolder {

        private AvatarView mUserAV;
        private TextView mNameTV;
        private TextView mDateTV;
        private TeatimeTextView mContentTTV;
        private LinearLayout mRefersLL;
        private ImageView mCommentBtn;

        CommentHolder(View itemView) {
            super(itemView);

            mUserAV = (AvatarView) itemView.findViewById(R.id.user_av);
            mNameTV = (TextView) itemView.findViewById(R.id.name_tv);
            mDateTV = (TextView) itemView.findViewById(R.id.pub_date_tv);
            mCommentBtn = (ImageView) itemView.findViewById(R.id.comment_btn);
            mContentTTV = (TeatimeTextView) itemView.findViewById(R.id.content_ttv);
            mRefersLL = (LinearLayout) itemView.findViewById(R.id.refers_ll);
        }

        void initData(Comment comment, RequestManager imageLoader, View.OnClickListener listener) {

            if (comment.getAuthorPortrait() != null) {
                imageLoader.load(comment.getAuthorPortrait()).error(R.drawable.widget_dface).into(mUserAV);
            } else {
                mUserAV.setImageResource(R.drawable.widget_dface);
            }

            mNameTV.setText(comment.getAuthor());
            mDateTV.setText(comment.getPubDate());
            CommentUtils.formatHtml(mContentTTV.getResources(), mContentTTV, comment.getContent());

            mRefersLL.removeAllViews();

            if (comment.getRefer() != null) {
                View view = CommentUtils.getReferLayout(LayoutInflater.from(mRefersLL.getContext()), comment.getRefer(), 5);
            }

            mCommentBtn.setTag(comment);
            mCommentBtn.setOnClickListener(listener);
        }
    }

    private class CommentAdapter extends BaseRecyclerAdapter<Comment> {
        CommentAdapter(Context context) {
            super(context, ONLY_FOOTER);
            mState = STATE_HIDE;
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, long itemId) {
                    CommentsActivity.this.onItemClick(position);
                }
            });

        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.recycler_view_comment, parent, false);
            return new CommentHolder(view);
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
            if (holder instanceof CommentHolder) {
                CommentHolder commentHolder = (CommentHolder) holder;
                RequestManager requestManager = getImageLoader();
                if (requestManager != null)
                    commentHolder.initData(item, requestManager, getCommentBtnClickListener());
            }
        }


    }

    private void onItemClick(int position) {

        final Comment comment = mAdapter.getItem(position);
        if (comment == null) return;

        String[] items;

        items = new String[]{getString(R.string.copy)};

        DialogUtils.getSelectDialog(this, getString(R.string.cancle), items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:
                        TDevice.copyTextToClipboard(HtmlUtils.delHTMLTag(comment.getContent()));
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        }).show();

    }

}
