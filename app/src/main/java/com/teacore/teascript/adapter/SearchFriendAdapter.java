package com.teacore.teascript.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.module.quickoption.activity.SelectFriendsActivity;
import com.teacore.teascript.widget.AvatarView;

import java.util.List;


/**
 * 搜索好友结果的适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-30
 */

public class SearchFriendAdapter extends BaseAdapter{

    final List<SelectFriendsActivity.SearchItem> mList;
    private LayoutInflater mInflater;

    public SearchFriendAdapter(List<SelectFriendsActivity.SearchItem> list){
        this.mList=list;
    }

    protected LayoutInflater getLayoutInflater(Context context){
        if (mInflater == null) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return mInflater;
    }

    @Override
    public int getCount(){
       return mList.size();
    }

    @Override
    public SelectFriendsActivity.SearchItem getItem(int position){
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null||convertView.getTag()==null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_cell_select_friend, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bind(getItem(position));
        return convertView;
    }

    static class ViewHolder{

        TextView nameTV;
        AvatarView userAV;
        CheckBox checkBox;

        ViewHolder(View view){
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            userAV=(AvatarView) view.findViewById(R.id.user_av);
            checkBox=(CheckBox) view.findViewById(R.id.check_cb);
            userAV.setClickable(false);
        }

        public void bind(SelectFriendsActivity.SearchItem item){
            SelectFriendsActivity.FriendItem friendItem=item.getFriendItem();
            nameTV.setText(friendItem.getFriend().getName());
            userAV.setAvatarUrl(friendItem.getFriend().getPortrait());
            userAV.setDisplayCircle(false);
            checkBox.setChecked(friendItem.isSelected());

            int start = item.getStartIndex();
            if(start != -1) {
                SpannableString ss = new SpannableString(friendItem.getFriend().getName());
                ss.setSpan(new ForegroundColorSpan(item.getHightLightColor()), start,
                        start + item.getKeyLength(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                nameTV.setText(ss);
            } else {
                nameTV.setText(friendItem.getFriend().getName());
            }
        }

    }

}
