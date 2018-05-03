package com.teacore.teascript.module.general.bean;

import java.io.Serializable;

/**
 * 综合下的软件bean类
 *@author 陈晓帆
 *@version 1.0
 * Created 2017-5-1
 */

public class Software implements Serializable{

    private long id;
    private String name;
    private String href;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }


}
