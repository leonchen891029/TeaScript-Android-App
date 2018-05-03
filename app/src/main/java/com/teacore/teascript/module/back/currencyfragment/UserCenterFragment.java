package com.teacore.teascript.module.back.currencyfragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.adapter.ActiveAdapter;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Active;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.bean.UserInformation;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.io.ByteArrayInputStream;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 用户中心Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-9
 */

public class UserCenterFragment extends BaseFragment implements OnItemClickListener, OnScrollListener{

    private static final String MALE="男";

    private ListView mListView;
    private EmptyLayout mEmptyLayout;

    private AvatarView mUserAV;
    private ImageView mGenderIV;
    private TextView mNameTV;
    private TextView mScoreTV;
    private LinearLayout mFollowingLL;
    private TextView mFollowingCountTV;
    private LinearLayout mFollowerLL;
    private TextView mFollowerCountTV;
    private TextView mLatestLoginTimeTV;
    private TextView mPrivateMsgBtn;
    private TextView mFollowUserBtn;

    private ActiveAdapter mAdapter;
    private int mActivePage=0;
    private int mHisUid;
    private String mHisName;
    private int mUid;
    private User mUser;

    //获取用户的动态信息
    private final AsyncHttpResponseHandler mActiveHandler=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {

            try{
                UserInformation information= XmlUtils.toBean(UserInformation.class,new ByteArrayInputStream(bytes));
                mUser=information.getUser();
                fillUI();
                List<Active> activeList=information.getActiveList();
                if(mState==STATE_REFRESH){
                    mAdapter.clear();
                }
                mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
                if(activeList.size()==0&&mState==STATE_REFRESH){
                    mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
                    mAdapter.setState(BaseListAdapter.STATE_NO_MORE);
                }else if(activeList.size()==0){
                    mAdapter.setState(BaseListAdapter.STATE_NO_MORE);
                }else{
                    mAdapter.setState(BaseListAdapter.STATE_LOAD_MORE);
                }
            }catch(Exception e){
                onFailure(i,headers,bytes,e);
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        }

        @Override
        public void onFinish(){
            mState=STATE_NONE;
        }
    };

    //初始化参数和创建视图
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_center, container,
                false);

        Bundle args = getArguments();

        mHisUid = args.getInt("his_id", 0);
        mHisName = args.getString("his_name");
        mUid = AppContext.getInstance().getLoginUid();

        initView(view);

        return view;
    }

    //初始化视图
    @Override
    public void initView(View view) {
        mListView=(ListView) view.findViewById(R.id.user_center_lv);
        mEmptyLayout=(EmptyLayout) view.findViewById(R.id.empty_layout);

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        //初始化HeaderView
        View headerView = LayoutInflater.from(getActivity()).inflate(
                R.layout.fragment_user_center_header, null, false);

        mUserAV = (AvatarView) headerView.findViewById(R.id.user_av);
        mGenderIV = (ImageView) headerView.findViewById(R.id.gender_iv);
        mNameTV = (TextView) headerView.findViewById(R.id.name_tv);
        mFollowingLL=(LinearLayout) headerView.findViewById(R.id.following_ll);
        mFollowingCountTV = (TextView) headerView.findViewById(R.id.following_count_tv);
        mFollowerLL=(LinearLayout) headerView.findViewById(R.id.follower_ll);
        mFollowerCountTV = (TextView) headerView.findViewById(R.id.follower_count_tv);
        mScoreTV = (TextView) headerView.findViewById(R.id.score_tv);
        mLatestLoginTimeTV = (TextView) headerView.findViewById(R.id.latest_login_time_tv);
        mPrivateMsgBtn = (TextView) headerView.findViewById(R.id.private_message_btn);
        mFollowUserBtn = (TextView) headerView.findViewById(R.id.follow_user_btn);

        mUserAV.setOnClickListener(this);
        mFollowingLL.setOnClickListener(this);
        mFollowerLL.setOnClickListener(this);
        mPrivateMsgBtn.setOnClickListener(this);
        mFollowUserBtn.setOnClickListener(this);
        headerView.findViewById(R.id.blog_tv).setOnClickListener(this);
        headerView.findViewById(R.id.information_tv).setOnClickListener(this);

        //添加headerView
        mListView.addHeaderView(headerView);

        //如果mAdapter为null,初始化mListView的适配器(动态适配器)
        if (mAdapter == null) {
            mAdapter = new ActiveAdapter();
            fristSendGetUserInformation();
        }

        mListView.setAdapter(mAdapter);

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fristSendGetUserInformation();
            }
        });
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        switch (id) {
            case R.id.user_av:
                //显示用户头像大图
                UiUtils.showUserAvatar(getActivity(), mUser.getPortrait());
                break;
            case R.id.following_ll:
                //显示用户关注
                UiUtils.showFriends(getActivity(), mUser.getId(), 0);
                break;
            case R.id.follower_ll:
                //显示用户粉丝
                UiUtils.showFriends(getActivity(), mUser.getId(), 1);
                break;
            case R.id.follow_user_btn:
                //关注(取消关注)
                handleUserRelation();
                break;
            case R.id.private_message_btn:
                //留言按钮
                if (mHisUid == AppContext.getInstance().getLoginUid()) {
                    AppContext.showToast("不能给自己发送留言:)");
                    return;
                }
                if (!AppContext.getInstance().isLogin()) {
                    UiUtils.showLoginActivity(getActivity());
                    return;
                }
                UiUtils.showMessageDetail(getActivity(), mHisUid, mHisName);
                break;
            case R.id.blog_tv:
                //显示用户的博客
                UiUtils.showUserBlog(getActivity(), mHisUid);
                break;
            case R.id.information_tv:
                //显示信息框
                showInformationDialog();
                break;
            default:
                break;
        }
    }

    //第一次获取用户信息
    private void fristSendGetUserInformation() {
        mState = STATE_REFRESH;
        mListView.setVisibility(View.GONE);
        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        sendGetUserInfomation();
    }

    //获取用户信息
    private void sendGetUserInfomation() {
        TeaScriptApi.getUserInformation(mUid, mHisUid, mActivePage,
                mActiveHandler);
    }

    //舒适化UI
    private void fillUI() {
        mListView.setVisibility(View.VISIBLE);
        mUserAV.setAvatarUrl(mUser.getPortrait());
        mHisUid = mUser.getId();
        mHisName = mUser.getName();
        mNameTV.setText(mHisName);

        int genderIcon = R.drawable.icon_userinfo_female;
        if (MALE.equals(mUser.getGender())) {
            genderIcon = R.drawable.icon_userinfo_male;
        }
        mGenderIV.setBackgroundResource(genderIcon);

        mFollowingCountTV.setText(mUser.getFollowers() + "");
        mFollowerCountTV.setText(mUser.getFans() + "");
        mScoreTV.setText(mUser.getScore() + "");
        mLatestLoginTimeTV.setText(getString(R.string.latest_login_time,
                TimeUtils.friendly_time(mUser.getLatestOnline())));
        updateUserRelation();
    }

    private void updateUserRelation() {
        switch (mUser.getRelation()) {
            case User.RELATION_TYPE_BOTH:
                mFollowUserBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.icon_follow_each_other, 0, 0, 0);
                mFollowUserBtn.setText(R.string.follow_each_other);
                mFollowUserBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
                mFollowUserBtn
                        .setBackgroundResource(R.drawable.btn_small_white_selector);
                break;
            case User.RELATION_TYPE_FANS_HIM:
                mFollowUserBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.icon_followed, 0, 0, 0);
                mFollowUserBtn.setText(R.string.unfollow_user);
                mFollowUserBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
                mFollowUserBtn
                        .setBackgroundResource(R.drawable.btn_small_white_selector);
                break;
            case User.RELATION_TYPE_FANS_ME:
                mFollowUserBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.icon_follow, 0, 0, 0);
                mFollowUserBtn.setText(R.string.follow_user);
                mFollowUserBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                mFollowUserBtn
                        .setBackgroundResource(R.drawable.btn_small_btn_selector);
                break;
            case User.RELATION_TYPE_NULL:
                mFollowUserBtn.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.icon_follow, 0, 0, 0);
                mFollowUserBtn.setText(R.string.follow_user);
                mFollowUserBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                mFollowUserBtn
                        .setBackgroundResource(R.drawable.btn_small_btn_selector);
                break;
        }
        int padding = (int) TDevice.dpToPixels(20);
        mFollowUserBtn.setPadding(padding, 0, padding, 0);
    }

    private AlertDialog mInformationDialog;

    private void showInformationDialog() {
        if (mInformationDialog == null) {
            mInformationDialog = DialogUtils.getDialog(getActivity()).show();
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.dialog_user_center_information, null);
            ((TextView) view.findViewById(R.id.join_time_tv))
                    .setText(TimeUtils.friendly_time(mUser.getJoinTime()));
            ((TextView) view.findViewById(R.id.location_tv))
                    .setText(StringUtils.getString(mUser.getFrom()));
            ((TextView) view.findViewById(R.id.development_platform_tv))
                    .setText(StringUtils.getString(mUser.getDevPlatform()));
            ((TextView) view.findViewById(R.id.academic_focus_tv))
                    .setText(StringUtils.getString(mUser.getExpertise()));
            mInformationDialog.setContentView(view);
        }

        mInformationDialog.show();
    }

    private void handleUserRelation() {
        if (mUser == null)
            return;
        // 判断登录
        if (!AppContext.getInstance().isLogin()) {
            UiUtils.showLoginActivity(getActivity());
            return;
        }
        String dialogTitle = "";
        int relationAction = 0;
        switch (mUser.getRelation()) {
            case User.RELATION_TYPE_BOTH:
                dialogTitle = "确定取消互粉吗？";
                relationAction = User.RELATION_ACTION_DELETE;
                break;
            case User.RELATION_TYPE_FANS_HIM:
                dialogTitle = "确定取消关注吗？";
                relationAction = User.RELATION_ACTION_DELETE;
                break;
            case User.RELATION_TYPE_FANS_ME:
                dialogTitle = "确定关注Ta吗？";
                relationAction = User.RELATION_ACTION_ADD;
                break;
            case User.RELATION_TYPE_NULL:
                dialogTitle = "确定关注Ta吗？";
                relationAction = User.RELATION_ACTION_ADD;
                break;
        }
        final int ra = relationAction;
        DialogUtils.getConfirmDialog(getActivity(), dialogTitle, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendUpdateRelcationRequest(ra);
            }
        }).show();
    }

    private void sendUpdateRelcationRequest(int ra) {
        TeaScriptApi.updateRelation(mUid, mHisUid,ra,new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        try {
                            Result result = XmlUtils.toBean(ResultData.class,
                                    new ByteArrayInputStream(arg2)).getResult();
                            if (result.OK()) {
                                switch (mUser.getRelation()) {
                                    case User.RELATION_TYPE_BOTH:
                                        mFollowUserBtn
                                                .setCompoundDrawablesWithIntrinsicBounds(
                                                        R.drawable.icon_follow,
                                                        0, 0, 0);
                                        mFollowUserBtn
                                                .setText(R.string.follow_user);
                                        mFollowUserBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                                        mFollowUserBtn
                                                .setBackgroundResource(R.drawable
                                                        .btn_small_btn_selector);
                                        mUser.setRelation(User.RELATION_TYPE_FANS_ME);
                                        break;
                                    case User.RELATION_TYPE_FANS_HIM:
                                        mFollowUserBtn
                                                .setCompoundDrawablesWithIntrinsicBounds(
                                                        R.drawable.icon_follow,
                                                        0, 0, 0);
                                        mFollowUserBtn
                                                .setText(R.string.follow_user);
                                        mFollowUserBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                                        mFollowUserBtn
                                                .setBackgroundResource(R.drawable
                                                        .btn_small_btn_selector);
                                        mUser.setRelation(User.RELATION_TYPE_NULL);
                                        break;
                                    case User.RELATION_TYPE_FANS_ME:
                                        mFollowUserBtn
                                                .setCompoundDrawablesWithIntrinsicBounds(
                                                        R.drawable.icon_followed, 0,
                                                        0, 0);
                                        mFollowUserBtn
                                                .setText(R.string.follow_each_other);
                                        mFollowUserBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
                                        mFollowUserBtn
                                                .setBackgroundResource(R.drawable
                                                        .btn_small_white_selector);
                                        mUser.setRelation(User.RELATION_TYPE_BOTH);
                                        break;
                                    case User.RELATION_TYPE_NULL:
                                        mFollowUserBtn
                                                .setCompoundDrawablesWithIntrinsicBounds(
                                                        R.drawable.icon_followed, 0,
                                                        0, 0);
                                        mFollowUserBtn
                                                .setText(R.string.unfollow_user);
                                        mFollowUserBtn.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
                                        mFollowUserBtn
                                                .setBackgroundResource(R.drawable
                                                        .btn_small_white_selector);
                                        mUser.setRelation(User.RELATION_TYPE_FANS_HIM);
                                        break;
                                }
                                int padding = (int) TDevice.dpToPixels(20);
                                mFollowUserBtn.setPadding(padding, 0, padding,
                                        0);
                            }
                            AppContext.showToast(result.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(arg0, arg1, arg2, e);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {
                    }
                });
    }

    @Override
    public void initData() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (position - 1 < 0) {
            return;
        }
        Active active = (Active) mAdapter.getItem(position - 1);
        if (active != null)
            UiUtils.showActiveRedirect(view.getContext(), active);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
        if (mState == STATE_NOMORE || mState == STATE_LOADMORE
                || mState == STATE_REFRESH) {
            return;
        }
        if (mAdapter != null
                && mAdapter.getDataSize() > 0
                && mListView.getLastVisiblePosition() == (mListView.getCount() - 1)) {
            if (mState == STATE_NONE
                    && mAdapter.getState() == BaseListAdapter.STATE_LOAD_MORE) {
                mState = STATE_LOADMORE;
                mActivePage++;
                sendGetUserInfomation();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }


}
