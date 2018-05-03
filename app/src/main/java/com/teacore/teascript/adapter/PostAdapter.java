package com.teacore.teascript.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Post;
import com.teacore.teascript.bean.PostList;
import com.teacore.teascript.util.HtmlUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.ThemeSwitchUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.widget.AvatarView;

/**
 * post(帖子)适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-2
 */

public class PostAdapter extends BaseListAdapter<Post>{

    static class ViewHolder{

        AvatarView userAV;
        TextView titleTV;
        TextView descriptionTV;
        TextView authorTV;
        TextView pubDateTV;
        TextView commentCountTV;


        public ViewHolder(View view){
            userAV=(AvatarView) view.findViewById(R.id.user_av);
            titleTV=(TextView) view.findViewById(R.id.title_tv);
            descriptionTV=(TextView) view.findViewById(R.id.description_tv);
            authorTV=(TextView) view.findViewById(R.id.author_tv);
            pubDateTV=(TextView) view.findViewById(R.id.pub_date_tv);
            commentCountTV=(TextView) view.findViewById(R.id.comment_count_tv);
        }

    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent){
        ViewHolder vh=null;
        if(convertView==null||convertView.getTag()==null){
            convertView=getLayoutInflater(parent.getContext()).inflate(R.layout.list_cell_post,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else{
            vh=(ViewHolder) convertView.getTag();
        }

        Post post=(Post) mDatas.get(position);
        vh.userAV.setUserInfo(post.getAuthorId(),post.getAuthor());
        vh.userAV.setAvatarUrl(post.getPortrait());
        vh.titleTV.setText(post.getTitle());

        String body=post.getBody();
        vh.descriptionTV.setVisibility(View.GONE);
        if(body!=null || !StringUtils.isEmpty(body)){
            vh.descriptionTV.setVisibility(View.VISIBLE);
            vh.descriptionTV.setText(HtmlUtils.replaceTag(post.getBody()).trim());
        }

        if(AppContext.isOnReadedPostList(PostList.PREF_READED_POST_LIST,post.getId()+"")){
            vh.titleTV.setTextColor(ContextCompat.getColor(parent.getContext(), ThemeSwitchUtils.getTitleReadedColor()));
        }else{
            vh.titleTV.setTextColor(ContextCompat.getColor(parent.getContext(), ThemeSwitchUtils.getTitleUnReadedColor()));
        };

        vh.authorTV.setText(post.getAuthor());
        vh.pubDateTV.setText(TimeUtils.friendly_time(post.getPubDate()));
        vh.commentCountTV.setText(post.getAnswerCount()+"回 /"+post.getViewCount()+"阅");

        return convertView;
    };


}
