package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**博客评论列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-6
 */
@XStreamAlias("teascript")
public class BlogCommentList extends Entity implements EntityList<Comment>{

    @XStreamAlias("pagesize")
    private int pageSize;
    @XStreamAlias("allCount")
    private int allCount;
    @XStreamAlias("comments")
    private List<Comment> commentlist = new ArrayList<Comment>();

    public int getPageSize() {
        return pageSize;
    }

    public int getAllCount() {
        return allCount;
    }

    public List<Comment> getCommentlist() {
        return commentlist;
    }

    @Override
    public List<Comment> getList() {
        return commentlist;
    }
}
