package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**团队讨论实体类列表
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-10
 */
@XStreamAlias("teascript")
public class TeamDiscussList extends Entity implements EntityList<TeamDiscuss> {

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("totalCount")
    private int totalCount;

    @XStreamAlias("discusses")
    private List<TeamDiscuss> list = new ArrayList<TeamDiscuss>();

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<TeamDiscuss> getList() {
        return list;
    }

    public void setList(List<TeamDiscuss> list) {
        this.list = list;
    }

}