package com.teacore.teascript.module.myinformation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.Message;
import com.teacore.teascript.bean.MessageList;
import com.teacore.teascript.bean.Notice;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.module.back.currencyfragment.ActiveListFragment;
import com.teacore.teascript.module.main.MainActivity;
import com.teacore.teascript.network.OperationResponseHandler;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.service.NoticeUtils;
import com.teacore.teascript.adapter.MessageAdapter;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

/**
 * 留言(私信)列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */

public class MessageListFragment extends BaseListFragment<Message> implements OnItemLongClickListener{

    protected static final String TAG = ActiveListFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "message_list";
    private boolean mIsWatingLogin;

    private final BroadcastReceiver mLogoutReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mEmptyLayout != null) {
                mIsWatingLogin = true;
                mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
                mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mLogoutReceiver, filter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mLogoutReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (mIsWatingLogin) {
            mCurrentPage = 0;
            mState = STATE_REFRESH;
            requestData(false);
        }
        refreshNotice();
        super.onResume();
    }

    private void refreshNotice() {
        Notice notice = MainActivity.mNotice;
        if (notice != null && notice.getMsgCount() > 0) {
            onRefresh();
        }
    }

    @Override
    protected MessageAdapter getListAdapter() {
        return new MessageAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX;
    }

    @Override
    protected MessageList parseList(InputStream is) throws Exception {
        return XmlUtils.toBean(MessageList.class, is);
    }

    @Override
    protected MessageList readList(Serializable seri) {
        return ((MessageList) seri);
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
        mListView.setOnItemLongClickListener(this);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                    requestData(false);
                } else {
                    UiUtils.showLoginActivity(getActivity());
                }
            }
        });
        if (AppContext.getInstance().isLogin()) {
            UiUtils.sendBroadcastForNotice(getActivity());
        }
    }

    @Override
    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {
            mIsWatingLogin = false;
            super.requestData(refresh);
        } else {
            mIsWatingLogin = true;
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
            mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getMessageList(AppContext.getInstance().getLoginUid(),
                mCurrentPage, mHandler);
    }

    @Override
    protected void onRefreshNetworkSuccess() {
        if ( NoticeViewPagerFragment.noticeCurrentPage == 2
                || NoticeViewPagerFragment.noticeShowCount[2] > 0) { // 在page中第三个位置
            NoticeUtils.clearNotice(Notice.TYPE_MESSAGE);
            UiUtils.sendBroadcastForNotice(getActivity());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Message message = mAdapter.getItem(position);
        if (message != null)
            UiUtils.showMessageDetail(getActivity(), message.getFriendId(),
                    message.getFriendName());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Message message = mAdapter.getItem(position);
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

    private void handleDeleteMessage(final Message message) {

        DialogUtils.getConfirmDialog(getActivity(), getString(R.string.confirm_delete_message,
                message.getFriendName()), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showWaitDialog(R.string.progress_submit);

                TeaScriptApi.deleteMessage(AppContext.getInstance()
                                .getLoginUid(), message.getFriendId(),
                        new DeleteMessageOperationHandler(message));
            }
        }).show();
    }

    class DeleteMessageOperationHandler extends OperationResponseHandler {

        public DeleteMessageOperationHandler(Object... args) {
            super(args);
        }

        @Override
        public void onSuccessOperation(int code, ByteArrayInputStream is, Object[] args)
                throws Exception {
            Result res = XmlUtils.toBean(ResultData.class, is).getResult();
            if (res.OK()) {
                Message msg = (Message) args[0];
                mAdapter.removeItem(msg);
                mAdapter.notifyDataSetChanged();
                hideWaitDialog();
                AppContext.showToast(R.string.tip_delete_success);
            } else {
                AppContext.showToast(res.getMessage());
                hideWaitDialog();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                error) {
            AppContext.showToast(R.string.tip_delete_fail);
            hideWaitDialog();
        }
    }
}
