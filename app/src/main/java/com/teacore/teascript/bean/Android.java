package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Android版本信息类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */
@XStreamAlias("android")
public class Android extends Entity{

    @XStreamAlias("versionCode")
    private int versionCode;
    @XStreamAlias("versionName")
    private String versionName;
    @XStreamAlias("downloadUrl")
    private String downloadUrl;
    @XStreamAlias("updateLog")
    private String updateLog;
    @XStreamAlias("coverUpdate")
    private String coverUpdate;
    @XStreamAlias("coverStartDate")
    private String coverStartDate;
    @XStreamAlias("coverEndDate")
    private String coverEndDate;
    @XStreamAlias("coverURL")
    private String coverURL;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getCoverUpdate() {
        return coverUpdate;
    }

    public void setCoverUpdate(String coverUpdate) {
        this.coverUpdate = coverUpdate;
    }

    public String getCoverStartDate() {
        return coverStartDate;
    }

    public void setCoverStartDate(String coverStartDate) {
        this.coverStartDate = coverStartDate;
    }

    public String getCoverEndDate() {
        return coverEndDate;
    }

    public void setCoverEndDate(String coverEndDate) {
        this.coverEndDate = coverEndDate;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

































}
