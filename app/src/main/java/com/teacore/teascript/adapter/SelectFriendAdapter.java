package com.teacore.teascript.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.module.quickoption.activity.SelectFriendsActivity;
import com.teacore.teascript.widget.AvatarView;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择朋友适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-30
 */

public class SelectFriendAdapter extends BaseAdapter{

    private static final char DEFAULT_OTHER_LETTER='#';

    private static final int VIEWTYPE_HEADER=0;
    private static final int VIEWTYPE_NORMAL=1;

    private List<ItemData> mList=new ArrayList<>();

    //记录索引值对应的位置
    private SparseArray<Integer> mPositionArray=new SparseArray<>();

    private LayoutInflater mInflater;

    public SelectFriendAdapter(){

    }

    protected LayoutInflater getLayoutInflater(Context context){
        if(mInflater==null){
            mInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return mInflater;
    }

    public void setFriendItems(List<SelectFriendsActivity.FriendItem> list) {
        mPositionArray.clear();
        mList.clear();

        //非字母开头的列表
        List<ItemData> otherList = null;

        char lastIndex = '0';
        for(SelectFriendsActivity.FriendItem item : list) {
            char indexLetter;
            char c = item.getFirstLetter();
            if(c >= 'A' && c <= 'Z') {
                indexLetter = c;
            } else if(c >= 'a' && c <= 'z') {
                indexLetter = (char)(c - 'a' + 'A');
            } else {
                indexLetter = DEFAULT_OTHER_LETTER;
            }
            if(indexLetter == DEFAULT_OTHER_LETTER) {
                if(otherList == null) {
                    otherList = new ArrayList<>();
                }
                otherList.add(new NormalItemData(item));
            } else {
                if (indexLetter != lastIndex) {
                    mPositionArray.append(indexLetter, mList.size());
                    mList.add(new HeaderItemData(String.valueOf(indexLetter)));
                    lastIndex = indexLetter;
                }
                mList.add(new NormalItemData(item));
            }
        }
        //将没有索引的数据列表放到最后
        if(otherList != null && !otherList.isEmpty()) {
            mPositionArray.append(DEFAULT_OTHER_LETTER, mList.size());
            mList.add(new HeaderItemData(String.valueOf(DEFAULT_OTHER_LETTER)));
            mList.addAll(otherList);
        }

        notifyDataSetChanged();
    }

    //根据索引获取位置
    public int getPositionByIndex(char indexLetter){
        Integer value=mPositionArray.get(indexLetter);
        if(value==null){
            return -1;
        }
        return value;
    }

    public SelectFriendsActivity.FriendItem getFriendItem(int position){
        ItemData data=getItem(position);
        if(data instanceof NormalItemData){
            return ((NormalItemData) data).friendItem;
        }
        return null;
    }

    @Override
    public int getCount(){
        return mList.size();
    }

    @Override
    public ItemData getItem(int position){
        return mList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    @Override
    public int getItemViewType(int position){
        return (getItem(position) instanceof  HeaderItemData)?VIEWTYPE_HEADER:VIEWTYPE_NORMAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ItemData itemData=getItem(position);

        if(itemData instanceof HeaderItemData){
            final HeaderViewHolder viewHolder;
            if(convertView==null || convertView.getTag()==null){
                convertView=getLayoutInflater(parent.getContext()).inflate(R.layout.list_header_select_friend,parent,false);
                viewHolder=new HeaderViewHolder(convertView);
                convertView.setTag(viewHolder);
                convertView.setEnabled(false);
            }else{
                viewHolder=(HeaderViewHolder) convertView.getTag();
            }
            viewHolder.bind(((HeaderItemData) itemData).index);
        }else{
            final NormalViewHolder viewHolder;
            if(convertView==null||convertView.getTag()==null){
                convertView=getLayoutInflater(parent.getContext()).inflate(R.layout.list_cell_select_friend,parent,false);
                viewHolder=new NormalViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder=(NormalViewHolder) convertView.getTag();
            }
            viewHolder.bind(((NormalItemData) itemData).friendItem);
        }
        return convertView;
    }

    private interface ItemData{};

    static class HeaderItemData implements ItemData{
        String index;

        public HeaderItemData(String index){
            this.index=index;
        }
    }

    static class NormalItemData implements ItemData{
        SelectFriendsActivity.FriendItem friendItem;

        public NormalItemData(SelectFriendsActivity.FriendItem friendItem){
            this.friendItem=friendItem;
        }
    }

    static class HeaderViewHolder{

        TextView headerTV;

        HeaderViewHolder(View view){
            headerTV=(TextView) view.findViewById(R.id.header_tv);

        }

        public void bind(String index){
            headerTV.setText(index);
        }
    }

    static class NormalViewHolder{

        TextView nameTV;
        AvatarView userAV;
        CheckBox checkBox;

        NormalViewHolder(View view){
            nameTV=(TextView) view.findViewById(R.id.name_tv);
            userAV=(AvatarView) view.findViewById(R.id.user_av);
            checkBox=(CheckBox) view.findViewById(R.id.check_cb);
            userAV.setClickable(false);
        }

        public void bind(SelectFriendsActivity.FriendItem item){
            nameTV.setText(item.getFriend().getName());
            userAV.setAvatarUrl(item.getFriend().getPortrait());
            userAV.setDisplayCircle(false);
            checkBox.setChecked(item.isSelected());
        }
    }

}
