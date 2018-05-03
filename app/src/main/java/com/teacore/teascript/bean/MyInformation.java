package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 我的资料实体类
 * @authot 陈晓帆
 * @version 1.0
 * Created 2017-4-14
 */
@XStreamAlias("teascript")
public class MyInformation extends Base{

    @XStreamAlias("user")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
