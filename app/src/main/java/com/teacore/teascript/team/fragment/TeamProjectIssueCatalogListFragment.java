package com.teacore.teascript.team.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.adapter.TeamIssueCatalogAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamIssueCatalog;
import com.teacore.teascript.team.bean.TeamIssueCatalogList;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 任务分组列表
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-21
 */

public class TeamProjectIssueCatalogListFragment extends BaseListFragment<TeamIssueCatalog>{

    private Team mTeam;

    private TeamProject mTeamProject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTeam = (Team) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
            mTeamProject = (TeamProject) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_PROJECT);
        }
    }

    @Override
    protected TeamIssueCatalogAdapter getListAdapter() {
        return new TeamIssueCatalogAdapter();
    }

    @Override
    protected TeamIssueCatalogList parseList(InputStream is)
            throws Exception {
        return XmlUtils.toBean(TeamIssueCatalogList.class, is);
    }

    @Override
    protected TeamIssueCatalogList readList(Serializable seri) {
        return (TeamIssueCatalogList) seri;
    }

    @Override
    protected void sendRequestData() {
        int uid = AppContext.getInstance().getLoginUid();
        int teamId= mTeam.getId();
        int projectId = mTeamProject.getGit().getId();
        String source = mTeamProject.getSource();
        TeaScriptTeamApi.getTeamProjectIssueList(uid, teamId, projectId, source, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeamIssueCatalog teamIssueCatalog = mAdapter.getItem(position);
        if (teamIssueCatalog != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_TEAM, mTeam);
            bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_PROJECT, mTeamProject);
            bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_ISSUE_CATALOG, teamIssueCatalog);
            UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.TEAM_ISSUE, bundle);
        }
    }

    @Override
    protected String getCacheKeyPrefix() {
        return "team_issue_catalog_list" + mTeam.getId() + "_" + mTeamProject.getGit().getId();
    }

}
