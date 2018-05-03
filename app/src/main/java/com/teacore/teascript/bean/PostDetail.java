package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**帖子详情
 * @author 陈晓帆
 * @version 1.0
 * Created by apple on 17/10/21.
 */
@XStreamAlias("teascript")
public class PostDetail extends Entity{

    @XStreamAlias("post")
    private Post post;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
