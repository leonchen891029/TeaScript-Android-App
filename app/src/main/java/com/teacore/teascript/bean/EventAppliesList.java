package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动报名者实体类列表
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-1
 */
@XStreamAlias("teascript")
public class EventAppliesList extends Entity implements EntityList<EventApply>{

    public final static int TYPE_FANS = 0x00;
    public final static int TYPE_FOLLOWER = 0x01;

    @XStreamAlias("applieslist")
    private List<EventApply> appliesList = new ArrayList<EventApply>();

    @Override
    public List<EventApply> getList() {
        return appliesList;
    }

    public void setList(List<EventApply> list) {
        this.appliesList = list;
    }
}
