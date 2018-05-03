package com.teacore.teascript.module.back.currencyfragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.R;
import com.teacore.teascript.adapter.CommentAdapter;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.network.OperationResponseHandler;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.BlogCommentList;
import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.bean.CommentList;
import com.teacore.teascript.bean.EntityList;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.widget.emoji.OnSendClickListener;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

import static com.teacore.teascript.util.UiUtils.showLoginActivity;

/**评论的list列表
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-22
 */
public class CommentListFragment extends BaseListFragment<Comment> implements OnItemLongClickListener,OnSendClickListener{

    protected static final String TAG=CommentListFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_ID="bundle_key_id";
    public static final String BUNDLE_KEY_CATALOG="bundle_key_catalog";
    public static final String BUNDLE_KEY_OWNER_ID="bundle_key_owner_id";
    public static final String BUNDLE_KEY_BLOG="bundle_key_blog";

    private static final String BLOG_CACHE_KEY_PREFIX="blogcomment_list";
    private static final String CACHE_KEY_PREFIX="comment_list";

    private static final int REQUEST_CODE=0x10;

    private int mId,mOwnerId;
    private boolean mIsBlogComment;
    private DetailActivity detailActivity;


    private final AsyncHttpResponseHandler mCommentHandler=new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {

            try{
                ResultData resultData = XmlUtils.toBean(ResultData.class, bytes);
                Result result=resultData.getResult();
                if(result.OK()){
                    hideWaitDialog();
                    AppContext.showToast(R.string.comment_publish_success);

                    mAdapter.addItem(0,resultData.getComment());
                    mAdapter.notifyDataSetChanged();
                    UiUtils.sendBroadcastCommentChanged(getActivity(),mIsBlogComment,mId,mCatalog,Comment.OPT_ADD,resultData.getComment());
                    detailActivity.emojiFragment.clean();
                }else{
                    hideWaitDialog();
                    AppContext.showToast(result.getMessage());
                }

            }catch (Exception  e){
                e.printStackTrace();
                onFailure(i,headers,bytes,e);
            }

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            hideWaitDialog();
            AppContext.showToast(R.string.comment_publish_faile);
        }

    };

    @Override
    public void initView(View view) {
        super.initView(view);
        mListView.setOnItemLongClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        detailActivity = (DetailActivity) getActivity();

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle args = getActivity().getIntent().getExtras();

        if (args != null) {

            mCatalog = args.getInt(BUNDLE_KEY_CATALOG, 0);
            mId = args.getInt(BUNDLE_KEY_ID, 0);
            mOwnerId = args.getInt(BUNDLE_KEY_OWNER_ID, 0);
            mIsBlogComment = args.getBoolean(BUNDLE_KEY_BLOG, false);

        }

        if (!mIsBlogComment && mCatalog == CommentList.CATALOG_POST) {
            ((BaseActivity) getActivity())
                    .setActionBarTitle(R.string.post_answer);
        }

        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

        getActivity().getWindow().setSoftInputMode(mode);
    }

    @Override
    public void onResume() {
        super.onResume();
        detailActivity.emojiFragment.hideFlagButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Comment comment = data
                    .getParcelableExtra(Comment.BUNDLE_KEY_COMMENT);
            if (comment != null) {
                mAdapter.addItem(0, comment);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected CommentAdapter getListAdapter() {
        return new CommentAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        String str = mIsBlogComment ? BLOG_CACHE_KEY_PREFIX : CACHE_KEY_PREFIX;
        return str + "_" + mId + "_Owner" + mOwnerId;
    }

    //
    @Override
    protected EntityList<Comment> parseList(InputStream is) throws Exception {

        if (mIsBlogComment) {
            return XmlUtils.toBean(BlogCommentList.class, is);
        } else {
            return XmlUtils.toBean(CommentList.class, is);
        }
    }

    @Override
    protected EntityList<Comment> readList(Serializable seri) {

        if (mIsBlogComment)
            return ((BlogCommentList) seri);
        return ((CommentList) seri);

    }

    @Override
    protected void sendRequestData() {

        if (mIsBlogComment) {
            TeaScriptApi.getBlogCommentList(mId, mCurrentPage, mHandler);
        } else {
            TeaScriptApi.getCommentList(mId, mCatalog, mCurrentPage, mHandler);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        final Comment comment = mAdapter.getItem(position);

        if (comment == null)
            return;

        detailActivity.emojiFragment.getEditText().setTag(comment);
        detailActivity.emojiFragment.getEditText().setHint("回复：" + comment.getAuthor());
        detailActivity.emojiFragment.showSoftKeyboard();
    }

    private void handleDeleteComment(Comment comment) {
        if (!AppContext.getInstance().isLogin()) {

            showLoginActivity(getActivity());

            return;
        }

        AppContext.showToast(R.string.deleting);

        if (mIsBlogComment) {
            TeaScriptApi.deleteBlogComment(
                    AppContext.getInstance().getLoginUid(), mId,
                    comment.getId(), comment.getAuthorId(), mOwnerId,
                    new DeleteOperationResponseHandler(comment));
        } else {
            TeaScriptApi.deleteComment(mId, mCatalog, comment.getId(),
                            comment.getAuthorId(),
                            new DeleteOperationResponseHandler(comment));
        }
    }

    class DeleteOperationResponseHandler extends OperationResponseHandler {

        DeleteOperationResponseHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccessOperation(int code, ByteArrayInputStream is, Object[] args) {

            try {
                Result res = XmlUtils.toBean(ResultData.class, is).getResult();

                if (res.OK()) {
                    AppContext.showToast(R.string.delete_success);

                    mAdapter.removeItem((Comment) args[0]);

                } else {

                    AppContext.showToast(res.getMessage());

                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailureOperation(code, e.getMessage(), args);
            }
        }

        @Override
        public void onFailureOperation(int code, String errorMessage, Object[] args) {
            AppContext.showToast(R.string.delete_fail);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {

        final Comment item = mAdapter.getItem(position);

        if (item == null)
            return false;

        int itemsLen = item.getAuthorId() == AppContext.getInstance()
                .getLoginUid() ? 2 : 1;

        String[] items = new String[itemsLen];

        items[0] = getResources().getString(R.string.copy);

        if (itemsLen == 2) {
            items[1] = getResources().getString(R.string.delete);
        }

        DialogUtils.getSelectDialog(getActivity(), items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {

                    TDevice.copyTextToClipboard(HtmlUtils.delHTMLTag(item
                            .getContent()));

                } else if (i == 1) {
                    handleDeleteComment(item);
                }

            }
        }).show();

        return true;
    }

    @Override
    public void onClickSendButton(Editable text) {

        if (!TDevice.hasInternet()) {
            AppContext.showToast(R.string.tip_no_internet);
            return;
        }
        if (TextUtils.isEmpty(text)) {
            AppContext.showToast(R.string.tip_comment_content_empty);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {

            UiUtils.showLoginActivity(getActivity());
            return;

        }

        if (detailActivity.emojiFragment.getEditText().getTag() != null) {

            handleReplyComment((Comment) detailActivity.emojiFragment.getEditText().getTag(),
                    text.toString());

        } else {
            sendReply(text.toString());
        }
    }

    private void sendReply(String text) {

        showWaitDialog(R.string.progress_submit);

        if (mIsBlogComment) {

            TeaScriptApi.pubBlogComment(mId, AppContext.getInstance()
                    .getLoginUid(), text, mCommentHandler);

        } else {

            TeaScriptApi.pubComment(mCatalog, mId, AppContext.getInstance()
                    .getLoginUid(), text, 1, mCommentHandler);

        }
    }

    private void handleReplyComment(Comment comment, String text) {
        showWaitDialog(R.string.progress_submit);

        if (!AppContext.getInstance().isLogin()) {

            UiUtils.showLoginActivity(getActivity());

            return;
        }

        if (mIsBlogComment) {

            TeaScriptApi.replyBlogComment(mId, AppContext.getInstance()
                    .getLoginUid(), text, comment.getId(), comment
                    .getAuthorId(), mCommentHandler);

        } else {

            TeaScriptApi.replyComment(mId, mCatalog, comment.getId(), comment
                            .getAuthorId(), AppContext.getInstance().getLoginUid(),
                    text, mCommentHandler);
        }
    }

    @Override
    public boolean onBackPressed() {

        if (detailActivity.emojiFragment.isShowEmojiKeyBoard()) {

            detailActivity.emojiFragment.hideAllKeyBoard();

            return true;
        }

        if (detailActivity.emojiFragment.getEditText().getTag() != null) {

            detailActivity.emojiFragment.getEditText().setTag(null);
            detailActivity.emojiFragment.getEditText().setHint("说点什么吧");
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onClickFlagButton() {
    }



}