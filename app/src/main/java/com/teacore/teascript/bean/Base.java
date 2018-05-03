package com.teacore.teascript.bean;


import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

/**
 * 实体类基类：实现序列化
 * @author 陈晓帆
 * @version 1.0
 * @created 2017-1-1
 *
 */
public abstract class Base implements Serializable{

    @XStreamAlias("notice")
    protected Notice notice;

    public Notice getNotice(){
        return notice;
    }

    public void  setNotice(Notice notice){
        this.notice=notice;
    }


}
