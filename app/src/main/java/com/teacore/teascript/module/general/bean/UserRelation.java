package com.teacore.teascript.module.general.bean;

import java.io.Serializable;

/**
 * 用户关系bean类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-2
 */

public class UserRelation implements Serializable{

    private int relation;
    private String author;
    private long authorId;

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
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

}
