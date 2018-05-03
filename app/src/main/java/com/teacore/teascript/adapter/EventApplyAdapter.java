package com.teacore.teascript.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.EventApply;
import com.teacore.teascript.widget.AvatarView;

/**
 * 活动报名者适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-1
 */

public class EventApplyAdapter extends BaseListAdapter<EventApply>{

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

    @Override
    protected View getRealView(int position, View convertView,
                               final ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_event_apply, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final EventApply item = (EventApply) mDatas.get(position);

        vh.userAV.setUserInfo(item.getId(), item.getName());
        vh.userAV.setAvatarUrl(item.getPortrait());
        vh.nameTV.setText(item.getName());
        vh.fromTV.setVisibility(View.GONE);
        vh.descTV.setText(item.getCompany() + " " + item.getJob());

        return convertView;
    }

}
