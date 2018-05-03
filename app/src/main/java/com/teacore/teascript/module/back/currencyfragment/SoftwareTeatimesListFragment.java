package com.teacore.teascript.module.back.currencyfragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.bean.TeatimesList;
import com.teacore.teascript.module.general.teatime.TeatimeDetailActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.service.TeatimeTaskUtils;
import com.teacore.teascript.adapter.TeatimeAdapter;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 软件Teatime列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-3
 */

public class SoftwareTeatimesListFragment extends BaseListFragment<Teatime> implements
       OnItemLongClickListener{

    public static final String BUNDLE_KEY_SOFTWARE_TEATIME_ID="bundle_key_software_teatime_id";
    private static final String CACHE_KEY_PREFIX="software_teatime_list";
    private int mId;

    @Override
    protected int getLayoutId(){
        return R.layout.fragment_software_teatimes;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        BaseActivity baseActivity=(BaseActivity) activity;
        try {
            //显示emoji_container
            activity.findViewById(R.id.emoji_container).setVisibility(
                    View.VISIBLE);
        } catch (NullPointerException e) {
        }

    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        //初始化软件Teatime的ID
        if (args != null) {
            mId = args.getInt(BUNDLE_KEY_SOFTWARE_TEATIME_ID, 0);
        }

        //设置软输入的模式
        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        getActivity().getWindow().setSoftInputMode(mode);
    }

    @Override
    protected TeatimeAdapter getListAdapter() {
        return new TeatimeAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mId;
    }

    @Override
    protected TeatimesList parseList(InputStream inputStream) throws Exception{
        return XmlUtils.toBean(TeatimesList.class,inputStream);
    }

    @Override
    protected TeatimesList readList(Serializable serializable){
        return (TeatimesList) serializable;
    }

    @Override
    protected void sendRequestData(){
        TeaScriptApi.getSoftActiveList(mId,mCurrentPage,mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        final Teatime teatime = mAdapter.getItem(position);
        if (teatime == null) {
            return;
        }

        TeatimeDetailActivity.show(getActivity(),teatime);
    }

    private void handleComment(String text) {
        Teatime tweet = new Teatime();
        tweet.setAuthorId(AppContext.getInstance().getLoginUid());
        tweet.setBody(text);
        TeatimeTaskUtils.pubSoftwareTeatime(getActivity(), tweet, mId);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        return true;
    }

}
