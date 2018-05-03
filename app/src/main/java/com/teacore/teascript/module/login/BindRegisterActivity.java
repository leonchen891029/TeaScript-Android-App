package com.teacore.teascript.module.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.bean.LoginUser;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.teacore.teascript.util.OpenIdCatalog;
import com.teacore.teascript.util.XmlUtils;

import net.qiujuer.genius.ui.widget.Button;

/**账户绑定、注册
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-12
 */
public class BindRegisterActivity extends BaseActivity {

    public static final String BUNDLE_KEY_CATALOG="bundle_key_catalog";
    public static final String BUNDLE_KEY_OPENIDINFO="bundle_key_openidinfo";

    private String catalog;
    private String openIdInfo;

    private TextView openidtipTV;
    private Button bindBtn;
    private Button registerBtn;

    @Override
    protected boolean hasBackButton(){
        return true;
    }

    @Override
    protected int getLayoutId(){
        return R.layout.activity_login_bind_register;
    }

    @Override
    public void initView(){
        openidtipTV=(TextView) findViewById(R.id.openid_tip_tv);
        bindBtn=(Button) findViewById(R.id.bind_btn);
        registerBtn=(Button) findViewById(R.id.reg_btn);

        bindBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }



    @Override
    public void initData(){
        Intent intent=getIntent();
        if(intent != null){
            catalog=intent.getStringExtra(BUNDLE_KEY_CATALOG);
            openIdInfo=intent.getStringExtra(BUNDLE_KEY_OPENIDINFO);
            initCatalogText();
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.bind_btn:
                toBind();
                break;
            case R.id.reg_btn:
                toRegister();
                break;
        }

    }

    private void initCatalogText(){
        if (catalog.equals(OpenIdCatalog.QQ)) {
            openidtipTV.setText("你好，QQ用户");
        } else if (catalog.equals(OpenIdCatalog.WECHAT)) {
            openidtipTV.setText("你好，微信用户");
        } else if (catalog.equals(OpenIdCatalog.WEIBO)) {
            openidtipTV.setText("你好，新浪用户");
        } else if (catalog.equals(OpenIdCatalog.GITHUB)) {
            openidtipTV.setText("你好，Github用户");
        }
    }

    //跳转到绑定的Activity
    private void toBind(){
        Intent intent=new Intent(this,BindOpenIdActivity.class);
        intent.putExtra(BUNDLE_KEY_CATALOG,catalog);
        intent.putExtra(BUNDLE_KEY_OPENIDINFO,openIdInfo);
        startActivityForResult(intent, LoginActivity.REQUEST_CODE_OPENID);
    }

    //注册
    private void toRegister(){
        final ProgressDialog waitDialog= DialogUtils.getProgressDialog(this,"加载中...");
        TeaScriptApi.register_openid(catalog, openIdInfo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                LoginUser loginUserBean = XmlUtils.toBean(LoginUser.class, bytes);

                if (loginUserBean != null && loginUserBean.getResult().OK()) {
                    backLogin(loginUserBean);
                } else {
                    if (loginUserBean.getResult() != null) {
                        DialogUtils.getMessageDialog(BindRegisterActivity.this, loginUserBean.getResult().getMessage()).show();
                    } else {
                        DialogUtils.getMessageDialog(BindRegisterActivity.this, "使用第三方注册失败").show();
                    }
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                DialogUtils.getMessageDialog(BindRegisterActivity.this, "网络出错" + i).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LoginActivity.REQUEST_CODE_OPENID:
                if (data == null) {
                    return;
                }
                LoginUser loginUserBean = (LoginUser) data.getSerializableExtra(LoginActivity.BUNDLE_KEY_LOGINBEAN);
                backLogin(loginUserBean);
                break;
        }
    }

    private void backLogin(LoginUser loginUserBean) {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(LoginActivity.BUNDLE_KEY_LOGINBEAN, loginUserBean);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
        finish();
    }

}
