package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 团队周报实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-24
 */
@XStreamAlias("teamdiary")
public class TeamDiary extends Entity {

    @XStreamAlias("title")
    private String title;

    @XStreamAlias("reply")
    private int reply;

    @XStreamAlias("createTime")
    private String createTime;

    @XStreamAlias("author")
    private Author author;

    @XStreamAlias("diarydaydata")
    private DiaryDayData diaryDayData;

    public DiaryDayData getTeamDiaryDetail() {
        return diaryDayData;
    }

    public void setTeamDiaryDetail(DiaryDayData diaryDayData) {
        this.diaryDayData = diaryDayData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

}
