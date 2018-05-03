package com.teacore.teascript.module.general.adapter.teatimeadapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.teacore.teascript.R;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.module.general.base.baseadapter.BaseRecyclerAdapter;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.util.UiUtils;

/**
 * teatime点赞用户适配器类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-21
 */

public class TeatimeLikeUserAdapter extends BaseRecyclerAdapter<User>{

    private RequestManager mRequestManager;
    private View.OnClickListener onAvatarClickListener;

    public TeatimeLikeUserAdapter(Context context){
        super(context,ONLY_FOOTER);
        mRequestManager= Glide.with(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent,int type){
        View view= LayoutInflater.from(mContext).inflate(R.layout.general_list_cell_teatime_like_user,parent,false);
        return new LikeUsersViewHolder(view);
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder,User item,int position){
        LikeUsersViewHolder vh=(LikeUsersViewHolder) holder;
        vh.userAV.setTag(null);
        if (TextUtils.isEmpty(item.getPortrait())){
            vh.userAV.setImageResource(R.drawable.widget_dface);
        }else{
            mRequestManager.load(item.getPortrait())
                    .asBitmap()
                    .placeholder(ContextCompat.getDrawable(mContext,R.drawable.widget_dface))
                    .error(ContextCompat.getDrawable(mContext,R.drawable.widget_dface))
                    .into(vh.userAV);
        }
        vh.userAV.setTag(item);
        vh.userAV.setOnClickListener(getOnAvatarClickListener());
        vh.nameTV.setText(item.getName());
    }

    private View.OnClickListener getOnAvatarClickListener(){
        if (onAvatarClickListener == null){
            onAvatarClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = (User) view.getTag();
                    UiUtils.showUserCenter(mContext, user.getId(), user.getName());
                }
            };
        }
        return onAvatarClickListener;
    }

    public static final class LikeUsersViewHolder extends RecyclerView.ViewHolder{

        AvatarView userAV;
        TextView nameTV;

        public LikeUsersViewHolder(View view) {
            super(view);
            userAV=(AvatarView) view.findViewById(R.id.avatar_av);
            nameTV=(TextView) view.findViewById(R.id.name_tv);
        }
    }

}
