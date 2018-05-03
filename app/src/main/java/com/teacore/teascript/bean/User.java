package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**用户实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-6.
 */
@XStreamAlias("user")
public class User extends Entity{

    //取消关注
    public static final int RELATION_ACTION_DELETE=0x00;
    //加关注
    public static final int RELATION_ACTION_ADD=0x01;
    //双方互为好友
    public static final int RELATION_TYPE_BOTH=0x01;
    //你单方便关注他
    public static final int RELATION_TYPE_FANS_HIM=0x02;
    //互不关注
    public static final int RELATION_TYPE_NULL=0x03;
    //他单方面关注你
    public static final int RELATION_TYPE_FANS_ME=0x04;

    @XStreamAlias("id")
    private int id;

    @XStreamAlias("name")
    private String name;

    @XStreamAlias("location")
    private String location;

    @XStreamAlias("fans")
    private int fans;

    @XStreamAlias("followers")
    private int followers;

    @XStreamAlias("score")
    private int score;

    @XStreamAlias("portrait")
    private String portrait;

    @XStreamAlias("joinTime")
    private String joinTime;

    @XStreamAlias("gender")
    private String gender;

    @XStreamAlias("devPlatform")
    private String devPlatform;

    @XStreamAlias("expertise")
    private String expertise;

    @XStreamAlias("relation")
    private int relation;

    @XStreamAlias("latestOnline")
    private  String latestOnline;

    @XStreamAlias("from")
    private String from;

    @XStreamAlias("favoriteCount")
    private int favoriteCount;

    @XStreamAlias("email")
    private String email;

    private String account;

    private String pwd;

    private boolean isRememberMe;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDevPlatform() {
        return devPlatform;
    }

    public void setDevPlatform(String devPlatform) {
        this.devPlatform = devPlatform;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public String getLatestOnline() {
        return latestOnline;
    }

    public void setLatestOnline(String latestOnline) {
        this.latestOnline = latestOnline;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public boolean isRememberMe() {
        return isRememberMe;
    }

    public void setRememberMe(boolean isRememberMe) {
        this.isRememberMe = isRememberMe;
    }

    @Override
    public String toString() {
        return "User [uid=" + id + ", location=" + location + ", name=" + name
                + ", fans=" + fans + ", followers=" + followers + ", score="
                + score + ", portrait=" + portrait + ", jointime=" + joinTime
                + ", gender=" + gender + ", devplatform=" + devPlatform
                + ", expertise=" + expertise + ", relation=" + relation
                + ", latestonline=" + latestOnline + ", from=" + from
                + ", favoritecount=" + favoriteCount + ", account=" + account
                + ", pwd=" + pwd + ", isRememberMe=" + isRememberMe + "]";
    }

}
