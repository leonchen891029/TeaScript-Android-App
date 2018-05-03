package com.teacore.teascript.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.app.AppManager;
import com.teacore.teascript.interfaces.BaseActivityInterface;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.widget.TSToast;
import com.teacore.teascript.widget.dialog.DialogControl;
import com.teacore.teascript.widget.dialog.DialogUtils;

/**
 * 抽象Activity基类
 * ActionBarActivity
 *
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-3
 */
public abstract class BaseActivity extends AppCompatActivity implements
        View.OnClickListener, DialogControl, BaseActivityInterface {

    public static final String INTENT_ACTION_EXIT_APP = "INTENT_ACTION_EXIT_APP";

    private boolean isVisible;
    private ProgressDialog waitDialog;

    protected LayoutInflater mLayoutInflater;
    protected android.support.v7.app.ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断是否是夜间模式
        if (AppContext.getNightModeSwitch()) {
            setTheme(R.style.AppBaseTheme_Night);
        } else {
            setTheme(R.style.AppBaseTheme_Light);
        }

        //将Activity加入到AppManager中
        AppManager.getAppManager().addActivity(this);

        onBeforeSetContentLayout();

        //如果加载的布局文件不为0，setContentView
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }

        mActionBar = getSupportActionBar();

        mLayoutInflater = getLayoutInflater();

        //如果ActionBar不为null
        if (hasActionBar()) {
            initActionBar(mActionBar);
        }

        //初始化savedInstanceState
        init(savedInstanceState);

        initView();

        initData();

        isVisible = true;
        
    }

    protected void onBeforeSetContentLayout() {

    }

    protected boolean hasActionBar() {
        return mActionBar != null;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return mLayoutInflater.inflate(resId, null);
    }

    protected int getActionBarTitle() {
        return R.string.app_name;
    }

    //是否具有回退键
    protected boolean hasBackButton() {
        return false;
    }

    protected void init(Bundle savedInstanceState) {

    }

    protected void initActionBar(ActionBar actionBar) {
        if (actionBar == null) {
            return;
        }

        if (hasBackButton()) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } else {
            actionBar.setDisplayUseLogoEnabled(false);
            int titleRes = getActionBarTitle();
            if (titleRes != 0) {
                actionBar.setTitle(titleRes);
            }
        }
    }

    public void setActionBarTitle(String title) {
        if (StringUtils.isEmpty(title)) {
            title = getString(R.string.app_name);
        }
        if (hasActionBar() && mActionBar != null) {
            mActionBar.setTitle(title);
        }
    }

    public void setActionBarTitle(int resId) {
        if (resId != 0) {
            setActionBarTitle(getString(resId));
        }
    }

    public void showToast(String message, int icon, int gravity) {

        TSToast commonToast = new TSToast(this);
        commonToast.setMessage(message);
        commonToast.setMessageIcon(icon);
        commonToast.setLayoutGravity(gravity);
        commonToast.show();
    }

    public void showToast(int msgResId, int icon, int gravity) {
        showToast(getString(msgResId), icon, gravity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.isFinishing()) {
            TDevice.hideSoftKeyboard(getCurrentFocus());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public ProgressDialog showWaitDialog(String message) {
        if (isVisible) {
            if (waitDialog == null) {
                waitDialog = DialogUtils.getProgressDialog(this, message);
            }
            if (waitDialog != null) {
                waitDialog.setMessage(message);
                waitDialog.show();
            }
            return waitDialog;
        }
        return null;
    }

    @Override
    public ProgressDialog showWaitDialog(int resId) {
        return showWaitDialog(getString(resId));
    }

    @Override
    public ProgressDialog showWaitDialog() {
        return showWaitDialog(R.string.loading);
    }

    @Override
    public void hideWaitDialog() {
        if (isVisible && waitDialog != null) {
            try {
                waitDialog.dismiss();
                waitDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}


