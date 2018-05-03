package com.teacore.teascript.team.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.team.bean.TeamIssueCatalog;
import com.teacore.teascript.util.StringUtils;

/**
 * 团队任务分组适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-20
 */

public class TeamIssueCatalogAdapter extends BaseListAdapter<TeamIssueCatalog>{

    static class ViewHolder{

        TextView titleTV;
        TextView stateTV;
        TextView descriptionTV;

        public ViewHolder(View view){
            titleTV=(TextView) view.findViewById(R.id.title_tv);
            stateTV=(TextView) view.findViewById(R.id.state_tv);
            descriptionTV=(TextView) view.findViewById(R.id.description_tv);
        }

    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_team_issue_catalog, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TeamIssueCatalog item = mDatas.get(position);

        vh.titleTV.setText(item.getTitle());
        vh.stateTV.setText(item.getOpenedIssueCount() + "/"
                + item.getAllIssueCount());

        String description = item.getDescription();
        if (description != null && !StringUtils.isEmpty(description)) {
            vh.descriptionTV.setText(description);
        } else {
            vh.descriptionTV.setText("暂无描述");
        }

        return convertView;
    }

}
