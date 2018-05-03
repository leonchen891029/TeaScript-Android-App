package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * Teatime点赞用户列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-21
 */
@XStreamAlias("teascript")
public class TeatimeLikeUserList implements EntityList<User>{

    @XStreamAlias("likeuserlist")
    private List<User> list = new ArrayList<User>();

    public List<User> getList() {
        return list;
    }

    public void setList(List<User> list) {
        this.list = list;
    }


}
