package com.teacore.teascript.widget.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.teacore.teascript.R;

/**
 * Dialog适配器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-3
 */

public class DialogAdapter extends BaseAdapter {

    private CharSequence[] items;
    private int selectId;
    private boolean showCHK=true;

    public DialogAdapter(CharSequence[] items,int selectId){
        this.items=items;
        this.selectId=selectId;
    }

    public DialogAdapter(CharSequence[] items){
        this.items=items;
    }

    @Override
    public int getCount(){
        return items.length;
    }

    @Override
    public String getItem(int i){
        return items[i].toString();
    }

    @Override
    public long getItemId(int i){
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        DialogHolder dh=null;
        if(view==null){
            dh=new DialogHolder();
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_cell_dialog,null,false);
            dh.titleTV=(TextView) view.findViewById(R.id.title_tv);
            dh.checkRB=(RadioButton) view.findViewById(R.id.select_rb);
            dh.divider=view.findViewById(R.id.list_divider);
            view.setTag(dh);
        }else{
            dh=(DialogHolder) view.getTag();
        }
        dh.titleTV.setText(getItem(i));
        if(i==-1+getCount()){
            dh.divider.setVisibility(View.GONE);
        }else{
            dh.divider.setVisibility(View.VISIBLE);
        }

        if(showCHK){
            dh.checkRB.setVisibility(View.VISIBLE);
            if(selectId==i){
                dh.checkRB.setChecked(true);
            }else{
                dh.checkRB.setChecked(false);
            }
        }else{
            dh.checkRB.setVisibility(View.GONE);
        }
        return view;
    }

    public boolean isShowCHK(){
        return showCHK;
    }

    public void setShowCHK(boolean showCHK){
        this.showCHK=showCHK;
    }

    private class DialogHolder{
        public TextView titleTV;
        public RadioButton checkRB;
        public View divider;
    }

}
