package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目的任务列表
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-20
 */
@XStreamAlias("teascript")
public class TeamIssueCatalogList extends Entity implements EntityList<TeamIssueCatalog>{

    @XStreamAlias("totalcount")
    private int totalCount;

    @XStreamAlias("catalogs")
    private ArrayList<TeamIssueCatalog> list = new ArrayList<TeamIssueCatalog>();

    @Override
    public List<TeamIssueCatalog> getList() {
        return list;
    }

    public void setList(ArrayList<TeamIssueCatalog> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

}
