package com.teacore.teascript.module.back;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.base.BaseFragment;
import com.teacore.teascript.module.back.currencyfragment.ChatMessageListFragment;
import com.teacore.teascript.module.quickoption.activity.TeatimePubActivity;
import com.teacore.teascript.module.teatime.TeatimeListFragment;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.widget.emoji.OnSendClickListener;

import java.lang.ref.WeakReference;

/**
 * 用于加载各个不同的Fragment的Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-14
 */

public class BackActivity extends BaseActivity implements OnSendClickListener{

    public static final String BUNDLE_KEY_PAGE="bundle_key_page";
    public static final String BUNDLE_KEY_ARGS="bundle_key_args";

    protected WeakReference<Fragment> mFragment;
    protected  int mFragmentValue=-1;

    @Override
    public void startActivity(Intent intent){
        //跳转之前，先获取焦点并隐藏软键盘
        View view=getCurrentFocus();
        if(view!=null){
            view.clearFocus();
            InputMethodManager manager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.startActivity(intent);
    }

    @Override
    protected int getLayoutId(){
        return R.layout.activity_simple_back_fragment;
    }

    @Override
    protected boolean hasBackButton(){
        return true;
    }

    @Override
    protected void init(Bundle savedInstanceState){
        super.init(savedInstanceState);
        Intent intent=getIntent();
        //此时Fragment的Id值如果为—1,则从Intent中取ID值，缺省值为0
        if (mFragmentValue == -1) {
            mFragmentValue = intent.getIntExtra(BUNDLE_KEY_PAGE,0);
        }
        initFromIntent(mFragmentValue, getIntent());
    }

    /**
     * 初始化Fragment
     * @param fragmentValue 加载的fragment的ID值
     * @param intent        获取fragment相应的参数
     */
    protected void initFromIntent(int fragmentValue,Intent intent){

        if(intent==null){
            throw new RuntimeException("你必须提供一个页面信息！");
        }

        BackFragmentEnum fragmentEnum= BackFragmentEnum.getFragmentByValue(fragmentValue);

        if(fragmentEnum==null){
            throw new IllegalArgumentException("未能通过value找到相应的Fragment"+fragmentValue);
        }

        setActionBarTitle(fragmentEnum.getTitle());

        try{
            Fragment fragment=(Fragment) fragmentEnum.getClz().newInstance();
            Bundle args=intent.getBundleExtra(BUNDLE_KEY_ARGS);
            if(args!=null){
                fragment.setArguments(args);
            }

            //加载Fragment
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container,fragment);
            transaction.commitAllowingStateLoss();

            mFragment=new WeakReference<Fragment>(fragment);
        }catch(Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("生成fragment出错 by value:"+fragmentValue);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mFragment.get() instanceof TeatimeListFragment){
            setActionBarTitle("话题");
        }
    }

    public boolean onCreateOptionMenu(Menu menu){
        if(mFragment.get() instanceof TeatimeListFragment){
            getMenuInflater().inflate(R.menu.menu_pub_topic,menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_chat,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.pub_topic_menu:
                if(mFragment.get() instanceof TeatimeListFragment){
                    sendTopic();
                }else{
                    return super.onOptionsItemSelected(item);
                }
                break;
            case R.id.chat_friend_home:
                if(mFragment.get() instanceof ChatMessageListFragment){
                    ((ChatMessageListFragment) mFragment.get()).showFriendUserCenter();
                }else{
                    return super.onOptionsItemSelected(item);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //发送话题
    private void sendTopic() {
        Bundle bundle = new Bundle();
        bundle.putString(TeatimePubActivity.REPOST_TEXT_KEY, "#"
                + ((TeatimeListFragment) mFragment.get()).getTopic() + "# ");
        UiUtils.showTeatimeActivity(this, TeatimePubActivity.ACTION_TYPE_TOPIC, bundle);
    }

    @Override
    public void onBackPressed(){
        if(mFragment!=null && mFragment.get()!=null && mFragment.get() instanceof BaseFragment){
            BaseFragment baseFragment=(BaseFragment) mFragment.get();
            if(!baseFragment.onBackPressed()){
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.ACTION_DOWN
                && mFragment.get() instanceof BaseFragment) {
            ((BaseFragment) mFragment.get()).onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClickSendButton(Editable str) {
        if (mFragment.get() instanceof ChatMessageListFragment) {
            ((OnSendClickListener) mFragment.get()).onClickSendButton(str);
            ((ChatMessageListFragment) mFragment.get()).emojiFragment.clean();
        }
    }

    @Override
    public void onClickFlagButton() {
    }

}
