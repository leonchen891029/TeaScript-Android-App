package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 软件简介列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-30
 */
@XStreamAlias("teascript")
public class SoftwareIntroList extends Entity implements EntityList<SoftwareIntro>{

    public final static String PREF_READED_SOFTWARE_LIST = "readed_software_list.pref";

    public final static String CATALOG_RECOMMEND = "recommend";
    public final static String CATALOG_TIME = "time";
    public final static String CATALOG_VIEW = "view";
    public final static String CATALOG_LIST_CN = "list_cn";

    @XStreamAlias("softwarecount")
    private int softwareCount;
    @XStreamAlias("pagesize")
    private int pageSize;
    @XStreamAlias("softwarelist")
    private List<SoftwareIntro> softwareList = new ArrayList<SoftwareIntro>();

    public int getSoftwareCount() {
        return softwareCount;
    }

    public void setSoftwareCount(int softwareCount) {
        this.softwareCount = softwareCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<SoftwareIntro> getSoftwareList() {
        return softwareList;
    }

    public void setSoftwareList(List<SoftwareIntro> softwareList) {
        this.softwareList = softwareList;
    }

    @Override
    public List<SoftwareIntro> getList() {
        return softwareList;
    }



}
