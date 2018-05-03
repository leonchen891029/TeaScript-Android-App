package com.teacore.teascript.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.SearchResult;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TimeUtils;

/**
 * 搜索结果适配器类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */

public class SearchResultAdapter extends BaseListAdapter<SearchResult>{

    static class ViewHolder{

        ImageView tipIV;
        TextView  titleTV;
        TextView  descriptionTV;
        TextView  authorTV;
        TextView  timeTV;
        TextView  commentCountTV;

        public ViewHolder(View view){

            tipIV=(ImageView) view.findViewById(R.id.tip_iv);
            titleTV=(TextView) view.findViewById(R.id.title_tv);
            descriptionTV=(TextView) view.findViewById(R.id.description_tv);
            authorTV=(TextView) view.findViewById(R.id.author_tv);
            timeTV=(TextView) view.findViewById(R.id.time_tv);
            commentCountTV=(TextView) view.findViewById(R.id.comment_count_tv);

        }

    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_search_result, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        SearchResult item = (SearchResult) mDatas.get(position);

        vh.tipIV.setVisibility(View.GONE);

        vh.titleTV.setText(item.getTitle());

        if (item.getDescription() == null || StringUtils.isEmpty(item.getDescription())) {
            vh.descriptionTV.setVisibility(View.GONE);
        } else {
            vh.descriptionTV.setVisibility(View.VISIBLE);
            vh.descriptionTV.setText(item.getDescription());
        }

        if (!StringUtils.isEmpty(item.getAuthor())) {
            vh.authorTV.setText(item.getAuthor());
        } else {
            vh.authorTV.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(item.getPubDate())) {
            vh.timeTV.setText(TimeUtils.friendly_time(item.getPubDate()));
        } else {
            vh.timeTV.setVisibility(View.GONE);
        }


        vh.commentCountTV.setVisibility(View.GONE);

        return convertView;
    }

}
