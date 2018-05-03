package com.teacore.teascript.module.general.fragment.generallistfragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.teacore.teascript.R;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.module.general.detail.activity.BlogDetailActivity;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.adapter.generaladapter.BlogAdapter;
import com.teacore.teascript.module.general.app.AppOperator;
import com.teacore.teascript.module.general.bean.Blog;
import com.teacore.teascript.module.general.base.basebean.PageBean;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.base.basefragment.BaseGeneralListFragment;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.widget.EmptyLayout;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 综合界面下的blog fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-18
 */

public class BlogGeneralListFragment extends BaseGeneralListFragment<Blog>{


    public static final String BUNDLE_BLOG_TYPE="bundle_blog_type";
    public static final String HISTORY_BLOG="history_blog";
    private boolean isFirst=true;

    @Override
    public void onRefreshing(){
        isFirst=true;
        super.onRefreshing();
    }

    @Override
    protected void requestData(){

        super.requestData();

        if(mIsRefresh){
            TeaScriptApi.getBlogList(Blog.BLOG_TYPE_HEAT, null, mHandler);
        } else {
            TeaScriptApi.getBlogList(Blog.BLOG_TYPE_NORMAL, mPageBean == null ? null : mPageBean.getNextPageToken(), mHandler);
        }

    }

    @Override
    protected BaseListAdapter<Blog> getListAdapter(){
        return new BlogAdapter(this);
    }

    @Override
    protected Type getType(){
        return new TypeToken<ResultBean<PageBean<Blog>>>(){}.getType();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,int position,long id){

        Blog blog=mAdapter.getItem(position);

        if(blog!=null){

            BlogDetailActivity.show(getActivity(),blog.getId());

            TextView titleTV=(TextView) view.findViewById(R.id.blog_title_tv);
            TextView contentTV=(TextView) view.findViewById(R.id.blog_body_tv);

            updateTextColor(titleTV,contentTV);
            saveToReadedList(BlogGeneralListFragment.HISTORY_BLOG,blog.getId()+"");
        }

    }

    @Override
    protected void setListData(ResultBean<PageBean<Blog>> resultBean){

        mPageBean.setNextPageToken(resultBean.getResult().getNextPageToken());

        if(mIsRefresh){

            List<Blog> blogs=resultBean.getResult().getItems();
            Blog blog=new Blog();
            blog.setViewType(Blog.VIEW_TYPE_HEAT_LINE);

            blogs.add(0,blog);
            mPageBean.setItems(blogs);
            mAdapter.clear();
            mAdapter.addItems(mPageBean.getItems());
            mRefreshLayout.setCanLoadMore();
            mIsRefresh=false;

            /*
            如果是mIsRefresh为true,上面加载了的是热门博客的代码
            所以要在这里在加载默认的普通博客的代码
             */
            TeaScriptApi.getBlogList(Blog.BLOG_TYPE_NORMAL,null,mHandler);

        }else{

            List<Blog> blogs = resultBean.getResult().getItems();

            if (isFirst) {
                Blog blog = new Blog();
                blog.setViewType(Blog.VIEW_TYPE_LATELY_LINE);
                blogs.add(0, blog);
                isFirst = false;
                AppOperator.runOnThread(new Runnable() {
                    @Override
                    public void run() {
                        CacheManager.saveObject(getActivity(), mPageBean, CACHE_NAME);
                    }
                });
            }

            mRefreshLayout.setCanLoadMore();

            mPageBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());

            mAdapter.addItems(blogs);
        }

        if (resultBean.getResult().getItems().size() < 20) {
            setFooterType(TYPE_NO_MORE);
            mRefreshLayout.setNoMoreData();
        }

        if (mAdapter.getDatas().size() > 0) {
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        }

    }


}
