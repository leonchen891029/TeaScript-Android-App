package com.teacore.teascript.app;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.teacore.teascript.base.BaseApplication;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.User;
import com.teacore.teascript.cache.DataCleanManager;
import com.teacore.teascript.network.TSHttpClient;
import com.teacore.teascript.util.CyptoUtils;
import com.teacore.teascript.util.MethodsCompat;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.UiUtils;
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import static com.teacore.teascript.app.AppConfig.KEY_FIRST_START;
import static com.teacore.teascript.app.AppConfig.KEY_LOAD_IMAGE;
import static com.teacore.teascript.app.AppConfig.KEY_NIGHT_MODE_SWITCH;
import static com.teacore.teascript.app.AppConfig.KEY_TEATIME_DRAFT;

/**全局应用程序类，保存和调用全局应用配置及访问网络数据
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-8
 */
public class AppContext extends BaseApplication{

    //Bitmap缓存地址
    public static String cachePath="/sdcard/teascript/imagecache";

    //友盟API实例
    public static UMShareAPI umShareAPI;

    //默认的分页大小
    public static final int PAGE_SIZE=20;
    //单例实例
    private static AppContext instance;
    //用户的id
    private int loginUid;
    //用户是否登录
    private boolean login;

    /*配置三方平台的appKey
    {
        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");
    }
   */

    @Override
    public void onCreate(){

        super.onCreate();

        /*初始化友盟sdk
        UMShareAPI.get(this);
        */

        instance=this;

        //App初始化
        init();

        //登录初始化
        initLogin();

        //发送通知广播
        UiUtils.sendBroadcastForNotice(this);
    }

    private void init(){

        //初始化网络请求
        AsyncHttpClient client=new AsyncHttpClient();
        PersistentCookieStore myCookieStore=new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        TSHttpClient.setHttpClient(client);
        TSHttpClient.setCookie(TSHttpClient.getCookie(this));

    }

    private void initLogin(){

        User user=getLoginUser();

        if(user!=null && user.getId()>0){

            login=true;

            loginUid=user.getId();

        }else{

            cleanLoginInfo();

        }

    }

    //获取当前App运行的AppContext，可以稍微修改
    public static AppContext getInstance(){
        return instance;
    }

    //判断是否包含特定的key值
    public boolean containProperty(String key){
        Properties props=getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties properties){
        AppConfig.getAppConfig(this).setProps(properties);
    }

    //间接调用了AppConfig的Properties功能
    public void setProperty(String key,String value){
        AppConfig.getAppConfig(this).setProp(key,value);
    }

    public Properties getProperties(){
        return AppConfig.getAppConfig(this).getProps();
    }

    public String getProperty(String key){
        return AppConfig.getAppConfig(this).getProp(key);
    }

    public void removeProperty(String... key){
        AppConfig.getAppConfig(this).removeProps(key);
    }

    //获取App唯一标识
    public String getAppId(){
        String uniqueID=getProperty(AppConfig.APP_UNIQUEID);
        if(StringUtils.isEmpty(uniqueID)){
            uniqueID= UUID.randomUUID().toString();
            setProperty(AppConfig.APP_UNIQUEID,uniqueID);
        }
        return uniqueID;
    }

    //获取App安装包信息
    public PackageInfo getPackageInfo(){
        PackageInfo info=null;
        try{
            info=getPackageManager().getPackageInfo(getPackageName(),0);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace(System.err);
        }
        if(info==null)
            info=new PackageInfo();
        return info;
    }

    //保存登录信息
    public void saveUserInfo(final User user){

        this.loginUid=user.getId();

        this.login=true;

        Properties userProperties=new Properties();

        userProperties.setProperty("user.uid",String.valueOf(user.getId()));
        userProperties.setProperty("user.pwd", CyptoUtils.encode("teascript", user.getPwd()));
        userProperties.setProperty("user.name",user.getName());
        userProperties.setProperty("user.gender",String.valueOf(user.getGender()));
        userProperties.setProperty("user.face",user.getPortrait());
        userProperties.setProperty("user.account",user.getAccount());
        userProperties.setProperty("user.location",user.getLocation());
        userProperties.setProperty("user.followers",String.valueOf(user.getFollowers()));
        userProperties.setProperty("user.fans",String.valueOf(user.getFans()));
        userProperties.setProperty("user.score",String.valueOf(user.getScore()));
        userProperties.setProperty("user.favoritecount",String.valueOf(user.getFavoriteCount()));
        userProperties.setProperty("user.isRememberMe",String.valueOf(user.isRememberMe()));

        setProperties(userProperties);
    }

    //更新用户数据
    public void updateUserInfo(final User user){

        Properties userProperties=new Properties();

        userProperties.setProperty("user.uid",String.valueOf(user.getId()));
        userProperties.setProperty("user.pwd",CyptoUtils.encode("teascript", user.getPwd()));
        userProperties.setProperty("user.name",user.getName());
        userProperties.setProperty("user.gender",String.valueOf(user.getGender()));
        userProperties.setProperty("user.face",user.getPortrait());
        userProperties.setProperty("user.account",user.getAccount());
        userProperties.setProperty("user.location",user.getLocation());
        userProperties.setProperty("user.followers",String.valueOf(user.getFollowers()));
        userProperties.setProperty("user.fans",String.valueOf(user.getFans()));
        userProperties.setProperty("user.score",String.valueOf(user.getScore()));
        userProperties.setProperty("user.favoritecount",String.valueOf(user.getFavoriteCount()));
        userProperties.setProperty("user.isRememberMe",String.valueOf(user.isRememberMe()));

        setProperties(userProperties);
    }

    //获取登录用户信息
    public User getLoginUser() {

        User user = new User();

        user.setId(StringUtils.toInt(getProperty("user.uid"), 0));
        user.setName(getProperty("user.name"));
        user.setPortrait(getProperty("user.face"));
        user.setAccount(getProperty("user.account"));
        user.setLocation(getProperty("user.location"));
        user.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
        user.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
        user.setScore(StringUtils.toInt(getProperty("user.score"), 0));
        user.setFavoriteCount(StringUtils.toInt(
                getProperty("user.favoritecount"), 0));
        user.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
        user.setGender(getProperty("user.gender"));

        return user;

    }

    //清除登录信息
    public void cleanLoginInfo() {
        this.loginUid = 0;
        this.login = false;
        removeProperty("user.uid", "user.name", "user.face", "user.location",
                "user.followers", "user.fans", "user.score",
                "user.isRememberMe", "user.gender", "user.favoritecount");
    }

    //得到用户的id
    public int getLoginUid(){
        return loginUid;
    }

    //用户是否登录
    public boolean isLogin(){
        return login;
    }

    //用户注销
    public void logout(){

        cleanLoginInfo();

        TSHttpClient.cleanCookie();

        cleanCookie();

        Intent intent=new Intent(Constants.INTENT_ACTION_LOGOUT);
        sendBroadcast(intent);
    }

    //清除保存的缓存
    public void cleanCookie(){
        removeProperty(AppConfig.APP_COOKIE);
    }

    //清除app缓存
    public void cleanAppCache(){
        DataCleanManager.cleanDatabases(this);
        //清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        //2.2版本才有将应用缓存到sd卡的功能
        if(isMethodsCompat(Build.VERSION_CODES.FROYO)){
            DataCleanManager.cleanCustomCache(MethodsCompat.getExternalCacheDir(this));
        }
        //清除临时的内容
        Properties properties=getProperties();
        for(Object key:properties.keySet()){
            String _key=key.toString();
            if(_key.startsWith("temp"))
                removeProperty(_key);
        }

        cleanImageCache();
    }

    public static void setLoadImage(boolean flag){
        set(KEY_LOAD_IMAGE, flag);
    }

    //判断当前的版本是否兼容目标方法
    public static boolean isMethodsCompat(int versionCode){
        int currentVersion= Build.VERSION.SDK_INT;
        return currentVersion>=versionCode;
    }

    public static String getTeatimeDraft() {
        return get(KEY_TEATIME_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setTeatimeDraft(String draft) {
        set(KEY_TEATIME_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static String getNoteDraft() {
        return get(AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), "");
    }

    public static void setNoteDraft(String draft) {
        set(AppConfig.KEY_NOTE_DRAFT + getInstance().getLoginUid(), draft);
    }

    public static boolean isFristStart() {
        return get(KEY_FIRST_START, true);
    }

    public static void setFristStart(boolean frist) {
        set(KEY_FIRST_START, frist);
    }

    //夜间模式
    public static boolean getNightModeSwitch() {
        //return getPreferences().getBoolean(KEY_NIGHT_MODE_SWITCH, false);
        return false;
    }

    // 设置夜间模式
    public static void setNightModeSwitch(boolean on) {
        set(KEY_NIGHT_MODE_SWITCH, on);
    }

    //获取Google的Gson对象
    public static Gson createGson() {
        com.google.gson.GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder();
        //gsonBuilder.setExclusionStrategies(new SpecificClassExclusionStrategy(null, Model.class));
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.create();
    }

    public static void cleanImageCache(){

        final File folder=new File("/sdcard/teascript/imagecache");

        final File[] files=folder.listFiles();

        if(files !=null && files.length>0){

            AsyncTask.execute(new Runnable() {
                @Override
                public void run(){
                    if(folder.isDirectory()){

                        for (File file:files){
                            file.delete();
                        }

                    }
                }
            });

        }
    }

}
