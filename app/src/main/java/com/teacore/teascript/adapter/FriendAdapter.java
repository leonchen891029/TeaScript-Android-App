package com.teacore.teascript.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Friend;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.widget.AvatarView;

/**
 * 好友适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-10
 */

public class FriendAdapter extends BaseListAdapter<Friend>{

    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_friend, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Friend item = mDatas.get(position);

        vh.userAV.setAvatarUrl(item.getPortrait());
        vh.userAV.setUserInfo(item.getUserid(), item.getName());

        vh.nameTV.setText(item.getName());

        vh.genderIV.setImageResource(item.getGender() == 1 ? R.drawable.icon_userinfo_male
                : R.drawable.icon_userinfo_female);

        String from = item.getFrom();
        if (from != null || !StringUtils.isEmpty(from)) {
            vh.fromTV.setText(from);
        } else {
            vh.fromTV.setVisibility(View.GONE);
        }

        String desc = item.getExpertise();
        if (desc != null || !StringUtils.isEmpty(from) || !"<无>".equals(desc)) {
            vh.descTV.setText(item.getExpertise());
        } else {
            vh.descTV.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {

        AvatarView userAV;
        TextView nameTV;
        ImageView genderIV;
        TextView fromTV;
        TextView descTV;

        public ViewHolder(View view) {
            userAV=(AvatarView) view.findViewById(R.id.user_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            genderIV=(ImageView) view.findViewById(R.id.gender_iv);
            fromTV=(TextView) view.findViewById(R.id.from_tv);
            descTV=(TextView) view.findViewById(R.id.description_tv);
        }

    }



















}
