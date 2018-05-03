package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 团队项目列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-22
 */
@XStreamAlias("teascript")
public class TeamProjectList extends Entity implements EntityList<TeamProject>{

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("projectlist")
    private List<TeamProject> list = new ArrayList<TeamProject>();

    @Override
    public List<TeamProject> getList() {
        return list;
    }

}
