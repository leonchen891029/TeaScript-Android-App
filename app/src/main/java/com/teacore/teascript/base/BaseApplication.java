package com.teacore.teascript.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teacore.teascript.R;
import com.teacore.teascript.util.TimeUtils;

/**Application基类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-13
 */
public class BaseApplication extends Application{

       //SharePreference的默认文件名
       private static String PREF_NAME="creativelock";
       private static String LAST_REFRESH_TIME="last_refresh_time";

       static Context context;
       static Resources resources;

       private static String lastToast="";
       private static long lastToastTime;

       //版本号是否大于姜饼。。
       private static boolean isAtLeastGB;
       static{
           if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD){
               isAtLeastGB=true;
           }
       }

       //创建BaseApplication的时候，获取context和resources
       @Override
       public void onCreate(){
           super.onCreate();
           context=getApplicationContext();
           resources=context.getResources();
       }

       //获取BaseApplication实例(单例模式)
       public static synchronized BaseApplication context(){
           return (BaseApplication) context;
       }

       public static Resources resources(){
           return resources;
       }

       //获取SharedPreference的两种方法
       public static SharedPreferences getPreferences(){
         SharedPreferences sharedPreferences=context().getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
         return sharedPreferences;
       }

       public static SharedPreferences getPreferences(String prefName){
         SharedPreferences sharedPreferences=context().getSharedPreferences(prefName,Context.MODE_PRIVATE);
         return sharedPreferences;
       }

       //放入已读文章列表(SharedPreferences)
       public static void putReadedPostList(String prefFileName,String key,String value){
           SharedPreferences preferences=getPreferences(prefFileName);
           int size=preferences.getAll().size();
           SharedPreferences.Editor editor=preferences.edit();
           //如果editor的size大于等于100，clear
           if(size>=100){
               editor.clear();
           }
           editor.putString(key,value);
           apply(editor);
       }

       //根据key来判断该文章是否在已读文章的列表
       public static boolean isOnReadedPostList(String preFileName,String key){
           return getPreferences(preFileName).contains(key);
       }

       //记录列表上次刷新时间
       public static void putLastRefreshTime(String key,String value){
          SharedPreferences preferences=getPreferences(LAST_REFRESH_TIME);
          SharedPreferences.Editor editor=preferences.edit();
          editor.putString(key,value);
          apply(editor);
       }

       //获取列表的上次刷新时间
       public static String getLastRefreshTime(String key){
          return getPreferences(LAST_REFRESH_TIME).getString(key, TimeUtils.getCurrentTimeStr());
       }

       @TargetApi(Build.VERSION_CODES.GINGERBREAD)
       public static void apply(SharedPreferences.Editor editor){
          if(isAtLeastGB){
              editor.apply();
          }else{
              editor.commit();
          }
       }

       //设置和读取默认的SharedPreference
       public static void set(String key,int value){
          SharedPreferences.Editor editor=getPreferences().edit();
          editor.putInt(key,value);
          apply(editor);
       }

       public static void set(String key,boolean value){
         SharedPreferences.Editor editor=getPreferences().edit();
         editor.putBoolean(key, value);
         apply(editor);
       }

       public static void set(String key,String value){
         SharedPreferences.Editor editor=getPreferences().edit();
         editor.putString(key, value);
         apply(editor);
       }

       public static int get(String key,int defValue){
        return getPreferences().getInt(key,defValue);
       }

       public static boolean get(String key,boolean defValue){
         return getPreferences().getBoolean(key, defValue);
       }

       public static String get(String key,String defValue){
         return getPreferences().getString(key, defValue);
       }

       public static long get(String key,long defValue){
         return getPreferences().getLong(key, defValue);
       }

       public static float get(String key,float defValue) {
           return getPreferences().getFloat(key, defValue);
       }

       //保存屏幕的信息
       public static void saveDisplaySize(Activity activity){
           DisplayMetrics displayMetrics=new DisplayMetrics();
           activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
           SharedPreferences.Editor editor=getPreferences().edit();
           editor.putInt("screen_width",displayMetrics.widthPixels);
           editor.putInt("screen_height",displayMetrics.heightPixels);
           editor.putFloat("density",displayMetrics.density);
           editor.commit();
       }

      //获取Resource资源
      public  static String string(int id){
         return resources.getString(id);
    }

      public  static String string(int id,Object...args){
        return resources.getString(id,args);
    }

      //用于显示Toast的静态方法
      public static void showToast(String message,int duration,int icon,int gravity){

        if(message!=null && !message.equalsIgnoreCase("")){
            long time=System.currentTimeMillis();

            //当前的message和之前的message不同或者时间差大于2000ms
            if(!message.equalsIgnoreCase(lastToast)||Math.abs(time-lastToastTime)>2000){

                View view= LayoutInflater.from(context()).inflate(R.layout.view_toast,null);

                ((TextView) view.findViewById(R.id.title_tv)).setText(message);

                if(icon !=0){
                    ((ImageView) view.findViewById(R.id.icon_iv)).setImageResource(icon);
                    ((ImageView) view.findViewById(R.id.icon_iv)).setVisibility(View.VISIBLE);
                }

                Toast toast=new Toast(context());
                //设置toast的view
                toast.setView(view);

                if(gravity== Gravity.CENTER){
                    toast.setGravity(gravity,0,0);
                }else{
                    toast.setGravity(gravity,0,35);
                }

                //设置toast的持续时间
                toast.setDuration(duration);

                toast.show();

                //设置lastToast和lastToastTime
                lastToast=message;
                lastToastTime=System.currentTimeMillis();
            }
        }
     }

     //各种showToast的重载方法
     public static void showToast(int message,int duration,int icon,int gravity,Object...args){
        showToast(context().getString(message,args),duration,icon,gravity);
     }

     public static void showToast(int message,int duration,int icon,int gravity){
        showToast(context().getString(message),duration,icon,gravity);
     }

     public static void showToast(int message,int duration,int icon){
        showToast(context().getString(message),duration,icon,Gravity.BOTTOM);
     }

     public static void showToast(int message,Object...args){
        showToast(message,Toast.LENGTH_LONG,0,Gravity.BOTTOM,args);
     }

     public static void showToast(int message){
        showToast(message,Toast.LENGTH_LONG,0);
    }

     public static void showToast(String message){
        showToast(message,Toast.LENGTH_LONG,0,Gravity.BOTTOM);
     }


}


















































