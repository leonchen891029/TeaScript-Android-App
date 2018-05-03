package com.teacore.teascript.module.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.bean.LoginUser;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.util.XmlUtils;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.EditText;

/**第三方登录账号绑定操作
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-16
 */
public class BindOpenIdActivity extends BaseActivity{

    private EditText usernameET;
    private EditText passwordET;
    private Button bindBtn;

    private String catalog;
    private String openIdInfo;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.login;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_bind_openid;
    }

    @Override
    public void initView() {
        usernameET=(EditText) findViewById(R.id.username_et);
        passwordET=(EditText) findViewById(R.id.password_et);

        bindBtn=(Button) findViewById(R.id.bind_btn) ;
        bindBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.bind_btn:
                toBind();
                break;
        }
    }



    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            catalog = intent.getStringExtra(BindRegisterActivity.BUNDLE_KEY_CATALOG);
            openIdInfo = intent.getStringExtra(BindRegisterActivity.BUNDLE_KEY_OPENIDINFO);
        }
    }

    private void toBind(){
        if(checkInput())
            return;

        String username=usernameET.getText().toString();
        String password=passwordET.getText().toString();

        final ProgressDialog waitDialog= DialogUtils.getProgressDialog(this, "加载中");

        TeaScriptApi.bind_openid(catalog, openIdInfo, username, password, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i,cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                LoginUser loginUserBean = XmlUtils.toBean(LoginUser.class, bytes);

                if (loginUserBean != null && loginUserBean.getResult().OK()) {
                    Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(LoginActivity.BUNDLE_KEY_LOGINBEAN, loginUserBean);
                    data.putExtras(bundle);
                    setResult(RESULT_OK, data);
                    finish();
                }else {
                    DialogUtils.getMessageDialog(BindOpenIdActivity.this, loginUserBean.getResult().getMessage()).show();
                }
            }

            @Override
            public void onFailure(int i,cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {

            }

            @Override
            public void onStart() {
                super.onStart();
                waitDialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                waitDialog.dismiss();
            }

        });

    }

    private boolean checkInput(){
        if(usernameET.length()==0){
            usernameET.setError("请输入用户名");
            usernameET.requestFocus();
            return true;
        }

        if(passwordET.length()==0){
            passwordET.setError("请输入密码");
            passwordET.requestFocus();
            return true;
        }

        return true;
    }



}
