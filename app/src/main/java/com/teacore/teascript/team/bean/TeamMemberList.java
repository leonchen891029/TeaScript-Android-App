package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * Team 成员列表
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-13
 */
@XStreamAlias("teascript")
public class TeamMemberList extends Entity implements EntityList<TeamMember>{
    private static final long serialVersionUID = 1L;

    @XStreamAlias("members")
    private List<TeamMember> list = new ArrayList<TeamMember>();

    public void setList(List<TeamMember> list) {
        this.list = list;
    }

    @Override
    public List<TeamMember> getList() {
        return list;
    }
}
