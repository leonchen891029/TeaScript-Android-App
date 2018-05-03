package com.teacore.teascript.team.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.adapter.TeamIssueAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamIssue;
import com.teacore.teascript.team.bean.TeamIssueList;
import com.teacore.teascript.team.fragment.viewpagerfragment.TeamMyIssueViewPagerFragment;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 我的团队任务列表Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-16
 */

public class TeamMyIssueListFragment extends BaseListFragment<TeamIssue>{

    protected static final String TAG=TeamMyIssueListFragment.class.getSimpleName();
    private  static final String  CACHE_KEY_PREFIX="team_my_issue_";

    private Team mTeam;
    private String mType="all";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if(bundle!=null){
            mTeam=(Team) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
            mType=bundle.getString(TeamMyIssueViewPagerFragment.TEAM_MY_ISSUE_KEY);
        }
    }

    @Override
    public void initView(View view){
        super.initView(view);
        mListView.setDivider(new ColorDrawable(0x00000000));
        mListView.setSelector(new ColorDrawable(0x00000000));
    }

    @Override
    protected TeamIssueAdapter getListAdapter(){
         return new TeamIssueAdapter();
    }

    @Override
    protected String getCacheKeyPrefix(){
        return CACHE_KEY_PREFIX+ AppContext.getInstance().getLoginUid()+"_"+mTeam.getId()+mCurrentPage+mType;
    }

    @Override
    protected TeamIssueList parseList(InputStream inputStream){
        TeamIssueList list= XmlUtils.toBean(TeamIssueList.class,inputStream);
        return list;
    }

    @Override
    protected TeamIssueList readList(Serializable seri){
        return (TeamIssueList) seri;
    }

    @Override
    protected void sendRequestData(){
        TeaScriptApi.getMyIssue(mTeam.getId()+"",AppContext.getInstance().getLoginUid()+"",mCurrentPage,mType,mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeamIssue item = mAdapter.getItem(position);

        if (item != null) {
            UiUtils.showTeamIssueDetail(getActivity(), mTeam, item, null);
        }
    }


}
