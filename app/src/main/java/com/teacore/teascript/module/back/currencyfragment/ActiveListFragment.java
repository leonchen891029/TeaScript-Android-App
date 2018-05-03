package com.teacore.teascript.module.back.currencyfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Active;
import com.teacore.teascript.bean.ActiveList;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.Notice;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.service.NoticeUtils;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.module.main.MainActivity;
import com.teacore.teascript.adapter.ActiveAdapter;
import com.teacore.teascript.module.myinformation.NoticeViewPagerFragment;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 动态列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-18
 */

public class ActiveListFragment extends BaseListFragment<Active> implements AdapterView.OnItemLongClickListener{

    protected static final String TAG=ActiveListFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX="active_list";
    //等待登录
    private boolean mIsWaitingLogin;

    //初始化广播接收者
    private final BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(mEmptyLayout!=null){
                mIsWaitingLogin=true;
                mEmptyLayout.setEmptyType(EmptyLayout.NO_LOGIN);
                mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
            }
        }
    };

    //注册广播接收者
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void initView(View view){
        super.initView(view);

        //如果是最近的Active列表，初始化OptionsMenu
        if(mCatalog==ActiveList.CATALOG_LASTEST){
            setHasOptionsMenu(true);
        }

        mListView.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppContext.getInstance().isLogin()){
                    mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                    requestData(false);
                }else{
                    UiUtils.showLoginActivity(getActivity());
                }
            }
        });

        //如果登录发送通知广播
        if(AppContext.getInstance().isLogin()){
            UiUtils.sendBroadcastForNotice(getActivity());
        }

    }

    //注销广播接收者
    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        //如果在等待登录状态
        if(mIsWaitingLogin){
            mCurrentPage=0;
            mState=STATE_REFRESH;
            requestData(false);
        }
        refreshNotice();
    }

    //请求消息
    private void refreshNotice(){
        Notice notice= MainActivity.mNotice;
        if(notice==null){
            return;
        }
        if(notice.getAtmeCount() > 0 && mCatalog == ActiveList.CATALOG_ATME) {
            onRefresh();
        }else if(notice.getCommentCount() > 0 && mCatalog == ActiveList.CATALOG_COMMENT){
            onRefresh();
        }
    }

    @Override
    protected ActiveAdapter getListAdapter(){
        return new ActiveAdapter();
    }

    @Override
    protected String getCacheKeyPrefix(){
        return CACHE_KEY_PREFIX+mCatalog+ AppContext.getInstance().getLoginUid();
    }

    @Override
    protected ActiveList parseList(InputStream inputStream){
        return XmlUtils.toBean(ActiveList.class,inputStream);
    }

    @Override
    protected ActiveList readList(Serializable seri){
        return (ActiveList) seri;
    }

    @Override
    protected void requestData(boolean refresh){
        if (AppContext.getInstance().isLogin()) {
            mIsWaitingLogin = false;
            super.requestData(refresh);
        } else {
            mIsWaitingLogin = true;
            mEmptyLayout.setEmptyType(EmptyLayout.NO_LOGIN);
            mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
        }
    }

    @Override
    protected void sendRequestData(){
        TeaScriptApi.getActiveList(AppContext.getInstance().getLoginUid(),mCatalog,mCurrentPage,mHandler);
    }

    @Override
    protected void onRefreshNetworkSuccess() {
        if (AppContext.getInstance().isLogin()) {
            //0 @me页面 1 comment页面
            if (NoticeViewPagerFragment.noticeCurrentPage == 0) {
                NoticeUtils.clearNotice(Notice.TYPE_ATME);
            } else if (NoticeViewPagerFragment.noticeCurrentPage == 1 || NoticeViewPagerFragment.noticeShowCount[1] > 0) {
                NoticeUtils.clearNotice(Notice.TYPE_COMMENT);
            } else {
                NoticeUtils.clearNotice(Notice.TYPE_ATME);
            }
        }
        UiUtils.sendBroadcastForNotice(getActivity());
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Active active = mAdapter.getItem(position);
        if (active != null)
            UiUtils.showActiveRedirect(view.getContext(), active);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        final Active active = mAdapter.getItem(position);
        if (active == null)
            return false;
        String[] items = new String[] { getResources().getString(R.string.copy) };
        DialogUtils.getSelectDialog(getActivity(), items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TDevice.copyTextToClipboard(HtmlUtils.delHTMLTag(active.getMessage()));
            }
        }).show();
        return true;
    }

    @Override
    protected long getAutoRefreshTime() {
        // 最新动态，即是好友圈
        if (mCatalog == ActiveList.CATALOG_LASTEST) {
            return 5 * 60;
        }
        return super.getAutoRefreshTime();
    }

}
