package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果实体类的列表类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-28
 */
@XStreamAlias("teascript")
public class SearchResultList extends Entity implements EntityList<SearchResult>{

    public final static String CATALOG_ALL = "all";
    public final static String CATALOG_NEWS = "news";
    public final static String CATALOG_POST = "post";
    public final static String CATALOG_SOFTWARE = "software";
    public final static String CATALOG_BLOG = "blog";

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("resultslist")
    private List<SearchResult> resultsList = new ArrayList<SearchResult>();

    public int getPageSize() {
        return pageSize;
    }

    @Override
    public List<SearchResult> getList() {
        return resultsList;
    }

    public void setList(List<SearchResult> list) {
        this.resultsList = list;
    }

}
