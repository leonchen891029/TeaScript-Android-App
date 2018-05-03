package com.teacore.teascript.widget.emoji;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.teacore.teascript.R;

import java.util.ArrayList;
import java.util.List;

/**表情适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-20
 */

public class EmojiAdapter extends BaseAdapter{

    private List<EmojiIcon> datas;
    private final Context context;

    public EmojiAdapter(Context context,List<EmojiIcon> emojiIcons){
        this.context=context;
        if(emojiIcons==null){
            emojiIcons=new ArrayList<EmojiIcon>(0);
        }

        this.datas=emojiIcons;
    }

    public void refresh(List<EmojiIcon> emojiIcons){
        if (emojiIcons == null) {
            emojiIcons = new ArrayList<EmojiIcon>(0);
        }
        this.datas = emojiIcons;
        notifyDataSetChanged();
    }

    @Override
    public int getCount(){
        return datas.size();
    }

    @Override
    public EmojiIcon getItem(int position){
        return datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    //使用viewholder实现listview的优化
    private static class ViewHolder{
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = new ImageView(context);

            int bound = (int) context.getResources().getDimension(R.dimen.space_49);

            AbsListView.LayoutParams params = new AbsListView.LayoutParams(bound, bound);

            convertView.setLayoutParams(params);

            int padding = (int) context.getResources().getDimension(
                    R.dimen.space_10);

            convertView.setPadding(padding, padding, padding, padding);

            holder.imageView = (ImageView) convertView;

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(datas.get(position).getResId());

        return convertView;
    }

}
