package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Teatime detail bean 类
 * @author 陈晓帆
 * @version 2017-3-9
 */
@XStreamAlias("teascript")
public class TeatimeDetail extends Entity{

    @XStreamAlias("teatime")
    private Teatime teatime;

    public Teatime getTeatime() {
        return teatime;
    }
    public void setTeatime(Teatime teatime) {
        this.teatime = teatime;
    }
}
