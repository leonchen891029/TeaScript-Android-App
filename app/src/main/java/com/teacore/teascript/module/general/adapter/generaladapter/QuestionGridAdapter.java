package com.teacore.teascript.module.general.adapter.generaladapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.teacore.teascript.R;

/**
 * 综合中问答页面的最上面的四个分类按钮适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-20
 */

public class QuestionGridAdapter extends BaseAdapter{

    private Context context;
    //提问,分享,综合,职业,站务
    private String[] datas;
    private int[] positions;

    public QuestionGridAdapter(Context context,int[] positions){
        this.context=context;
        this.datas=context.getResources().getStringArray(R.array.ques_item);
        this.positions=positions;
    }

    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public Object getItem(int position) {
        return datas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        Button actionBT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null || convertView.getTag()==null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.general_question_grid_item, parent, false);
            viewHolder.actionBT = (Button) convertView.findViewById(R.id.ques_grid_btn);
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (positions[position] == 1) {
            viewHolder.actionBT.setTextColor(ContextCompat.getColor(context,R.color.ques_bt_text_color_light));
        } else {
            viewHolder.actionBT.setTextColor(ContextCompat.getColor(context,R.color.ques_bt_text_color_dark));
        }

        viewHolder.actionBT.setText(datas[position]);
        return convertView;

    }


}
