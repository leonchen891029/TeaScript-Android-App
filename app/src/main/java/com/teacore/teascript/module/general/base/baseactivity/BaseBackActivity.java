package com.teacore.teascript.module.general.base.baseactivity;

import android.support.v7.app.ActionBar;

/**
 * XXXDetailActivity的基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-6-21
 */

public abstract class BaseBackActivity extends BaseActivity{

    @Override
    protected void initWindow(){
        super.initWindow();
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return super.onSupportNavigateUp();
    }

}
