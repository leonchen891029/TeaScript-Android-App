package com.teacore.teascript.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Favorite;

/**
 * 用户收藏适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-17
 */

public class UserFavoriteAdapter extends BaseListAdapter<Favorite>{

    static class ViewHolder{

        TextView titleTV;

        public ViewHolder(View view){
            titleTV=(TextView) view.findViewById(R.id.favorite_title_tv);
        }
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_favorite, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Favorite favorite = (Favorite) mDatas.get(position);

        vh.titleTV.setText(favorite.getTitle());
        return convertView;
    }



}
