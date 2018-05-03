package com.teacore.teascript.team.fragment;


import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.EntityList;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.adapter.TeamIssueAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamIssue;
import com.teacore.teascript.team.bean.TeamIssueCatalog;
import com.teacore.teascript.team.bean.TeamIssueList;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.dialog.DialogUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**任务列表fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-8
 */

public class TeamIssueListFragment extends BaseListFragment<TeamIssue>{

    protected static final String TAG = TeamIssueListFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "team_issue_list_";
    private String issueState = TeamIssue.TEAM_ISSUE_STATE_OPENED;

    private Team mTeam;
    private TeamProject mProject;
    private TeamIssueCatalog mCatalog;
    private int mTeamId;
    private int mProjectId;
    private int mCatalogId = -1;
    private boolean isNeedMenu;

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

            TeamProject project = (TeamProject) bundle
                    .getSerializable(TeamMainActivity.BUNDLE_KEY_PROJECT);

            if (project != null) {
                this.mProject = project;
                this.mProjectId = project.getGit().getId();
            } else {
                this.mProjectId = -1;
            }

            TeamIssueCatalog catalog = (TeamIssueCatalog) bundle
                    .getSerializable(TeamMainActivity.BUNDLE_KEY_ISSUE_CATALOG);

            if (catalog != null) {
                this.mCatalog = catalog;
                this.mCatalogId = catalog.getId();
                String title = catalog.getTitle() + "("
                        + catalog.getOpenedIssueCount() + "/"
                        + catalog.getAllIssueCount() + ")";
                ((BaseActivity) getActivity()).setActionBarTitle(title);
            }
            isNeedMenu = bundle.getBoolean("needmenu", true);
        }
        setHasOptionsMenu(isNeedMenu);
    }

    @Override
    public void onResume(){
        super.onResume();
        mListView.setSelector(android.R.color.transparent);
        mListView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(),android.R.color.transparent)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_team_issue, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            case R.id.team_new_issue:
                UiUtils.showCreateNewIssue(getActivity(),mTeam,mProject,mCatalog);
                break;
            case R.id.team_issue_change_state:
                changeShowIssueState();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    AlertDialog dialog=null;

    private void changeShowIssueState(){
        String[] items={"所有任务","待办中","进行中","已完成","已验收"};

        final CharSequence[] itemsEn = {"all",
                TeamIssue.TEAM_ISSUE_STATE_OPENED,
                TeamIssue.TEAM_ISSUE_STATE_UNDERWAY,
                TeamIssue.TEAM_ISSUE_STATE_CLOSED,
                TeamIssue.TEAM_ISSUE_STATE_ACCEPTED};

        int index=0;
        for (int i = 0; i < itemsEn.length; i++) {
            if (issueState.equals(itemsEn[i])) {
                index = i;
            }
        }

        dialog = DialogUtils.getSingleChoiceDialog(getActivity(), "选择任务状态", items, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                issueState = (itemsEn[i]).toString();

                onRefresh();
                dialog.dismiss();
            }
        }).show();

    }

    @Override
    protected TeamIssueAdapter getListAdapter() {
        return new TeamIssueAdapter();
    }

    public TeamIssueCatalog getTeamIssueCatalog() {
        return this.mCatalog;
    }

    //获取当前展示页面的缓存数据
    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mTeamId + "_" + mProjectId + "_" + mCatalogId
                + "_" + issueState;
    }

    @Override
    protected EntityList<TeamIssue> parseList(InputStream inputStream)throws Exception{
        TeamIssueList list= XmlUtils.toBean(TeamIssueList.class,inputStream);
        return list;
    }

    @Override
    protected EntityList<TeamIssue> readList(Serializable serializable){
        return ((TeamIssueList) serializable);
    }

    @Override
    protected void executeOnLoadDataSuccess(List<TeamIssue> data) {
        super.executeOnLoadDataSuccess(data);
        if (mAdapter.getCount() == 1) {
            setNoTeamIssue();
        }
    }

    private void setNoTeamIssue() {
        mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        mEmptyLayout.setEmptyImage(R.drawable.page_icon_empty);
        String str = getResources().getString(R.string.team_empty_issue);
        mEmptyLayout.setEmptyMessage(str);
    }

    @Override
    protected void sendRequestData() {
        int teamId = this.mTeamId;
        int projectId = this.mProjectId;
        int catalogId = mCatalogId;
        String source = mProject == null ? "" : mProject.getSource();
        int uid = 0;
        String scope = "";
        TeaScriptTeamApi.getTeamIssueList(teamId, projectId, catalogId, source,
                uid, issueState, scope, mCurrentPage, AppContext.PAGE_SIZE,
                mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeamIssue issue = mAdapter.getItem(position);
        if (issue != null) {
            UiUtils.showTeamIssueDetail(getActivity(), mTeam, issue, mCatalog);
        }
    }

    @Override
    protected long getAutoRefreshTime() {
        // 1小时间距，主动刷新列表
        return 1 * 60 * 60;
    }


}
