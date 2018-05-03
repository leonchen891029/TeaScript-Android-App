package com.teacore.teascript.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.SoftwareIntro;
import com.teacore.teascript.bean.SoftwareIntroList;
import com.teacore.teascript.util.ThemeSwitchUtils;

/**
 * 软件介绍适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-1
 */
public class SoftwareAdapter extends BaseListAdapter<SoftwareIntro>{

    static class ViewHolder{
        TextView titleTV;
        TextView introTV;

        public ViewHolder(View view){
            titleTV=(TextView) view.findViewById(R.id.title_tv);
            introTV=(TextView) view.findViewById(R.id.software_intro_tv);
        }
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_cell_software, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        SoftwareIntro softwareDes = (SoftwareIntro) mDatas.get(position);
        vh.titleTV.setText(softwareDes.getName());

        if (AppContext.isOnReadedPostList(SoftwareIntroList.PREF_READED_SOFTWARE_LIST,
                softwareDes.getName())) {
            vh.titleTV.setTextColor(ContextCompat.getColor(parent.getContext(),ThemeSwitchUtils.getTitleReadedColor()));
        } else {
            vh.titleTV.setTextColor(ContextCompat.getColor(parent.getContext(),ThemeSwitchUtils.getTitleUnReadedColor()));
        }

        vh.introTV.setText(softwareDes.getDescription());

        return convertView;
    }


}
