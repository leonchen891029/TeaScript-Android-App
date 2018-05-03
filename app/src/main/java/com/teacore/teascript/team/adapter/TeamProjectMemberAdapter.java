package com.teacore.teascript.team.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.team.bean.TeamMember;
import com.teacore.teascript.widget.AvatarView;

/**
 * 团队项目成员适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-22
 */

public class TeamProjectMemberAdapter extends BaseListAdapter<TeamMember>{

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = View.inflate(parent.getContext(),
                    R.layout.list_cell_team_project_member, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TeamMember item = mDatas.get(position);

        vh.userAV.setAvatarUrl(item.getPortrait());
        vh.nameTV.setText(item.getName());

        return convertView;
    }

    public static class ViewHolder {

        AvatarView userAV;

        TextView nameTV;

        public ViewHolder(View view) {
            userAV=(AvatarView) view.findViewById(R.id.user_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
        }
    }

}
