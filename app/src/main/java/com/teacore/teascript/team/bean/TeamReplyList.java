package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.teacore.teascript.bean.EntityList;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**TeamRepliesList类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-6
 */

public class TeamReplyList extends Entity implements EntityList<TeamReply>{

    @XStreamAlias("pagesize")
    private int pagesize;

    @XStreamAlias("totalCount")
    private int totalCount;

    @XStreamAlias("replies")
    private List<TeamReply> list;

    @Override
    public List<TeamReply> getList() {
        return list;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setList(List<TeamReply> list) {
        this.list = list;
    }


}
