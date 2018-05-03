package com.teacore.teascript.team.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.R;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.team.adapter.TeamMemberAdapter;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamMember;
import com.teacore.teascript.team.bean.TeamMemberList;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 团队成员界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-13
 */

public class TeamMemberFragment extends BaseFragment{

    public static final String TEAM_MEMBER_FILE="teammemberfragment_cache_file";
    public static String TEAM_MEMBER_KEY="teammemberfragment_key";
    public static String TEAM_MEMBER_DATA="teammemberfragment_key";

    //多行多列列表
    private GridView mGridView;
    private EmptyLayout mEmptyLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Activity activity;
    private Team mTeam;
    private List<TeamMember> datas=null;
    private long preRefreshTime;
    private TeamMemberAdapter adapter;

    @Override
    public void onCreate(Bundle savedIntanceState){
        super.onCreate(savedIntanceState);
        Bundle args=getArguments();

        if (args != null) {
            mTeam = (Team) args
                    .getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
        }

        TEAM_MEMBER_KEY += mTeam.getId();
        TEAM_MEMBER_DATA += mTeam.getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        View rootView=inflater.inflate(R.layout.fragment_team_member,container,false);

        initView(rootView);

        activity=getActivity();

        TeamMemberList list=(TeamMemberList) CacheManager.readObject(activity,TEAM_MEMBER_DATA);

        if(list==null){
            initData();
        }else{
            datas=list.getList();
            adapter=new TeamMemberAdapter(activity,datas,mTeam);
            mGridView.setAdapter(adapter);
        }

        return rootView;
    }

    @Override
    public void onClick(View view){}

    @Override
    public void initView(View view){

        mSwipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.team_member_srl);
        mGridView=(GridView) view.findViewById(R.id.team_member_gv);
        mEmptyLayout=(EmptyLayout) view.findViewById(R.id.team_member_el);

        mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh(true);
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UiUtils.showTeamMemberInfo(activity,mTeam.getId(),datas.get(i));
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mState==STATE_REFRESH){
                    return;
                }
                refresh(false);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    @Override
    public void initData(){
        refresh(true);
    }

    //刷新列表数据
    private void refresh(final boolean isFirst){
        final long currentTime=System.currentTimeMillis();
        if (currentTime - preRefreshTime < 100000) {
            setSwipeRefreshLoadedState();
            return;
        }

        TeaScriptApi.getTeamMemberList(mTeam.getId(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (isFirst) {
                    mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                }
            }

            @Override
            public void onSuccess(int i, Header[] headers, final byte[] bytes) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        TeamMemberList list= XmlUtils.toBean(TeamMemberList.class,bytes);

                        CacheManager.saveObject(activity,list,TEAM_MEMBER_DATA);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (adapter == null) {
                                    adapter = new TeamMemberAdapter(
                                            activity, datas, mTeam);
                                    mGridView.setAdapter(adapter);
                                } else {
                                    adapter.refresh(datas);
                                }

                                preRefreshTime=currentTime;
                                if(isFirst){
                                    mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
                                }
                                setSwipeRefreshLoadedState();
                            }
                        });

                    }
                });

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast("成员信息获取失败");
                if (isFirst) {
                    mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
                }
                setSwipeRefreshLoadedState();
            }
        });

    }

    //设置顶部加载完毕的状态
    private void setSwipeRefreshLoadedState() {
        mState = STATE_NOMORE;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

}
