package com.teacore.teascript.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseListAdapter;
import com.teacore.teascript.bean.Event;
import com.teacore.teascript.bean.EventList;
import com.teacore.teascript.util.GlideImageLoader;

/**
 * 活动列表适配器类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */

public class EventAdapter extends BaseListAdapter<Event>{

    //近期活动
    private int eventType = EventList.EVENT_LIST_TYPE_NEW_EVENT;

    private RequestManager mImageLoader;

    static class ViewHolder {

        ImageView statusIV;
        ImageView eventIV;
        TextView  titleTV;
        TextView  timeTV;
        TextView  spotTV;

        public ViewHolder(View view) {
           statusIV=(ImageView) view.findViewById(R.id.event_status_iv);
            eventIV=(ImageView) view.findViewById(R.id.event_iv);
            titleTV=(TextView) view.findViewById(R.id.event_title_tv);
            timeTV=(TextView) view.findViewById(R.id.event_time_tv);
            spotTV=(TextView) view.findViewById(R.id.event_spot_tv);
        }
    }

    //设置活动类型
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;

        mImageLoader= Glide.with(parent.getContext());

        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_event, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Event item =(Event) mDatas.get(position);

        //设置活动状态
        setEventStatus(item, vh);

        //加载活动图片
        GlideImageLoader.loadImage(mImageLoader,vh.eventIV,item.getCover());

        vh.titleTV.setText(item.getTitle());
        vh.timeTV.setText(item.getStartTime());
        vh.spotTV.setText(item.getSpot());

        return convertView;
    }

    private void setEventStatus(Event event, ViewHolder vh) {

        switch (this.eventType) {
            case EventList.EVENT_LIST_TYPE_NEW_EVENT:
                if (event.getApplyStatus() == Event.APPLYSTATUS_CHECKING
                        || event.getApplyStatus() == Event.APPLYSTATUS_CHECKED) {
                    vh.statusIV.setImageResource(R.drawable.icon_event_status_checked);
                    vh.statusIV.setVisibility(View.VISIBLE);
                } else {
                    vh.statusIV.setVisibility(View.GONE);
                }
                break;
            case EventList.EVENT_LIST_TYPE_MY_EVENT:
                if (event.getApplyStatus() == Event.APPLYSTATUS_ATTEND) {
                    vh.statusIV.setImageResource(R.drawable.icon_event_status_attend);
                } else if (event.getStatus() == Event.EVENT_STATUS_APPLYING) {
                    vh.statusIV.setImageResource(R.drawable.icon_event_status_checked);
                } else {
                    vh.statusIV.setImageResource(R.drawable.icon_event_status_over);
                }
                vh.statusIV.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

}
