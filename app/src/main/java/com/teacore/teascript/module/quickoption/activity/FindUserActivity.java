package com.teacore.teascript.module.quickoption.activity;

import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.adapter.FindUserAdapter;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.bean.EntityList;
import com.teacore.teascript.bean.FindUsersList;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.EmptyLayout;

import java.io.ByteArrayInputStream;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 找人Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-21
 */

public class FindUserActivity extends BaseActivity implements OnItemClickListener{

    private SearchView mSearchView;

    private ListView mListView;
    private EmptyLayout mEmptyLayout;

    private FindUserAdapter mAdapter;

    @Override
    public int getLayoutId(){
        return R.layout.fragment_find_user;
    }

    @Override
    protected boolean hasBackButton(){
        return true;
    }

    @Override
    public void initView(){
        mListView=(ListView) findViewById(R.id.list_lv);
        mEmptyLayout=(EmptyLayout) findViewById(R.id.empty_layout);

        mAdapter=new FindUserAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(mSearchView.getQuery().toString());
            }
        });

    }

    @Override
    public void initData(){

    }

    @Override
    public void onClick(View view){

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id){
        User user=mAdapter.getItem(position);
        if(user!=null){
            UiUtils.showUserCenter(FindUserActivity.this,user.getId(),user.getName());
        }
    }

    private AsyncHttpResponseHandler mHanlder=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            EntityList<User> list= XmlUtils.toBean(FindUsersList.class,new ByteArrayInputStream(bytes));
            executeOnLoadDataSuccess(list.getList());
            mActionBar.setTitle("找人");
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
        }
    };

    private void executeOnLoadDataSuccess(List<User> data) {
        mAdapter.clear();
        mAdapter.addDatas(data);
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem searchItem=menu.findItem(R.id.search_content);

        mSearchView=(SearchView) searchItem.getActionView();
        mSearchView.setIconifiedByDefault(false);
        initSearchView();
        return super.onCreateOptionsMenu(menu);
    }

    private void initSearchView(){
        mSearchView.setQueryHint("请输入用户昵称");
        TextView textView=(TextView) mSearchView.findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(0x90ffffff);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });

    }

    private void search(String nickName){

        if(nickName ==null || StringUtils.isEmpty(nickName))
            return;

        mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);

        mListView.setVisibility(View.GONE);

        TeaScriptApi.findUser(nickName,mHanlder);
    }


}
