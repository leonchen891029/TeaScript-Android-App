package com.teacore.teascript.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppConfig;
import com.teacore.teascript.interfaces.OnDownloadResultListener;
import com.teacore.teascript.module.main.MainActivity;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TDevice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载服务类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-13
 */
@SuppressLint("all")
public class DownloadService extends Service{

    public static final String BUNDLE_KEY_DOWNLOAD_URL="download_url";
    public static final String BUNDLE_KEY_TITLE="title";

    private final String tag="download";
    private static final int NOTIFY_ID=0;
    private int progress;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private boolean canceled;

    private String downloadUrl;
    private String mTitle;
    private String saveFilePath= AppConfig.DEFAULT_SAVE_FILE_PATH;
    private OnDownloadResultListener callbackResult;
    private DownloadBinder binder;
    private boolean isServiceDestroy=false;
    private Context mContext=this;
    private Thread downloadThread;

    @Override
    public void onCreate(){
        super.onCreate();
        mNotificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //下载完毕
                    mNotificationManager.cancel(NOTIFY_ID);
                    installApk();
                    break;
                case 1:
                    int rate=msg.arg1;
                    if(rate<100) {
                        RemoteViews views = mNotification.contentView;
                        views.setTextViewText(R.id.download_state_tv, mTitle + "(" + rate
                                + "%" + ")");
                        views.setProgressBar(R.id.download_pb, 100, rate, false);
                    }else {
                        //已经下载完毕更换通知样式
                        mNotification.flags=Notification.FLAG_AUTO_CANCEL;
                        mNotification.contentView=null;
                        //告知下载已经完成
                        Intent intent=new Intent(mContext, MainActivity.class);
                        intent.putExtra("completed","yes");
                        //更新参数
                        PendingIntent contentIntent=PendingIntent.getActivity(mContext,
                                0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        isServiceDestroy=true;
                        //停掉自身服务
                        stopSelf();
                    }
                    mNotificationManager.notify(NOTIFY_ID,mNotification);
                    break;
                case 2:
                    //取消通知
                    mNotificationManager.cancel(NOTIFY_ID);
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent){
        downloadUrl=intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_URL);
        saveFilePath=saveFilePath+getSaveFileName(downloadUrl);
        mTitle=String.format(mTitle,intent.getStringExtra(BUNDLE_KEY_TITLE));
        return binder;
    }

    private String getSaveFileName(String downloadUrl){
        if (downloadUrl == null || StringUtils.isEmpty(downloadUrl)) {
            return "";
        }
        return downloadUrl.substring(downloadUrl.lastIndexOf("/"));
    }

    private void startDownload(){
        canceled=false;
        download();
    }

    //创建通知
    private void setupNotification(){
        int icon=R.drawable.icon_notification;
        CharSequence hintText="准备下载";
        long when=System.currentTimeMillis();
        mNotification=new Notification(icon,hintText,when);

        //放置在"正在运行"栏目中
        mNotification.flags=Notification.FLAG_ONGOING_EVENT;
        RemoteViews contentView=new RemoteViews(getPackageName(),R.layout.notification_download_show);
        contentView.setTextViewText(R.id.download_state_tv,mTitle);
        //为Notification指定自定义视图
        mNotification.contentView=contentView;

        Intent intent=new Intent(mContext,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //设置Intent
        mNotification.contentIntent=pendingIntent;
        mNotificationManager.notify(NOTIFY_ID,mNotification);
    }

    //下载Apk
    private void download(){
        downloadThread=new Thread(mDownloadRunnable);
        downloadThread.start();
    }

    private Runnable mDownloadRunnable=new Runnable() {
        @Override
        public void run() {
            File file=new File(AppConfig.DEFAULT_SAVE_FILE_PATH);
            if(!file.exists()){
                file.mkdirs();
            }
            File saveFile=new File(saveFilePath);
            try{
                downloadUpdateFile(downloadUrl,saveFile);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };

    //安装Apk
    private void installApk(){
        File apkfile = new File(saveFilePath);
        if (!apkfile.exists()) {
            return;
        }
        TDevice.installAPK(mContext, apkfile);
    }

    public long downloadUpdateFile(String downloadUrl,File saveFile) throws Exception{
        int  downloadCount=0;
        int  currentSize=0;
        long totalSize=0;
        int  updateTotalSize=0;

        HttpURLConnection httpURLConnection=null;
        InputStream inputStream=null;
        FileOutputStream fos=null;

        try{
            URL url=new URL(downloadUrl);
            httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("User-Agent","PacificHttpClient");
            if(currentSize>0){
                httpURLConnection.setRequestProperty("RANGE","bytes="+currentSize+"-");
            }
            updateTotalSize=httpURLConnection.getContentLength();

            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(20000);
            if(httpURLConnection.getResponseCode()==404){
                throw new Exception("fail!");
            }

            inputStream=httpURLConnection.getInputStream();
            fos=new FileOutputStream(saveFile,false);
            byte[] buffer=new byte[1024];
            int readSize=0;
            while((readSize=inputStream.read(buffer))>0){
                fos.write(buffer,0,readSize);
                totalSize+=readSize;

                //为了防止频繁的通知，百分比每增加10才通知一次
                if ((downloadCount == 0)
                        || (int) (totalSize * 100 / updateTotalSize) - 4 > downloadCount) {
                    downloadCount += 4;
                    // 更新进度
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = downloadCount;
                    mHandler.sendMessage(msg);
                    if (callbackResult != null)
                        callbackResult.OnBackResult(progress);
                }

            }

            //下载完成通知安装
            mHandler.sendEmptyMessage(0);

            //设置canceled
            canceled=true;
        }finally{
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (fos != null) {
                fos.close();
            }
        }

        return totalSize;
    }

    public class DownloadBinder extends Binder {
        public void start() {
            if (downloadThread == null || !downloadThread.isAlive()) {
                progress = 0;
                setupNotification();
                new Thread() {
                    public void run() {
                        // 下载
                        startDownload();
                    }

                    ;
                }.start();
            }
        }

        public void cancel() {
            canceled = true;
        }

        public int getProgress() {
            return progress;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public boolean isServiceDestroy() {
            return isServiceDestroy;
        }

        public void cancelNotification() {
            mHandler.sendEmptyMessage(2);
        }

        public void addCallback(OnDownloadResultListener callbackResult) {
            DownloadService.this.callbackResult = callbackResult;
        }
    }


}
