package com.teacore.teascript.team.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.adapter.TeamActiveAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamActive;
import com.teacore.teascript.team.bean.TeamActiveList;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.TLog;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**Team动态界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */

public class TeamActiveListFragment extends BaseListFragment<TeamActive>{

    public static final String BUNDLE_KEY_UID="UID";
    public static final String ACTIVE_FRAGMENT_KEY="DynamicFragment";
    public static final String ACTIVE_FRAGMENT_TEAM_KEY="DynamicFragmentTeam";

    protected static final String TAG=TeamActiveListFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX="DynamicFragment_list";

    private Activity activity;
    private Team team;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        if(args!=null){
            team=(Team) args.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
        }
        if(team==null){
            team=new Team();
            TLog.log(getClass().getSimpleName(),"team对象初始化异常");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        activity = getActivity();
        return view;
    }

    @Override
    public void initView(View view){
        super.initView(view);
        mListView.setDivider(new ColorDrawable(0x00000000));
        mListView.setSelector(new ColorDrawable(0x00000000));
    }

    @Override
    protected TeamActiveAdapter getListAdapter() {
        return new TeamActiveAdapter(activity);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + team.getId() + "_" + mCurrentPage;
    }

    @Override
    protected TeamActiveList parseList(InputStream inputStream) throws Exception{
        TeamActiveList list= XmlUtils.toBean(TeamActiveList.class,inputStream);
        if(list.getList()==null){
            list.setActives(new ArrayList<TeamActive>());
        }
        return list;
    }

    @Override
    protected TeamActiveList readList(Serializable seri){
        return (TeamActiveList) seri;
    }

    @Override
    protected void sendRequestData(){
        TeaScriptApi.teamDynamic(team,mCurrentPage,mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        try {
            TeamActive active = mAdapter.getItem(position);
            if (active != null) {
                UiUtils.showTeamActiveDetail(activity, team.getId(), active);
            }
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Override
    protected long getAutoRefreshTime() {
        // 1小时间距，主动刷新列表
        return 1 * 60 * 60;
    }

























}
