package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 消息details实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-18
 */
@XStreamAlias("teascript")
public class NoticeDetail extends Entity{

    @XStreamAlias("notice")
    private Notice notice;

    public Notice getNotice() {
        return notice;
    }

    public void setNews(Notice notice) {
        this.notice = notice;
    }
}
