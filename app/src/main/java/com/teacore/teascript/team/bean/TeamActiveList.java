package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态列表
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-11
 */
@XStreamAlias("teascript")
public class TeamActiveList extends Entity implements EntityList<TeamActive>{

    private static final long serialVersionUID = 1L;

    @XStreamAlias("actives")
    ArrayList<TeamActive> actives = new ArrayList<TeamActive>();

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public List<TeamActive> getList() {
        return actives;
    }

    public ArrayList<TeamActive> getActives() {
        return actives;
    }

    public void setActives(ArrayList<TeamActive> actives) {
        this.actives = actives;
    }

}
