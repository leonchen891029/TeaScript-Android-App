package com.teacore.teascript.module.back.currencyfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.adapter.ChatMessageAdapter;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.ChatMessage;
import com.teacore.teascript.bean.ChatMessageList;
import com.teacore.teascript.bean.CommentList;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.network.OperationResponseHandler;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.widget.emoji.EmojiFragment;
import com.teacore.teascript.widget.emoji.OnSendClickListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 与某人的聊天记录界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-15
 */

public class ChatMessageListFragment extends BaseListFragment<ChatMessage> implements
        AdapterView.OnItemLongClickListener,OnSendClickListener,ChatMessageAdapter.OnRetrySendMessageListener{

    protected static final String TAG = ActiveListFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_FID = "BUNDLE_KEY_FID";
    public static final String BUNDLE_KEY_FNAME = "BUNDLE_KEY_FNAME";
    private static final String CACHE_KEY_PREFIX = "chat_message_list";

    //聊天时间显示，只有两个消息的时间间隔超过5分钟才会显示出来
    private final static long TIME_INTERVAL = 1000 * 60 * 5;

    private int mFid;
    private String mFName;
    private int mSendingID;
    private int mPageCount;
    //最后显示出来的时间
    private long mLastShowDate;

    public EmojiFragment emojiFragment = new EmojiFragment();

    /*
    **存放正在发送的消息，key 为生成的一个临时mSendingID，value为Message实体
    **当消息发送成功后，从mSendingMsgs删除对应的ChatMessage实体
    */
    private SparseArray<ChatMessage> mSendingList = new SparseArray<ChatMessage>();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mEmptyLayout != null) {
                mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
                mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mFid = args.getInt(BUNDLE_KEY_FID);
            mFName = args.getString(BUNDLE_KEY_FNAME);
            mCatalog = CommentList.CATALOG_MESSAGE;
        }
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);

        ((BaseActivity) getActivity()).setActionBarTitle(mFName);

        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getActivity().getWindow().setSoftInputMode(mode);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.emoji_container, emojiFragment).commit();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        if (emojiFragment.isShowEmojiKeyBoard()) {
            emojiFragment.hideAllKeyBoard();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    @Override
    protected ChatMessageAdapter getListAdapter() {
        ChatMessageAdapter adapter = new ChatMessageAdapter();
        adapter.setOnRetrySendMessageListener(this);
        return adapter;
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mFid;
    }

    @Override
    protected ChatMessageList parseList(InputStream inputStream) throws Exception {
        ChatMessageList list = XmlUtils.toBean(ChatMessageList.class, inputStream);
        handleShowDate(list.getList());
        mPageCount = (int) Math.ceil((float) list.getMessageCount() / getPageSize());
        return list;
    }

    @Override
    protected ChatMessageList readList(Serializable seri) {
        ChatMessageList list = ((ChatMessageList) seri);
        handleShowDate(list.getList());
        return list;
    }

    //处理时间显示，设置哪些需要显示时间，哪些不需要显示时间
    private void handleShowDate(List<ChatMessage> list) {
        ChatMessage msg = null;
        long lastGroupTime = 0L;

        //因为获得的列表是按时间降序的，所以需要倒着遍历
        for (int i = list.size() - 1; i >= 0; i--) {
            msg = list.get(i);
            Date date =TimeUtils.toDate(msg.getPubDate());
            if (date != null && isNeedShowDate(date.getTime(), lastGroupTime)) {
                lastGroupTime = date.getTime();
                msg.setShowDate(true);
            }
        }

        //只设置最新的时间
        if (lastGroupTime > mLastShowDate) {
            mLastShowDate = lastGroupTime;
        }

    }

    private boolean isNeedShowDate(long currentTime, long lastTime) {
        return currentTime - lastTime > TIME_INTERVAL;
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        if (!AppContext.getNightModeSwitch()) {
            mListView.setBackgroundColor(Color.parseColor("#ebebeb"));
        }
        mListView.setOnItemLongClickListener(this);
        //移除父类设置的OnScrollListener，这里不需要下拉加载
        mListView.setOnScrollListener(null);

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    requestData(false);
                } else {
                    UiUtils.showLoginActivity(getActivity());
                }
            }
        });

    }

    @Override
    protected void requestData(boolean refresh) {
        mEmptyLayout.setEmptyMessage("");
        if (AppContext.getInstance().isLogin()) {
            super.requestData(refresh);
        } else {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
            mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getChatMessageList(mFid, mCurrentPage, mHandler);
    }

    @Override
    protected boolean isReadCacheData(boolean refresh) {
        return !TDevice.hasInternet();
    }

    @Override
    protected void executeOnLoadDataSuccess(List<ChatMessage> datas) {
        mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
        if (datas == null) {
            datas = new ArrayList<>();
        }
        if (mAdapter != null) {
            if (mCurrentPage == 0)
                mAdapter.clear();
            mAdapter.addDatas(datas);
            if (datas.size() == 0 && mState == STATE_REFRESH) {
                mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
            } else if (datas.size() < getPageSize()) {
                mAdapter.setState(BaseListAdapter.STATE_OTHER);
            } else {
                mAdapter.setState(BaseListAdapter.STATE_LOAD_MORE);
            }
            mAdapter.notifyDataSetChanged();
            //只有第一次加载，才需要滚动到底部
            if (mCurrentPage == 0)
                mListView.setSelection(mListView.getBottom());
            else if (datas.size() > 1) {
                mListView.setSelection(datas.size() - 1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        final ChatMessage message = mAdapter.getItem(mAdapter.getDataSize() - position -1);
        DialogUtils.getSelectDialog(getActivity(), getResources().getStringArray(R.array
                .message_list_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        TDevice.copyTextToClipboard(HtmlUtils.delHTMLTag(message
                                .getContent()));
                        break;
                    case 1:
                        handleDeleteMessage(message);
                        break;
                    default:
                        break;
                }
            }
        }).show();
        return true;
    }

    // 下拉加载数据
    @Override
    public void onRefresh() {
        if (mState == STATE_REFRESH) {
            return;
        }
        if (mCurrentPage == mPageCount - 1) {
            AppContext.showToast("已加载全部数据！");
            setSwipeRefreshLoadedState();
            return;
        }
        // 设置顶部正在刷新
        mListView.setSelection(0);
        setSwipeRefreshLoadingState();
        mState = STATE_REFRESH;
        mCurrentPage++;
        requestData(true);
    }

    public void showFriendUserCenter() {
        UiUtils.showUserCenter(getActivity(), mFid, mFName);
    }

    @Override
    public void onResume() {
        super.onResume();
        emojiFragment.hideFlagButton();
    }

    private void handleDeleteMessage(final ChatMessage message) {
        DialogUtils.getConfirmDialog(getActivity(), "是否删除该私信?", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showWaitDialog(R.string.progress_submit);
                TeaScriptApi.deleteComment(mFid,
                        CommentList.CATALOG_MESSAGE, message.getId(),
                        message.getAuthorId(),
                        new DeleteMessageOperationHandler(message));
            }
        }).show();
    }

    class DeleteMessageOperationHandler extends OperationResponseHandler {

        public DeleteMessageOperationHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccessOperation(int code, ByteArrayInputStream inputStream, Object[] args)
                throws Exception {
            Result result = XmlUtils.toBean(ResultData.class,inputStream).getResult();
            if (result.OK()) {
                ChatMessage msg = (ChatMessage) args[0];
                mAdapter.removeItem(msg);
                mAdapter.notifyDataSetChanged();
                hideWaitDialog();
                AppContext.showToast(R.string.tip_delete_success);
            } else {
                AppContext.showToast(result.getMessage());
                hideWaitDialog();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                error) {
            AppContext.showToast(R.string.tip_delete_fail);
            hideWaitDialog();
        }
    }

    @Override
    public void onClickSendButton(Editable str) {

        String string=str.toString();

        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        if (StringUtils.isEmpty(string)) {
            AppContext.showToast(R.string.tip_content_empty);
            return;
        }

        ChatMessage message = new ChatMessage();
        User user = AppContext.getInstance().getLoginUser();
        int msgTag = mSendingID++;
        message.setId(msgTag);
        message.setPortrait(user.getPortrait());
        message.setAuthor(user.getName());
        message.setAuthorId(user.getId());
        message.setContent(string);
        sendMessage(message);
    }

    //发送消息
    private void sendMessage(ChatMessage msg) {
        msg.setStatus(ChatMessage.MessageStatus.SENDING);
        Date date = new Date();
        msg.setPubDate(TimeUtils.getDateString(date));

        //如果此次发表的时间距离上次的时间达到了 TIME_INTERVAL 的间隔要求，则显示时间
        if (isNeedShowDate(date.getTime(), mLastShowDate)) {
            msg.setShowDate(true);
            mLastShowDate = date.getTime();
        }

        //如果待发送列表没有此条消息，说明是新消息，不是发送失败再次发送的，不需要再次添加
        if (mSendingList.indexOfKey(msg.getId()) < 0) {
            mSendingList.put(msg.getId(), msg);
            mAdapter.addItem(0, msg);
            mListView.setSelection(mListView.getBottom());
        } else {
            mAdapter.notifyDataSetChanged();
        }

        TeaScriptApi.pubMessage(msg.getAuthorId(), mFid, msg.getContent(), new
                SendMessageResponseHandler(msg.getId()));
    }

    @Override
    public void onClickFlagButton() {
    }

    @Override
    public void onRetrySendMessage(int msgId) {
        ChatMessage message = mSendingList.get(msgId);
        if (message != null) {
            sendMessage(message);
        }
    }

    class SendMessageResponseHandler extends AsyncHttpResponseHandler {

        private int msgTag;

        public SendMessageResponseHandler(int msgTag) {
            this.msgTag = msgTag;
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                ResultData resultData= XmlUtils.toBean(ResultData.class, new ByteArrayInputStream(arg2));
                Result result=resultData.getResult();

                if (result.OK()) {
                    //从mSendingMsgs获取发送时放入的MessageDetail实体
                    ChatMessage message = mSendingList.get(this.msgTag);
                    ChatMessage serverMsg = resultData.getMessage();
                    //把id设置为服务器返回的id
                    message.setId(serverMsg.getId());
                    message.setStatus(ChatMessage.MessageStatus.NORMAL);
                    //从待发送列表移除
                    mSendingList.remove(this.msgTag);
                    mAdapter.notifyDataSetChanged();
                } else {
                    error();
                    AppContext.showToast(result.getMessage());
                }
                emojiFragment.clean();
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }


        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
            error();
        }

        private void error() {
            mSendingList.get(this.msgTag).setStatus(ChatMessage.MessageStatus.ERROR);
            mAdapter.notifyDataSetChanged();
        }
    }



}
