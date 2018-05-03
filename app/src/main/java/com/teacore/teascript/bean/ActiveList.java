package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态实体列表
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-12
 */
@XStreamAlias("teascript")
public class ActiveList extends Entity implements EntityList<Active>{

    //最新
    public final static int CATALOG_LASTEST = 1;
    //@我
    public final static int CATALOG_ATME = 2;
    //评论
    public final static int CATALOG_COMMENT = 3;
    // 我自己
    public final static int CATALOG_MYSELF = 4;

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("activecount")
    private int activeCount;

    @XStreamAlias("actives")
    private List<Active> activeList = new ArrayList<Active>();

    @XStreamAlias("result")
    private Result result;

    public int getPageSize() {
        return pageSize;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public List<Active> getActiveList() {
        return activeList;
    }

    @Override
    public List<Active> getList() {
        return activeList;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }


}
