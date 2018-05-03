package com.teacore.teascript.team.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.network.remote.TeaScriptTeamApi;
import com.teacore.teascript.team.adapter.TeamProjectAdapterPW;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamGit;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.team.bean.TeamProjectList;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 团队项目选择
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-21
 */

public class TeamProjectPopupWindow extends PopupWindow implements OnItemClickListener{

    private final Team mTeam;

    private LayoutInflater inflater;

    private ListView mListView;

    private EmptyLayout mEmptyLayout;

    private TeamProjectAdapterPW mAdapter;

    private final TeamProjectPopupWindowCallBack mCallBack;

    private TeamProject mCurrentProject;

    private final AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onStart() {
            super.onStart();
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            TeamProjectList teamProjectList = XmlUtils.toBean(TeamProjectList.class, new ByteArrayInputStream(arg2));
            loadSuccess(teamProjectList.getList());
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            mEmptyLayout.setEmptyMessage("" + teamProjectList.getList().size());
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
        }

    };

    public TeamProjectPopupWindow(final Activity context, Team team,TeamProjectPopupWindowCallBack callBack) {
        super(context);
        this.mTeam = team;
        this.mCallBack = callBack;
        initView(context);
        initData();
    }

    private void initView(Context context) {

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = inflater.inflate(R.layout.popup_window_team_project, null);

        mListView = (ListView) view.findViewById(R.id.listview);
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);
        setContentView(view);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 所有的的DropDownMenu的根的ID都需要时set_up
                int height = view.findViewById(R.id.team_project_pw).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        mListView.setOnItemClickListener(this);
    }

    private void initData() {
        mAdapter = new TeamProjectAdapterPW();
        mListView.setAdapter(mAdapter);
        getProjectsList();
    }

    private void loadSuccess(List<TeamProject> list) {
        addAllIssueOption(list);
        mAdapter.addDatas(list);
    }

    private void getProjectsList() {
        TeaScriptTeamApi.getTeamProjectList(mTeam.getId(), mHandler);
    }

    private void addAllIssueOption(List<TeamProject> list) {

        TeamProject unProjectIssue = new TeamProject();
        TeamGit unGit = new TeamGit();
        unProjectIssue.setSource("");
        unGit.setId(0);// 0表示非项目任务
        unGit.setName("非项目任务");
        unProjectIssue.setGit(unGit);

        list.add(0, unProjectIssue);

        TeamProject allIssue = new TeamProject();
        TeamGit allGit = new TeamGit();
        allIssue.setSource("");
        allGit.setId(-1);// -1表示
        allGit.setName("所有任务");
        allIssue.setGit(allGit);

        list.add(0, allIssue);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeamProject project = mAdapter.getItem(position);
        if (mCallBack != null && project != null) {
            mCallBack.callBack(project);
        }
        this.dismiss();
    }

    public interface TeamProjectPopupWindowCallBack {
        public void callBack(TeamProject teamProject);
    }

}
