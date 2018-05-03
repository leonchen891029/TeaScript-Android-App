package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 周报列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-26
 */
@XStreamAlias("teascript")
public class TeamDiaryList extends Entity implements EntityList<TeamDiary>{

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("totalcount")
    private int totalCount;

    @XStreamAlias("teamdiaries")
    private List<TeamDiary> list = new ArrayList<TeamDiary>();

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

    public List<TeamDiary> getList() {
        return list;
    }

    public void setList(List<TeamDiary> list) {
        this.list = list;
    }

}
