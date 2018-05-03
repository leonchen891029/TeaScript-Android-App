package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**新闻详情
 * @author 陈晓帆
 * @version 1,0
 * Created 2017-2-18
 */
@XStreamAlias("teascript")
public class NewsDetail extends Entity{

    @XStreamAlias("news")
    private News news;

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

}
