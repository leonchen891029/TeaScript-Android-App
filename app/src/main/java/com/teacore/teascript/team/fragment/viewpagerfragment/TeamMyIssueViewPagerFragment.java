package com.teacore.teascript.team.fragment.viewpagerfragment;

import android.os.Bundle;
import android.view.View;

import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.bean.TeamIssue;
import com.teacore.teascript.team.fragment.TeamBoardFragment;
import com.teacore.teascript.team.fragment.TeamMyIssueListFragment;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;

/**
 * 我的团队任务ViewPagerFragment界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-15
 */

public class TeamMyIssueViewPagerFragment extends BaseViewPagerFragment{

    public static final String TEAM_MY_ISSUE_KEY="team_my_issue_key";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager.setOffscreenPageLimit(2);
    }


    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {
        adapter.addTab("待办中", TeamIssue.TEAM_ISSUE_STATE_OPENED, TeamMyIssueListFragment.class, getBundle(TeamIssue.TEAM_ISSUE_STATE_OPENED));

        adapter.addTab("进行中", TeamIssue.TEAM_ISSUE_STATE_UNDERWAY, TeamMyIssueListFragment.class, getBundle(TeamIssue.TEAM_ISSUE_STATE_UNDERWAY));

        adapter.addTab("已完成", TeamIssue.TEAM_ISSUE_STATE_CLOSED, TeamMyIssueListFragment.class, getBundle(TeamIssue.TEAM_ISSUE_STATE_CLOSED));

        adapter.addTab("已验收", TeamIssue.TEAM_ISSUE_STATE_ACCEPTED, TeamMyIssueListFragment.class, getBundle(TeamIssue.TEAM_ISSUE_STATE_ACCEPTED));
    }

    private Bundle getBundle(String state) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_TEAM, getArguments().getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM));
        bundle.putString(TeamMyIssueViewPagerFragment.TEAM_MY_ISSUE_KEY, state);
        return bundle;
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentPage = 0;
        try {
            currentPage = getArguments().getInt(TeamBoardFragment.WHICH_PAGER_KEY, 0);
        } catch (NullPointerException e) {
        }
        mViewPager.setCurrentItem(currentPage);
    }



}
