package com.teacore.teascript.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**应用程序的配置类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-10
 */
public class AppConfig {

    private static final String APP_CONFIG="config";
    public static final String  APP_COOKIE="cookie";
    public static final String  APP_UNIQUEID="unique_id";

    public static final String KEY_LOAD_IMAGE = "KEY_LOAD_IMAGE";
    public static final String KEY_NOTIFICATION_ACCEPT = "KEY_NOTIFICATION_ACCEPT";
    public static final String KEY_NOTIFICATION_SOUND = "KEY_NOTIFICATION_SOUND";
    public static final String KEY_NOTIFICATION_VIBRATION = "KEY_NOTIFICATION_VIBRATION";
    public static final String KEY_NOTIFICATION_DISABLE_WHEN_EXIT = "KEY_NOTIFICATION_DISABLE_WHEN_EXIT";
    public static final String KEY_CHECK_UPDATE = "KEY_CHECK_UPDATE";
    public static final String KEY_DOUBLE_CLICK_EXIT = "KEY_DOUBLE_CLICK_EXIT";

    public static final String KEY_TEATIME_DRAFT = "KEY_TEATIME_DRAFT";
    public static final String KEY_NOTE_DRAFT = "KEY_NOTE_DRAFT";

    public static final String KEY_FIRST_START = "KEY_FRIST_START";

    public static final String KEY_NIGHT_MODE_SWITCH = "KEY_NIGHT_MODE_SWITCH";

   /**待确定
    public static final String APP_QQ_KEY = "100942993";
    */

    //默认存放图片的路径
    public static final String DEFAULT_SAVE_IMAGE_PATH= Environment.getExternalStorageDirectory()+ File.separator
            +"teascript"+File.separator+"img"+ File.separator;

    //默认存放文件下载的路径
    public static final String DEFAULT_SAVE_FILE_PATH=Environment.getExternalStorageDirectory()+ File.separator
            +"teascript"+File.separator+"download"+ File.separator;

    private Context mContext;
    private static AppConfig appConfig;

    //获取AppConfig实例(单例模式)
    public static AppConfig getAppConfig(Context context){
        if(appConfig==null){
            appConfig=new AppConfig();
            appConfig.mContext=context;
        }
        return appConfig;
    }
    
    //获取SharedPreference
    public static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    //获取config文件中相应key的值
    public String getProp(String key){
        Properties properties=getProps();
        return (properties!=null)?properties.getProperty(key):null;
    }

    //读取config文件
    public Properties getProps(){
        FileInputStream fis=null;
        Properties properties=new Properties();
        try{
            //获取config文件的目录
            File dirConf=mContext.getDir(APP_CONFIG,Context.MODE_PRIVATE);
            //获取config文件
            fis=new FileInputStream(dirConf.getPath()+File.separator+APP_CONFIG);
            properties.load(fis);
        }catch (Exception e){
        }finally {
            try{fis.close();}catch (Exception e){}
        }
        return properties;
    }

    //设施Properties属性值(私有方法)
    private void set(Properties p){
        FileOutputStream fos=null;
        try{
            //把config建在(自定义)app_config的目录下
            File dirConf=mContext.getDir(APP_CONFIG,Context.MODE_PRIVATE);
            File conf=new File(dirConf,APP_CONFIG);
            fos=new FileOutputStream(conf);

            p.store(fos,null);
            fos.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{fos.close();}catch (Exception e){}
        }
    }

    public void setProps(Properties p){
        Properties properties=getProps();
        properties.putAll(p);
        set(properties);
    }

    public void setProp(String key,String value){
        Properties properties=getProps();
        properties.setProperty(key, value);
        set(properties);
    }

    public void removeProps(String... key) {
        Properties props = getProps();
        for (String k : key)
            props.remove(k);
        set(props);
    }

    //有效期是否在一天之内，如果是则在有效期内
    public static boolean isExpiryDate(String time){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try{
            Date date=format.parse(time);
            long delta=new Date().getTime()-date.getTime();
            if(delta<24L*3600000L)
                return true;
            return false;
        }catch (ParseException e){
            e.printStackTrace();
            return false;
        }
    }

}
