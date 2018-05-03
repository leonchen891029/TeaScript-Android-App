package com.teacore.teascript.module.general.adapter.teatimeadapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.teacore.teascript.R;
import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.module.general.base.baseadapter.BaseRecyclerAdapter;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.TeatimeTextView;
import com.teacore.teascript.widget.emoji.EmojiInputUtils;

/**
 * TeatimeComment的RecyclerAdapter
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-17
 */

public class TeatimeCommentAdapter extends BaseRecyclerAdapter<Comment>{

    private RequestManager requestManager;
    private View.OnClickListener onPortraitClickListener;

    public TeatimeCommentAdapter(Context context) {
        super(context, ONLY_FOOTER);
        requestManager = Glide.with(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new TeatimeCommentViewHolder(LayoutInflater.from(mContext).inflate(R.layout.general_list_cell_teatime_comment, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {

        TeatimeCommentViewHolder vh = (TeatimeCommentViewHolder) holder;

        vh.userAV.setTag(null);

        if (TextUtils.isEmpty(item.getPortrait())){
            vh.userAV.setImageResource(R.drawable.widget_dface);
        }else{
            requestManager
                    .load(item.getPortrait())
                    .asBitmap()
                    .placeholder(ContextCompat.getDrawable(mContext,R.drawable.widget_dface))
                    .error(ContextCompat.getDrawable(mContext,R.drawable.widget_dface))
                    .into(vh.userAV);
        }
        vh.userAV.setTag(item);
        vh.userAV.setOnClickListener(getOnPortraitClickListener());

        vh.nameTV.setText(item.getAuthor());
        vh.contentTTV.setText(EmojiInputUtils.displayEmoji(mContext.getResources(), item.getContent()));
        vh.pubDateTV.setText(TimeUtils.friendly_time(item.getPubDate()));
    }

    private View.OnClickListener getOnPortraitClickListener(){
        if (onPortraitClickListener == null){
            onPortraitClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comment comment = (Comment) v.getTag();
                    UiUtils.showUserCenter(mContext, comment.getAuthorId(), comment.getAuthor());
                }
            };
        }
        return onPortraitClickListener;
    }

    public static final class TeatimeCommentViewHolder extends RecyclerView.ViewHolder{

        public AvatarView userAV;
        public TextView nameTV;
        public TextView pubDateTV;
        public ImageView commentBtn;
        public TeatimeTextView contentTTV;

        public TeatimeCommentViewHolder(View view) {
            super(view);
            userAV=(AvatarView) view.findViewById(R.id.user_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            pubDateTV=(TextView) view.findViewById(R.id.pub_date_tv);
            commentBtn=(ImageView) view.findViewById(R.id.comment_btn);
            contentTTV=(TeatimeTextView) view.findViewById(R.id.content_ttv);
        }
    }


}
