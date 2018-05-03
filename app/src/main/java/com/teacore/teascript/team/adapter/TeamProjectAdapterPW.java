package com.teacore.teascript.team.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.team.bean.TeamProject;
import com.teacore.teascript.util.TypefaceUtils;

/**
 * 供TeamProjectPopWindow使用的团队项目适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-21
 */

public class TeamProjectAdapterPW extends BaseListAdapter<TeamProject>{

    static class ViewHolder{

        TextView sourceTV;
        TextView nameTV;

        public ViewHolder(View view){
            sourceTV=(TextView) view.findViewById(R.id.project_source_tv);
            nameTV=(TextView) view.findViewById(R.id.project_name_tv);
        }

    }

    @Override
    public View getRealView(int position, View convertView, ViewGroup parent){

        ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = View.inflate(parent.getContext(),
                    R.layout.list_cell_team_project, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TeamProject item = mDatas.get(position);

        String source = item.getSource();
        if (TextUtils.isEmpty(source)) {
            if (item.getGit().getId() == -1) {
                TypefaceUtils.setTypeface(vh.sourceTV, R.string.fa_tasks);
            } else {
                TypefaceUtils.setTypeface(vh.sourceTV, R.string.fa_inbox);
            }
        } else if (source.equalsIgnoreCase(TeamProject.GITOSC)) {
            TypefaceUtils.setTypeface(vh.sourceTV, R.string.fa_gitosc);
        } else if (source.equalsIgnoreCase(TeamProject.GITHUB)) {
            TypefaceUtils.setTypeface(vh.sourceTV, R.string.fa_github);
        } else {
            TypefaceUtils.setTypeface(vh.sourceTV, R.string.fa_list_alt);
        }

        vh.sourceTV.setText(item.getGit().getName());

        return convertView;

    }

}
