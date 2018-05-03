package com.teacore.teascript.module.general.bean;

import java.io.Serializable;

public class QuestionTags implements Serializable {

    private long id;
    private String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {return content;}

    public void setContent(String content){this.content=content;}

}
