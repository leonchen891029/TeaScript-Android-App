package com.teacore.teascript.team.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.bean.Notebook;
import com.teacore.teascript.bean.NotebookList;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.database.NotebookDatabase;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.team.adapter.NotebookAdapter;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.KJDragGridView;
import com.teacore.teascript.widget.KJDragGridView.OnDeleteListener;
import com.teacore.teascript.widget.KJDragGridView.OnMoveListener;
import com.teacore.teascript.util.AnimationsUtils;
import com.teacore.teascript.util.SynchronizeController;
import com.teacore.teascript.util.SynchronizeController.SynchronizeListener;
import com.teacore.teascript.util.XmlUtils;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 便签Fragment
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-2
 */

public class NotebookFragment extends BaseFragment implements OnItemClickListener,OnRefreshListener {

    private EmptyLayout mEmptyLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private KJDragGridView mNotebookGV;
    private ImageView mNotebookIV;

    private NotebookDatabase noteDB;
    private SynchronizeController mController;
    private List<Notebook> datas;
    private NotebookAdapter adapter;
    private User user;
    private Activity aty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_notebook, container, false);
        aty = getActivity();
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refurbish();
        setListCanPull();
    }

    @Override
    public void onDestroy() {
        noteDB.destroy();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notebook, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pub_menu_send:
                Bundle bundle = new Bundle();
                bundle.putInt(NotebookEditFragment.NOTEBOOK_WHEREFROM_KEY,
                        NotebookEditFragment.NOTEBOOK_FRAGMENT);
                UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.NOTE_EDIT,
                        bundle);
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Bundle bundle = new Bundle();
        bundle.putInt(NotebookEditFragment.NOTEBOOK_WHEREFROM_KEY,
                NotebookEditFragment.NOTEBOOK_ITEM);
        bundle.putSerializable(NotebookEditFragment.NOTEBOOK_KEY, datas.get(position));
        UiUtils.showSimpleBack(getActivity(), BackFragmentEnum.NOTE_EDIT, bundle);
    }

    @Override
    public void initView(View view) {
        mEmptyLayout=(EmptyLayout) view.findViewById(R.id.empty_layout);
        mSwipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        mNotebookGV=(KJDragGridView) view.findViewById(R.id.notebook_gv);
        mNotebookIV=(ImageView) view.findViewById(R.id.notebook_iv);

        mNotebookGV.setAdapter(adapter);
        mNotebookGV.setOnItemClickListener(this);
        mNotebookGV.setTrashView(mNotebookIV);
        mNotebookGV.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mNotebookGV.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void onDelete(int position) {
                delete(position);
            }
        });

        mNotebookGV.setOnMoveListener(new OnMoveListener() {
            @Override
            public void startMove() {
                mSwipeRefreshLayout.setEnabled(false);
                mNotebookIV.startAnimation(AnimationsUtils.getTranslateAnimation(0,
                        0, mNotebookIV.getTop(), 0, 500));
                mNotebookIV.setVisibility(View.VISIBLE);
            }

            @Override
            public void finishMove() {
                setListCanPull();
                mNotebookIV.setVisibility(View.INVISIBLE);
                mNotebookIV.startAnimation(AnimationsUtils.getTranslateAnimation(0,
                        0, 0, mNotebookIV.getTop(), 500));
                if (adapter.getDataChange()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            noteDB.reset(adapter.getDatas());
                        }
                    }).start();
                }
            }

            @Override
            public void cancleMove() {}
        });

        mSwipeRefreshLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    mState = STATE_PRESSNONE;
                    mNotebookGV.setDragEnable(false);
                    // 如果你愿意还可以进一步人性化处理，请看mHandler注释
                    // mHandler.sendMessageDelayed(Message.obtain(), 400);
                } else {
                    mNotebookGV.setDragEnable(true);
                }
                return false;
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        if (!datas.isEmpty()) {
            mEmptyLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void initData() {
        user = AppContext.getInstance().getLoginUser();
        mController = new SynchronizeController();
        noteDB = new NotebookDatabase(aty);
        datas = noteDB.query();
        if (datas != null) {
            adapter = new NotebookAdapter(aty, datas);
        }
    }

    @Override
    public void onRefresh() {
        if (mState == STATE_REFRESH) {
            return;
        }
        // 设置顶部正在刷新
        mNotebookGV.setSelection(0);
        setSwipeRefreshLoadingState();

        refurbish();
    }

    /**
     * 设置顶部正在加载的状态
     */
    private void setSwipeRefreshLoadingState() {
        mState = STATE_REFRESH;
        mNotebookGV.setDragEnable(false);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /**
     * 设置顶部加载完毕的状态
     */
    private void setSwipeRefreshLoadedState() {
        mState = STATE_NOMORE;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
        mNotebookGV.setDragEnable(true);
    }

    /**
     * 未登陆用户不能下拉同步，只能使用本地存储
     */
    private void setListCanPull() {
        if (!AppContext.getInstance().isLogin()) {
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    /**
     * 更新空视图时的tip显示
     */
    private void updateEmptyView() {
        if (datas != null && !datas.isEmpty()) {
            mEmptyLayout.setVisibility(View.GONE);
        } else {
            mEmptyLayout.setVisibility(View.VISIBLE);
            mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
            mEmptyLayout.setNoDataContent("暂无便签，请添加或下拉同步");
        }
    }

    private void refurbish() {
        datas = noteDB.query();
        if (datas != null) {
            if (adapter != null) {
                adapter.refurbishData(datas);
            } else {
                adapter = new NotebookAdapter(aty, datas);
                mNotebookGV.setAdapter(adapter);
            }
        }
        if (user.getId() == 0) { // 未登录时不请求网络
            return;
        }
        mController.doSynchronize(aty, new SynchronizeListener() {
            @Override
            public void onSuccess(byte[] arg2) {
                NotebookList dataList = XmlUtils.toBean(
                        NotebookList.class, arg2);
                if (dataList != null && dataList.getList() != null) {
                    noteDB.reset(dataList.getList());
                    datas = noteDB.query();
                    adapter.refurbishData(datas);
                    updateEmptyView();
                }
                setSwipeRefreshLoadedState();
            }

            @Override
            public void onFailure() {
                AppContext.showToast("网络不好，请稍后执行同步");
                setSwipeRefreshLoadedState();
            }
        });
        updateEmptyView();
    }

    /**
     * 删除数据
     *
     * @param index
     */
    private void delete(int index) {
        final int noteId = datas.get(index).getId();
        // 只有是登录用户才执行网络请求
        if (user.getId() > 0) {
            TeaScriptApi.deleteNoteBook(noteId, user.getId(), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {}

                        @Override
                        public void onFailure(int arg0, Header[] arg1,
                                              byte[] arg2, Throwable arg3) {
                            AppContext.showToast("网络异常,云端文件暂未删除");
                        }
                    });
        }

        noteDB.delete(noteId);
        datas.remove(index);
        if (datas != null && adapter != null) {
            adapter.refurbishData(datas);
            mNotebookGV.setAdapter(adapter);
        }

        updateEmptyView();
    }

}
