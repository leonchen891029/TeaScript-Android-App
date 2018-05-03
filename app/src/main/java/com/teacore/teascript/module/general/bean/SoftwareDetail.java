package com.teacore.teascript.module.general.bean;

import java.util.List;

/**
 * SoftwareDetail bean类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-1
 */

public class SoftwareDetail extends Software{

    //软件别名
    private String extName;
    private String logo;
    private String body;
    private String author;
    private long authorId;
    private String authorPortrait;
    //软件相关的license信息
    private String license;
    //软件的首页
    private String homePage;
    //软件文档
    private String document;
    private String download;
    //开发语言
    private String language;
    //支持平台
    private String supportOS;
    //收录时间
    private String collectionDate;
    //发布时间
    private String pubDate;
    //评论量(动弹量)
    private int commentCount;
    //浏览量
    private int viewCount;//浏览量
    //是否收藏
    private boolean favorite;
    //是否推荐
    private boolean recommend;
    //相关推荐
    private List<About> abouts;

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSupportOS() {
        return supportOS;
    }

    public void setSupportOS(String supportOS) {
        this.supportOS = supportOS;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
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

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public List<About> getAbouts() {
        return abouts;
    }

    public void setAbouts(List<About> abouts) {
        this.abouts = abouts;
    }
























}
