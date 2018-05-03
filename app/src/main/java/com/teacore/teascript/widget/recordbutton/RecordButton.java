package com.teacore.teascript.widget.recordbutton;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.util.AnimationsUtils;
import com.teacore.teascript.widget.recordbutton.RecordButtonUtils.OnPlayListener;

import java.io.File;
import java.lang.ref.WeakReference;

/**录音Button，可以弹出自定义录音Dialog，需要配合RecordButtonUtils使用
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-19
 */

public class RecordButton extends RelativeLayout{
    //录音最短的时间(毫秒)
    private static final int MIN_RECORD_TIME=900;
    //录音最长的事件(秒)
    private static final int MAX_RECORD_TIME=60;
    //用作handler标识
    private static final int HANDLE_FLAG = 33333721;

    private ImageView mPlayIV;
    private ImageView mListenIV;
    private ImageView mDeleteIV;
    private TextView  mRecordTimeTV;
    private ImageView mVolumeIV;

    private View mBottomFlagView;
    private View mTopFlagView;

    //左边界值，mListenIV右值
    private int mLeftButtonX=0;
    //右边界值，mDeleteIV的左值
    private int mRightButtonX=0;

    //录音起始时间
    private long mStartTime;
    private String mAudioFile=null;
    //手指抬起或者划出时判断主动取消录音
    private boolean mIsCancel=false;
    //手指是否按在录音按钮上
    private boolean mTouchInPlayButton=false;

    private OnFinishedRecordListener mFinishedListener;

    private RecordButtonUtils mAudioUtils;

    private ObtainDecibelThread mThread;
    //用于更新录音音量大小的图片
    private Handler mVolumeHandler;

    public RecordButton(Context context){
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        init();
    }

    public RecordButton(Context context,AttributeSet attributeSet,int defStyle){
        super(context,attributeSet,defStyle);
        init();
    }

    private void init(){

        mVolumeHandler=new ShowVolumeHandler(this);

        mAudioUtils=new RecordButtonUtils();

        initSavePath();
        LayoutInflater.from(getContext()).inflate(R.layout.view_record,this);

        mPlayIV=(ImageView) findViewById(R.id.recordview_start);
        mListenIV=(ImageView) findViewById(R.id.recordview_listen);
        mDeleteIV=(ImageView) findViewById(R.id.recordview_delete);
        mTopFlagView=findViewById(R.id.recordview_layout);
        mBottomFlagView=findViewById(R.id.recordview_text);
        mRecordTimeTV=(TextView) findViewById(R.id.recordview_text_time);
        mVolumeIV=(ImageView) findViewById(R.id.recordview_img_volume);

        initPlayButtonEvent();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAudioFile == null) {
            return false;
        }
        if (!mTouchInPlayButton) {
            return false;
        }
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                initBorderLine();
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    viewToInit();
                    break;
                }
                if (event.getX() > mRightButtonX) {
                    mIsCancel = true;
                    scaleView(mDeleteIV, 1.5f);
                } else if (event.getX() < mLeftButtonX) {
                    scaleView(mListenIV, 1.5f);
                } else {
                    mIsCancel = false;
                    viewToInit();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mIsCancel || event.getY() < -50) {
                    cancelRecord();
                } else if (event.getX() < mLeftButtonX) {// 试听
                    playRecord();
                    finishRecord();
                } else if (event.getX() > mRightButtonX) {// 删除
                    cancelRecord();
                } else {
                    finishRecord();
                }
                viewToInit();
                mBottomFlagView.setVisibility(View.VISIBLE);
                mTopFlagView.setVisibility(View.GONE);
                mIsCancel = false;
                mTouchInPlayButton = false;
                break;
        }
        return true;
    }

    //初始化三个主要的功能
    private void initPlayButtonEvent() {
        mDeleteIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRecord();
            }
        });

        mListenIV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecord();
            }
        });

        mPlayIV.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mPlayIV.startAnimation(AnimationsUtils.clickAnimation(0.8f,
                            400));
                    initRecorder();
                    mBottomFlagView.setVisibility(View.GONE);
                    mRecordTimeTV.setText("0\"");
                    mTopFlagView.setVisibility(View.VISIBLE);
                    mTouchInPlayButton = true;
                }
                return false;
            }
        });
    }

    //通过listenIV和deleteIV的坐标值来更新leftButtonX和rightButtonX这两个值
    private void initBorderLine() {
        int[] xy = new int[2];
        mListenIV.getLocationInWindow(xy);
        mLeftButtonX = xy[0] + mListenIV.getWidth();
        mDeleteIV.getLocationInWindow(xy);
        mRightButtonX = xy[0];
    }

    private void viewToInit() {
        scaleView(mListenIV, 1f);
        scaleView(mDeleteIV, 1f);
    }

    private void scaleView(View view, float scaleXY) {

            view.setScaleX(scaleXY);

            view.setScaleY(scaleXY);
    }

    private void changeVolume(int volume) {
        switch (volume) {
            case 0:
            case 1:
            case 2:
                mVolumeIV.setImageResource(R.drawable.icon_audio0);
                break;
            case 3:
                mVolumeIV.setImageResource(R.drawable.icon_audio1);
                break;
            case 4:
                mVolumeIV.setImageResource(R.drawable.icon_audio2);
                break;
            case 5:
                mVolumeIV.setImageResource(R.drawable.icon_audio3);
                break;
        }
    }

    //调用该方法设置录音文件存储点
    private void initSavePath() {

        mAudioFile = RecordButtonUtils.AUDIO_DIR;

        File file = new File(mAudioFile);

        if (!file.exists()) {
            file.mkdirs();
        }
        mAudioFile += File.separator + System.currentTimeMillis() + ".amr";
    }

    //初始化录音器
    private void initRecorder() {
        mStartTime = System.currentTimeMillis();
        startRecording();
    }

    //获取已经录音的时间(秒)
    private int getRecordTime() {
        return (int) ((System.currentTimeMillis() - mStartTime) / 1000);
    }

    /*
    录音完成
    1.达到最长录音时间
    2.用户决定录音完成
     */
    private void finishRecord() {
        //停止录音
        stopRecording();

        long intervalTime = System.currentTimeMillis() - mStartTime;

        if (intervalTime < MIN_RECORD_TIME) {

            AppContext.showToast(R.string.record_sound_short);
            File file = new File(mAudioFile);

            file.delete();

            if (mFinishedListener != null) {
                mFinishedListener.onCancleRecord();
            }
            return;
        } else {
            if (mFinishedListener != null) {
                mFinishedListener.onFinishedRecord(mAudioFile, getRecordTime());
            }
        }
    }

    //用户取消录音
    private void cancelRecord() {
        stopRecording();
        File file = new File(mAudioFile);
        file.delete();
        if (mFinishedListener != null) {
            mFinishedListener.onCancleRecord();
        }
    }

    // 开始录音
    private void startRecording() {
        mAudioUtils.setAudioPath(mAudioFile);
        mAudioUtils.recordAudio();
        mThread = new ObtainDecibelThread();
        mThread.start();

    }

    // 停止录音
    private void stopRecording() {
        if (mThread != null) {
            mThread.exit();
            mThread = null;
        }
        if (mAudioUtils != null) {
            mAudioUtils.stopRecord();
        }
    }

    //返回录音工具
    public RecordButtonUtils getAudioUtils() {
        return mAudioUtils;
    }

    public void playRecord() {
        mAudioUtils.startPlay();
    }

    //获取最近一次录音的文件路径
    public String getCurrentAudioPath() {
        return mAudioFile;
    }

    //设置要播放的声音的路径
    public void setAudioPath(String path) {
        mAudioUtils.setAudioPath(path);
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        mAudioUtils.startPlay();
    }

    /**
     * 删除当前文件
     */
    public void delete() {
        File file = new File(mAudioFile);
        file.delete();
    }

    //结束录音的监听器
    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        mFinishedListener = listener;
    }

    //播放结束监听器
    public void setOnPlayListener(OnPlayListener l) {
        mAudioUtils.setOnPlayListener(l);
    }


    //内部线程类，用来监听分贝的变化
    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {

            while (running) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Message msg = mVolumeHandler.obtainMessage();

                msg.what = HANDLE_FLAG;
                msg.arg1 = getRecordTime();

                if (mAudioUtils != null && running) {
                    // 如果用户仍在录音
                    msg.arg2 = mAudioUtils.getVolumn();
                } else {
                    exit();
                }

                mVolumeHandler.sendMessage(msg);
            }

        }
    }

    static class ShowVolumeHandler extends Handler {
        private final WeakReference<RecordButton> mOuterInstance;

        public ShowVolumeHandler(RecordButton outer) {
            mOuterInstance = new WeakReference<RecordButton>(outer);
        }

        @Override
        public void handleMessage(Message msg) {

            RecordButton outerButton = mOuterInstance.get();
            if (msg.what == HANDLE_FLAG) {
                if (msg.arg1 > MAX_RECORD_TIME) {
                    outerButton.finishRecord();
                } else {
                    outerButton.changeVolume(msg.arg2);
                    outerButton.mRecordTimeTV.setText(msg.arg1 + "\"");
                }
            }
        }
    }

    public interface OnFinishedRecordListener {
        /** 用户手动取消 */
        public void onCancleRecord();

        /** 录音完成 */
        public void onFinishedRecord(String audioPath, int recordTime);
    }


}
