package com.teacore.teascript.module.general.fragment.generallistfragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.teacore.teascript.R;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.module.general.adapter.generaladapter.QuestionAdapter;
import com.teacore.teascript.module.general.adapter.generaladapter.QuestionGridAdapter;
import com.teacore.teascript.module.general.base.baseadapter.BaseListAdapter;
import com.teacore.teascript.module.general.base.basebean.PageBean;
import com.teacore.teascript.module.general.base.basebean.ResultBean;
import com.teacore.teascript.module.general.base.basefragment.BaseGeneralListFragment;
import com.teacore.teascript.module.general.bean.Question;
import com.teacore.teascript.module.general.detail.activity.QuestionDetailActivity;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.widget.EmptyLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 综合下面的技术问答界面
 * @author 陈晓帆
 * @version 1.0
 * Created by apple on 17/11/16.
 */

public class QuestionGeneralListFragment extends BaseGeneralListFragment<Question>{

    public static final String TAG=QuestionGeneralListFragment.class.getSimpleName();
    public static final String QUES_ASK="ques_ask";
    public static final String QUES_SHARE="ques_share";
    public static final String QUES_COMPOSITE="ques_composite";
    public static final String QUES_PROFESSION="ques_profession";
    public static final String QUES_WEBSITE="ques_website";

    private int type=1;
    private QuestionGridAdapter questionGridAdapter;
    private int[] positions={1,0,0,0,0};
    private ConnectivityManager connectivityManager;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        connectivityManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void initView(View view){

        super.initView(view);

        //初始化头部的GridView
        View headerView= LayoutInflater.from(getActivity()).inflate(R.layout.general_question_grid,null,false);
        GridView questionGV=(GridView) headerView.findViewById(R.id.question_gv);
        //初始化GridAdapter
        questionGridAdapter=new QuestionGridAdapter(getActivity(),positions);
        questionGV.setAdapter(questionGridAdapter);
        //初始化GridView的位置
        questionGV.setItemChecked(0,true);
        //初始化GridView点击事件
        questionGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //分类是从1开始的,所以这里要+1,后面根据分类加载数据的
                type = (position + 1);

                ((QuestionAdapter) mAdapter).setActionPosition(position + 1);
                //加载标识设为true
                if (!mIsRefresh) {
                    mIsRefresh = true;
                }
                //更新position[]和GridAdapter
                updateAction(position);
                //请求问答数据
                if (positions[position] == 1) {
                    requestQuestionData();
                }

            }
        });

        mListView.addHeaderView(headerView);

    }

    private void requestQuestionData(){

        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();

        if(networkInfo!=null&&networkInfo.isAvailable()){

            boolean connectedOrConnecting=networkInfo.isConnectedOrConnecting();

            NetworkInfo.State state=networkInfo.getState();
            //网络可用，加载网络数据
            if(connectedOrConnecting && state==NetworkInfo.State.CONNECTED){
                requestData();
            }else{
                //加载本地缓存
                requestLocalCache();
            }

        }else{
            requestLocalCache();
        }
    }

    private void updateAction(int position) {
        int len = positions.length;
        for (int i = 0; i < len; i++) {
            if (i != position) {
                positions[i] = 0;
            } else {
                positions[i] = 1;
            }
        }
        //更新GridAdapter
        questionGridAdapter.notifyDataSetChanged();
    }

    private void requestLocalCache(){

        //验证缓存类型
        verifyCacheType();
        //取PageBean
        mPageBean=(PageBean<Question>) CacheManager.readObject(getActivity(),CACHE_NAME);
        //PageBean非空
        if(mPageBean!=null){
            mAdapter.clear();;
            mAdapter.addItems(mPageBean.getItems());
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRefreshLayout.setCanLoadMore();
        }else{
        //PageBean空，取网络数据
            mPageBean=new PageBean<>();
            mPageBean.setItems(new ArrayList<Question>());
            onRefreshing();
        }

    }

    @Override
    protected void initData(){
        CACHE_NAME=QUES_ASK;
        super.initData();
    }

    @Override
    protected void onRequestError(int code){
        super.onRequestError(code);
        requestLocalCache();
    }

    @Override
    protected BaseListAdapter<Question> getListAdapter(){
        return new QuestionAdapter(this);
    }

    @Override
    protected void requestData(){

        super.requestData();

        verifyCacheType();

        mPageBean.setNextPageToken("igferrari430yyf");

        TeaScriptApi.getQuestionList(type, mIsRefresh ? (mPageBean != null ? mPageBean.getNextPageToken() : null) : (mPageBean != null ? mPageBean.getPrevPageToken() : null), mHandler);
    }

    @Override
    protected Type getType(){
        return new TypeToken<ResultBean<PageBean<Question>>>(){}.getType();
    }

    @Override
    protected void setListData(ResultBean<PageBean<Question>> resultBean) {

        verifyCacheType();

        super.setListData(resultBean);
    }

    //修改CACHE_NAME
    private void verifyCacheType() {

        switch (type) {
            case 1:
                CACHE_NAME = QUES_ASK;
                break;
            case 2:
                CACHE_NAME = QUES_SHARE;
                break;
            case 3:
                CACHE_NAME = QUES_COMPOSITE;
                break;
            case 4:
                CACHE_NAME = QUES_PROFESSION;
                break;
            case 5:
                CACHE_NAME = QUES_WEBSITE;
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //有一个headview position-1
        Question question = mAdapter.getItem(position - 1);
        if (question != null) {
            QuestionDetailActivity.show(getActivity(), question.getId());
            TextView title = (TextView) view.findViewById(R.id.question_title_tv);
            TextView content = (TextView) view.findViewById(R.id.question_content_tv);
            updateTextColor(title, content);
            verifyCacheType();
            saveToReadedList(CACHE_NAME, question.getId() + "");
        }
    }

}
