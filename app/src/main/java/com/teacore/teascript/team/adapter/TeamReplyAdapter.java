package com.teacore.teascript.team.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.team.bean.TeamReply;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.TeatimeTextView;

import java.util.List;

/**团队回复适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-10
 */

public class TeamReplyAdapter extends BaseListAdapter<TeamReply> {

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder=null;

        if(convertView==null || convertView.getTag()==null){
            convertView=getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_team_reply,null
            );

            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        TeamReply item=mDatas.get(position);
        viewHolder.nameTV.setText(item.getAuthor().getName());
        viewHolder.avatarView.setAvatarUrl(item.getAuthor().getPortrait());
        setContent(viewHolder.contentTTV, HtmlUtils.delHTMLTag(item.getContent()));
        viewHolder.timeTV.setText(TimeUtils.friendly_time(item.getCreateTime()));

        if(StringUtils.isEmpty(item.getAppName())){
            viewHolder.fromTV.setVisibility(View.GONE);
        }else{
            viewHolder.fromTV.setVisibility(View.VISIBLE);
            viewHolder.fromTV.setText(item.getAppName());
        }

        setReplies(parent.getContext(),item,viewHolder);

        return convertView;
    }

    private void setReplies(Context context,TeamReply item,ViewHolder viewHolder){

        List<TeamReply> replies=item.getReplies();
        viewHolder.repliesLL.removeAllViews();
        if(replies==null||replies.size()<=0){
            viewHolder.repliesLL.setVisibility(View.GONE);
        }else{
            viewHolder.repliesLL.setVisibility(View.VISIBLE);

            //添加统计总数的TextView
            View countView=getLayoutInflater(context).inflate(R.layout.list_cell_reply_count,null,false);
            TextView countTV=(TextView) countView.findViewById(R.id.comment_reply_count_tv);
            countTV.setText(context.getResources().getString(R.string.comment_reply_count)+replies.size());
            viewHolder.repliesLL.addView(countTV);

            //添加一个个TeamReply 这里layout文件和上面只差一个LinearLayout
            for(TeamReply teamReply:replies){
                View replyViewUnit=getLayoutInflater(context).inflate(R.layout.list_cell_team_reply_unit,null,false);
                replyViewUnit.setBackgroundResource(R.drawable.comment_background);

                AvatarView avatarUnit=(AvatarView) replyViewUnit.findViewById(R.id.avatar_av);
                avatarUnit.setAvatarUrl(teamReply.getAuthor().getPortrait());
                TextView nameUnit=(TextView) replyViewUnit.findViewById(R.id.name_tv);
                nameUnit.setText(teamReply.getAuthor().getName());
                TeatimeTextView contentUnit=(TeatimeTextView) replyViewUnit.findViewById(R.id.content_ttv);
                setContent(contentUnit, HtmlUtils.delHTMLTag(teamReply.getContent()));
                TextView timeUnit=(TextView) replyViewUnit.findViewById(R.id.time_tv);
                timeUnit.setText(TimeUtils.friendly_time(teamReply.getCreateTime()));
                TextView fromUnit=(TextView) replyViewUnit.findViewById(R.id.from_tv);
                if(StringUtils.isEmpty(teamReply.getAppName())){
                    fromUnit.setVisibility(View.GONE);
                }else{
                    fromUnit.setVisibility(View.VISIBLE);
                    fromUnit.setText(teamReply.getAppName());
                }

                viewHolder.repliesLL.addView(replyViewUnit);
            }

        }
    }

    static class ViewHolder{
         AvatarView avatarView;
         TextView nameTV;
         TextView timeTV;
         TextView fromTV;
         TeatimeTextView contentTTV;
         LinearLayout repliesLL;

        public ViewHolder(View view){
            avatarView=(AvatarView) view.findViewById(R.id.avatar_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            timeTV=(TextView) view.findViewById(R.id.time_tv);
            fromTV=(TextView) view.findViewById(R.id.from_tv);
            contentTTV=(TeatimeTextView) view.findViewById(R.id.content_ttv);
            repliesLL=(LinearLayout) view.findViewById(R.id.replies_ll);
        }

    }


}
