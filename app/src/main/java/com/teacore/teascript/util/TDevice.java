package com.teacore.teascript.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.R;
import com.teacore.teascript.base.BaseApplication;

import java.io.File;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.List;
import java.util.UUID;

/**设备相关信息
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-15
 */
public class TDevice {

    //手机网络类型
    public static final int NETTYPE_WIFI=0x01;
    public static final int NETTYPE_CMWAP=0x02;
    public static final int NETTYPE_CMNET=0x03;

    /* 判断版本的的范围
    1.GTE_ICS 版本号大于Ice Cream Sandwich
    2.GTE_HC  版本号大于HoneyComb
    3.PRE_HC  版本号小于HoneyComb
     */
    public static boolean GTE_ICS;
    public static boolean GTE_HC;
    public static boolean PRE_HC;

    //设备是否是大屏幕设备,这些都是基本类型的封装类
    private static Boolean hasBigScreen=null;
    private static Boolean hasCamera=null;
    private static Boolean isTablet=null;
    private static Integer loadFactor=null;

    private static int pageSize=-1;

    public static float displayDensity=0.0F;

    static {
        GTE_ICS= Build.VERSION.SDK_INT>=14;
        GTE_HC=Build.VERSION.SDK_INT>=11;
        PRE_HC=Build.VERSION.SDK_INT<11;
    }

    public TDevice(){
    }

    public static float dpToPixels(float dp){
        return dp*(getDisplayMetrics().densityDpi/160F);
    }

    public static float pixelsToDp(float f) {
        return f / (getDisplayMetrics().densityDpi / 160F);
    }

    public static int getDefaultLoadFactor() {
        if (loadFactor == null) {
            //BaseApplication.context得到一个BaseApplication对象
            Integer integer = Integer.valueOf(0xf & BaseApplication.context()
                    .getResources().getConfiguration().screenLayout);

            loadFactor = integer;
            loadFactor = Integer.valueOf(Math.max(integer.intValue(), 1));
        }
        return loadFactor.intValue();
    }

    public static float getDensity(){
        if(displayDensity==0.0){
            displayDensity=getDisplayMetrics().density;
        }

        return displayDensity;
    }

    public static DisplayMetrics getDisplayMetrics(){
        DisplayMetrics displayMetrics=new DisplayMetrics();

        ((WindowManager) BaseApplication.context().getSystemService(Context.WINDOW_SERVICE) ).getDefaultDisplay()
                .getMetrics(displayMetrics);

        return displayMetrics;
    }

    public static float getScreenWidth(){
        return getDisplayMetrics().widthPixels;
    }

    public static float getScreenHeight(){
        return getDisplayMetrics().heightPixels;
    }

    //根据Activity得到screen大小
    public static int[] getRealScreenSize(Activity activity){
        int[] size=new int[2];
        int screenWidth=0,screenHeight=0;

        WindowManager windowManager=activity.getWindowManager();
        Display display=windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics=new DisplayMetrics();
        display.getMetrics(displayMetrics);

        screenWidth=displayMetrics.widthPixels;
        screenHeight=displayMetrics.heightPixels;

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                screenWidth = (Integer) Display.class.getMethod("getRawWidth")
                        .invoke(display);
                screenHeight = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display,
                        realSize);
                screenWidth = realSize.x;
                screenHeight = realSize.y;
            } catch (Exception ignored) {
            }

        size[0] = screenWidth;
        size[1] = screenHeight;
        return size;

    }

    //获取statusbar的高度
    public static int getStatusBarHeight(){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            //获得一个c的实例对象
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());

            return BaseApplication.context().getResources()
                    .getDimensionPixelSize(x);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //获取设备唯一认证码
    public static String getUdid(){
        String udid=BaseApplication.getPreferences().getString("udid","");

        if(udid.length()==0){
            SharedPreferences.Editor editor=BaseApplication.getPreferences().edit();
            udid=String.format("%s", UUID.randomUUID());
            editor.putString("udid",udid);
            editor.commit();
        }

        return udid;
    }

    public static boolean hasBigScreen(){
        boolean flag;

        if(hasBigScreen==null){
            if((0xf & BaseApplication.context().getResources()
                    .getConfiguration().screenLayout) >= 3)
                flag=true;
            else
                flag=false;

            hasBigScreen=Boolean.valueOf(flag);
        }

        return hasBigScreen.booleanValue();
    }

    //是否有镜头
    public static final boolean hasCamera(){
        if(hasCamera==null){
            PackageManager packageManager=BaseApplication.context().getPackageManager();

            boolean flag1=packageManager.hasSystemFeature("android.hardware.camera.front");
            boolean flag2=packageManager.hasSystemFeature("android.hardware.camera");
            boolean flag;
            if(flag1 || flag2)
                flag=true;
            else
                flag=false;

            hasCamera=Boolean.valueOf(flag);
        }

        return hasCamera.booleanValue();
    }

    //判断是否有实体的menu键
    public static boolean hasHardwareMenuKey(Context context) {
        boolean flag = false;
        if (PRE_HC)
            flag = true;
        else if (GTE_ICS) {
            flag = ViewConfiguration.get(context).hasPermanentMenuKey();
        } else
            flag = false;
        return flag;
    }

    //判断是否有网络连接
    public static boolean hasInternet() {
        boolean flag;
        if (((ConnectivityManager) BaseApplication.context().getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    //Google商店
    public static boolean gotoGoogleMarket(Activity activity, String pck) {
        try {
            Intent intent = new Intent();
            intent.setPackage("com.android.vending");
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + pck));
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //判定一个程序是否存在
    public static boolean isPackageExist(String pckName) {
        try {
            PackageInfo pckInfo = BaseApplication.context().getPackageManager()
                    .getPackageInfo(pckName, 0);

            if (pckInfo != null)
                return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;

    }

    public static void showAnimatedView(View view) {
        if (PRE_HC && view != null)
            view.setPadding(0, 0, 0, 0);
    }

    //隐藏view
    public static void hideAnimatedView(View view) {

        if (PRE_HC && view != null)
            view.setPadding(view.getWidth(), 0, 0, 0);

    }

    //判定屏幕是否被限定在水平方向上
    public static boolean isLandscape() {
        boolean flag;
        if (BaseApplication.context().getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        return flag;
    }

    //判断屏幕是否被限定在竖直方向上
    public static boolean isPortrait() {
        boolean flag;
        if (BaseApplication.context().getResources().getConfiguration().orientation == 1)
            flag = true;
        else
            flag = false;
        return flag;
    }

    //判断是否是平板
    public static boolean isTablet() {
        if (isTablet == null) {
            boolean flag;
            if ((0xf & BaseApplication.context().getResources()
                    .getConfiguration().screenLayout) >= 3)
                flag = true;
            else
                flag = false;

            isTablet = Boolean.valueOf(flag);
        }
        return isTablet.booleanValue();
    }

    //隐藏软键盘
    public static void hideSoftKeyboard(View view){
        if(view==null)
            return;
        View focusView =null;

        if(view instanceof EditText)
            focusView=view;

        Context context=view.getContext();

        if(context !=null && context instanceof Activity){
            Activity activity=((Activity) context);
            focusView=activity.getCurrentFocus();
        }

        if(focusView!=null){

            InputMethodManager inputMethodManager=(InputMethodManager) focusView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            inputMethodManager.hideSoftInputFromInputMethod(focusView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        }

    }

    public static void showSoftKeyboard(Dialog dialog){
        dialog.getWindow().setSoftInputMode(4);
    }

    public static void showSoftKeyboard(View view){
        if(view == null)
            return;

        if(!view.isFocusable()){
            view.setFocusable(true);
        }

        if(!view.isFocusableInTouchMode()){
            view.setFocusableInTouchMode(true);
        }

        if(!view.isFocused()) {
            view.requestFocus();
        }

        InputMethodManager inputMethodManager=(InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view,0);
        inputMethodManager.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }
    
    public static void toggleSoftKeyboard(View view){

        ((InputMethodManager) BaseApplication.context().getSystemService(Context.INPUT_METHOD_SERVICE)).
                toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }

    //sd卡是否已经准备好了 MEDIA_MOUNTED存储媒体已经挂载，并且挂载点可读/写
    public static boolean isSdcardReady(){
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    //返回当前国家的语言
    public static String getCurCountryLan(){

        return BaseApplication.context().getResources().getConfiguration().locale.getLanguage()+"-"
                +BaseApplication.context().getResources().getConfiguration().locale.getCountry();

    }

    //判断是不是中国
    public static boolean isCN(){

        String country=BaseApplication.context().getResources().getConfiguration().locale.getCountry();

        if(country.equalsIgnoreCase("CN")){
            return true;
        }else {
            return false;
        }

    }

    //计算两个浮点数的百分比，返回的是字符串
    public static String percent(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        str = nf.format(p3);
        return str;
    }

    public static String percent2(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(0);
        str = nf.format(p3);
        return str;
    }

    //前往商店下载相关的app
    public static void gotoMarket(Context context, String pck) {
        if (!isHaveMarket(context)) {
            AppContext.showToast("你手机中没有安装应用市场！");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pck));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    //判断手机中是否有应用商店
    public static boolean isHaveMarket(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        return infos.size() > 0;
    }

    //在商店中打开app
    public static void openAppInMarket(Context context) {
        if (context != null) {
            String pckName = context.getPackageName();
            try {
                gotoMarket(context, pckName);
            } catch (Exception ex) {
                try {
                    String otherMarketUri = "http://market.android.com/details?id="
                            + pckName;
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(otherMarketUri));
                    context.startActivity(intent);
                } catch (Exception e) {

                }
            }
        }
    }

    //打开全屏模式
    public static void setFullScreen(Activity activity){
        WindowManager.LayoutParams params = activity.getWindow()
                .getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    //取消全屏模式
    public static void cancelFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow()
                .getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(params);
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    //获取应用的相关信息
    public static PackageInfo getPackageInfo(String pckName){
        try{
            return BaseApplication.context().getPackageManager().getPackageInfo(pckName,0);
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        return null;
    }

    //获取应用的版本号
    public static int getVersionCode(){
        int versionCode;
        try{
            versionCode= BaseApplication.context().getPackageManager().getPackageInfo(BaseApplication.context().getPackageName(),0).versionCode;
        }catch(PackageManager.NameNotFoundException e){
            versionCode=0;
        }

        return versionCode;

    }

    //获取其他应用的版本号
    public static int getVersionCode(String pckName){
        int versionCode;
        try{
            versionCode= BaseApplication.context().getPackageManager().getPackageInfo(pckName,0).versionCode;
        }catch(PackageManager.NameNotFoundException e){
            versionCode=0;
        }

        return versionCode;

    }

    //获取版本的名称
    public static String getVersionName() {
        String name = "";
        try {
            name = BaseApplication
                    .context()
                    .getPackageManager()
                    .getPackageInfo(BaseApplication.context().getPackageName(),
                            0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            name = "";
        }
        return name;
    }

    //判定屏幕是不是锁屏
    public static boolean isScreenOn(){

        if (android.os.Build.VERSION.SDK_INT >= 20) {

            DisplayManager dm = (DisplayManager) BaseApplication.context().getSystemService(Context.DISPLAY_SERVICE);

            Display[] displays = dm.getDisplays();

            for (Display display : displays) {

                if (display.getState () == Display.STATE_ON
                        || display.getState () == Display.STATE_UNKNOWN) {
                    return true;
                }
            }
            return false;
        }

        // If you use less than API20:
        PowerManager powerManager = (PowerManager) BaseApplication.context().getSystemService(Context.POWER_SERVICE);
        if (powerManager.isScreenOn ()) {
            return true;
        }
        return false;
    }

    //安装APK文件
    public static void installAPK(Context context,File file){
        if(file==null||!file.exists())
            return;
        Intent intent=new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static Intent getInstallApkIntent(File file) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        return intent;
    }

    //打电话
    public static void openDial(Context context,String tel){
        Uri uri=Uri.parse("tel:"+tel);
        Intent intent=new Intent(Intent.ACTION_DIAL,uri);
        context.startActivity(intent);
    }

    //打电话对话框
    public static void openDial(Context context){
        Intent intent=new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //发送短信
    public static void openSMS(Context context,String smsBody,String tel){
        Uri uri=Uri.parse("smsto"+tel);
        Intent intent=new Intent(Intent.ACTION_SENDTO,uri);
        intent.putExtra("sms_body", smsBody);
        context.startActivity(intent);
    }

    //发送短信对话框
    public static void openSMS(Context context){
        Uri uri=Uri.parse("smsto");
        Intent intent=new Intent(Intent.ACTION_SENDTO,uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //调用照相机
    public static void openCamera(Context context){
        Intent intent=new Intent();
        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        intent.setFlags(0x34c40000);
        context.startActivity(intent);
    }

    //获取国际移动装备辨识码
    public static String getIMEI(){
        TelephonyManager telephonyManager=(TelephonyManager) BaseApplication.context().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    //打开第三方app
    public static void openApp(Context context,String packageName){
        Intent mainIntent=BaseApplication.context().getPackageManager().getLaunchIntentForPackage(packageName);
        if(mainIntent==null){
            mainIntent=new Intent(packageName);
        }
        context.startActivity(mainIntent);
    }

    //打开第三方app指定的Activity
    public static boolean openAppActivity(Context context,String packageName,String activityName){

        Intent intent=new Intent(Intent.ACTION_MAIN);
        ComponentName cn=new ComponentName(packageName,activityName);

        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(cn);

        try{
            context.startActivity(intent);
            return true;
        }catch(Exception e){
            return false;
        }


    }

    //判断wifi是否打开
    public static boolean isWifiOpen(){
        boolean b=false;

        ConnectivityManager connectivityManager=(ConnectivityManager) BaseApplication.context()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        //查看所有的网络选项
        Network[] networks=connectivityManager.getAllNetworks();

        NetworkInfo networkInfo;

        for(Network network:networks){

            networkInfo=connectivityManager.getNetworkInfo(network);

            if(networkInfo.getState()==NetworkInfo.State.CONNECTED){
                if(networkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                    b=false;
                }
                if(networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
                    b=true;
                }

            }

        }

        return b;
    }

    //卸载程序
    public static void unintallAPK(Context context,String packageName){
        if(isPackageExist(packageName)){
            Uri packageURI=Uri.parse("package:"+packageName);
            Intent unintallIntent=new Intent(Intent.ACTION_DELETE,packageURI);
            context.startActivity(unintallIntent);
        }
    }

    //将文本复制到剪切板
    public static void copyTextToClipboard(String string){
        if(TextUtils.isEmpty(string))
            return;

        ClipboardManager clipboardManager=(ClipboardManager) BaseApplication.context().getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clipData=ClipData.newPlainText("text",string);

        clipboardManager.setPrimaryClip(clipData);

        AppContext.showToast(R.string.copy_success);
    }

    /*
    发送邮件
    @param subject 主题
    @param content 内容
    @param emails 邮件地址
     */
    public static void sendEmail(Context context,String subject,String content,String... emails){
        try{
            Intent intent=new Intent(Intent.ACTION_SEND);

            intent.setType("message/rfc822");

            intent.putExtra(Intent.EXTRA_EMAIL, emails);
            intent.putExtra(Intent.EXTRA_SUBJECT,subject);
            intent.putExtra(Intent.EXTRA_TEXT,content);

            context.startActivity(intent);
        }catch(ActivityNotFoundException e){
            e.printStackTrace();
        }

    }

    public static int getStatuBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;// 默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = BaseApplication.context().getResources()
                    .getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    public static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize,
                tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    context.getResources().getDisplayMetrics());

        if (actionBarHeight == 0
                && context.getTheme().resolveAttribute(R.attr.actionBarSize,
                tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    context.getResources().getDisplayMetrics());
        }

        return actionBarHeight;
    }

    public static boolean hasStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        if ((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return false;
        } else {
            return true;
        }
    }


    /*
     调用系统安装了的应用分享
     * @param context
     * @param title
     * @param url
     */
    public static void showSystemShareOption(Activity context,
                                             final String title, final String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        context.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    //获取当前网络类型 0:没有网络 1：WiFi网络 2:WAP网络 3:NET网络
    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) AppContext
                .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return netType;
        }

        int nType = networkInfo.getType();

        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtils.isEmpty(extraInfo)) {

                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }

        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }


}
