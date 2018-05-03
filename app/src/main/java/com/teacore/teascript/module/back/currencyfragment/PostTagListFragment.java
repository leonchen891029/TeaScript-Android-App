package com.teacore.teascript.module.back.currencyfragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseListFragment;
import com.teacore.teascript.bean.Post;
import com.teacore.teascript.bean.PostList;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.adapter.PostAdapter;
import com.teacore.teascript.util.XmlUtils;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 标签相关的问答帖子
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-15
 */

public class PostTagListFragment extends BaseListFragment<Post>{


    public static final String BUNDLE_KEY_TAG = "BUNDLE_KEY_TAG";
    private static final String CACHE_KEY_PREFIX = "post_tag_";
    private String mTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        //初始化帖子的标签
        if (args != null) {
            mTag = args.getString(BUNDLE_KEY_TAG);
            ((BaseActivity) getActivity()).setActionBarTitle(getString(
                    R.string.actionbar_title_question_tag, mTag));
        }
    }

    @Override
    protected PostAdapter getListAdapter() {
        return new PostAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mTag;
    }

    @Override
    protected PostList parseList(InputStream is) throws Exception {
        return XmlUtils.toBean(PostList.class, is);
    }

    @Override
    protected PostList readList(Serializable seri) {
        return ((PostList) seri);
    }

    @Override
    protected void sendRequestData() {
        TeaScriptApi.getPostListByTag(mTag, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Post post = mAdapter.getItem(position);
        if (post != null)
            //帖子不为null，显示帖子详情
            UiUtils.showQuestionDetailActivity(view.getContext(), post.getId(),
                    post.getAnswerCount());
    }

}
