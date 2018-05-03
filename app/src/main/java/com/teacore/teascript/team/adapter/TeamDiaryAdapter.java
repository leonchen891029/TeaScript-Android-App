package com.teacore.teascript.team.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.team.bean.TeamDiary;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.widget.AvatarView;

import java.util.ArrayList;
import java.util.List;


/**
 * 团队周报适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-25
 */

public class TeamDiaryAdapter extends BaseAdapter{

    private final Context mContext;
    private List<TeamDiary> mDiaryList;

    public TeamDiaryAdapter(Context context,List<TeamDiary> list){
        this.mContext=context;
        if(list==null){
            list=new ArrayList<TeamDiary>(1);
        }
        this.mDiaryList=list;
    }

    public void refresh(List<TeamDiary> list) {
        if (list == null) {
            list = new ArrayList<TeamDiary>(1);
        }
        this.mDiaryList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDiaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDiaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TeamDiary data = mDiaryList.get(position);
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() ==null) {
            convertView = View.inflate(mContext, R.layout.list_cell_team_diary, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.userAV.setAvatarUrl(data.getAuthor().getPortrait());
        vh.authorTV.setText(data.getAuthor().getName());
        vh.titleTV.setText(Html.fromHtml(data.getTitle()).toString().trim());
        vh.dateTV.setText(TimeUtils.friendly_time(data.getCreateTime()));
        vh.commentCountTV.setText(data.getReply() + "");

        return convertView;
    }

    static class ViewHolder {
        AvatarView userAV;
        TextView authorTV;
        TextView titleTV;
        TextView dateTV;
        TextView commentCountTV;

        public ViewHolder(View view){
            userAV=(AvatarView) view.findViewById(R.id.user_av);
            authorTV=(TextView) view.findViewById(R.id.author_tv);
            titleTV=(TextView) view.findViewById(R.id.title_tv);
            dateTV=(TextView) view.findViewById(R.id.date_tv);
            commentCountTV=(TextView) view.findViewById(R.id.comment_count_tv);
        }
    }


}
