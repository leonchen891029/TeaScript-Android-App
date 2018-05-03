package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 被点赞的Teatime的实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-10
 */
@XStreamAlias("teatimelike")
public class TeatimeLike extends Entity{

    @XStreamAlias("user")
    private User user;

    @XStreamAlias("teatime")
    private Teatime teatime;

    @XStreamAlias("datatime")
    private String dataTime;

    @XStreamAlias("appclient")
    private int appClient;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Teatime getTeatime() {
        return teatime;
    }

    public void setTeatime(Teatime teatime) {
        this.teatime = teatime;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public int getAppClient() {
        return appClient;
    }

    public void setAppClient(int appClient) {
        this.appClient = appClient;
    }


}
