package com.teacore.teascript.team.fragment.viewpagerfragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.team.fragment.TeamProjectActiveListFragment;
import com.teacore.teascript.team.fragment.TeamProjectIssueCatalogListFragment;
import com.teacore.teascript.team.fragment.TeamProjectMemberListFragment;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;

/**
 * 团队项目ViewPagerFragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-18
 */

public class TeamProjectViewPagerFragment extends BaseViewPagerFragment{

    private Team mTeam;
    private TeamProject mTeamProject;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if(bundle!=null){
            mTeam=(Team) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
            mTeamProject=(TeamProject) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_PROJECT);
            ((BaseActivity) getActivity()).setActionBarTitle(mTeamProject.getGit().getName());
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_team_project,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.team_new_issue:
                UiUtils.showCreateNewIssue(getActivity(), mTeam, mTeamProject, null);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter){
        adapter.addTab("项目任务分组", "issue",TeamProjectIssueCatalogListFragment.class, getBundle());
        adapter.addTab("项目动态", "active", TeamProjectActiveListFragment.class, getBundle());
        adapter.addTab("项目成员", "member", TeamProjectMemberListFragment.class, getBundle());
    }

    private Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_TEAM, mTeam);
        bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_PROJECT, mTeamProject);
        return bundle;
    }

}
