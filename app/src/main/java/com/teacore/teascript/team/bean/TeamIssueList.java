package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;

/**团队任务列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-8
 */
@XStreamAlias("teascript")
public class TeamIssueList extends Entity implements EntityList<TeamIssue> {

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("totalCount")
    private int totalCount;

    @XStreamAlias("issues")
    private ArrayList<TeamIssue> list = new ArrayList<TeamIssue>();

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

    @Override
    public ArrayList<TeamIssue> getList() {
        return list;
    }

    public void setList(ArrayList<TeamIssue> list) {
        this.list = list;
    }





}
