package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 找寻用户的列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */
@XStreamAlias("teascript")
public class FindUsersList extends Entity implements EntityList<User>{

    public final static int TYPE_FANS = 0x00;
    public final static int TYPE_FOLLOWER = 0x01;

    @XStreamAlias("users")
    private List<User> usersList = new ArrayList<User>();

    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    @Override
    public List<User> getList() {
        return usersList;
    }



}
