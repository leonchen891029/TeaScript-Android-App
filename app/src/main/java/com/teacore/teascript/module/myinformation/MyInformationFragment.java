package com.teacore.teascript.module.myinformation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.MyInformation;
import com.teacore.teascript.bean.Notice;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.module.back.BackActivity;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.module.main.MainActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.BadgeView;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.dialog.QrCodeDialog;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import cz.msebera.android.httpclient.Header;

/**
 * 我的信息Fragment界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-14
 */

public class MyInformationFragment extends BaseFragment{

    private AvatarView mAvatarAV;
    private TextView mNameTV;
    private ImageView mGenderIV;
    private TextView mScoreTV;
    private TextView mFavoriteTV;
    private TextView mFollowingTV;
    private TextView mFollowerTV;
    private View mMessageTV;
    private EmptyLayout mEmptyLayout;
    private ImageView mQrCodeIV;
    private View mUserContainer;
    private View mUserUnlogin;
    private static BadgeView mMessageCount;

    private boolean mIsWaitingLogin;

    private User mUser;

    private AsyncTask<String,Void,User> mCacheTask;

    //广播监听器
    private final BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action=intent.getAction();

            switch(action){

                case Constants.INTENT_ACTION_LOGOUT:
                    if(mEmptyLayout!=null){
                        mIsWaitingLogin=true;
                        setupUser();
                        mMessageCount.hide();
                    }
                    break;

                case Constants.INTENT_ACTION_USER_CHANGE:
                    requestData(true);
                    break;

                case Constants.INTENT_ACTION_NOTICE:
                    setNotice();
                    break;

            }
        }
    };

    //获取用户信息
    private final AsyncHttpResponseHandler mHandler=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {

            mUser= XmlUtils.toBean(MyInformation.class,new ByteArrayInputStream(bytes)).getUser();

            if(mUser!=null){

                fillUI();

                AppContext.getInstance().updateUserInfo(mUser);

                new SaveCacheTask(getActivity(),mUser,getCacheKey()).execute();

            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

        }
    };

    //初始化fragment_my_informaition_header
    private void setupUser(){
        if (mIsWaitingLogin) {
            mUserContainer.setVisibility(View.GONE);
            mUserUnlogin.setVisibility(View.VISIBLE);
        } else {
            mUserContainer.setVisibility(View.VISIBLE);
            mUserUnlogin.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //注册IntentFilter
        IntentFilter filter=new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
        filter.addAction(Constants.INTENT_ACTION_USER_CHANGE);
        getActivity().registerReceiver(mReceiver,filter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_my_information,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view,Bundle savedIntanceState){
        super.onViewCreated(view,savedIntanceState);
        requestData(true);
        mUser=AppContext.getInstance().getLoginUser();
        fillUI();
    }

    @Override
    public void initView(View view){
        mAvatarAV=(AvatarView) view.findViewById(R.id.avatar_av);
        mGenderIV=(ImageView) view.findViewById(R.id.gender_iv);
        mNameTV=(TextView) view.findViewById(R.id.name_tv);
        mScoreTV=(TextView) view.findViewById(R.id.score_tv);
        mFavoriteTV=(TextView) view.findViewById(R.id.favorite_tv);
        mFollowingTV=(TextView) view.findViewById(R.id.following_tv);
        mFollowerTV=(TextView) view.findViewById(R.id.follower_tv);

        mMessageTV=(TextView) view.findViewById(R.id.message_tv);
        mEmptyLayout=(EmptyLayout) view.findViewById(R.id.empty_layout);
        mQrCodeIV=(ImageView) view.findViewById(R.id.qr_code_iv);

        mUserContainer=view.findViewById(R.id.user_container_ll);
        mUserUnlogin=view.findViewById(R.id.user_unlogin_ll);

        mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(AppContext.getInstance().isLogin()){
                    requestData(true);
                }else{
                    UiUtils.showLoginActivity(getActivity());
                }
            }
        });

        mAvatarAV.setOnClickListener(this);
        mQrCodeIV.setOnClickListener(this);
        view.findViewById(R.id.favorite_ll).setOnClickListener(this);
        view.findViewById(R.id.following_ll).setOnClickListener(this);
        view.findViewById(R.id.follower_ll).setOnClickListener(this);
        view.findViewById(R.id.message_ll).setOnClickListener(this);
        view.findViewById(R.id.blog_ll).setOnClickListener(this);
        view.findViewById(R.id.team_ll).setOnClickListener(this);
        view.findViewById(R.id.event_ll).setOnClickListener(this);
        view.findViewById(R.id.note_ll).setOnClickListener(this);
        view.findViewById(R.id.feedback_ll).setOnClickListener(this);
        view.findViewById(R.id.setting_ll).setOnClickListener(this);

        mUserUnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UiUtils.showLoginActivity(getActivity());

            }
        });

        mMessageCount=new BadgeView(getActivity(),mMessageTV);
        mMessageCount.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
        mMessageCount.setBadgePosition(BadgeView.POSITION_CENTER);
        mMessageCount.setGravity(Gravity.CENTER);
        mMessageCount.setBackgroundResource(R.drawable.notification_bg);
    }

    private void fillUI(){

        if(mUser==null)
            return;

        mAvatarAV.setAvatarUrl(mUser.getPortrait());
        mNameTV.setText(mUser.getName());
        mGenderIV.setImageResource(StringUtils.toInt(mUser.getGender())!=2?R.drawable.icon_userinfo_male:R.drawable.icon_userinfo_female);
        mScoreTV.setText(String.valueOf(mUser.getScore()));
        mFavoriteTV.setText(String.valueOf(mUser.getFavoriteCount()));
        mFollowingTV.setText(String.valueOf(mUser.getFans()));
        mFollowerTV.setText(String.valueOf(mUser.getFollowers()));
    }

    private void requestData(boolean refresh){

        if (AppContext.getInstance().isLogin()) {
            mIsWaitingLogin = false;
            String key = getCacheKey();
            if (refresh || TDevice.hasInternet()
                    && (!CacheManager.isCacheExist(getActivity(), key))) {
                sendRequestData();
            } else {
                readCacheData(key);
            }
        } else {
            mIsWaitingLogin = true;
        }

        setupUser();
    }

    private void readCacheData(String key){
        cancelCacheTask();
        mCacheTask=new CacheTask(getActivity()).execute(key);
    }

    private void cancelCacheTask(){
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    private void sendRequestData(){
        int uid=AppContext.getInstance().getLoginUid();
        TeaScriptApi.getMyInformation(uid,mHandler);
    }

    private String getCacheKey(){
        return "my_information"+AppContext.getInstance().getLoginUid();
    }

    @Override
    public void onResume(){
        super.onResume();
        setNotice();
    }

    public void setNotice() {
        if (MainActivity.mNotice != null) {

            Notice notice = MainActivity.mNotice;
            int atmeCount = notice.getAtmeCount();
            int msgCount = notice.getMsgCount();
            int reviewCount = notice.getCommentCount();
            int newFansCount = notice.getNewFansCount();
            int newLikeCount = notice.getNewLikeCount();
            int activeCount = atmeCount + reviewCount + msgCount + newFansCount + newLikeCount;//
            // 信息总数
            if (activeCount > 0) {
                mMessageCount.setText(String.format("%d", activeCount));
                mMessageCount.show();
            } else {
                mMessageCount.hide();
            }

        } else {
            mMessageCount.hide();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    private class CacheTask extends AsyncTask<String, Void, User> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected User doInBackground(String... params) {
            Serializable seri = CacheManager.readObject(mContext.get(),
                    params[0]);
            if (seri == null) {
                return null;
            } else {
                return (User) seri;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (user != null) {
                mUser = user;
                fillUI();
            }
        }
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.setting_ll) {
            UiUtils.showSimpleBack(getActivity(),BackFragmentEnum.SETTING);
        } else {

            if (mIsWaitingLogin) {
                UiUtils.showLoginActivity(getActivity());
                return;
            }

            switch (id) {
                case R.id.avatar_av:
                    UiUtils.showSimpleBack(getActivity(),
                            BackFragmentEnum.MY_INFORMATION_DETAIL);
                    break;
                case R.id.qr_code_iv:
                    showMyQrCode();
                    break;
                case R.id.favorite_ll:
                    UiUtils.showUserFavorite(getActivity(), AppContext.getInstance()
                            .getLoginUid());
                    break;
                case R.id.following_ll:
                    UiUtils.showFriends(getActivity(), AppContext.getInstance()
                            .getLoginUid(), 0);
                    break;
                case R.id.follower_ll:
                    UiUtils.showFriends(getActivity(), AppContext.getInstance()
                            .getLoginUid(), 1);
                    break;
                case R.id.message_ll:
                    UiUtils.showSimpleBack(getActivity(),BackFragmentEnum.USER_MESSAGE);
                    setNoticeReaded();
                    break;
                case R.id.blog_ll:
                    UiUtils.showUserBlog(getActivity(), AppContext.getInstance()
                            .getLoginUid());
                    break;
                case R.id.team_ll:
                    UiUtils.showTeamMainActivity(getActivity());
                    break;
                case R.id.event_ll:
                    Bundle bundle = new Bundle();
                    bundle.putInt(BackActivity.BUNDLE_KEY_ARGS, 1);
                    UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.USER_EVENT, bundle);
                    break;
                case R.id.feedback_ll:
                    UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.FEED_BACK);
                    break;
                case R.id.user_center_rl:
                    UiUtils.showUserCenter(getActivity(), AppContext.getInstance()
                            .getLoginUid(), AppContext.getInstance().getLoginUser()
                            .getName());
                    break;
                case R.id.note_ll:
                    UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.NOTE);
                    break;
                default:
                    break;
            }
        }

    }

    private void showMyQrCode() {
        QrCodeDialog dialog = new QrCodeDialog(getActivity());
        dialog.show();
    }

    @Override
    public void initData() {
    }

    private void setNoticeReaded() {
        mMessageCount.setText("");
        mMessageCount.hide();
    }

}
