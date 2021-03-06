package com.teacore.teascript.module.general.bean;

import java.io.Serializable;

/**
 * 相关推荐bean类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-2
 */

public class About implements Serializable{

    private long id;
    private String title;
    private int commentCount;
    private int type;
    private int viewCount;

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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

}
