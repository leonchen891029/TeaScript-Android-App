package com.teacore.teascript.team.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.EntityList;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.adapter.TeamDiscussAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamDiscuss;
import com.teacore.teascript.team.bean.TeamDiscussList;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**team讨论区列表界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-10
 */

public class TeamDiscussListFragment extends BaseListFragment<TeamDiscuss> {

    protected static final String TAG=TeamDiscussListFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX="team_discuss_list_";

    private Team mTeam;
    private int mTeamId;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        if(args!=null){
            Team team=(Team) args.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
            if(team!=null){
                mTeam=team;
                mTeamId= StringUtils.toInt(mTeam.getId());
            }
        }
    }

    @Override
    protected TeamDiscussAdapter getListAdapter(){
        return new TeamDiscussAdapter();
    }

    //获取当前展示页面的缓存数据
    @Override
    protected String getCacheKeyPrefix(){
        return CACHE_KEY_PREFIX+mTeamId+"_"+mCurrentPage;
    }

    @Override
    protected TeamDiscussList parseList(InputStream is)
            throws Exception {
        TeamDiscussList list = XmlUtils.toBean(
                TeamDiscussList.class, is);
        return list;
    }

    @Override
    protected EntityList<TeamDiscuss> readList(Serializable seri) {
        return ((TeamDiscussList) seri);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        TeamDiscuss item = (TeamDiscuss) mAdapter.getItem(position);
        if (item != null) {
            UiUtils.showTeamDiscussDetail(getActivity(), mTeam, item);
        }

    }



}
