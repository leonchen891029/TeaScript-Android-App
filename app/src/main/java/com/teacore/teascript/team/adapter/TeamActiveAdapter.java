package com.teacore.teascript.team.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.module.general.activity.PreviewImageActivity;
import com.teacore.teascript.team.bean.TeamActive;
import com.teacore.teascript.util.BitmapUtils;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.TeatimeTextView;

/**
 * 团队动态适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-2
 */

public class TeamActiveAdapter extends BaseListAdapter{

    private final Context mContext;

    public TeamActiveAdapter(Context cxt) {
        this.mContext = cxt;
    }

    static class ViewHolder{

        TextView titleTV;
        AvatarView userAV;
        TextView nameTV;
        TeatimeTextView contentTTV;
        ImageView picIV;
        TextView clientTV;
        TextView dateTV;
        TextView commentCountTV;

        public ViewHolder(View view){
            titleTV=(TextView) view.findViewById(R.id.title_tv);
            userAV=(AvatarView) view.findViewById(R.id.user_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            contentTTV=(TeatimeTextView) view.findViewById(R.id.content_ttv);
            picIV=(ImageView) view.findViewById(R.id.pic_iv);
            clientTV=(TextView) view.findViewById(R.id.client_tv);
            dateTV=(TextView) view.findViewById(R.id.date_tv);
            commentCountTV=(TextView) view.findViewById(R.id.comment_count_tv);

        }

    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;

        if (convertView == null || convertView.getTag() == null) {

            convertView = View.inflate(mContext, R.layout.list_cell_team_active, null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TeamActive teamActive = (TeamActive) mDatas.get(position);

        vh.userAV.setAvatarUrl(teamActive.getAuthor().getPortrait());
        vh.userAV.setUserInfo(teamActive.getAuthor().getId(), teamActive.getAuthor()
                .getName());

        vh.nameTV.setText(teamActive.getAuthor().getName());

        setContent(vh.contentTTV, stripTags(teamActive.getBody().getTitle()));
        vh.contentTTV.setMaxLines(3);

        String date = TimeUtils.friendly_time2(teamActive.getCreateTime());
        String preDate = "";
        TeamActive preTeamActive=(TeamActive) mDatas.get(position-1);
        if (position > 0) {
            preDate = TimeUtils.friendly_time2(preTeamActive.getCreateTime());
        }
        if (preDate.equals(date)) {
            vh.titleTV.setVisibility(View.GONE);
        } else {
            vh.titleTV.setText(date);
            vh.titleTV.setVisibility(View.VISIBLE);
        }

        vh.dateTV.setText(TimeUtils.friendly_time(teamActive.getCreateTime()));
        vh.commentCountTV.setText(teamActive.getReply());

        String imgPath = teamActive.getBody().getImage();
        if (!StringUtils.isEmpty(imgPath)) {
            vh.picIV.setVisibility(View.VISIBLE);
            setTeatimeImage(vh.picIV, imgPath);
        } else {
            vh.picIV.setVisibility(View.GONE);
        }
        return convertView;
    }

    //移除字符串中的Html标签
    public static String stripTags(final String pHTMLString) {
        String str = pHTMLString.replaceAll("\\t", "");
        str = str.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "").trim();
        return str;
    }

    //动态设置图片显示样式
    private void setTeatimeImage(final ImageView pic, final String url) {
        pic.setVisibility(View.VISIBLE);

        Glide.with(mContext).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (resource != null) {
                    resource = BitmapUtils.scaleWithXY(resource,
                            360 / resource.getHeight());
                    pic.setImageBitmap(resource);
                }
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewImageActivity.showImagePreview(mContext, url);
            }
        });
    }

}
