package com.teacore.teascript.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.teacore.teascript.R;
import com.teacore.teascript.util.TDevice;

/**
 * 自定义toast
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-3
 */
public class TSToast {
       public static final long DURATION_LONG=5000L;

       public static final long DURATION_MEDIUM=3500L;

       public static final long DURATION_SHORT=2500L;

       private long duration=3500L;

       private ToastView toastView;

       public TSToast(Activity activity){
           init(activity);
       }

       public TSToast(Activity activity, String message, int icon, String action, int actionIcon, long l){
           duration=l;
           init(activity);
           setMessage(message);
           setMessageIcon(icon);
           setAction(action);
           setActionIcon(actionIcon);
       }

       private void init(Activity activity){
           toastView=new ToastView(activity);
           setLayoutGravity(81);
       }

       public void setDuration(long l){
           duration=l;
       }

       public long getDuration(){
           return duration;
       }

       public void setAction(String string){
           toastView.actionTV.setText(string);
       }

       public void setActionIcon(int i){
           toastView.actionIcon.setImageResource(i);
       }

       public void setMessage(String string){
           toastView.messageTV.setText(string);
       }

       public void setMessageIcon(int i){
           toastView.messageIcon.setImageResource(i);
       }

       public void setLayoutGravity(int i){

           if(i !=0){

               FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(-2,-2);

               params.gravity=i;

               int j=(int) TDevice.dpToPixels(16F);

               params.setMargins(j,j,j,j);

               toastView.setLayoutParams(params);
           }

       }

    public void show() {

        //得到toastView的ViewGroup
        final ViewGroup content = (ViewGroup) ((Activity) toastView.getContext())
                .findViewById(android.R.id.content);

        if (content != null) {

            ObjectAnimator.ofFloat(toastView, "alpha", 0.0F).setDuration(0L)
                    .start();

            content.addView(toastView);

            ObjectAnimator.ofFloat(toastView, "alpha", 0.0F, 1.0F)
                    .setDuration(167L).start();

            toastView.postDelayed(new Runnable() {

                @Override
                public void run() {

                    ObjectAnimator animator = ObjectAnimator.ofFloat(toastView,
                            "alpha", 1.0F, 0.0F);
                    animator.setDuration(100L);
                    animator.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            content.removeView(toastView);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }
                    });
                    animator.start();
                }
            }, duration);
        } else {

        }
    }

    private class ToastView  extends FrameLayout {

        public ImageView actionIcon;
        public TextView actionTV;
        public ImageView messageIcon;
        public TextView messageTV;

        public ToastView(Context context) {
            this(context, null);
        }

        public ToastView(Context context, AttributeSet attributeset) {
            this(context, attributeset, 0);
        }

        public ToastView(Context context, AttributeSet attributeset, int i) {
            super(context, attributeset, i);
            init();
        }

        private void init() {

            //init的时候将该layout下面的view_base_toast绑定到这个ToastView上面
            LayoutInflater.from(getContext()).inflate(
                    R.layout.view_base_toast, this, true);

            messageTV = (TextView) findViewById(R.id.title_tv);

            messageIcon = (ImageView) findViewById(R.id.icon_iv);

            actionTV = (TextView) findViewById(R.id.title_tv);

        }
    }


}
