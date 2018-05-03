package com.teacore.teascript.module.quickoption.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.adapter.SearchFriendAdapter;
import com.teacore.teascript.adapter.SelectFriendAdapter;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.bean.Friend;
import com.teacore.teascript.bean.FriendsList;
import com.teacore.teascript.cache.CacheManager;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.AvatarView;
import com.teacore.teascript.widget.EmptyLayout;
import com.teacore.teascript.widget.IndexView;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 选择好友Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-25
 */

public class SelectFriendsActivity extends BaseActivity {

    //最大可选择的好友数量
    private static final int MAX_SELECTED_SIZE=10;
    //数据缓存的有效时间(秒)
    private static final int CACHE_TIME=2*60;

    private static final String CACHE_KEY_PREFIX="friends_list";

    private ListView mListView;
    private EmptyLayout mEmptyLayout;
    private ListView mSearchLV;
    private EditText mSearchET;
    private TextView mFloatTV;
    private IndexView mIndexView;
    private View mTopLayout;
    private HorizontalScrollView mHorizontalScrollView;
    private ViewGroup mSelectContainer;
    private View mSearchLayout;
    private View mSearchResult;
    private View mSearchIcon;
    private View mDividerView1;
    private View mDividerView2;
    private TextView mActionBarButton;

    private List<FriendItem> mFriendItems=new ArrayList<>();
    //选中的好友ID
    private List<Integer> mCheckedFriendIds=new ArrayList<>();
    //搜索结果集合
    private List<SearchItem> mSearchResultList=new ArrayList<>();

    private SearchFriendAdapter mSearchAdapter;
    private SelectFriendAdapter mSelectAdapter;

    //是否正在输入
    private boolean isEditMode=false;

    private int relation=1;

    //未选中edittext的线条颜色
    private int lineColor;
    //选中edittext的线条颜色
    private int colorPrimary;

    @Override
    public void onClick(View view){

    }

    @Override
    protected int getLayoutId(){
        return R.layout.activity_select_friends;
    }

    //创建一个空白的ListView的HeaderView
    private View createHeaderView(){
        View view=new View(this);

        int headerHeight=getResources().getDimensionPixelOffset(R.dimen.select_friend_header_height);
        AbsListView.LayoutParams lp=new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,headerHeight);
        view.setLayoutParams(lp);
        return view;
    }

    @Override
    public void initView(){

        TypedArray typedArray=obtainStyledAttributes(new int[]{R.attr.lineColor,R.attr.colorPrimary});
        lineColor=typedArray.getColor(0,0xFFDADADA);
        colorPrimary=typedArray.getColor(1,0xFF40AA53);
        typedArray.recycle();

        mListView=(ListView) findViewById(R.id.listview);
        mEmptyLayout=(EmptyLayout) findViewById(R.id.empty_layout);
        mSearchLV=(ListView) findViewById(R.id.search_lv);
        mSearchET=(EditText) findViewById(R.id.search_et);
        mFloatTV=(TextView) findViewById(R.id.float_tv);
        mIndexView=(IndexView) findViewById(R.id.indexview);
        mTopLayout=findViewById(R.id.top_ll);
        mHorizontalScrollView=(HorizontalScrollView) findViewById(R.id.hs_container);
        mSelectContainer=(ViewGroup) findViewById(R.id.select_container_ll);
        mSearchLayout=findViewById(R.id.search_fl);
        mSearchResult=findViewById(R.id.search_result_tv);
        mSearchIcon=findViewById(R.id.search_iv);
        mDividerView1=findViewById(R.id.divider1);
        mDividerView2=findViewById(R.id.divider2);

        mListView.addHeaderView(createHeaderView());
        mListView.setOnScrollListener(scrollListener);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                position=position-mListView.getHeaderViewsCount();

                if(position<0){
                    return;
                }
                FriendItem item=mSelectAdapter.getFriendItem(position);
                if(item==null);
                itemClick(item);
            }
        });


        mSearchLV.addHeaderView(createHeaderView());
        mSearchLV.setOnScrollListener(scrollListener);
        mSearchLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - mSearchLV.getHeaderViewsCount();
                if (position < 0) {
                    return;
                }
                SearchItem item = mSearchResultList.get(position);

                resetSearchResult();
                //清空搜索字键字
                mSearchET.setText("");
                mSearchAdapter.notifyDataSetChanged();

                itemClick(item.friendItem);
            }
        });

        mIndexView.setOnIndexTouchListener(new IndexView.OnIndexTouchListener() {
            @Override
            public void onIndexTouchMove(char indexLeter) {
                //显示悬浮界面
                if (mFloatTV.getVisibility() != View.VISIBLE) {
                    mFloatTV.setVisibility(View.VISIBLE);
                }

                mFloatTV.setText(String.valueOf(indexLeter));
                //索引值对应的位置
                int positoin;
                if (indexLeter == '☆') {
                    positoin = 0;
                } else {
                    positoin = mSelectAdapter.getPositionByIndex(indexLeter);
                }

                if (positoin != -1) {
                    mListView.setSelection(positoin);
                }
            }

            @Override
            public void onIndexTouchUp() {
                mFloatTV.setVisibility(View.GONE);
            }
        });

        mSearchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (mSearchET.length() == 0) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                            handleDeleteKeyEvent();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mSearchET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                if (action == MotionEvent.ACTION_DOWN) {
                    setEditMode(true);
                }
                return false;
            }
        });

        mSearchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                resetLastSelectView();
                if (mSearchET.length() == 0) {
                    mSearchLayout.setVisibility(View.GONE);
                } else {
                    mSearchLayout.setVisibility(View.VISIBLE);
                    searchKey(mSearchET.getText().toString());
                }
            }
        });

        //顶部设置透明度
        View selectLayout = findViewById(R.id.select_fl);
        Drawable bg = selectLayout.getBackground();
        if (bg != null) {
            bg.setAlpha(238);
        }

        //提示刷新界面
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                requestData(true);
            }
        });

        //添加右上角的按钮
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(true);
            View view=View.inflate(this,R.layout.actionbar_blue_button,null);
            mActionBarButton=(TextView) view.findViewById(R.id.button);
            mActionBarButton.setText(R.string.ok);
            mActionBarButton.setEnabled(false);
            mActionBarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickOK();
                }
            });
            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams
                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            actionBar.setCustomView(view, lp);
        }

    }

    @Override
    public void initData(){
        mSelectAdapter = new SelectFriendAdapter();
        mListView.setAdapter(mSelectAdapter);

        mSearchAdapter = new SearchFriendAdapter(mSearchResultList);
        mSearchLV.setAdapter(mSearchAdapter);

        requestData(false);
    }

    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                //滚动状态下，隐藏输入法
                if (isEditMode) {
                    setEditMode(false);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int
                totalItemCount) {
            //是否滚动到顶部
            if (ViewCompat.canScrollVertically(view, -1)) {
                mDividerView2.setVisibility(View.VISIBLE);
            } else {
                mDividerView2.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected boolean hasBackButton(){
        return true;
    }

    //点击列表
    private void itemClick(FriendItem item){
        boolean isSelected=!item.isSelected;
        int userId = item.friend.getUserid();
        boolean contains = mCheckedFriendIds.contains(userId);

        if (isSelected) {
            if (mCheckedFriendIds.size() < MAX_SELECTED_SIZE) {
                if (!contains) {
                    mCheckedFriendIds.add(userId);
                    item.isSelected = true;
                    mSelectAdapter.notifyDataSetChanged();

                    addSelectedUseAV(item.friend);
                }
            } else {
                AppContext.showToast(getString(R.string.select_friends_max_tips,
                        MAX_SELECTED_SIZE));
            }
        } else {
            if (contains) {
                Iterator<Integer> iterator = mCheckedFriendIds.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next() == userId) {
                        iterator.remove();
                    }
                }
                item.isSelected = false;
                mSelectAdapter.notifyDataSetChanged();

                removeSelectedUserAV(userId);
            }
        }
        updateActionBarButton();

    }

    private void updateActionBarButton() {
        String ok = getString(R.string.ok);
        if (mCheckedFriendIds.isEmpty()) {
            mActionBarButton.setEnabled(false);
            mActionBarButton.setText(ok);
        } else {
            mActionBarButton.setEnabled(true);
            mActionBarButton.setText(String.format("%s(%d/%d)", ok, mCheckedFriendIds.size(),
                    MAX_SELECTED_SIZE));
        }
    }

    //点击确定
    private void clickOK() {
        if (mCheckedFriendIds.isEmpty()) {
            setResult(RESULT_CANCELED);
        } else {
            Intent result = new Intent();
            int userIds[] = new int[mCheckedFriendIds.size()];
            String names[] = new String[mCheckedFriendIds.size()];

            for (int i = 0; i < mCheckedFriendIds.size(); i++) {
                int id = mCheckedFriendIds.get(i);
                userIds[i] = id;

                for (FriendItem item : mFriendItems) {
                    if (item.friend.getUserid() == id) {
                        names[i] = item.friend.getName();
                        break;
                    }
                }
            }
            result.putExtra("userIds", userIds);
            result.putExtra("names", names);
            setResult(RESULT_OK, result);
        }
        finish();
    }

    //清空搜索结果
    private void resetSearchResult() {
        mSearchResultList.clear();
    }

    //根据关键字搜索
    private void searchKey(String key){
        resetSearchResult();

        if (!TextUtils.isEmpty(key)) {
            key = key.toLowerCase();
            int len = key.length();
            int keyLength;
            int start;
            for (FriendItem item : mFriendItems) {
                String name = item.name;
                if (TextUtils.isEmpty(name)) {
                    continue;
                }
                keyLength = len;

                //先按名字查找
                start = name.indexOf(key);
                if (start == -1) {
                    //判断是否是纯英文
                    if (!key.matches("[a-zA-Z]+")) {
                        continue;
                    }
                    boolean match = false;
                    //判断是不是拼音
                    if (!TextUtils.isEmpty(item.pinyin)) {
                        if (item.pinyin.startsWith(key)) {
                            match = true;
                            start = 0;
                            int total = 0;
                            for (int i = 0; i < item.pinyinArray.length; i++) {
                                String py = item.pinyinArray[i];
                                total += py.length();
                                if (len <= total) {
                                    keyLength = i + 1;
                                    break;
                                }
                            }
                        } else if (key.startsWith(item.pinyin)) {
                            match = true;
                            start = -1;
                            keyLength = 0;
                        }
                    }

                    if (!match) {
                        if (!TextUtils.isEmpty(item.firstPinyin)) {
                            //判断是不是首字母拼间
                            if (item.firstPinyin.startsWith(key)) {
                                match = true;
                                start = 0;
                                keyLength = len;
                            } else if (key.startsWith(item.firstPinyin)) {
                                match = true;
                                start = -1;
                                keyLength = 0;
                            }
                        }
                    }

                    if (!match) {
                        continue;
                    }
                }

                mSearchResultList.add(new SearchItem(item, start, keyLength, colorPrimary));
            }
        }
        if (mSearchResultList.isEmpty()) {
            mSearchET.setVisibility(View.VISIBLE);
        } else {
            mSearchET.setVisibility(View.GONE);
        }
        mSearchAdapter.notifyDataSetChanged();

    }

    //设置输入模式
    private void setEditMode(boolean edit){
        if(!edit){
            //隐藏输入法
            TDevice.hideSoftKeyboard(mSearchET);
            resetLastSelectView();
            mSearchET.setCursorVisible(false);
            mDividerView1.setBackgroundColor(lineColor);
        }else{
            mDividerView1.setBackgroundColor(colorPrimary);
            mSearchET.setCursorVisible(true);
        }
        isEditMode=edit;
    }

    //添加顶部选中的用户的图像
    private void addSelectedUseAV(Friend friend){
        mHorizontalScrollView.setVisibility(View.VISIBLE);
        mSearchIcon.setVisibility(View.GONE);

        final int userId=friend.getUserid();
        final AvatarView userAV=new AvatarView(this);

        userAV.setDisplayCircle(false);
        userAV.setImageResource(R.drawable.widget_dface);
        userAV.setAvatarUrl(friend.getPortrait());
        userAV.setTag(userId);

        //点击删除
        userAV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCheckedFriend(userId);
            }
        });

        int size=getResources().getDimensionPixelSize(R.dimen.select_friend_avatar_size);
        int minSize=(int) mSearchET.getPaint().measureText("搜 索");

        //修正HorizontalScrollView的大小
        ViewGroup.LayoutParams layoutParams = mHorizontalScrollView.getLayoutParams();
        if (size + 10 + mHorizontalScrollView.getWidth() > mTopLayout.getWidth() - minSize
                && layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.width = mHorizontalScrollView.getWidth();
            mHorizontalScrollView.setLayoutParams(layoutParams);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.leftMargin = 5;
        lp.rightMargin = 5;
        mSelectContainer.addView(userAV, lp);

        //滚动到最后
        userAV.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                userAV.getViewTreeObserver().removeOnPreDrawListener(this);
                mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                return false;
            }
        });

    }

    //移除顶部选中的用户头像
    private void removeSelectedUserAV(int userId) {
        View view = mSelectContainer.findViewWithTag(userId);
        if (view != null) {
            //修正HorizontalScrollView的大小
            ViewGroup.LayoutParams layoutParams = mHorizontalScrollView.getLayoutParams();
            if (layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
                int minSize = (int) mSearchET.getPaint().measureText("搜 索");
                if (mSelectContainer.getWidth() - view.getWidth() <= mTopLayout.getWidth() - minSize)
                    layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                mHorizontalScrollView.setLayoutParams(layoutParams);
            }

            mSelectContainer.removeView(view);
        }
        if (mSelectContainer.getChildCount() == 0) {
            mSearchIcon.setVisibility(View.VISIBLE);
            mHorizontalScrollView.setVisibility(View.GONE);
        }
    }

    //恢复最后的选中状态的界面的透明度
    private void resetLastSelectView() {
        if (mSelectContainer.getChildCount() == 0) {
            return;
        }
        View view = mSelectContainer.getChildAt(mSelectContainer.getChildCount() - 1);
        int key = R.id.select_container_ll;
        view.setTag(key, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setAlpha(1);
        }
    }

    //处理删除按键事件
    private void handleDeleteKeyEvent() {
        if (mSelectContainer.getChildCount() == 0) {
            return;
        }
        View view = mSelectContainer.getChildAt(mSelectContainer.getChildCount() - 1);
        int userId = (Integer) view.getTag();

        int key = R.id.select_container_ll;
        Integer count = (Integer) view.getTag(key);

        //连续点两次删除键才删除选中的项
        if (count == null || count == 0) {
            view.setTag(key, 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                view.setAlpha(0.5f);
            }
        } else {
            removeCheckedFriend(userId);
        }
    }

    /**
     * 移除一个已选择的好友
     */
    private void removeCheckedFriend(int userId) {
        Iterator<Integer> iterator = mCheckedFriendIds.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == userId) {
                iterator.remove();
            }
        }
        for (FriendItem item : mFriendItems) {
            if (item.friend.getUserid() == userId) {
                item.isSelected = false;
                break;
            }
        }

        mSelectAdapter.notifyDataSetChanged();
        removeSelectedUserAV(userId);

        updateActionBarButton();
    }

    public static class FriendItem implements Serializable,Comparable<FriendItem>{

        private Friend friend;
        private String indexStr;

        //首字母索引
        private char firstLetter;

        private String pinyinArray[];

        //拼音
        private String pinyin;
        //拼音的首字母
        private String firstPinyin;

        private String name;

        private transient boolean isSelected;

        public FriendItem(Friend friend, String name, String indexStr, String pinYinArray[],
                          String pinYin, String firstPinYin) {
            this.friend = friend;
            this.name = name;
            this.indexStr = indexStr;
            this.pinyinArray = pinYinArray;
            this.pinyin = pinYin;
            this.firstPinyin = firstPinYin;

            if (indexStr != null && indexStr.length() >= 0) {
                this.firstLetter = indexStr.charAt(0);
            }
        }

        public boolean isSelected() {
            return isSelected;
        }

        public Friend getFriend() {
            return friend;
        }

        public String getIndexStr() {
            return indexStr;
        }

        public char getFirstLetter() {
            return firstLetter;
        }

        @Override
        public int compareTo(FriendItem another) {
            String s1 = indexStr == null ? "" : indexStr;
            String s2 = another == null || another.indexStr == null ? "" : another.indexStr;
            return s1.compareTo(s2);
        }

    }

    public static class SearchItem {

        private FriendItem friendItem;

        //搜索关键字的开始位置
        private int startIndex = -1;
        //搜索关键字的长度
        private int keyLength = 0;
        //高亮显示的颜色值
        private int hightLightColor;

        public SearchItem(FriendItem friendItem, int startIndex, int keyLength, int
                hightLightColor) {
            this.friendItem = friendItem;
            this.startIndex = startIndex;
            this.keyLength = keyLength;
            this.hightLightColor = hightLightColor;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getKeyLength() {
            return keyLength;
        }

        public int getHightLightColor() {
            return hightLightColor;
        }

        public FriendItem getFriendItem() {
            return friendItem;
        }
    }

    private String getCacheKey(int uid) {
        return CACHE_KEY_PREFIX + "_" + relation + "_" + uid;
    }

    /***
     * 获取列表数据
     */
    private void requestData(boolean refresh) {
        int uid = AppContext.getInstance().getLoginUid();
        String key = getCacheKey(uid);
        if (refresh) {
            // 读取新的数据
            sendRequestData(uid, key);
        } else {
            boolean hasCache = CacheManager.isCacheExist(this, key);
            if (hasCache) {
                readCacheData(key);
            } else {
                sendRequestData(uid, key);
            }
        }
    }

    /**
     * 请求数据
     */
    private void sendRequestData(int uid, String key) {
        if (uid == 0) {
            mEmptyLayout.setNoDataContent(getString(R.string.select_friends_empty));
            mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        } else {
            if (TDevice.hasInternet()) {
                if (mFriendItems.isEmpty()) {
                    mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_LOADING);
                }
                TeaScriptApi.getAllFriendsList(uid, relation, new ResponseHandler(this, key));
            } else {
                if (mFriendItems.isEmpty()) {
                    mEmptyLayout.setEmptyType(EmptyLayout.NETWORK_ERROR);
                }
            }
        }
    }

    private CacheTask mCacheTask;

    private void readCacheData(String cacheKey) {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
        mCacheTask = new CacheTask(this, cacheKey);
        mCacheTask.execute();
    }

    /**
     * 回调成功的数据
     */
    private void handleResult(String cacheKey, boolean fromCache, List<FriendItem> list) {
        if (list.isEmpty()) {
            if (mFriendItems.isEmpty()) {
                mEmptyLayout.setNoDataContent(getString(R.string.select_friends_empty));
                mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
            } else {
                mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            }
        } else {
            mEmptyLayout.setEmptyType(EmptyLayout.HIDE_LAYOUT);
            if (mFriendItems.isEmpty() || (!isEditMode && mCheckedFriendIds.isEmpty())) {
                mFriendItems.clear();
                mFriendItems.addAll(list);
                mSelectAdapter.setFriendItems(mFriendItems);
            }
        }

        if (fromCache) {
            String lastRefreshTime = AppContext.getLastRefreshTime(cacheKey);
            String currTime = TimeUtils.getCurrentTimeStr();
            long diff = TimeUtils.dateDifferent(lastRefreshTime, currTime);
            if (diff > CACHE_TIME) { //缓存超过有效时间，则重新请求数据
                requestData(true);
            }
        }
    }

    /**
     * 回调失败的结果
     */
    private void handleFail() {
        //如果为空时，才显示没有数据的提示
        if (mFriendItems.isEmpty()) {
            mEmptyLayout.setNoDataContent(getString(R.string.select_friends_empty));
            mEmptyLayout.setEmptyType(EmptyLayout.NODATA);
        }
    }

    /**
     * 读取缓存任务
     */
    private static class CacheTask extends AsyncTask<Void, Void, List<FriendItem>> {
        private final WeakReference<SelectFriendsActivity> mActivity;
        private final String cacheKey;

        private CacheTask(SelectFriendsActivity activity, String key) {
            this.mActivity = new WeakReference<>(activity);
            this.cacheKey = key;
        }

        @Override
        protected List<FriendItem> doInBackground(Void... params) {
            SelectFriendsActivity activity = mActivity.get();
            if (activity == null) {
                return null;
            }
            return (List<FriendItem>) CacheManager.readObject(activity.getApplicationContext(),
                    cacheKey);
        }

        @Override
        protected void onPostExecute(List<FriendItem> list) {
            SelectFriendsActivity activity = mActivity.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            //如果缓存为空，则读取网络的数据
            if (list == null || list.isEmpty()) {
                activity.requestData(true);
            } else {
                activity.handleResult(cacheKey, true, list);
            }
        }
    }

    /**
     * 网络请求回调
     */
    private static class ResponseHandler extends AsyncHttpResponseHandler {

        private final WeakReference<SelectFriendsActivity> mActivity;
        private final Context applicationContext;
        private final String cacheKey;

        private ResponseHandler(SelectFriendsActivity activity, String cacheKey) {
            this.mActivity = new WeakReference<>(activity);
            this.applicationContext = activity.getApplicationContext();
            this.cacheKey = cacheKey;
        }

        @Override
        public void onSuccess(final int statusCode, final Header[] headers, final byte[]
                responseBody) {
            //对数据进行异常解析
            new AsyncTask<Void, Void, List<FriendItem>>() {

                @Override
                protected List<FriendItem> doInBackground(Void... params) {
                    try {
                        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
                        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
                        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

                        FriendsList friendsList = XmlUtils.toBean(FriendsList.class,
                                new ByteArrayInputStream(responseBody));
                        ArrayList<FriendItem> newList = new ArrayList<>();

                        StringBuilder indexStr = new StringBuilder("");
                        StringBuilder firstPinYin = new StringBuilder("");
                        StringBuilder pinYin = new StringBuilder("");

                        String name;
                        char[] input;

                        for (Friend friend : friendsList.getList()) {
                            indexStr.replace(0, indexStr.length(), "");
                            firstPinYin.replace(0, firstPinYin.length(), "");
                            pinYin.replace(0, pinYin.length(), "");

                            if (TextUtils.isEmpty(friend.getName())) {
                                name = "";
                            } else {
                                name = friend.getName().toLowerCase();
                                input = name.trim().toCharArray();

                                boolean lastCNChar = true;
                                for (char c : input) {
                                    String str = Character.toString(c);
                                    //判断是否为中文
                                    if (str.matches("[\u4E00-\u9FA5]+")) {
                                        try {
                                            String[] temp = PinyinHelper.toHanyuPinyinStringArray
                                                    (c, format);
                                            String pinyin = temp[0];
                                            if (!TextUtils.isEmpty(pinyin)) {
                                                indexStr.append(pinyin);
                                                if (lastCNChar) {
                                                    pinYin.append(pinyin).append(" ");
                                                    firstPinYin.append(pinyin.charAt(0));
                                                }
                                            }
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        lastCNChar = false;
                                        indexStr.append(str);
                                    }
                                }
                            }
                            String py = pinYin.toString();
                            String pyArray[] = py.split(" ");
                            py = py.replace(" ", "");
                            FriendItem item = new FriendItem(friend, name, indexStr.toString(),
                                    pyArray,
                                    py, firstPinYin.toString());
                            newList.add(item);
                        }
                        //将集合按字母排序
                        Collections.sort(newList);

                        //保存缓存结果
                        CacheManager.saveObject(applicationContext, newList, cacheKey);
                        //记录保存时间
                        AppContext.putLastRefreshTime(cacheKey, TimeUtils.getCurrentTimeStr());
                        return newList;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(List<FriendItem> friendItems) {
                    SelectFriendsActivity activity = mActivity.get();
                    if (activity == null || activity.isFinishing()) {
                        return;
                    }
                    if (friendItems == null) {
                        onFailure(statusCode, headers, responseBody, null);
                    } else {
                        activity.handleResult(cacheKey, false, friendItems);
                    }
                }
            }.execute();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                error) {
            SelectFriendsActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            activity.handleFail();
        }
    }



}
