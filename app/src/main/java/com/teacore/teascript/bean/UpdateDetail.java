package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 更新实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-420
 */

@XStreamAlias("teascript")
public class UpdateDetail extends Entity{

    @XStreamAlias("update")
    private Update update;

    public  Update getUpdate(){
        return update;
    }

    public void setUpdate(Update update){
        this.update=update;
    }




}
