package com.teacore.teascript.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.widget.AvatarView;

/**
 * 找人适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */

public class FindUserAdapter extends BaseListAdapter<User> {

    @Override
    protected View getRealView(int position, View convertView,final ViewGroup parent){
        ViewHolder viewHolder=null;

        if(convertView==null || convertView.getTag()==null){
            convertView=getLayoutInflater(parent.getContext()).inflate(R.layout.list_cell_friend,null);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        final User user=(User) mDatas.get(position);

        viewHolder.userAV.setAvatarUrl(user.getPortrait());
        viewHolder.userAV.setUserInfo(user.getId(), user.getName());

        viewHolder.nameTV.setText(user.getName());
        viewHolder.fromTV.setText(user.getFrom());
        viewHolder.descTV.setVisibility(View.GONE);

        int genderIcon = R.drawable.icon_userinfo_male;
        if ("女".equals(user.getGender())) {
            genderIcon = R.drawable.icon_userinfo_female;
        }
        viewHolder.genderIV.setImageResource(genderIcon);

        return convertView;
    }

    static class ViewHolder{

        AvatarView userAV;
        TextView nameTV;
        ImageView genderIV;
        TextView fromTV;
        TextView descTV;

        public ViewHolder(View view){
            userAV=(AvatarView) view.findViewById(R.id.avatar_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            genderIV=(ImageView) view.findViewById(R.id.gender_iv);
            fromTV=(TextView) view.findViewById(R.id.from_tv);
            descTV=(TextView) view.findViewById(R.id.description_tv);
        }


    }

}
