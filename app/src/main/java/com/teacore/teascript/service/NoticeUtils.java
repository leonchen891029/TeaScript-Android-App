package com.teacore.teascript.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.util.TLog;

import java.util.HashMap;

/**
 * 消息服务的工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */

public class NoticeUtils {

    public static INoticeService NService=null;

    private static HashMap<Context,ServiceBinder> NConnectionMap=new HashMap<Context,ServiceBinder>();

    public static boolean bindToService(Context context){
        return bindToService(context,null);
    }

    public static boolean bindToService(Context context, ServiceConnection callback){

        context.startService(new Intent(context,NoticeService.class));

        ServiceBinder serviceBinder=new ServiceBinder(callback);

        NConnectionMap.put(context,serviceBinder);

        return context.bindService((new Intent()).setClass(context,NoticeService.class),serviceBinder,0);
    }


    public static void unbindService(Context context) {
        ServiceBinder sb = NConnectionMap.remove(context);
        if (sb == null) {
            return;
        }
        context.unbindService(sb);

        if (NConnectionMap.isEmpty()) {
            NService = null;
        }
    }

    public static void clearNotice(int type) {
        if (NService != null) {
            try {
                NService.clearNotice(AppContext.getInstance().getLoginUid(),
                        type);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void requestNotice(Context context) {
        if (NService != null) {
            try {
                TLog.log("requestNotice...");
                NService.requestNotice();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            context.sendBroadcast(new Intent(
                    NoticeService.INTENT_ACTION_REQUEST));
            TLog.log("requestNotice,service is null");
        }
    }

    public static void scheduleNotice() {
        if (NService != null) {
            try {
                NService.scheduleNotice();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public static void tryToShutDown(Context context) {
        if (AppContext.get(AppConfig.KEY_NOTIFICATION_DISABLE_WHEN_EXIT, true)) {
            context.sendBroadcast(new Intent(
                    NoticeService.INTENT_ACTION_SHUTDOWN));
        }
    }


    private static class ServiceBinder implements ServiceConnection{

        ServiceConnection mCallback;

        ServiceBinder(ServiceConnection callback){
            this.mCallback=callback;
        }

        @Override
        public void onServiceConnected(ComponentName className,android.os.IBinder service){
            NService=INoticeService.Stub.asInterface(service);
            if(mCallback!=null){
                mCallback.onServiceConnected(className,service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            if (mCallback != null) {
                mCallback.onServiceDisconnected(className);
            }
            NService = null;
        }

    }

}
