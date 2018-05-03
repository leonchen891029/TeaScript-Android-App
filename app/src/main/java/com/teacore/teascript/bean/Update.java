package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * update bean类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-29
 */
@XStreamAlias("update")
public class Update extends Entity{

    @XStreamAlias("wp7")
    private String wp7;
    @XStreamAlias("ios")
    private String ios;
    @XStreamAlias("android")
    private Android android;

    public String getWp7() {
        return wp7;
    }

    public void setWp7(String wp7) {
        this.wp7 = wp7;
    }

    public String getIos() {
        return ios;
    }

    public void setIos(String ios) {
        this.ios = ios;
    }

    public Android getAndroid() {
        return android;
    }

    public void setAndroid(Android android) {
        this.android = android;
    }





}
