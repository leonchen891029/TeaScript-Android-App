package com.teacore.teascript.module.general.base.baseactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * 综合的activity基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-21
 */

public abstract class BaseActivity extends AppCompatActivity{

    protected RequestManager mImageLoader;
    private boolean mIsDestroy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(initBundle(getIntent().getExtras())){

            setContentView(getLayoutId());

            initWindow();

            initView();

            initData();
        }else{
            finish();
        }

    }

    protected abstract int getLayoutId();

    protected boolean initBundle(Bundle bundle){
        return true;
    };

    protected void initWindow(){

    }

    protected void initView(){

    }

    protected void initData(){

    }

    public synchronized RequestManager getImageLoader(){
        if(mImageLoader==null){
            mImageLoader= Glide.with(this);
        }
        return mImageLoader;
    }

    @Override
    protected void onDestroy() {
        mIsDestroy = true;
        super.onDestroy();
    }

    public boolean isDestroy() {
        return mIsDestroy;
    }

}
