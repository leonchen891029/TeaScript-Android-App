package com.teacore.teascript.team.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.bean.MyIssueState;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.TypefaceUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;

import java.io.ByteArrayInputStream;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * Team面板Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */

public class TeamBoardFragment extends BaseFragment{

    public static final String WHICH_PAGER_KEY="myissuefragment_which_pager";

    private View mWaitDoLL;
    private View mOutdateLL;
    private View mIngLL;
    private View mAllLL;
    private View mActiveLL;
    private View mProjectLL;
    private View mIssueLL;
    private View mDiscussLL;
    private View mDiaryLL;


    private TextView mWaitDoTV;
    private TextView mOutdateTV;
    private TextView mIngTV;
    private TextView mAllTV;

    private AvatarView mAvatarAV;
    private TextView mNameTV;
    private TextView mDateTV;



    private Team mTeam;

    public void setTeam(Team team){
        this.mTeam=team;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        if(args!=null){
            mTeam=(Team) args.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_team_board,
                container, false);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void initView(View view){
        mWaitDoLL=view.findViewById(R.id.waitdo_ll);
        mOutdateLL=view.findViewById(R.id.outdate_ll);
        mIngLL=view.findViewById(R.id.ing_ll);
        mAllLL=view.findViewById(R.id.all_ll);
        mActiveLL=view.findViewById(R.id.team_active_ll);
        mProjectLL=view.findViewById(R.id.team_project_ll);
        mIssueLL=view.findViewById(R.id.team_issue_ll);
        mDiscussLL=view.findViewById(R.id.team_discuss_ll);
        mDiaryLL=view.findViewById(R.id.team_diary_ll);

        mWaitDoLL.setOnClickListener(this);
        mOutdateLL.setOnClickListener(this);
        mIngLL.setOnClickListener(this);
        mAllLL.setOnClickListener(this);
        mActiveLL.setOnClickListener(this);
        mProjectLL.setOnClickListener(this);
        mIssueLL.setOnClickListener(this);
        mDiscussLL.setOnClickListener(this);
        mDiaryLL.setOnClickListener(this);

        mWaitDoTV=(TextView) view.findViewById(R.id.waitdo_num_tv);
        mOutdateTV=(TextView) view.findViewById(R.id.outdate_num_tv);
        mIngTV=(TextView) view.findViewById(R.id.ing_num_tv);
        mAllTV=(TextView) view.findViewById(R.id.all_num_tv);

        mAvatarAV=(AvatarView) view.findViewById(R.id.avatar_av);
        mNameTV=(TextView) view.findViewById(R.id.name_tv);
        mDateTV=(TextView) view.findViewById(R.id.date_tv);

        mNameTV.setText(AppContext.getInstance().getLoginUser().getName()+","+getGreetings());
        mAvatarAV.setAvatarUrl(AppContext.getInstance().getLoginUser().getPortrait());
        mDateTV.setText("今天是"+getWeekDay()+","+ TimeUtils.getSystemTime("yyyy年MM月dd日"));

        TypefaceUtils.setTypeface((TextView) view
                .findViewById(R.id.team_active_tv));
        TypefaceUtils.setTypeface((TextView) view
                .findViewById(R.id.team_project_tv));
        TypefaceUtils.setTypeface((TextView) view
                .findViewById(R.id.team_issue_tv));
        TypefaceUtils.setTypeface((TextView) view
                .findViewById(R.id.team_discuss_tv));
        TypefaceUtils.setTypeface((TextView) view
                .findViewById(R.id.team_diary_tv));
    }

    private String getGreetings(){
        Calendar calendar= Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        if (hour < 6) {
            return "凌晨好!";
        } else if (hour < 9) {
            return "早上好!";
        } else if (hour < 12) {
            return "上午好!";
        } else if (hour < 14) {
            return "中午好!";
        } else if (hour < 17) {
            return "下午好!";
        } else if (hour < 19) {
            return "傍晚好!";
        } else if (hour < 22) {
            return "晚上好!";
        } else {
            return "夜里好!";
        }
    }

    private String getWeekDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        switch (dayOfWeek) {
            case 1:
                weekStr = "星期日";
                break;
            case 2:
                weekStr = "星期一";
                break;
            case 3:
                weekStr = "星期二";
                break;
            case 4:
                weekStr = "星期三";
                break;
            case 5:
                weekStr = "星期四";
                break;
            case 6:
                weekStr = "星期五";
                break;
            case 7:
                weekStr = "星期六";
                break;
        }
        return weekStr;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.waitdo_ll:
                UiUtils.showSimpleBack(getActivity(),
                        BackFragmentEnum.TEAM_MY_ISSUE, getMyIssueStateBundle(0));
                break;
            case R.id.ing_ll:
                UiUtils.showSimpleBack(getActivity(),
                        BackFragmentEnum.TEAM_MY_ISSUE, getMyIssueStateBundle(1));
                break;
            case R.id.outdate_ll:
                UiUtils.showSimpleBack(getActivity(),
                        BackFragmentEnum.TEAM_MY_ISSUE, getMyIssueStateBundle(0));
                break;
            case R.id.all_ll:
                UiUtils.showSimpleBack(getActivity(),
                        BackFragmentEnum.TEAM_MY_ISSUE, getMyIssueStateBundle(2));
                break;
            case R.id.team_active_ll:
                UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.TEAM_ACTIVE,
                        getArguments());
                break;
            case R.id.team_project_ll:
                UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.TEAM_PROJECT,
                        getArguments());
                break;
            case R.id.team_issue_ll:
                UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.TEAM_ISSUE,
                        getBundle());
                break;
            case R.id.team_discuss_ll:
                UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.TEAM_DISCUSS,
                        getBundle());
                break;
            case R.id.team_diary_ll:
                UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.TEAM_DIRAY,
                        getBundle());
                break;
            default:
                break;
        }
    }

    private Bundle getMyIssueStateBundle(int index) {
        Bundle bundle = getBundle();
        bundle.putInt(WHICH_PAGER_KEY, index);
        return bundle;
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_TEAM, mTeam);
        return bundle;
    }

    @Override
    public void initData() {
        requestData();
    }

    private void requestData() {
        TeaScriptApi.getMyIssueState(mTeam.getId() + "", AppContext.getInstance()
                .getLoginUid() + "", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                MyIssueState data = XmlUtils.toBean(MyIssueState.class,
                        new ByteArrayInputStream(arg2));
                if (data != null) {
                    fillUI(data);
                } else {
                    // 用户未登陆的情况
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideWaitDialog();
            }
        });
    }

    public void refresh() {
        requestData();
    }

    private void fillUI(MyIssueState data) {
        try {
            mNameTV.setText(data.getUser().getName() + "，" + getGreetings());
        } catch (NullPointerException e) {
            mNameTV.setText("哈喽，" + getGreetings());
        }
        mWaitDoTV.setText(data.getOpened());
        mOutdateTV.setText(data.getOutdate());
        mIngTV.setText(data.getUnderway());
        mAllTV.setText(data.getFinished());
    }

}
