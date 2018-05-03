package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-30
 */
@XStreamAlias("teascript")
public class FriendsList extends Entity implements EntityList<Friend>{

    public final static int TYPE_FANS = 0x00;
    public final static int TYPE_FOLLOWER = 0x01;

    @XStreamAlias("friends")
    private List<Friend> friendlist = new ArrayList<Friend>();

    public List<Friend> getFriendlist() {
        return friendlist;
    }

    public void setFriendlist(List<Friend> resultlist) {
        this.friendlist = resultlist;
    }

    @Override
    public List<Friend> getList() {
        return friendlist;
    }

}
