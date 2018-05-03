package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 软件实体的包装类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-26
*/
@XStreamAlias("teascript")
public class SoftwareDetail extends Entity{

    @XStreamAlias("software")
    private Software software;

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

}
