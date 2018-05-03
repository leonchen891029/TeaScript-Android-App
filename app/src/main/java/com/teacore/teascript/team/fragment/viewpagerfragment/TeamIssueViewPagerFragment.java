package com.teacore.teascript.team.fragment.viewpagerfragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamGit;
import com.teacore.teascript.team.bean.TeamIssueCatalog;
import com.teacore.teascript.team.bean.TeamIssueCatalogList;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.team.fragment.TeamIssueListFragment;
import com.teacore.teascript.team.widget.TeamProjectPopupWindow;
import com.teacore.teascript.team.widget.TeamProjectPopupWindow.TeamProjectPopupWindowCallBack;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.EmptyLayout;

import java.io.ByteArrayInputStream;

import cz.msebera.android.httpclient.Header;

/**
 * 团队任务ViewPagerFragment
 * Created by apple on 17/12/14.
 */

public class TeamIssueViewPagerFragment extends BaseViewPagerFragment {

    private Team mTeam;
    private int mTeamId;
    private TeamIssueCatalogList mIssueCatalogList;
    private TeamProject mTeamProject;

    //-1表示显示所有的任务列表
    private int mProjectId=-1;

    private TeamProjectPopupWindow mProjectPW;

    private AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            TeamIssueCatalogList catalogList = XmlUtils.toBean(TeamIssueCatalogList.class, new ByteArrayInputStream(arg2));
            if (catalogList != null) {
                mIssueCatalogList = catalogList;
                mViewPagerAdapter.removeAll();
                addCatalogList();
            } else {
                onFailure(arg0, arg1, arg2, null);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
        }

    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_team_issue, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.team_issue_change_state:
                showProjectsSelectDialog();
                break;
            case R.id.team_new_issue:
                UiUtils.showCreateNewIssue(getActivity(), mTeam, mTeamProject, null);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private TeamProjectPopupWindowCallBack mCallBack = new TeamProjectPopupWindowCallBack() {

        @Override
        public void callBack(TeamProject teamProject) {
            if (teamProject.getGit().getId() == mProjectId) {
                return;
            }
            mProjectId = teamProject.getGit().getId();
            mTeamProject = teamProject;
            sendRequestCatalogList();
        }

    };

    private void showProjectsSelectDialog() {
        if (mProjectPW == null) {
            mProjectPW = new TeamProjectPopupWindow(getActivity(),
                    mTeam, mCallBack);
        }
        mProjectPW.showAsDropDown(((BaseActivity) getActivity())
                .getActionBar().getCustomView());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Team team = (Team) bundle
                    .getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
            if (team != null) {
                mTeam = team;
                mTeamId = StringUtils.toInt(mTeam.getId());
            }
        }
        mTeamProject = getDefaultProject();
        setHasOptionsMenu(true);
    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter) {
        sendRequestCatalogList();
    }

    private void sendRequestCatalogList() {
        int uid = AppContext.getInstance().getLoginUid();
        TeaScriptTeamApi.getTeamProjectIssueList(uid, mTeamId, mProjectId, "",
                handler);
    }

    private void addCatalogList() {
        if (mIssueCatalogList != null && ! mIssueCatalogList.getList().isEmpty()
                && mViewPagerAdapter != null) {
            for (TeamIssueCatalog catalog : mIssueCatalogList.getList()) {
                Bundle bundle = getBundle(mTeam, mTeamProject, catalog);
                mViewPagerAdapter.addTab(catalog.getTitle(), catalog.getTitle(),
                        TeamIssueListFragment.class, bundle);
            }
            mViewPagerAdapter.notifyDataSetChanged();
        }
    }

    private Bundle getBundle(Team team, TeamProject teamProject, TeamIssueCatalog issueCatalog) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_TEAM, team);
        bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_PROJECT, teamProject);
        bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_ISSUE_CATALOG, issueCatalog);
        return bundle;
    }

    private TeamProject getDefaultProject() {
        TeamProject project = new TeamProject();
        project.setSource("Team@OSC");
        TeamGit git = new TeamGit();
        git.setId(-1);
        git.setName("所有任务");
        project.setGit(git);
        return project;
    }



}
