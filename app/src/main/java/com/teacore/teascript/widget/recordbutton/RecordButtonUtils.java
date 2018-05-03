package com.teacore.teascript.widget.recordbutton;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.util.StringUtils;

/**RecordButton需要的工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-12
 */

public class RecordButtonUtils {
    private static final String TAG="AudioUtils";
    //录音音频保存的根目录
    public static  final String AUDIO_DIR= Environment.getExternalStorageDirectory().getAbsolutePath()+"/teacore/audio";
    //要播放的声音的路径
    private String mAudioPath;
    //是否正在录音
    private boolean mIsRecording;
    //是否正在播放
    private  boolean mIsPlaying;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private OnPlayListener listener;

    //判断是否正在播放
    public boolean isPlaying(){
        return mIsPlaying;
    }

    //设置要播放声音的路径
    public void setAudioPath(String path){
        this.mAudioPath=path;
    }

    public void setOnPlayListener(OnPlayListener playListenr){
        this.listener=playListenr;
    }

    //初始化录音器
    private void initRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(mAudioPath);
        mIsRecording = true;
    }

    //开始录音，并保存到文件中
    public void recordAudio() {

        initRecorder();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            AppContext.showToast("小屁孩不听你说话了,请返回重试");
        }

    }

    //获取录音的音量,getMaxAmplitude是需要间隔一段时间调用一次的，需要放在线程里面调用，第一次调用会返回0
    public int getVolumn(){
        int volumn=0;

        if(mediaRecorder!=null && mIsRecording){
            volumn=mediaRecorder.getMaxAmplitude();
            //将振幅转换为分贝
            if (volumn != 0)
                volumn = (int) (10 * Math.log(volumn) / Math.log(10)) / 5;
        }

        return volumn;
    }

    //停止录音
    public void stopRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            mIsRecording = false;
        }
    }

    //播放器停止播放
    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mIsPlaying = false;
            if (listener != null) {
                listener.stopPlay();
            }
        }
    }

    public void startPlay(String audioPath, TextView timeView) {

        if (!mIsPlaying) {

            if (!StringUtils.isEmpty(audioPath)) {

                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(audioPath);
                    mediaPlayer.prepare();

                    if (timeView != null) {

                        int len = (mediaPlayer.getDuration() + 500) / 1000;
                        timeView.setText(len + "s");
                    }

                    mediaPlayer.start();

                    if (listener != null) {
                        listener.starPlay();
                    }

                    mIsPlaying = true;

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopPlay();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                AppContext.showToast(R.string.record_sound_notfound);
            }
        } else {
            stopPlay();
        } // end playing
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        startPlay(mAudioPath, null);
    }


    //播放接口
    public interface OnPlayListener {

        //停止播放声音时调用
        void stopPlay();

        //播放声音是调用
        void starPlay();

    }



}
