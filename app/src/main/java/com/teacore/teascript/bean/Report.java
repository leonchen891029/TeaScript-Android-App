package com.teacore.teascript.bean;

/**
 * 举报实体类
 * @author陈晓帆
 * @version 1.0
 * Created 2017-6-21
 */

public class Report extends Entity{

    //类型
    public static final byte TYPE_QUESTION=0x02;

    //需要举报的ID
    private long objId;
    //举报的链接地址
    private String url;
    //举报的类型
    private byte objType;
    //举报的原因
    private int reason;
    //其他原因
    private String otherReason;

    public long getObjId() {
        return objId;
    }

    public void setObjId(long objId) {
        this.objId = objId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte getObjType() {
        return objType;
    }

    public void setObjType(byte objType) {
        this.objType = objType;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public String getOtherReason() {
        return otherReason;
    }

    public void setOtherReason(String otherReason) {
        this.otherReason = otherReason;
    }

}
