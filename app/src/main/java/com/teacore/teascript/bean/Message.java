package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 留言实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */
@XStreamAlias("message")
public class Message extends Entity{

    public final static int CLIENT_MOBILE=1;
    public final static int CLIENT_ANDROID =2;
    public final static int CLIENT_IPHONE =3;
    public final static int CLIENT_WINDOWS_PHONE =4;

    @XStreamAlias("portrait")
    private String portrait;

    @XStreamAlias("friendid")
    private int friendId;

    @XStreamAlias("friendname")
    private String friendName;

    @XStreamAlias("sender")
    private String sender;

    @XStreamAlias("senderid")
    private int senderId;

    @XStreamAlias("content")
    private String content;

    @XStreamAlias("messageCount")
    private int messageCount;

    @XStreamAlias("pubDate")
    private String pubDate;

    @XStreamAlias("appClient")
    private int appClient;

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getAppClient() {
        return appClient;
    }

    public void setAppClient(int appClient) {
        this.appClient = appClient;
    }

}
