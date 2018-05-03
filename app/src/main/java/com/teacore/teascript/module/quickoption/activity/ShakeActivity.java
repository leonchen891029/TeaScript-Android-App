package com.teacore.teascript.module.quickoption.activity;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.teacore.teascript.R;
import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.base.BaseActivity;
import com.teacore.teascript.bean.ShakeObject;
import com.teacore.teascript.network.remote.TeaScriptApi;
import com.teacore.teascript.util.AnimationsUtils;
import com.teacore.teascript.util.GlideImageLoader;
import com.teacore.teascript.util.StringUtils;
import com.teacore.teascript.util.TimeUtils;
import com.teacore.teascript.util.TypefaceUtils;
import com.teacore.teascript.util.UiUtils;
import com.teacore.teascript.util.XmlUtils;

import java.io.ByteArrayInputStream;

import cz.msebera.android.httpclient.Header;

/**
 * 摇一摇Activity
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-21
 */

public class ShakeActivity extends BaseActivity implements SensorEventListener{

    private SensorManager mSensorManager=null;
    private Vibrator mVibrator=null;

    private boolean isRequest=false;

    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdateTime;
    //SPEED_SHRESHOLD越大你需要越大的力气来摇晃手机
    private static final int SPEED_SHRESHOLD=45;
    private static final int UPDATE_INTERVAL_TIME=50;

    private ImageView mShakeIV;
    private ProgressBar mShakePB;
    private LinearLayout mBottomLL;
    private ImageView mFaceIV;
    private TextView mTitleTV;
    private TextView mDescriptionTV;
    private TextView mAuthorTV;
    private TextView mDateTV;
    private TextView mCommentCountTV;


    @Override
    protected  boolean hasBackButton(){
        return true;
    }

    @Override
    protected int getLayoutId(){
       return R.layout.activity_shake;
    }

    @Override
    public void initView(){
        mShakeIV=(ImageView) findViewById(R.id.shake_iv);
        mShakePB=(ProgressBar) findViewById(R.id.shake_pb);
        mBottomLL=(LinearLayout) findViewById(R.id.shake_bottom_ll);
        mFaceIV=(ImageView) findViewById(R.id.face_iv);
        mTitleTV=(TextView) findViewById(R.id.title_tv);
        mDescriptionTV=(TextView) findViewById(R.id.description_tv);
        mAuthorTV=(TextView) findViewById(R.id.author_tv);
        mDateTV=(TextView) findViewById(R.id.time_tv);
        mCommentCountTV=(TextView) findViewById(R.id.comment_count_tv);
    }

    @Override
    public void initData(){
        mSensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mVibrator=(Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    }

    //成功摇动手机以后被调用
    private void onShake(){
        final RequestManager mImageLoader= Glide.with(this);
        isRequest=true;
        mShakePB.setVisibility(View.VISIBLE);
        Animation shakeAnimation= AnimationsUtils.shakeAnimation(mShakeIV.getLeft());

        shakeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TeaScriptApi.shake(new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        isRequest=false;
                        final ShakeObject shakeObject= XmlUtils.toBean(ShakeObject.class,new ByteArrayInputStream(bytes));

                        if (shakeObject != null) {
                            if (StringUtils.isEmpty(shakeObject.getAuthor())
                                    && StringUtils.isEmpty(shakeObject
                                    .getCommentCount())
                                    && StringUtils.isEmpty(shakeObject.getPubDate())) {
                                shakeFail();
                            } else {
                                mBottomLL.setVisibility(View.VISIBLE);
                                mBottomLL
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                UiUtils.showUrlShake(ShakeActivity.this, shakeObject);
                                            }
                                        });
                                GlideImageLoader.loadImage(mImageLoader,mFaceIV,shakeObject.getImage(),R.drawable.widget_dface);
                                mTitleTV.setText(shakeObject.getTitle());
                                mDescriptionTV.setText(shakeObject.getDetail());
                                mAuthorTV.setText(shakeObject.getAuthor());
                                TypefaceUtils.setTypeface(mAuthorTV);
                                TypefaceUtils.setTypefaceWithText(mCommentCountTV, R.string
                                        .fa_comments, shakeObject.getCommentCount() + "");
                                TypefaceUtils.setTypefaceWithText(mDateTV, R.string.fa_clock_o,
                                        TimeUtils.friendly_time(shakeObject.getPubDate()));
                            }
                        } else {
                            shakeFail();
                        }

                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        isRequest=false;

                        shakeFail();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mShakePB != null) {
                            mShakePB.setVisibility(View.GONE);
                        }
                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mShakeIV.startAnimation(shakeAnimation);
    }

    private void shakeFail() {
        AppContext.showToast("你没有摇出任何东西。。");
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSensorManager != null) {
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor != null) {
                mSensorManager.registerListener(this, sensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - lastUpdateTime;
        if (timeInterval < UPDATE_INTERVAL_TIME) {
            return;
        }
        lastUpdateTime = currentUpdateTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        lastX = x;
        lastY = y;
        lastZ = z;

        double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ) / timeInterval) * 100;
        if (speed >= SPEED_SHRESHOLD && !isRequest) {
            mBottomLL.setVisibility(View.GONE);
            mVibrator.vibrate(300);
            onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {

    }




}
