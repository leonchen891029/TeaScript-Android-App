package com.teacore.teascript.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.R;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.bean.Constants;
import com.teacore.teascript.bean.Notice;
import com.teacore.teascript.bean.NoticeDetail;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.broadcast.NoticeReceiver;
import com.teacore.teascript.module.main.MainActivity;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;

import cz.msebera.android.httpclient.Header;

/**
 * 消息服务类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-20
 */

public class NoticeService extends Service {

    public static final String INTENT_ACTION_GET="com.teacore.teascript.service.get_notice";
    public static final String INTENT_ACTION_CLEAR = "com.teacore.teascript.service.clear_notice";
    public static final String INTENT_ACTION_BROADCAST = "com.teacore.teascript.service.broadcast";
    public static final String INTENT_ACTION_SHUTDOWN = "com.teacore.teascript.service.shutdown";
    public static final String INTENT_ACTION_REQUEST = "com.teacore.teascript.service.request";
    public static final String BUNDLE_KEY_NS = "bundle_key_type";

    private static final long INTERVAL=1000*120;
    private AlarmManager mAlarmManager;

    private Notice mNotice;

    private final BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(Constants.INTENT_ACTION_NOTICE)){
                Notice notice=(Notice) intent.getSerializableExtra("notice_bean");
                int atmeCount=notice.getAtmeCount();
                int msgCount=notice.getMsgCount();
                int commentCount=notice.getCommentCount();
                int newFansCount=notice.getNewFansCount();
                int newLikeCount=notice.getNewLikeCount();
                int activeCount=atmeCount+msgCount+commentCount+newFansCount+newLikeCount;

                if(activeCount==0){
                    NotificationManagerCompat.from(NoticeService.this).cancel(R.string.you_have_news_messages);
                }
            }else if(action.equals(INTENT_ACTION_BROADCAST)){
                if(mNotice!=null){
                    UiUtils.sendBroadcastForNotice(NoticeService.this,mNotice);
                }
            }else if(action.equals(INTENT_ACTION_SHUTDOWN)){
                stopSelf();
            }else if(action.equals(INTENT_ACTION_REQUEST)){
                requestNotice();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        mAlarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);

        startRequestAlarm();

        requestNotice();

        IntentFilter filter=new IntentFilter(Constants.INTENT_ACTION_NOTICE);
        filter.addAction(INTENT_ACTION_BROADCAST);
        filter.addAction(INTENT_ACTION_SHUTDOWN);
        filter.addAction(INTENT_ACTION_REQUEST);
        registerReceiver(mReceiver,filter);
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy(){
        cancelRequestAlarm();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void startRequestAlarm(){
        cancelRequestAlarm();

        //从一秒后开始，每隔两分钟执行getOperationIntent()
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+1000,INTERVAL,getOperationIntent());
    }

    private void cancelRequestAlarm(){
        mAlarmManager.cancel(getOperationIntent());
    }

    /*
    采用轮询的方式来实现消息的推送:
    每次都去调用AlarmReceiver onReceiver()方法
     */
    private PendingIntent getOperationIntent(){
        Intent intent=new Intent(this,NoticeReceiver.class);
        PendingIntent operationIntent=PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return operationIntent;
    }

    private void clearNotice(int uid,int type){
        TeaScriptApi.clearNotice(uid,type,mClearNoticeHandler);
    }

    private int lastNotifyCount;

    private void getNotification(Notice notice){
        int atmeCount=notice.getAtmeCount();
        int msgCount=notice.getMsgCount();
        int commentCount=notice.getCommentCount();
        int newFansCount=notice.getNewFansCount();
        int newLikeCount=notice.getNewLikeCount();

        int activeCount=atmeCount+msgCount+commentCount+newFansCount+newLikeCount;

        if(activeCount==0){
            lastNotifyCount=0;
            NotificationManagerCompat.from(this).cancel(R.string.you_have_news_messages);
            return;
        }

        if(activeCount==lastNotifyCount)
            return;

        lastNotifyCount=activeCount;

        Resources res=getResources();

        String contentTitle=res.getString(R.string.you_have_news_messages,activeCount);

        String contentText;
        StringBuffer contentSB=new StringBuffer();
        if(atmeCount>0){
            contentSB.append(getString(R.string.atme_count,atmeCount)).append("");
        }
        if (msgCount > 0) {
            contentSB.append(getString(R.string.msg_count, msgCount)).append(" ");
        }
        if (commentCount > 0) {
            contentSB.append(getString(R.string.comment_count, commentCount))
                    .append(" ");
        }
        if (newFansCount > 0) {
            contentSB.append(getString(R.string.fans_count, newFansCount));
        }
        if (newLikeCount > 0) {
            contentSB.append(getString(R.string.like_count, newLikeCount));
        }
        contentText = contentSB.toString();

        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("NOTICE",true);

        PendingIntent pi=PendingIntent.getActivity(this,1000,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setTicker(contentTitle).setContentTitle(contentTitle).setContentText(contentText)
                .setAutoCancel(true).setContentIntent(pi).setSmallIcon(R.drawable.icon_notification);

        if(AppContext.get(AppConfig.KEY_NOTIFICATION_SOUND,true)){
            builder.setSound(Uri.parse("android.resource://"
                        +AppContext.getInstance().getPackageName()+"/"
                        +R.raw.notificationsound));
        }

        if(AppContext.get(AppConfig.KEY_NOTIFICATION_VIBRATION,true)){
            long[] vibrate={0,10,20,30};
            builder.setVibrate(vibrate);
        }

        Notification notification=builder.build();

        NotificationManagerCompat.from(this).notify(R.string.you_have_news_messages,notification);

    }

    private final AsyncHttpResponseHandler mGetNoticeHandler=new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            try{
                Notice notice= XmlUtils.toBean(NoticeDetail.class,bytes).getNotice();

                if(notice!=null){
                    UiUtils.sendBroadcastForNotice(NoticeService.this,notice);
                    if (AppContext.get(AppConfig.KEY_NOTIFICATION_ACCEPT, true)) {
                        getNotification(notice);
                    }
                    mNotice = notice;
                }


            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        }
    };

    private final AsyncHttpResponseHandler mClearNoticeHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                ResultData rsb = XmlUtils.toBean(ResultData.class,
                        new ByteArrayInputStream(arg2));
                Result res = rsb.getResult();
                if (res.OK() && rsb.getNotice() != null) {
                    mNotice = rsb.getNotice();
                    UiUtils.sendBroadcastForNotice(NoticeService.this, rsb.getNotice());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {}    };

    //请求是否有新通知
    private void requestNotice() {
        TeaScriptApi.getNotices(mGetNoticeHandler);
    }

    private static class ServiceStub extends INoticeService.Stub{

        WeakReference<NoticeService> mService;

        ServiceStub(NoticeService service) {
            mService = new WeakReference<NoticeService>(service);
        }

        @Override
        public void clearNotice(int uid, int type) throws RemoteException {
            mService.get().clearNotice(uid, type);
        }

        @Override
        public void scheduleNotice() throws RemoteException {
            mService.get().startRequestAlarm();
        }

        @Override
        public void requestNotice() throws RemoteException {
            mService.get().requestNotice();
        }

    }

    private final IBinder mBinder = new ServiceStub(this);

}