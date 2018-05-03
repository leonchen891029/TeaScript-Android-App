package com.teacore.teascript.module.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.LoginUser;
import com.teacore.teascript.network.TSHttpClient;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.OpenIdCatalog;
import com.teacore.teascript.util.TDevice;
import com.teacore.teascript.util.TLog;
import com.teacore.teascript.util.XmlUtils;
import com.teacore.teascript.widget.dialog.DialogUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.protocol.ClientContext;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.protocol.HttpContext;
import mehdi.sakout.fancybuttons.FancyButton;

/**用户登录Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-8
 */
public class LoginActivity extends BaseActivity implements IUiListener{

    public static int REQUEST_CODE_INIT=0;
    public static final String BUNDLE_KEY_REQUEST_CODE="BUNDLE_KEY_REQUEST_CODE";
    protected static final String TAG=LoginActivity.class.getSimpleName();


    private static final int requestCode=REQUEST_CODE_INIT;
    private String username="";
    private String password="";

    private static final int LOGIN_TYPE_SINA=1;
    private static final int LOGIN_TYPE_QQ=2;
    private static final int LOGIN_TYPE_WX=3;
    private int loginType;

    private EditText usernameET;
    private EditText passwordET;

    private FancyButton loginBtn;
    private ImageView qqLoginIV;
    private ImageView sinaLoginIV;
    private ImageView wxLoginIV;

    //private UMShareAPI umShareAPI=UMShareAPI.get(this);
    private SHARE_MEDIA platform;


    @Override
    protected int getLayoutId(){
        return R.layout.activity_login;
    }

    @Override
    public void initView(){
        usernameET=(EditText) findViewById(R.id.username_et);
        passwordET=(EditText) findViewById(R.id.password_et);

        loginBtn=(FancyButton) findViewById(R.id.login_btn);

        qqLoginIV=(ImageView) findViewById(R.id.qq_login_iv);
        sinaLoginIV=(ImageView) findViewById(R.id.sina_login_iv);
        wxLoginIV=(ImageView) findViewById(R.id.wx_login_iv);

        loginBtn.setOnClickListener(this);
        qqLoginIV.setOnClickListener(this);
        sinaLoginIV.setOnClickListener(this);
        wxLoginIV.setOnClickListener(this);
    }

    @Override
    public void initData(){
        usernameET.setText(AppContext.getInstance().getProperty("user.account"));
        passwordET.setText(AppContext.getInstance().getProperty("user.pwd"));
    }

    @Override
    protected boolean hasBackButton(){
        return true;
    }

    @Override
    protected int getActionBarTitle(){
        return R.string.login;
    }

    @Override
    public void onClick(View view){

        if(Build.VERSION.SDK_INT>=23){
            String[] mPermissionList = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE,Manifest.permission.READ_LOGS,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.SET_DEBUG_APP,Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.GET_ACCOUNTS,Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }

        int id = view.getId();
        switch (id) {
            case R.id.login_btn:
                handleLogin();
                break;
            case R.id.qq_login_iv:
                qqLogin();
                break;
            case R.id.wx_login_iv:
                wxLogin();
                break;
            case R.id.sina_login_iv:
                sinaLogin();
                break;
            default:
                break;
        }

    }

    private void handleLogin(){

        if(prepareForLogin()){
            return;
        }

        username=usernameET.getText().toString();
        password=passwordET.getText().toString();

        showWaitDialog(R.string.progress_login);

        TeaScriptApi.login(username,password,mhandler);
    }

    private final AsyncHttpResponseHandler mhandler=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i,Header[] headers, byte[] bytes) {

            LoginUser loginUserBean= XmlUtils.toBean(LoginUser.class, bytes);

            if(loginUserBean!=null){
                 handleLoginBean(loginUserBean,headers);
            }

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            AppContext.showToast("网络出错"+i);
        }

        @Override
        public void onFinish(){
            super.onFinish();
            hideWaitDialog();
        }

    };

    //LoginActivity Login成功返回数据data，结束LoginActivity
    private void handleLoginSuccess(){
        Intent data=new Intent();
        data.putExtra(BUNDLE_KEY_REQUEST_CODE,requestCode);
        setResult(RESULT_OK, data);
        this.sendBroadcast(new Intent(Constants.INTENT_ACTION_USER_CHANGE));
        TDevice.hideSoftKeyboard(getWindow().getDecorView());
        finish();
    }

    private boolean prepareForLogin(){

        if(!TDevice.hasInternet()){
            AppContext.showToast(R.string.tip_no_internet);
            return true;
        }

        if(usernameET.length()==0){
            usernameET.setError("请输入邮箱/用户名");
            usernameET.requestFocus();
            return true;
        }

        if(passwordET.length()==0){
            passwordET.setError("请输入密码");
            passwordET.requestFocus();
            return true;
        }

        return false;
    }

    //QQ登录授权
    private void qqLogin(){

        loginType=LOGIN_TYPE_QQ;

        //umShareAPI.doOauthVerify(this, SHARE_MEDIA.QQ,umAuthListener);

    }

    BroadcastReceiver receiver;

    //微信登录授权
    private void wxLogin(){
        loginType=LOGIN_TYPE_WX;

        //umShareAPI.doOauthVerify(this,SHARE_MEDIA.WEIXIN,umAuthListener);
    }

    //新浪微博登录授权
    private void sinaLogin(){
        loginType=LOGIN_TYPE_SINA;

        //umShareAPI.doOauthVerify(this,SHARE_MEDIA.SINA,umAuthListener);

        // 注册一个广播，监听微信的获取openid返回（类：WXEntryActivity中）
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(OpenIdCatalog.WECHAT);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {

                    String openid_info = intent.getStringExtra(BindRegisterActivity.BUNDLE_KEY_OPENIDINFO);

                    openIdLogin(OpenIdCatalog.WECHAT, openid_info);

                    // 注销这个监听广播
                    if (receiver != null) {
                        unregisterReceiver(receiver);
                    }
                }
            }
        };

        registerReceiver(receiver, intentFilter);
    }

    UMAuthListener umAuthListener = new UMAuthListener() {

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            //根据loginType调用openIdLogin，其中有调用绑定操作的Activity

        }

        //@param t 错误原因
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {

            if(platform==SHARE_MEDIA.QQ){
                AppContext.showToast("QQ授权失败"+" 错误代码:"+t.toString());
            }

            if(platform==SHARE_MEDIA.SINA){
                AppContext.showToast("新浪授权失败"+" 错误代码:"+t.toString());
            }

            if(platform==SHARE_MEDIA.WEIXIN){
                AppContext.showToast("微信授权失败"+" 错误代码:"+t.toString());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {

            if(platform==SHARE_MEDIA.QQ){
                AppContext.showToast("已取消QQ登录");
            }

            if(platform==SHARE_MEDIA.SINA){
                AppContext.showToast("已取消SINA登录");
            }

            if(platform==SHARE_MEDIA.WEIXIN){
                AppContext.showToast("已取消微信登录");
            }
        }
    };

    //获取到QQ授权登录的信息
    @Override
    public void onComplete(Object object){
        openIdLogin(OpenIdCatalog.QQ,object.toString());
    }


    @Override
    public void onError(UiError uiError){

    }

    @Override
    public void onCancel(){

    }

    //使用第三方登录  catalog 第三方登录的类别 openIdInfo 第三方信息
    private void openIdLogin(final String catalog, final String openIdInfo) {

        final ProgressDialog waitDialog = DialogUtils.getProgressDialog(this, "登陆中...");

        TeaScriptApi.open_login(catalog, openIdInfo, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i,Header[] headers, byte[] bytes) {

                LoginUser loginUserBean = XmlUtils.toBean(LoginUser.class, bytes);

                if (loginUserBean.getResult().OK()) {

                    handleLoginBean(loginUserBean, headers);

                } else {

                    // 前往绑定或者注册操作
                    Intent intent = new Intent(LoginActivity.this, BindRegisterActivity.class);
                    intent.putExtra(BindRegisterActivity.BUNDLE_KEY_CATALOG, catalog);
                    intent.putExtra(BindRegisterActivity.BUNDLE_KEY_OPENIDINFO, openIdInfo);
                    startActivityForResult(intent, REQUEST_CODE_OPENID);
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast("网络出错" + i);
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

    //OPENID请求码
    public static final int REQUEST_CODE_OPENID=1000;
    //登录实体类
    public static final String BUNDLE_KEY_LOGINBEAN = "bundle_key_loginbean";

    //这里别忘了加上UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //umShareAPI.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_OPENID){
            if (data == null) {
                return;
            }

            LoginUser loginUserBean = (LoginUser) data.getSerializableExtra(BUNDLE_KEY_LOGINBEAN);

            if (loginUserBean != null) {
                handleLoginBean(loginUserBean, null);
            }
        }

    }

    //处理loginbean实体类
    private void handleLoginBean(LoginUser loginUserBean, Header[] headers)
    {
        if (loginUserBean.getResult().OK()) {

            AsyncHttpClient client = TSHttpClient.getHttpClient();

            HttpContext httpContext = client.getHttpContext();

            CookieStore cookies = (CookieStore) httpContext
                    .getAttribute(ClientContext.COOKIE_STORE);

            if (cookies != null) {

                String tmpcookies = "";

                for (Cookie c : cookies.getCookies()) {

                    TLog.log(TAG,
                            "cookie:" + c.getName() + " " + c.getValue());

                    tmpcookies += (c.getName() + "=" + c.getValue()) + ";";
                }

                if (TextUtils.isEmpty(tmpcookies)) {

                    if (headers != null) {

                        for (Header header : headers) {

                            String key = header.getName();
                            String value = header.getValue();
                            if (key.contains("Set-Cookie"))
                                tmpcookies += value + ";";

                        }

                        if (tmpcookies.length() > 0) {
                            tmpcookies = tmpcookies.substring(0, tmpcookies.length() - 1);
                        }

                    }

                }
                TLog.log(TAG, "cookies:" + tmpcookies);

                AppContext.getInstance().setProperty(AppConfig.APP_COOKIE,
                        tmpcookies);

                TSHttpClient.setCookie(TSHttpClient.getCookie(AppContext
                        .getInstance()));
            }

            // 保存登录信息
            loginUserBean.getUser().setAccount(username);
            loginUserBean.getUser().setPwd(password);
            loginUserBean.getUser().setRememberMe(true);

            AppContext.getInstance().saveUserInfo(loginUserBean.getUser());

            hideWaitDialog();
            handleLoginSuccess();

        } else {
            AppContext.getInstance().cleanLoginInfo();
            AppContext.showToast(loginUserBean.getResult().getMessage());
        }

    }


}
