package com.teacore.teascript.widget;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.team.adapter.TeamDiaryAdapter;
import com.teacore.teascript.team.bean.TeamDiary;
import com.teacore.teascript.team.bean.TeamDiaryList;
import com.teacore.teascript.team.fragment.viewpagerfragment.TeamDiaryViewPagerFragment;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * 显示周报控件
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-22
 */

public class DiaryPagerView {

    private final RelativeLayout mRootView;
    private final ListView mListView;
    private final SwipeRefreshLayout mRefreshLayout;
    private final EmptyLayout mEmptyLayout;
    private final Activity aty;

    private final int mTeamId;
    private final int mYear;
    private final int mWeek;

    private TeamDiaryList mDatas;
    private final TeamDiaryAdapter mAdapter;

    public DiaryPagerView(Context context, int teamId, int year, int week) {

        this.aty = (Activity) context;
        this.mTeamId = teamId;
        this.mYear = year;
        this.mWeek = week;

        mRootView = (RelativeLayout) View.inflate(context, R.layout.view_diary_pager, null);
        mListView = (ListView) mRootView.findViewById(R.id.listview);
        mRefreshLayout= (SwipeRefreshLayout) mRootView.findViewById(R.id.swiperefreshlayout);
        mEmptyLayout = (EmptyLayout) mRootView.findViewById(R.id.empty_layout);

        mAdapter = new TeamDiaryAdapter(aty, null);
        initView();
        requestData(true);
    }

    private void initView() {

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData(true);
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Bundle args = new Bundle();
                args.putInt(TeamDiaryViewPagerFragment.TEAM_ID_KEY, mTeamId);
                args.putSerializable(TeamDiaryViewPagerFragment.TEAM_DIARY_KEY, mDatas
                        .getList().get(position));
                UiUtils.showDiaryDetail(aty, args);
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (BaseFragment.mState == BaseFragment.STATE_REFRESH) {
                    return;
                } else {
                    mEmptyLayout.setEmptyMessage("本周无人提交周报");
                    requestData(false);
                }
            }
        });

        mRefreshLayout.setColorSchemeResources(R.color.swiperefresh_color1,
                R.color.swiperefresh_color2, R.color.swiperefresh_color3,
                R.color.swiperefresh_color4);

    }

    private void requestData(final boolean isFirst) {
        TeaScriptApi.getDiaryFromWhichWeek(mTeamId, mYear, mWeek, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        setSwipeRefreshLoadingState(mRefreshLayout);
                        if (isFirst) {
                            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                          Throwable arg3) {

                        if (mEmptyLayout != null) {
                            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                        setSwipeRefreshLoadedState(mRefreshLayout);
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1,
                                          final byte[] arg2) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                mDatas = XmlUtils.toBean(TeamDiaryList.class, arg2);

                                CacheManager.saveObject(aty, mDatas,
                                        "TeamDiaryPagerFragment" + mWeek);

                                aty.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final List<TeamDiary> tempData = mDatas
                                                .getList();
                                        if (tempData == null
                                                || tempData.isEmpty()) {
                                            mEmptyLayout.setNoDataContent("本周无人提交周报");
                                            mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
                                            mEmptyLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            mEmptyLayout.setVisibility(View.GONE);
                                            mAdapter.refresh(tempData);
                                            mListView.setAdapter(mAdapter);
                                        }
                                        setSwipeRefreshLoadedState(mRefreshLayout);
                                    }
                                });
                            }
                        });
                    }
                });
    }

    /**
     * 设置顶部正在加载的状态
     */
    private void setSwipeRefreshLoadingState(
            SwipeRefreshLayout mSwipeRefreshLayout) {
        BaseFragment.mState = BaseFragment.STATE_REFRESH;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    public RelativeLayout getView() {
        return mRootView;
    }

    /**
     * 设置顶部加载完毕的状态
     */
    private void setSwipeRefreshLoadedState(
            SwipeRefreshLayout mSwipeRefreshLayout) {
        BaseFragment.mState = BaseFragment.STATE_NOMORE;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

}
