package com.teacore.teascript.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * 帖子列表实体类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-2
 */
@XStreamAlias("teascript")
public class PostList extends Entity implements EntityList<Post>{

    public final static String PREF_READED_POST_LIST = "readed_post_list.pref";

    @XStreamAlias("pagesize")
    private int pageSize;

    @XStreamAlias("postCount")
    private int postCount;

    @XStreamAlias("posts")
    private List<Post> postlist = new ArrayList<Post>();

    public int getPageSize() {
        return pageSize;
    }
    public int getPostCount() {
        return postCount;
    }
    public List<Post> getPostlist() {
        return postlist;
    }
    @Override
    public List<Post> getList() {
        return postlist;
    }

}
