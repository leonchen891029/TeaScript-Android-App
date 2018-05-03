package com.teacore.teascript.module.general.adapter.generaladapter;

import android.support.v4.content.ContextCompat;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.module.general.adapter.ViewHolder;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.bean.Event;
import com.teacore.teascript.module.general.fragment.generallistfragment.EventGeneralListFragment;

/**
 * 综合页面下活动的Adapter
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-11
 */

public class EventAdapter extends BaseListAdapter<Event> {

    public EventAdapter(Callback callback){
        super(callback);
    }

    @Override
    protected int getLayoutId(int position, Event item) {
        return R.layout.general_list_cell_event;
    }

    @Override
    protected void convert(ViewHolder vh,Event item,int position){

        vh.setText(R.id.event_title_tv,item.getTitle());
        vh.setImageFromNet(R.id.event_iv,item.getImg());
        vh.setText(R.id.event_pub_date_tv, item.getStartDate());
        vh.setText(R.id.event_member_tv,String.valueOf(item.getApplyCount()));
        vh.setTextColor(R.id.event_title_tv, AppContext.isOnReadedPostList(EventGeneralListFragment.HISTORY_EVENT,item.getId()+"")?
                ContextCompat.getColor(mCallback.getContext(),R.color.count_text_color_light):
                ContextCompat.getColor(mCallback.getContext(),R.color.day_textColor));
        switch(item.getStatus()){
            case Event.STATUS_END:
                vh.setText(R.id.event_state_tv, R.string.event_status_end, R.drawable.bg_event_end, 0x1a000000);
                vh.setTextColor(R.id.event_title_tv, ContextCompat.getColor(mCallback.getContext(),R.color.gray));
                break;
            case Event.STATUS_ING:
                vh.setText(R.id.event_state_tv, R.string.event_status_ing, R.drawable.bg_event_ing, 0xFF24cf5f);
                break;
            case Event.STATUS_DEADLINE_FOR_REGISTER:
                vh.setText(R.id.event_state_tv, R.string.event_status_dfr, R.drawable.bg_event_end, 0x1a000000);
                vh.setTextColor(R.id.event_title_tv, ContextCompat.getColor(mCallback.getContext(),R.color.light_gray));
                break;
        }
        int typeStr=R.string.tssite;
        switch(item.getType()){
            case Event.EVENT_TYPE_TEASCRIPT:
                typeStr=R.string.event_type_teascript;
                break;
            case Event.EVENT_TYPE_TEC:
                typeStr=R.string.event_type_tec;
                break;
            case Event.EVENT_TYPE_OTHER:
                typeStr=R.string.event_type_other;
                break;
            case Event.EVENT_TYPE_OUTSIDE:
                typeStr = R.string.event_type_outside;
                break;
        }
        vh.setText(R.id.event_type_tv,typeStr);
    }

}
