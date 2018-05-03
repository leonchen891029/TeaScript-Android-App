package com.teacore.teascript.team.fragment.viewpagerfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.teacore.teascript.R;
import com.teacore.teascript.adapter.ViewPagerFragmentAdapter;
import com.teacore.teascript.base.BaseViewPagerFragment;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.fragment.TeamBoardFragment;
import com.teacore.teascript.team.fragment.TeamIssueListFragment;
import com.teacore.teascript.team.fragment.TeamMemberFragment;
import com.teacore.teascript.team.activity.TeamMainActivity;
import com.teacore.teascript.team.activity.TeamNewActiveActivity;
import com.teacore.teascript.util.UiUtils;

/**Team主界面的ViewPager
 * @author陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */

public class TeamMainViewPagerFragment extends BaseViewPagerFragment{

    private Team mTeam;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_team_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.team_new_active:
                showCreateNewActive();
                break;
            case R.id.team_new_issue:
                UiUtils.showCreateNewIssue(getActivity(), mTeam, null, null);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCreateNewActive(){
        Intent intent=new Intent(getActivity(),TeamNewActiveActivity.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable(TeamMainActivity.BUNDLE_KEY_TEAM,mTeam);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        setHasOptionsMenu(true);
        mViewPager.setOffscreenPageLimit(2);
        Bundle bundle=getArguments();
        if(bundle!=null){
            mTeam=(Team) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
        }
    }

    @Override
    public void onClick(View view){

    }

    @Override
    public void initView(View view){

    }

    @Override
    public void initData(){

    }

    @Override
    protected void onSetupTabAdapter(ViewPagerFragmentAdapter adapter){

        FrameLayout generalActionBar=(FrameLayout) mRootView.findViewById(R.id.general_actionbar);
        generalActionBar.setVisibility(View.GONE);

        String[] strings=getResources().getStringArray(R.array.team_main_viewpager);

        adapter.addTab(strings[0], "team_board", TeamBoardFragment.class,
                getArguments());

        Bundle issueFragmentBundle = getArguments();
        issueFragmentBundle.putBoolean("needmenu", false);
        adapter.addTab(strings[1], "team_issue", TeamIssueListFragment.class,
                issueFragmentBundle);
        adapter.addTab(strings[2], "team_member", TeamMemberFragment.class,
                getArguments());


    }


}
