package com.teacore.teascript.team.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.adapter.TeamProjectMemberAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamMember;
import com.teacore.teascript.team.bean.TeamMemberList;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 团队项目成员列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-21
 */

public class TeamProjectMemberListFragment extends BaseListFragment<TeamMember>{

    private Team mTeam;

    private int mTeamId;

    private TeamProject mTeamProject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTeam = (Team) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
            mTeamProject = (TeamProject) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_PROJECT);
            mTeamId = mTeam.getId();
        }
    }

    @Override
    protected TeamProjectMemberAdapter getListAdapter() {
        return new TeamProjectMemberAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return "team_project_member_list_" + mTeamId + "_" + mTeamProject.getGit().getId();
    }

    @Override
    protected TeamMemberList parseList(InputStream is) throws Exception {
        TeamMemberList list = XmlUtils.toBean(
                TeamMemberList.class, is);
        return list;
    }

    @Override
    protected TeamMemberList readList(Serializable seri) {
        return ((TeamMemberList) seri);
    }

    @Override
    protected void sendRequestData() {
        TeaScriptTeamApi.getTeamProjectMemberList(mTeamId, mTeamProject,
                mHandler);
    }

    @Override
    protected void executeOnLoadDataSuccess(List<TeamMember> data) {
        super.executeOnLoadDataSuccess(data);
        if (mAdapter.getDatas().isEmpty()) {
            setNoProjectMember();
        }
        mAdapter.setState(BaseListAdapter.STATE_NO_MORE);
    }

    private void setNoProjectMember() {
        mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        mEmptyLayout.setEmptyImage(R.drawable.page_icon_empty);
        String str = getResources().getString(R.string.team_project_empty_member);
        mEmptyLayout.setEmptyMessage(str);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeamMember teamMember = mAdapter.getItem(position);
        if (teamMember != null) {
            UiUtils.showTeamMemberInfo(getActivity(), mTeamId, teamMember);
        }
    }

}
