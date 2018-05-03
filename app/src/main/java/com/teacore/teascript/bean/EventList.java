package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动实体类列表
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */
@XStreamAlias("teascript")
public class EventList extends Entity implements EntityList<Event>{


    public final static int EVENT_LIST_TYPE_NEW_EVENT = 0X00;// 近期活动

    public final static int EVENT_LIST_TYPE_MY_EVENT = 0X01;// 我的活动

    @XStreamAlias("events")
    private List<Event> eventsList = new ArrayList<Event>();

    public List<Event> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<Event> list) {
        this.eventsList = list;
    }

    @Override
    public List<Event> getList() {
        return eventsList;
    }
}
