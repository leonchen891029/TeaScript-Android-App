package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 活动报名者实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */
@XStreamAlias("eventapply")
public class EventApply extends Entity{

    @XStreamAlias("uid")
    private int uid;

    @XStreamAlias("name")
    private String name;

    @XStreamAlias("portrait")
    private String portrait;

    @XStreamAlias("company")
    private String company;

    @XStreamAlias("job")
    private String job;

    public int getId() {
        return uid;
    }
    public void setId(int userid) {
        this.uid = userid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPortrait() {
        return portrait;
    }
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getJob() {
        return job;
    }
    public void setJob(String job) {
        this.job = job;
    }


}
