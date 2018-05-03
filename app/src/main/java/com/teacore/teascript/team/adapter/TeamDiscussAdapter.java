package com.teacore.teascript.team.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.team.bean.TeamDiscuss;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.widget.AvatarView;

/**团队讨论适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */

public class TeamDiscussAdapter extends BaseListAdapter<TeamDiscuss> {

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_team_discuss, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        TeamDiscuss item = mDatas.get(position);

        viewHolder.faceAV.setUserInfo(item.getAuthor().getId(), item.getAuthor()
                .getName());
        viewHolder.faceAV.setAvatarUrl(item.getAuthor().getPortrait());
        viewHolder.titleTV.setText(item.getTitle());
        String body = item.getBody().trim();
        viewHolder.descriptionTV.setVisibility(View.GONE);
        viewHolder.descriptionTV.setVisibility(View.VISIBLE);
        viewHolder.descriptionTV.setText(HtmlUtils.replaceTag(body));
        viewHolder.authorTV.setText(item.getAuthor().getName());
        viewHolder.timeTV.setText(TimeUtils.friendly_time(item.getCreateTime()));
        viewHolder.voteupTV.setText(item.getVoteUp() + "");
        viewHolder.commentcountTV.setText(item.getReplyCount() + "");
        return convertView;
    }

    static class ViewHolder{
        TextView titleTV;
        TextView descriptionTV;
        TextView authorTV;
        TextView timeTV;
        TextView commentcountTV;
        TextView voteupTV;
        AvatarView faceAV;

        public ViewHolder(View view) {
            titleTV = (TextView) view.findViewById(R.id.title_tv);
            descriptionTV = (TextView) view.findViewById(R.id.description_tv);
            authorTV = (TextView) view.findViewById(R.id.author_tv);
            timeTV = (TextView) view.findViewById(R.id.time_tv);
            commentcountTV = (TextView) view.findViewById(R.id.comment_count_tv);
            voteupTV = (TextView) view.findViewById(R.id.vote_up_tv);
            faceAV = (AvatarView) view.findViewById(R.id.face_av);
        }

    }

}
