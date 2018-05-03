package com.teacore.teascript.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Comment;
import com.teacore.teascript.bean.Result;
import com.teacore.teascript.bean.ResultData;
import com.teacore.teascript.bean.Teatime;
import com.teacore.teascript.network.OperationResponseHandler;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务服务类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-25
 */

public class TaskService extends IntentService{

    public static final String ACTION_PUB_BLOG_COMMENT = "com.teacore.teascript.ACTION_PUB_BLOG_COMMENT";
    public static final String ACTION_PUB_COMMENT = "com.teacore.teascript.ACTION_PUB_COMMENT";
    public static final String ACTION_PUB_POST = "com.teacore.teascript.ACTION_PUB_POST";
    public static final String ACTION_PUB_TEATIME = "com.teacore.teascript.ACTION_PUB_TEATIME";
    public static final String ACTION_PUB_SOFTWARE_TEATIME ="com.teacore.teascript.ACTION_PUB_SOFTWARE_TEATIME";

    public static final String KEY_ADAPTER = "adapter";

    public static final String BUNDLE_PUB_COMMENT_TASK = "BUNDLE_PUB_COMMENT_TASK";
    public static final String BUNDLE_PUB_POST_TASK = "BUNDLE_PUB_POST_TASK";
    public static final String BUNDLE_PUB_Teatime_TASK = "BUNDLE_PUB_Teatime_TASK";
    public static final String BUNDLE_PUB_SOFTWARE_Teatime_TASK = "BUNDLE_PUB_SOFTWARE_Teatime_TASK";
    public static final String KEY_SOFTID = "soft_id";

    private static final String SERVICE_NAME = "TaskService";
    private static final String KEY_COMMENT = "comment_";
    private static final String KEY_Teatime = "Teatime_";
    private static final String KEY_SOFTWARE_Teatime = "software_Teatime_";
    private static final String KEY_POST = "post_";

    public static List<String> pendingTasks=new ArrayList<String>();

    class PublicCommentResponseHandler extends OperationResponseHandler{

        public PublicCommentResponseHandler(Looper looper,Object... args){
            super(looper,args);
        }

        @Override
        public void onSuccessOperation(int code, ByteArrayInputStream is,Object[] args) throws Exception{
            PublicCommentTask task=(PublicCommentTask) args[0];
            int id=task.getId()*task.getUid();
            ResultData resultData= XmlUtils.toBean(ResultData.class,is);
            Result result=resultData.getResult();
            if(result.OK()){
                Comment comment=resultData.getComment();
                notifySimpleNotification(id,
                        getString(R.string.comment_publish_success),
                        getString(R.string.comment_blog),
                        getString(R.string.comment_publish_success),false,true);
                removePendingTask(KEY_COMMENT+id);
            }else{
                onFailureOperation(100,result.getMessage(),args);
            }

        }

        @Override
        public void onFailureOperation(int code, String errorMessage, Object[] args) {
            PublicCommentTask task = (PublicCommentTask) args[0];
            int id = task.getId() * task.getUid();
            notifySimpleNotification(id,
                    getString(R.string.comment_publish_faile),
                    getString(R.string.comment_blog),
                    code == 100 ? errorMessage
                            : getString(R.string.comment_publish_faile), false,
                    true);
            removePendingTask(KEY_COMMENT + id);
        }

        @Override
        public void onFinish() {
            tryToStopServie();
        }

    }

    class PublicTeatimeResponseHandler extends OperationResponseHandler{
        String key=null;

        public PublicTeatimeResponseHandler(Looper looper, Object... args) {
            super(looper, args);
            key = (String) args[1];
        }

        @Override
        public void onSuccessOperation(int code, ByteArrayInputStream is, Object[] args)
                throws Exception {
            Teatime Teatime = (Teatime) args[0];
            final int id = Teatime.getId();
            Result res = XmlUtils.toBean(ResultData.class, is).getResult();
            if (res.OK()) {
                // 发布成功之后，删除草稿
                AppContext.setTeatimeDraft("");
                notifySimpleNotification(id,
                        getString(R.string.teatime_publish_success),
                        getString(R.string.teatime_public),
                        getString(R.string.teatime_publish_success), false, true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancellNotification(id);
                    }
                }, 3000);
                removePendingTask(key + id);
                if (Teatime.getImageFilePath() != null) {
                    File imgFile = new File(Teatime.getImageFilePath());
                    if (imgFile.exists()) {
                        imgFile.delete();
                    }
                }
            } else {
                onFailureOperation(100, res.getMessage(), args);
            }
        }

        @Override
        public void onFailureOperation(int code, String errorMessage, Object[] args) {
            Teatime Teatime = (Teatime) args[0];
            int id = Teatime.getId();
            notifySimpleNotification(id,
                    getString(R.string.teatime_publish_faile),
                    getString(R.string.teatime_public),
                    code == 100 ? errorMessage
                            : getString(R.string.teatime_publish_faile), false,
                    true);
            removePendingTask(key + id);
        }

        @Override
        public void onFinish() {
            tryToStopServie();
        }
    }

    public TaskService(){
        this(SERVICE_NAME);
    }

    public TaskService(String name){
        super(name);
    }

    private synchronized void tryToStopServie(){
        if(pendingTasks==null||pendingTasks.size()==0){
            stopSelf();
        }
    }

    private synchronized void addPendingTask(String key) {
        pendingTasks.add(key);
    }

    private synchronized void removePendingTask(String key) {
        pendingTasks.remove(key);
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent){
        String action=intent.getAction();

        if(ACTION_PUB_BLOG_COMMENT.equals(action)){
            PublicCommentTask task = intent
                    .getParcelableExtra(BUNDLE_PUB_COMMENT_TASK);
            if (task != null) {
                publicBlogComment(task);
            }
        } else if (ACTION_PUB_COMMENT.equals(action)) {
            PublicCommentTask task = intent
                    .getParcelableExtra(BUNDLE_PUB_COMMENT_TASK);
            if (task != null) {
                publicComment(task);
            }
        } else if (ACTION_PUB_POST.equals(action)) {
            // Post post = intent.getParcelableExtra(BUNDLE_PUBLIC_POST_TASK);
            // if (post != null) {
            // publicPost(post);
            // }
        } else if (ACTION_PUB_TEATIME.equals(action)) {
            Teatime Teatime = intent.getParcelableExtra(BUNDLE_PUB_Teatime_TASK);
            if (Teatime != null) {
                pubTeatime(Teatime);
            }
        } else if (ACTION_PUB_SOFTWARE_TEATIME.equals(action)) {
            Teatime Teatime = intent
                    .getParcelableExtra(BUNDLE_PUB_SOFTWARE_Teatime_TASK);
            int softid = intent.getIntExtra(KEY_SOFTID, -1);
            if (Teatime != null && softid != -1) {
                pubSoftwareTeatime(Teatime, softid);
            }
        }
    }

    private void publicBlogComment(final PublicCommentTask task) {
        int id = task.getId() * task.getUid();
        addPendingTask(KEY_COMMENT + id);

        notifySimpleNotification(id, getString(R.string.comment_publishing),
                getString(R.string.comment_blog),
                getString(R.string.comment_publishing), true, false);

        TeaScriptApi.pubBlogComment(task.getId(), task.getUid(), task
                .getContent(), new PublicCommentResponseHandler(
                getMainLooper(), task, true));
    }

    private void publicComment(final PublicCommentTask task) {
        int id = task.getId() * task.getUid();
        addPendingTask(KEY_COMMENT + id);

        notifySimpleNotification(id, getString(R.string.comment_publishing),
                getString(R.string.comment_blog),
                getString(R.string.comment_publishing), true, false);

        TeaScriptApi.pubComment(task.getCatalog(), task.getId(),
                task.getUid(), task.getContent(), task.getIsPostToMyZone(),
                new PublicCommentResponseHandler(getMainLooper(), task, false));
    }

    /*
    // private void publicPost(Post post) {
    // post.setId((int) System.currentTimeMillis());
    // int id = post.getId();
    // addPenddingTask(KEY_POST + id);
    // notifySimpleNotifycation(id, getString(R.string.post_publishing),
    // getString(R.string.post_public),
    // getString(R.string.post_publishing), true, false);
    // OSChinaApi.publicPost(post, new
    // PublicPostResponseHandler(getMainLooper(),
    // post));
    // }
    */

    private void pubTeatime(final Teatime Teatime) {

        Teatime.setId((int) System.currentTimeMillis());

        int id = Teatime.getId();

        addPendingTask(KEY_Teatime + id);

        notifySimpleNotification(id, getString(R.string.teatime_publishing),
                getString(R.string.teatime_public),
                getString(R.string.teatime_publishing), true, false);

        TeaScriptApi.pubTeatime(Teatime, new PublicTeatimeResponseHandler(
                getMainLooper(), Teatime, KEY_Teatime));

    }

    private void pubSoftwareTeatime(final Teatime Teatime, int softid) {
        Teatime.setId((int) System.currentTimeMillis());
        int id = Teatime.getId();
        addPendingTask(KEY_SOFTWARE_Teatime + id);
        notifySimpleNotification(id, getString(R.string.teatime_publishing),
                getString(R.string.teatime_public),
                getString(R.string.teatime_publishing), true, false);
        TeaScriptApi.pubSoftWareTeatime(Teatime, softid,
                new PublicTeatimeResponseHandler(getMainLooper(), Teatime,
                        KEY_SOFTWARE_Teatime));
    }

    private void notifySimpleNotification(int id, String ticker, String title,
                                          String content, boolean ongoing, boolean autoCancel) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(
                        PendingIntent.getActivity(this, 0, new Intent(), 0))
                .setSmallIcon(R.drawable.icon_notification);

        // if (AppContext.isNotificationSoundEnable()) {
        // builder.setDefaults(Notification.DEFAULT_SOUND);
        // }

        Notification notification = builder.build();

        NotificationManagerCompat.from(this).notify(id, notification);
    }

    private void cancellNotification(int id) {
        NotificationManagerCompat.from(this).cancel(id);
    }

}
