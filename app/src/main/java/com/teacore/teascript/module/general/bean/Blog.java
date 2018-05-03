package com.teacore.teascript.module.general.bean;

import java.io.Serializable;

/**
 * 综合下的博客bean类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-2
 */

public class Blog implements Serializable{

    //博客界面类型 0:常规 1:热门 2:最近
    public static final int VIEW_TYPE_DATA=0;
    public static final int VIEW_TYPE_HEAT_LINE=1;
    public static final int VIEW_TYPE_LATELY_LINE=2;

    //博客类型
    public static final int BLOG_TYPE_NORMAL=1;
    public static final int BLOG_TYPE_HEAT=2;

    private long id;
    private String title;
    private String body;
    private String author;
    private String pubDate;
    private int commentCount;
    private int viewCount;
    private String href;
    private boolean recommend;
    //是否是原创
    private boolean original;
    //博客类型
    private int type;
    //博客界面类型
    private int viewType;

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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }


}
