package com.teacore.teascript.team.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.module.back.BackActivity;
import com.teacore.teascript.module.detail.DetailActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.team.adapter.TeamActiveAdapter;
import com.teacore.teascript.team.adapter.TeamMemberAdapter;
import com.teacore.teascript.team.bean.TeamActive;
import com.teacore.teascript.team.bean.TeamActiveList;
import com.teacore.teascript.team.bean.TeamMember;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;

import java.io.InputStream;
import java.io.Serializable;


/**
 * 团队成员信息列表Fragment(列表Adapter是TeamActive)
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-14
 */

public class TeamMemberInformationFragment extends BaseListFragment<TeamActive>{

    private AvatarView mUserAV;
    private TextView mNameTV;
    private TextView mUserNameTV;
    private TextView mEmailTV;
    private TextView mTelephoneTV;
    private TextView mAddressTV;
    private ImageView mTelephoneIV;

    private Activity aty;
    private int mTeamId;
    private TeamMember mTeamMember;

    protected static final String TAG=TeamMemberInformationFragment.class.getSimpleName();

    private static String CACHE_KEY_PREFIX="team_active_list";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle bundle=getActivity().getIntent().getBundleExtra(BackActivity.BUNDLE_KEY_ARGS);

        if(bundle!=null){
            mTeamId=bundle.getInt(TeamMemberAdapter.TEAM_ID_KEY);
            mTeamMember=(TeamMember) bundle.getSerializable(TeamMemberAdapter.TEAM_MEMBER_KEY);
        }else{
            mTeamMember=new TeamMember();
        }

        aty=getActivity();
    }

    @Override
    public void initView(View view){
        View headerView=View.inflate(aty, R.layout.view_team_member_information_head,null);

        mUserAV=(AvatarView) headerView.findViewById(R.id.user_av);
        mNameTV=(TextView) headerView.findViewById(R.id.team_name_tv);
        mUserNameTV=(TextView) headerView.findViewById(R.id.team_username_tv);
        mEmailTV=(TextView) headerView.findViewById(R.id.team_email_tv);
        mTelephoneTV=(TextView) headerView.findViewById(R.id.team_telephone_tv);
        mAddressTV=(TextView) headerView.findViewById(R.id.team_address_tv);
        mTelephoneIV=(ImageView) headerView.findViewById(R.id.team_telephone_iv);

        mTelephoneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + mTeamMember.getTeamTelephone()));

                startActivity(intent);
            }
        });

        mListView.addHeaderView(headerView);
    }

    @Override
    public void initData(){

        mUserAV.setAvatarUrl(mTeamMember.getPortrait());

        mNameTV.setText(mTeamMember.getName());
        mUserNameTV.setText(mTeamMember.getTeascriptName());

        String email = mTeamMember.getTeamEmail();
        if (StringUtils.isEmpty(email)) {
            email = "未填写邮箱";
        }
        mEmailTV.setText(email);

        String tel = mTeamMember.getTeamTelephone();
        if (StringUtils.isEmpty(tel)) {
            tel = "未填写手机号";
            mTelephoneIV.setVisibility(View.GONE);

        }
        mTelephoneTV.setText(tel);

        mAddressTV.setText(mTeamMember.getLocation());

        mListView.setSelector(new ColorDrawable(ContextCompat.getColor(aty,android.R.color.transparent)));
        mListView.setDivider(new ColorDrawable(ContextCompat.getColor(aty,android.R.color.transparent)));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        if (position == 0) {// 第一项是头部
        } else {
                TeamActive item =(TeamActive) mAdapter.getItem(position);

                Intent intent = new Intent(aty, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(
                        TeamActiveListFragment.ACTIVE_FRAGMENT_KEY, item);
                bundle.putInt(
                        TeamActiveListFragment.ACTIVE_FRAGMENT_TEAM_KEY,
                        mTeamId);
                bundle.putInt(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                        DetailActivity.DISPLAY_TEAM_TEATIME_DETAIL);
                intent.putExtras(bundle);
                aty.startActivity(intent);
        }

    }

    @Override
    protected TeamActiveAdapter getListAdapter() {
        return new TeamActiveAdapter(aty);
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mTeamMember.getId() + mCurrentPage;
    }

    @Override
    protected TeamActiveList parseList(InputStream inputStream) throws Exception {
        TeamActiveList list = XmlUtils.toBean(TeamActiveList.class, inputStream);
        return list;
    }

    @Override
    protected TeamActiveList readList(Serializable seri) {
        return (TeamActiveList) seri;
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getUserDynamic(mTeamId, mTeamMember.getId() + "",
                mCurrentPage, mHandler);
    }

}
