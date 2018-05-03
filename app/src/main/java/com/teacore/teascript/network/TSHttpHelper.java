package com.teacore.teascript.network;

import android.os.Build;

import com.teacore.teascript.app.AppContext;

/**获得User Agent(用户代理)，使得服务器能够识别用户使用的操作系统等信息
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-16
 */

public class TSHttpHelper {

    public static String getUserAgent(AppContext appContext){

        StringBuilder ua=new StringBuilder("TeaScript.ORG");
        //app版本信息
        ua.append('/'+appContext.getPackageInfo().versionName+"_"+appContext.getPackageInfo().versionCode);
        //手机系统平台
        ua.append("/Android");
        //手机系统版本
        ua.append("/"+ Build.VERSION.RELEASE);
        //手机型号
        ua.append("/"+ Build.MODEL);
        //客户端唯一标识
        ua.append("/"+appContext.getAppId());

        return ua.toString();
    }

}



