package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**
 * 被赞过的Teatime列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-12
 */
@XStreamAlias("teascript")
public class TeatimeLikesList extends Entity implements EntityList<TeatimeLike>{


    @XStreamAlias("teatimelikelist")
    private List<TeatimeLike> teatimeLikeList;

    @Override
    public List<TeatimeLike> getList() {
        return teatimeLikeList;
    }

    public void setList(List<TeatimeLike> list) {
        this.teatimeLikeList = list;
    }


}
