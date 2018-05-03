package com.teacore.teascript.module.general.bean;

import java.io.Serializable;

/**
 * 综合下的question bean类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-13
 */

public class Question implements Serializable{

    public static int QUESTION_TYPE_ASK=1;
    public static int QUESTION_TYPE_SHARE=2;
    public static int QUESTION_TYPE_COMPOSITE=3;
    public static int QUESTION_TYPE_PROFESSION=4;
    public static int QUESTION_TYPE_WEBSITE=5;

    private long id;
    private String title;
    private String body;
    private String author;
    private long authorId;
    private String authorPortrait;
    private String pubDate;
    private int commentCount;
    //浏览次数
    private int viewCount;
    //question的type类型
    private int type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPortrait() {
        return authorPortrait;
    }

    public void setAuthorPortrait(String authorPortrait) {
        this.authorPortrait = authorPortrait;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

}
