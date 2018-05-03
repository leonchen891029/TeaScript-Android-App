package com.teacore.teascript.team.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.adapter.TeamProjectAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.team.bean.TeamProjectList;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 团队项目列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-26
 */

public class TeamProjectListFragment extends BaseListFragment<TeamProject>{

    private Team mTeam;

    private int mTeamId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Team team = (Team) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
            if (team != null) {
                mTeam = team;
                mTeamId = mTeam.getId();
            }
        }
    }

    @Override
    protected TeamProjectAdapter getListAdapter() {
        return new TeamProjectAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return "team_project_list_" + mTeamId + "_" + mCurrentPage;
    }

    @Override
    protected TeamProjectList parseList(InputStream is) throws Exception {
        TeamProjectList list = XmlUtils.toBean(TeamProjectList.class, is);
        return list;
    }

    @Override
    protected TeamProjectList readList(Serializable seri) {
        return ((TeamProjectList) seri);
   }

    @Override
    protected void sendRequestData() {
        TeaScriptTeamApi.getTeamProjectList(mTeamId, mHandler);
    }

    @Override
    protected void executeOnLoadDataSuccess(List<TeamProject> data) {
        super.executeOnLoadDataSuccess(data);
        if (mAdapter.getDatas().isEmpty()) {
            setNoProject();
        }
        mAdapter.setState(BaseListAdapter.STATE_NO_MORE);
    }

    private void setNoProject() {
        mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        mEmptyLayout.setEmptyImage(R.drawable.page_icon_empty);
        String str = getResources().getString(R.string.team_project_empty);
        mEmptyLayout.setEmptyMessage(str);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeamProject teamProject = mAdapter.getItem(position);
        if (teamProject != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_TEAM, mTeam);
            bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_PROJECT, teamProject);
            UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.TEAM_PROJECT_MAIN, bundle);
        }
    }

}
