package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**评论列表的实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-22
 */
@XStreamAlias("teascript")
public class CommentList extends Entity implements EntityList<Comment>{

    public final static int CATALOG_NEWS = 1;
    public final static int CATALOG_POST = 2;
    public final static int CATALOG_Teatime = 3;
    // 动态与留言都属于消息中心
    public final static int CATALOG_ACTIVE = 4;
    public final static int CATALOG_MESSAGE = 4;

    @XStreamAlias("pagesize")
    private int pageSize;
    @XStreamAlias("allcount")
    private int allCount;
    @XStreamAlias("comments")
    private final List<Comment> commentlist = new ArrayList<Comment>();

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

    public void sortList() {
        Collections.sort(commentlist, new Comparator<Comment>() {

            @Override
            public int compare(Comment lhs, Comment rhs) {
                return lhs.getPubDate().compareTo(rhs.getPubDate());
            }
        });
    }

}
