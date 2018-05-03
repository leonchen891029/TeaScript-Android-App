package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 软件简介实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-28
 */
@XStreamAlias("softwareinfo")
public class SoftwareIntro extends Entity{

    @XStreamAlias("name")
    private String name;
    @XStreamAlias("description")
    private String description;
    @XStreamAlias("url")
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
