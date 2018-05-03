package com.teacore.teascript.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.teacore.teascript.R;
import com.teacore.teascript.module.main.MainActivity;
import com.teacore.teascript.util.TDevice;

/**应用启动界面
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-15
 */
public class AppStart extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){

        /* UMENG Android6.0 权限适配
        if(Build.VERSION.SDK_INT>=23){
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.SET_DEBUG_APP,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.GET_ACCOUNTS,Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        */
        super.onCreate(savedInstanceState);

        //防止第三方跳转时出现双实例
        Activity activity= AppManager.getActivity(MainActivity.class);
        if(activity!=null && !activity.isFinishing()){
            finish();
        }

        boolean firstStart=AppContext.get("firstStart",true);
        if(firstStart){
            Intent intent=new Intent(AppStart.this,AppWelcome.class);
            AppContext.set("firstStart",false);
            startActivity(intent);
            finish();
        }

        final View view=View.inflate(this, R.layout.view_app_start,null);
        setContentView(view);

        //渐变展示启动屏
        AlphaAnimation aa=new AlphaAnimation(0.5f,1.0f);
        aa.setDuration(800);
        view.startAnimation(aa);

        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    protected void onResume(){

        super.onResume();

        int cacheVersion= AppContext.get("firstInstall",-1);

        int currentVersion= TDevice.getVersionCode();

        if(cacheVersion<currentVersion){
            AppContext.set("first_install",currentVersion);
            AppContext.cleanImageCache();
        }

    }

    //跳转到MainActivity.class
    private void redirectTo() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
