package com.teacore.teascript.module.general.bean;

import java.util.List;

/**
 * 综合下的QuestionDetail类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-13
 */

public class QuestionDetail extends Question{


    private boolean favorite;
    private String href;
    private List<QuestionTags> tags;

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


    public List<QuestionTags> getTags() {
        return tags;
    }

    public void setTags(List<QuestionTags> tags) {
        this.tags = tags;
    }
}
