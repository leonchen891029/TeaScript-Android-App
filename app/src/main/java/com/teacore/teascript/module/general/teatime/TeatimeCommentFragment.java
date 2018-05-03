package com.teacore.teascript.module.general.teatime;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.bean.CommentList;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.module.general.adapter.teatimeadapter.TeatimeCommentAdapter;
import com.teacore.teascript.module.general.base.baseadapter.BaseRecyclerAdapter;
import com.teacore.teascript.module.general.base.basefragment.BaseRecyclerViewFragment;
import com.teacore.teascript.module.general.detail.constract.TeatimeDetailContract;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Teatime评论列表Fragment
 * Created by apple on 17/12/7.
 */

public class TeatimeCommentFragment extends BaseRecyclerViewFragment<Comment>
        implements TeatimeDetailContract.ICommentView,BaseRecyclerAdapter.OnItemLongClickListener{

    private TeatimeDetailContract.Presenter mPresenter;
    private TeatimeDetailContract.IAgencyView mAgencyView;
    private int pageNum=0;
    private int mDeleteIndex=0;
    private Dialog mDeleteDialog;

    private AsyncHttpResponseHandler mRequestHandler=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            CommentList data = XmlUtils.toBean(CommentList.class, responseBody);

            setCommentListData(data.getList());

            onRequestSuccess(1);

            onRequestFinish();

            if (mAdapter.getCount() < 20 && mAgencyView != null)
                mAgencyView.resetCmnCount(mAdapter.getCount());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            mRefreshLayout.onComplete();
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
        }
    };

    public static TeatimeCommentFragment instantiate(TeatimeDetailContract.Presenter presenter,TeatimeDetailContract.IAgencyView iAgencyView) {
        TeatimeCommentFragment fragment = new TeatimeCommentFragment();
        fragment.mPresenter = presenter;
        fragment.mAgencyView = iAgencyView;
        return fragment;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPresenter = (TeatimeDetailContract.Presenter) activity;
    }

    @Override
    protected void initView(View view){
        super.initView(view);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    mPresenter.onScroll();
                }
            }
        });
    }

    @Override
    protected BaseRecyclerAdapter<Comment> getRecyclerAdapter(){
        TeatimeCommentAdapter adapter=new TeatimeCommentAdapter(getContext());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        return adapter;
    }

    @Override
    protected Type getType(){
        return new TypeToken<CommentList>(){
        }.getType();
    }

    @Override
    public void onLoadMore(){
        requestData(pageNum);
    }

    @Override
    protected void requestData(){
        requestData(0);
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        if(mIsRefresh) pageNum = 0;
        ++pageNum;
    }

    private void requestData(int pageNum) {
        TeaScriptApi.getCommentList(mPresenter.getTeatime().getId(), 3, pageNum, mRequestHandler);
    }

    private void setCommentListData(List<Comment> comments) {
        if (mIsRefresh) {
            //cache the time
            mAdapter.clear();
            mAdapter.addAll(comments);
            mRefreshLayout.setCanLoadMore(true);
        } else {
            mAdapter.addAll(comments);
        }
        if (comments.size() < 20) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
        }
    }

    @Override
    public void onCommentSuccess(Comment comment) {
        onRefreshing();
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        mPresenter.toReply(mAdapter.getItem(position));
    }

    @Override
    public void onLongClick(int position, long itemId) {
        final Comment comment = mAdapter.getItem(position);
        if (comment == null) return;
        int itemsLen = comment.getAuthorId() == AppContext.getInstance().getLoginUid() ? 2 : 1;
        String[] items = new String[itemsLen];
        items[0] = getResources().getString(R.string.copy);
        if (itemsLen == 2) {
            items[1] = getResources().getString(R.string.delete);
        }
        mDeleteIndex = position;
        DialogUtils.getSelectDialog(getActivity(), items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    TDevice.copyTextToClipboard(HtmlUtils.delHTMLTag(comment.getContent()));
                } else if (i == 1) {
                    handleDeleteComment(comment);
                }
            }
        }).show();
    }

    private void handleDeleteComment(Comment comment) {
        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        mDeleteDialog = DialogUtils.getProgressDialog(getContext(), "正在删除...");
        TeaScriptApi.deleteComment(mPresenter.getTeatime().getId(), 3, comment.getId(), comment.getAuthorId(),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Result result = XmlUtils.toBean(ResultData.class, responseBody).getResult();
                        if (result.OK()) {
                            if (mDeleteDialog != null) {
                                mDeleteDialog.dismiss();
                                mDeleteDialog = null;
                            }
                            mAdapter.removeItem(mDeleteIndex);
                            int count = Integer.valueOf(mPresenter.getTeatime().getCommentCount()) -1;
                            mPresenter.getTeatime().setCommentCount(count); // Bean就这样写的,我也不知道为什么!!!!
                            mAgencyView.resetCmnCount(count);
                        } else {
                            if (mDeleteDialog != null) {
                                mDeleteDialog.dismiss();
                                mDeleteDialog = null;
                            }
                            Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (mDeleteDialog != null) {
                            mDeleteDialog.dismiss();
                            mDeleteDialog = null;
                        }
                        Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
