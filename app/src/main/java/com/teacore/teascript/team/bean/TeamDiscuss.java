package com.teacore.teascript.team.bean;

import com.teacore.teascript.bean.Entity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**团队讨论实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-8
 */
@XStreamAlias("discuss")
public class TeamDiscuss extends Entity {

    @XStreamAlias("type")
    private String type;

    @XStreamAlias("title")
    private String title;

    @XStreamAlias("body")
    private String body;

    @XStreamAlias("createTime")
    private String createTime;

    // 回复数量
    @XStreamAlias("replyCount")
    private int replyCount;

    // 点赞数
    @XStreamAlias("voteUp")
    private int voteUp;

    @XStreamAlias("author")
    private Author author;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getVoteUp() {
        return voteUp;
    }

    public void setVoteUp(int voteUp) {
        this.voteUp = voteUp;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
