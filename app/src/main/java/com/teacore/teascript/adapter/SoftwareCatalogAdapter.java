package com.teacore.teascript.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.SoftwareCatalogList.SoftwareType;

/**
 *Software Catalog和Tag的适配器类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-1
 */

public class SoftwareCatalogAdapter extends BaseListAdapter<SoftwareType>{

    static class ViewHolder{
        TextView titleTV;

        public ViewHolder(View view){
            titleTV=(TextView) view.findViewById(R.id.name_tv);
        }

    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh = null;

        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout
                    .list_cell_software_catalog, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        SoftwareType softwareType = (SoftwareType) mDatas.get(position);
        vh.titleTV.setText(softwareType.getName());
        return convertView;
    }

}
