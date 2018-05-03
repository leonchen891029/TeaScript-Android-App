package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**博客详情
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-8
*/
@XStreamAlias("teascript")
public class BlogDetail extends Entity{

    @XStreamAlias("blog")
    private Blog blog;

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

}
