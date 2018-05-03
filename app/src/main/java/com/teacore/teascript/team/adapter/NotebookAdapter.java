package com.teacore.teascript.team.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.bean.Notebook;
import com.teacore.teascript.team.fragment.NotebookEditFragment;
import com.teacore.teascript.util.DensityUtils;
import com.teacore.teascript.widget.KJDragGridView.DragGridBaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  便签列表适配器
 *  @author 陈晓帆
 *  @version 1.0
 *  Created 2017-5-2
 */

public class NotebookAdapter extends BaseAdapter implements DragGridBaseAdapter{

    private List<Notebook> datas;
    private final Activity aty;
    private int currentHidePosition = -1;
    private final int width;
    private final int height;
    private boolean dataChange = false;

    public NotebookAdapter(Activity aty, List<Notebook> datas) {
        super();
        Collections.sort(datas);
        this.datas = datas;
        this.aty = aty;
        width = DensityUtils.getScreenWidth(aty) / 2;
        height = (int) aty.getResources().getDimension(R.dimen.space_35);
    }

    public void refurbishData(List<Notebook> datas) {
        if (datas == null) {
            datas = new ArrayList<Notebook>(1);
        }
        Collections.sort(datas);
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<Notebook> getDatas() {
        return datas;
    }

    //数据是否发生了改变
    public boolean getDataChange() {
        return dataChange;
    }

    static class ViewHolder {

        View titleRL;
        TextView dateTV;
        ImageView stateIV;
        TextView  contentTV;
        ImageView thumbtackIV;

        public ViewHolder(View view){

            titleRL=view.findViewById(R.id.notebook_title_rl);
            dateTV=(TextView) view.findViewById(R.id.notebook_date_tv);
            stateIV=(ImageView) view.findViewById(R.id.notebook_state_iv);
            contentTV=(TextView) view.findViewById(R.id.notebook_content_tv);
            thumbtackIV=(ImageView) view.findViewById(R.id.notebook_thumbtack_iv);

        }

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        datas.get(position).setIid(position);
        Notebook data = datas.get(position);

        ViewHolder vh = null;
        if (view == null || view.getTag()==null) {
            view = View.inflate(aty, R.layout.list_cell_notebook, null);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
       }

        vh.titleRL.setBackgroundColor(NotebookEditFragment.titleBackGrounds[data.getColor()]);
        vh.dateTV.setText(data.getDate());

        if (data.getId() > 0) {
            vh.stateIV.setVisibility(View.GONE);
        } else {
            vh.stateIV.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vh.contentTV
                .getLayoutParams();
        params.width = width;
        params.height = (params.width - height);
        vh.contentTV.setLayoutParams(params);
        vh.contentTV.setText(Html.fromHtml(data.getContent()));
        vh.contentTV.setBackgroundColor(NotebookEditFragment.backGrounds[data
                .getColor()]);

        vh.thumbtackIV.setImageResource(NotebookEditFragment.thumbtackImgs[data
                .getColor()]);

        if (position == currentHidePosition) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        dataChange = true;
        if (oldPosition >= datas.size() || oldPosition < 0) {
            return;
        }
        Notebook temp = datas.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(datas, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(datas, i, i - 1);
            }
        }
        datas.set(newPosition, temp);
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.currentHidePosition = hidePosition;
        notifyDataSetChanged();
    }



}
