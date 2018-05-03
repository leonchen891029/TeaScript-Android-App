package com.teacore.teascript.module.teatime;

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
import com.teacore.teascript.adapter.TeatimeAdapter;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.bean.TeatimesList;
import com.teacore.teascript.interfaces.OnTabReselectListener;
import com.teacore.teascript.module.general.teatime.TeatimeDetailActivity;
import com.teacore.teascript.network.OperationResponseHandler;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import cz.msebera.android.httpclient.Header;


/**
 * Teatime列表界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-15
 */

public class TeatimeListFragment extends BaseListFragment<Teatime> implements OnItemLongClickListener,OnTabReselectListener{

    protected static final String TAG=TeatimeListFragment.class.getSimpleName();

    //缓存键的前缀
    private static final String CACHE_KEY_PREFIX="teatime_list_";

    //删除Teatime的Handler
    private class DeleteTeatimeResponseHandler extends OperationResponseHandler{

        DeleteTeatimeResponseHandler(Object... args){
            super(args);
        }

        @Override
        public void onSuccessOperation(int code, ByteArrayInputStream is, Object[] args)
                throws Exception {

            try {

                Result res = XmlUtils.toBean(ResultData.class, is).getResult();

                if (res != null && res.OK()) {

                    AppContext.showToast(R.string.delete_success);

                    Teatime Teatime = (Teatime) args[0];

                    mAdapter.removeItem(Teatime);

                    mAdapter.notifyDataSetChanged();

                } else {

                    onFailureOperation(code, res.getMessage(), args);

                }

            } catch (Exception e) {

                e.printStackTrace();

                onFailureOperation(code, e.getMessage(), args);

            }
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            super.onSuccess(arg0, arg1, arg2);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                error) {
            AppContext.showToast(R.string.delete_fail);
        }

    }

    @Override
    protected int getLayoutId(){
        return R.layout.fragment_teatimes;
    }

    //注册广播器接受者
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (mCatalog > 0) {
            IntentFilter filter = new IntentFilter(
                    Constants.INTENT_ACTION_USER_CHANGE);
            filter.addAction(Constants.INTENT_ACTION_LOGOUT);
            getActivity().registerReceiver(mReceiver, filter);
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setupContent();
        }
    };

    @Override
    public void onDestroy() {
        if (mCatalog > 0) {
            getActivity().unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected TeatimeAdapter getListAdapter(){
        return new TeatimeAdapter();
    }

    @Override
    protected String getCacheKeyPrefix(){
        Bundle args=getArguments();
        if(args!=null){
            String str=args.getString("topic");
            if(str!=null){
                return str;
            }
        }
        return CACHE_KEY_PREFIX+mCatalog;
    }

    public String getTopic(){
        Bundle  args=getArguments();
        if(args!=null){
            String str=args.getString("topic");
            if(str!=null){
                return str;
            }
        }

        return "";
    }

    @Override
    protected TeatimesList parseList(InputStream inputStream) throws Exception{

        TeatimesList list=XmlUtils.toBean(TeatimesList.class,inputStream);

        return list;

    }

    @Override
    protected TeatimesList readList(Serializable seri){
        return ((TeatimesList) seri);
    }

    //请求数据
    @Override
    protected void sendRequestData(){

        Bundle args=getArguments();

        if(args!=null){

            String str=args.getString("topic");

            if(str!=null){

                TeaScriptApi.getTeatimeTopicList(mCurrentPage,str,mHandler);

                return;

            }
        }

        TeaScriptApi.getTeatimeList(mCatalog,mCurrentPage,mHandler);
    }

    private void setupContent() {

        if (AppContext.getInstance().isLogin()) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
            requestData(true);
        } else {
            mCatalog = TeatimesList.CATALOG_ME;
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
            mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));
        }

    }

    @Override
    protected void requestData(boolean refresh) {

        if (mCatalog > 0) {

            if (AppContext.getInstance().isLogin()) {

                mCatalog = AppContext.getInstance().getLoginUid();

                super.requestData(refresh);

            } else {

                mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);

                mEmptyLayout.setEmptyMessage(getString(R.string.unlogin_tip));

            }

        } else {

            super.requestData(refresh);

        }
    }

    @Override
    public void initView(View view){
        super.initView(view);
        mListView.setOnItemLongClickListener(this);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCatalog > 0) {
                    if (AppContext.getInstance().isLogin()) {
                        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                        requestData(true);
                    } else {
                        UiUtils.showLoginActivity(getActivity());
                    }
                } else {
                    mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                    requestData(true);
                }
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent,View view,int position,long id){
        Teatime Teatime=mAdapter.getItem(position);

        if(Teatime!=null){
            TeatimeDetailActivity.show(getContext(),Teatime);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        Teatime Teatime = mAdapter.getItem(position);
        if (Teatime != null) {
            handleLongClick(Teatime);
            return true;
        }
        return false;
    }

    private void handleLongClick(final Teatime Teatime) {
        String[] items;
        if (AppContext.getInstance().getLoginUid() == Teatime.getAuthorId()) {
            items = new String[]{getString(R.string.copy),
                    getString(R.string.delete)};
        } else {
            items = new String[]{getString(R.string.copy)};
        }

        DialogUtils.getSelectDialog(getActivity(), items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:
                        TDevice.copyTextToClipboard(HtmlUtils.delHTMLTag(Teatime.getBody()));
                        break;
                    case 1:
                        handleDeleteTeatime(Teatime);
                        break;
                }
            }
        }).show();
    }

    private void handleDeleteTeatime(final Teatime Teatime) {
        DialogUtils.getConfirmDialog(getActivity(), "是否删除该动弹?", new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TeaScriptApi.deleteTeatime(Teatime.getAuthorId(), Teatime
                        .getId(), new DeleteTeatimeResponseHandler(Teatime));
            }
        }).show();
    }

    @Override
    public void onTabReselect() {
        onRefresh();
    }

    @Override
    protected long getAutoRefreshTime() {
        // 最新动弹3分钟刷新一次
        if (mCatalog == TeatimesList.CATALOG_LATEST) {
            return 3 * 60;
        }
        return super.getAutoRefreshTime();
    }


}
