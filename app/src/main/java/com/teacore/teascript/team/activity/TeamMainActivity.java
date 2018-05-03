package com.teacore.teascript.team.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamList;
import com.teacore.teascript.team.fragment.viewpagerfragment.TeamMainViewPagerFragment;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.EmptyLayout;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 团队主界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */

public class TeamMainActivity extends BaseActivity implements ActionBar.OnNavigationListener{

    public static final String BUNDLE_KEY_TEAM="bundle_key_team";
    public static final String BUNDLE_KEY_PROJECT="bundle_key_project";
    public static final String BUNDLE_KEY_ISSUE_CATALOG="bundle_key_issue_catalog";

    private final String TEAM_LIST_FILE="team_list_file";
    private final String TEAM_LIST_KEY="team_list_key"+ AppContext.getInstance().getLoginUid();
    private FragmentManager mFragmentManager;
    private int mCurrentContentIndex=-1;

    private EmptyLayout mEmptyLayout;
    private View container;

    @Override
    protected boolean hasBackButton(){
        return true;
    }

    @Override
    protected int getLayoutId(){
        return R.layout.activity_team_main;
    }

    @Override
    public void onClick(View v){

    }

    @Override
    public void initView(){
        mEmptyLayout=(EmptyLayout) findViewById(R.id.error_layout);
        container=findViewById(R.id.main_content);

        //隐藏actionbar的标题
        mActionBar.setDisplayShowTitleEnabled(false);
        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
        mEmptyLayout.setEmptyMessage("获取团队中");
        mEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                requestTeamList();
            }
        });


        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        adapter = new TSSpinnerAdapter(this, teamName);
        mActionBar.setListNavigationCallbacks(adapter, this);
        requestTeamList();

        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void initData(){

    }

    private TSSpinnerAdapter adapter;

    private final List<String> teamName = new ArrayList<String>();
    private List<Team> teamDatas = new ArrayList<Team>();

    private void switchTeam(int pos) {
        if (pos == mCurrentContentIndex)
            return;
        showWaitDialog("正在切换...");
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        String tag = "team_view";
        Fragment mfragment = mFragmentManager.findFragmentByTag(tag);
        if (mfragment != null) {
            fragmentTransaction.remove(mfragment);
        }
        try {
            TeamMainViewPagerFragment fragment = TeamMainViewPagerFragment.class
                    .newInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_KEY_TEAM, teamDatas.get(pos));
            fragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.main_content, fragment, tag);
            fragmentTransaction.commitAllowingStateLoss();
            mCurrentContentIndex = pos;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        hideWaitDialog();
    }

    private void requestTeamList() {
        // 初始化团队列表数据
        String cache=AppContext.get(TEAM_LIST_FILE,"");

        if (!StringUtils.isEmpty(cache)) {
            // mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            teamDatas = TeamList.toTeamList(cache);
            setTeamDataState();
        }

        TeaScriptApi.teamList(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                TeamList datas = XmlUtils.toBean(TeamList.class, arg2);
                if (teamDatas.isEmpty() && datas != null) {
                    teamDatas.addAll(datas.getList());
                    setTeamDataState();
                } else {
                    if (teamDatas == null && datas == null) {
                        AppContext.showToast(new String(arg2));
                        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
                        mEmptyLayout.setEmptyMessage("获取团队失败");
                    }
                }

                if (datas != null) {
                    // 保存新的团队列表
                    AppContext.set(TEAM_LIST_KEY,datas.toCacheData());
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                //AppContext.showToast("网络不好，请稍后重试");
            }
        });
    }

    private void setTeamDataState() {
        if (teamDatas == null) {
            teamDatas = new ArrayList<Team>();
        }
        if (teamDatas.isEmpty()) {
            mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
            String msg = getResources().getString(R.string.team_empty);
            mEmptyLayout.setEmptyMessage(msg);
            mEmptyLayout.setEmptyImage(R.drawable.page_icon_empty);
        } else {
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            container.setVisibility(View.VISIBLE);
        }
        for (Team team : this.teamDatas) {
            teamName.add(team.getName());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        Team team = teamDatas.get(itemPosition);
        if (team != null) {
            switchTeam(itemPosition);
            adapter.setSelectIndex(itemPosition);
        }

        return false;
    }

    public class TSSpinnerAdapter extends BaseAdapter {

        private final List<String> teams;

        private final Context context;

        private int selectIndex = 0;

        public void setSelectIndex(int index) {
            this.selectIndex = index;
        }

        public TSSpinnerAdapter(Context context, List<String> teams) {
            this.teams = teams;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(context, R.layout.spinner_head, null);
            }
            ((TextView) convertView).setText(getItem(position));

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.list_cell_team, null, false);
            }
            String team = getItem(position);
            TextView tv = (TextView) convertView.findViewById(R.id.name_tv);
            if (team != null) {
                tv.setText(team);
            }
            if (selectIndex != position) {
                tv.setTextColor(Color.parseColor("#acd4b3"));
            } else {
                tv.setTextColor(Color.parseColor("#6baf77"));
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return teams.size();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public String getItem(int position) {
            return teams.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

}
