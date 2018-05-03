package com.teacore.teascript.team.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.team.bean.Team;
import com.teacore.teascript.team.bean.TeamMember;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.AvatarView;

import java.util.ArrayList;
import java.util.List;

/**
 * 团队成员GridView适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-14
 */

public class TeamMemberAdapter extends BaseAdapter{

    public static final String TEAM_MEMBER_KEY="teammemberadapter_teammemberkey";
    public static final String TEAM_ID_KEY="teammemberadapter_teaminfokey";

    private final Context context;
    private List<TeamMember> datas;
    private final Team mTeam;


    public TeamMemberAdapter(Context context, List<TeamMember> datas, Team team) {
        this.context = context;
        this.mTeam = team;
        if (datas == null) {
            datas = new ArrayList<TeamMember>(1);
        }
        this.datas = datas;
    }

    public void refresh(List<TeamMember> datas){
        if (datas == null) {
            datas = new ArrayList<TeamMember>(1);
        }
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        ImageView tip_iv;
        AvatarView avatar_av;
        TextView name_tv;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent){
        ViewHolder viewHolder=null;
        TeamMember data=datas.get(position);
        if(view==null||view.getTag()==null){
            view=View.inflate(context, R.layout.item_team_member,null);
            viewHolder=new ViewHolder();
            viewHolder.avatar_av=(AvatarView) view.findViewById(R.id.team_member_av);
            viewHolder.tip_iv=(ImageView) view.findViewById(R.id.team_member_tip);
            viewHolder.name_tv=(TextView) view.findViewById(R.id.iteam_member_name);

            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.name_tv.setText(data.getName());

        viewHolder.avatar_av.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.showTeamMemberInfo(context, mTeam.getId(),
                        datas.get(position));
            }
        });
        viewHolder.avatar_av.setAvatarUrl(data.getPortrait());
        if (127 == data.getTeamRole()) { // 创建人，红色
            viewHolder.tip_iv.setImageDrawable(new ColorDrawable(0xffff0000));
        } else if (126 == data.getTeamRole()) { // 管理者，黄色
            viewHolder.tip_iv.setImageDrawable(new ColorDrawable(0xffffb414));
        } else {
            viewHolder.tip_iv.setImageDrawable(null);
        }
        return view;
    }

}
