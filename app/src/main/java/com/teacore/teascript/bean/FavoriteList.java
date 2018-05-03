package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**收藏列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-22
 */
@XStreamAlias("teascript")
public class FavoriteList extends Entity implements EntityList<Favorite>{

    public final static int TYPE_ALL = 0x00;
    public final static int TYPE_SOFTWARE = 0x01;
    public final static int TYPE_POST = 0x02;
    public final static int TYPE_BLOG = 0x03;
    public final static int TYPE_NEWS = 0x04;
    public final static int TYPE_CODE = 0x05;

    @XStreamAlias("pagesize")
    private int pageSize;
    @XStreamAlias("favorites")
    private List<Favorite> favoritelist = new ArrayList<Favorite>();

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pagesize) {
        this.pageSize = pagesize;
    }

    public List<Favorite> getFavoritelist() {
        return favoritelist;
    }

    public void setFavoritelist(List<Favorite> favoritelist) {
        this.favoritelist = favoritelist;
    }

    @Override
    public List<Favorite> getList() {
        return favoritelist;
    }

}
