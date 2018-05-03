package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**
 * 个人信息专用实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-15
 */
@XStreamAlias("teascript")
public class UserInformation extends Base{

    @XStreamAlias("user")
    private User user;

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("actives")
    private List<Active> activeList;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Active> getActiveList() {
        return activeList;
    }

    public void setActiveList(List<Active> activeList) {
        this.activeList = activeList;
    }

}
