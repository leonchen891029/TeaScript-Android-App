package com.teacore.teascript.module.general.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.teacore.teascript.R;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.adapter.generaladapter.BlogAdapter;
import com.teacore.teascript.module.general.bean.Blog;
import com.teacore.teascript.module.general.base.basebean.PageBean;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.base.basefragment.BaseListFragment;
import com.teacore.teascript.module.general.detail.activity.BlogDetailActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;

import java.lang.reflect.Type;

/**
 * 用户博客界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-18
 */

public class UserBlogFragment extends BaseListFragment<Blog>{

    public static final String HISTORY_BLOG="history_my_blog";
    public static final String USER_ID="user_id";

    private int userId;

    @Override
    protected void initBundle(Bundle bundle){
        super.initBundle(bundle);
        userId=bundle.getInt(USER_ID,0);
    }

    @Override
    protected void requestData(){
        super.requestData();
        TeaScriptApi.getUserBlogList(Blog.BLOG_TYPE_NORMAL,(mIsRefresh ? (mPageBean != null ? mPageBean.getPrevPageToken() : null) : (mPageBean != null ? mPageBean.getNextPageToken() : null))
                , userId, mHandler);
    }

    @Override
    protected BaseListAdapter<Blog> getListAdapter(){
        BlogAdapter blogAdapter = new BlogAdapter(this);
        blogAdapter.setUserBlog(true);
        return blogAdapter;
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Blog>>>() {
        }.getType();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Blog blog = mAdapter.getItem(position);
        if (blog != null) {
            BlogDetailActivity.show(getActivity(), blog.getId());
            TextView titleTV = (TextView) view.findViewById(R.id.blog_title_tv);
            TextView contentTV = (TextView) view.findViewById(R.id.blog_body_tv);

            updateTextColor(titleTV, contentTV);
            saveToReadedList(UserBlogFragment.HISTORY_BLOG, blog.getId() + "");
        }
    }


}
