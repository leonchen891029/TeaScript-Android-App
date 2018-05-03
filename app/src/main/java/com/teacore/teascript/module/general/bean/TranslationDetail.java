package com.teacore.teascript.module.general.bean;

import java.io.Serializable;

/**
 * 翻译的文章
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-5
 */

public class TranslationDetail implements Serializable{


    private long id;
    private String title;
    //原始文章名称，比如英文的
    private String originlTitle;
    private String body;
    private String author;
    private long authorId;
    private String authorPortrait;
    //与翻译作者的关系
    private int authorRelation;
    private String pubDate;
    private int commentCount;
    private int viewCount;
    private String href;
    private boolean favorite;

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

    public String getOriginlTitle() {
        return originlTitle;
    }

    public void setOriginlTitle(String originlTitle) {
        this.originlTitle = originlTitle;
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

    public int getAuthorRelation() {
        return authorRelation;
    }

    public void setAuthorRelation(int authorRelation) {
        this.authorRelation = authorRelation;
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }


}
