package com.teacore.teascript.module.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.app.AppManager;
import com.teacore.teascript.base.BaseApplication;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.Notice;
import com.teacore.teascript.cache.DataCleanManager;
import com.teacore.teascript.interfaces.BaseActivityInterface;
import com.teacore.teascript.interfaces.OnTabReselectListener;
import com.teacore.teascript.module.back.BackActivity;
import com.teacore.teascript.module.back.BackFragmentEnum;
import com.teacore.teascript.module.myinformation.MyInformationFragment;
import com.teacore.teascript.module.quickoption.QuickOptionDialog;
import com.teacore.teascript.service.NoticeUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.UpdateManager;
import com.teacore.teascript.widget.BadgeView;
import com.teacore.teascript.widget.TSFragmentTabHost;

/**
 *主界面Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-15
 */

public class MainActivity extends AppCompatActivity implements TabHost.OnTabChangeListener,
        BaseActivityInterface,View.OnClickListener,View.OnTouchListener{

    private long mBackPressedTime;

    private TSFragmentTabHost mFragmentTabHost;

    private ImageView mQuickOptionIV;

    private BadgeView mNoticeBV;

    public static Notice mNotice;

    private CharSequence mTitle;

    //接收INTENT_ACTION_NOTICE和INTENT_ACTION_LOGOUT的Intent
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTENT_ACTION_NOTICE)) {
                mNotice = (Notice) intent.getSerializableExtra("notice_bean");
                //@我的数量
                int atmeCount = mNotice.getAtmeCount();
                //留言的数量
                int msgCount = mNotice.getMsgCount();
                //评论的数量
                int commentCount = mNotice.getCommentCount();
                //新粉丝的数量
                int newFansCount = mNotice.getNewFansCount();
                //收到赞的数量
                int newLikeCount = mNotice.getNewLikeCount();
                //动态的总数
                int activeCount = atmeCount + msgCount + commentCount + newFansCount + newLikeCount;

                Fragment fragment = getCurrentFragment();

                if (fragment instanceof MyInformationFragment) {
                    ((MyInformationFragment) fragment).setNotice();
                }else{
                    if (activeCount > 0) {
                        mNoticeBV.setText(String.format("%s", activeCount + ""));
                        mNoticeBV.show();
                    } else {
                        mNoticeBV.hide();
                        mNotice = null;
                    }
                }
            } else if (intent.getAction()
                    .equals(Constants.INTENT_ACTION_LOGOUT)) {
                mNoticeBV.hide();
                mNotice = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //设置主题
        if(AppContext.getNightModeSwitch()){
            setTheme(R.style.AppBaseTheme_Night);
        }else{
            setTheme(R.style.AppBaseTheme_Light);
        }

        setContentView(R.layout.activity_main);

        //初始化View
        initView();

        //添加到Activity的管理器
        AppManager.getAppManager().addActivity(this);

        handleIntent(getIntent());

    }

    //实现BaseViewInterface接口的initView方法
    @Override
    public void initView(){

        mFragmentTabHost=(TSFragmentTabHost) findViewById(android.R.id.tabhost);
        mQuickOptionIV=(ImageView) findViewById(R.id.quick_option_iv);

        mTitle=getResources().getString(R.string.main_tab_name_news);

        mFragmentTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mFragmentTabHost.getTabWidget().setShowDividers(0);

        //初始化FragmentTabHost的Tabs
        initTabs();

        //中间按键触发
        mQuickOptionIV.setOnClickListener(this);

        //初始化Tab位置，注册监听器
        mFragmentTabHost.setCurrentTab(0);
        mFragmentTabHost.setOnTabChangedListener(this);

        //注册广播接受者
        IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_NOTICE);
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        registerReceiver(mReceiver, filter);

        //开始NoticeService服务
        NoticeUtils.bindToService(this);

        if(AppContext.isFristStart()){
            DataCleanManager.cleanInternalCache(AppContext.getInstance());
            AppContext.setFristStart(false);
        }

        checkUpdate();
    }

    //实现BaseViewInterface接口的initData方法
    @Override
    public void initData(){

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    //处理传进来的Intent
    private void handleIntent(Intent intent){

        if (intent == null)
            return;

        String action = intent.getAction();

        /*
        *如果Intent为ACTION_VIEW，通过Url跳转
        *如果Intent的带"NOTICE"为true，为从通知栏点击
        */
        if (action != null && action.equals(Intent.ACTION_VIEW)) {
            UiUtils.showUrlRedirect(this, intent.getDataString());
        } else if(intent.getBooleanExtra("NOTICE", false)) {
            notifitcationBarClick(intent);
        }

    }

    //从通知栏点击的响应
    private void notifitcationBarClick(Intent fromWhich) {
        if (fromWhich != null) {
            boolean fromNoticeBar = fromWhich.getBooleanExtra("NOTICE", false);
            if (fromNoticeBar) {
                Intent toMyInfor = new Intent(this, BackActivity.class);
                toMyInfor.putExtra(BackActivity.BUNDLE_KEY_PAGE,
                        BackFragmentEnum.USER_MESSAGE.getValue());
                startActivity(toMyInfor);
            }
        }
    }

    private void initTabs(){

        MainActivityTabEnum[] tabs=MainActivityTabEnum.values();

        int size=tabs.length;

        for(int i=0;i<size;i++){
            MainActivityTabEnum mainTab=tabs[i];

            TabHost.TabSpec tabSpec=mFragmentTabHost.newTabSpec(getString(mainTab.getResName()));

            View indicator=View.inflate(this,R.layout.tab_indicator,null);
            ImageView tabIconIV=(ImageView) indicator.findViewById(R.id.icon_iv);
            TextView tabTitleTV=(TextView) indicator.findViewById(R.id.tab_title_tv);
            Drawable drawable= ContextCompat.getDrawable(this,mainTab.getResIcon());
            tabIconIV.setImageDrawable(drawable);
            if(i==2){
                indicator.setVisibility(View.INVISIBLE);
                mFragmentTabHost.setNoTabChangedTag(getString(mainTab.getResName()));
            }

            tabTitleTV.setText(getString(mainTab.getResName()));

            tabSpec.setIndicator(indicator);

            tabSpec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String s) {
                    return new View(MainActivity.this);
                }
            });

            mFragmentTabHost.addTab(tabSpec,mainTab.getClz(),null);

            if (mainTab.equals(MainActivityTabEnum.ME)) {
                View cn = indicator.findViewById(R.id.tab_mes_tv);
                mNoticeBV = new BadgeView(MainActivity.this, cn);
                mNoticeBV.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                mNoticeBV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                mNoticeBV.setBackgroundResource(R.drawable.notification_bg);
                mNoticeBV.setGravity(Gravity.CENTER);
            }

            mFragmentTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);

        }

    }

    //检查更新信息
    private void checkUpdate(){

        if (!AppContext.get(AppConfig.KEY_CHECK_UPDATE, true)) {
            return;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                new UpdateManager(MainActivity.this, false).checkUpdate();
            }
        }, 2000);

    }

    //恢复actionbar
    @SuppressWarnings("deprecation")
    private void restoreActionBar(){
        ActionBar actionBar=getSupportActionBar();
        if(actionBar != null){
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main_activity,menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search:
                UiUtils.showSimpleBack(this, BackFragmentEnum.SEARCH);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabChanged(String tabId) {
        final int size = mFragmentTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View view = mFragmentTabHost.getTabWidget().getChildAt(i);
            if (i == mFragmentTabHost.getCurrentTab()) {
                view.setSelected(true);
            } else {
                view.setSelected(false);
            }
        }
        if (tabId.equals(getString(MainActivityTabEnum.ME.getResName()))) {
            mNoticeBV.setText("");
            mNoticeBV.hide();
        }
        mTitle = tabId;
        supportInvalidateOptionsMenu();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            // 点击了快速操作按钮
            case R.id.quick_option_iv:
                showQuickOption();
                break;
            default:
                break;
        }
    }

    // 显示快速操作界面
    private void showQuickOption() {
        final QuickOptionDialog dialog = new QuickOptionDialog(
                MainActivity.this);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouchEvent(event);
        boolean consumed = false;
        // use getTabHost().getCurrentTabView to decide if the current tab is
        // touched again
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && v.equals(mFragmentTabHost.getCurrentTabView())) {
            // use getTabHost().getCurrentView() to get a handle to the view
            // which is displayed in the tab - and to get this views context
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment != null
                    && currentFragment instanceof OnTabReselectListener) {
                OnTabReselectListener listener = (OnTabReselectListener) currentFragment;
                listener.onTabReselect();
                consumed = true;
            }
        }
        return consumed;
    }

    //返回当前的Fragment
    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentByTag(
                mFragmentTabHost.getCurrentTabTag());
    }

    @Override
    public void onBackPressed() {

        boolean isDoubleClick = BaseApplication.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true);

        if (isDoubleClick) {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (3 * 1000)) {
                finish();
            } else {
                mBackPressedTime = curTime;
                Toast.makeText(this, R.string.tip_exit, Toast.LENGTH_LONG).show();
            }
        } else {
            finish();
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mReceiver=null;
        NoticeUtils.unbindService(this);
        NoticeUtils.tryToShutDown(this);
        AppManager.getAppManager().removeActivity(this);
    }


}
