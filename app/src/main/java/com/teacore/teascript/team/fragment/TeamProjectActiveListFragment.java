package com.teacore.teascript.team.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.adapter.TeamActiveAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamActive;
import com.teacore.teascript.team.bean.TeamActiveList;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 项目动态的列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-21
 */

public class TeamProjectActiveListFragment extends BaseListFragment<TeamActive>{

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
    public void onResume() {
        super.onResume();
        mListView.setSelector(new ColorDrawable(ContextCompat.getColor(getContext(),android.R.color.transparent)));
        mListView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(),android.R.color.transparent)));
    }

    @Override
    protected TeamActiveAdapter getListAdapter() {
        return new TeamActiveAdapter(getActivity());
    }

    @Override
    protected String getCacheKeyPrefix() {
        return "team_project_active_list_" + mTeamId + "_"
                + mTeamProject.getGit().getId();
    }

    @Override
    protected TeamActiveList parseList(InputStream is) throws Exception {
        TeamActiveList list = XmlUtils.toBean(TeamActiveList.class, is);
        return list;
    }

    @Override
    protected TeamActiveList readList(Serializable seri) {
        return ((TeamActiveList) seri);
    }

    @Override
    protected void sendRequestData() {
        TeaScriptTeamApi.getTeamProjectActiveList(mTeamId, mTeamProject, "all",
                mCurrentPage, mHandler);
    }

    @Override
    protected void executeOnLoadDataSuccess(List<TeamActive> data) {
        super.executeOnLoadDataSuccess(data);
        if (mAdapter.getDatas().isEmpty()) {
            setNoProjectActive();
        }
        mAdapter.setState(BaseListAdapter.STATE_NO_MORE);
    }

    private void setNoProjectActive() {
        mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        mEmptyLayout.setEmptyImage(R.drawable.page_icon_empty);
        String str = getResources().getString(
                R.string.team_project_empty_active);
        mEmptyLayout.setEmptyMessage(str);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeamActive active = mAdapter.getItem(position);
        if (active != null) {
            UiUtils.showTeamActiveDetail(getActivity(), mTeam.getId(), active);
        }
    }

}
