package com.teacore.teascript.widget;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.teacore.teascript.R;

/**
 * 自定义Switch开关
 * Created by apple on 17/12/10.
 */

public class TSSwitchButton extends View {

    private float radius;

    //开启状态颜色
    private int onColor= Color.parseColor("#4ebb7f");
    //关闭状态颜色
    private int offColor=Color.parseColor("#ffffff");
    //关闭状态下的边框颜色
    private int offBorderColor=Color.parseColor("#dadbda");
    //手柄颜色
    private int spotColor = Color.parseColor("#ffffff");
    //边框颜色
    private int borderColor=offBorderColor;

    private Paint mPaint;
    //开关状态
    private boolean switchState=false;
    //边框大小
    private int borderWidth=2;
    //垂直中心
    private float centerY;
    //按钮的开始和结束的位置
    private float startX,endX;
    //手柄X坐标的最小和最大值
    private float spotMinX,spotMaxX;
    //手柄大小
    private int spotSize;
    //手柄X位置
    private float spotX;
    //关闭时内部灰色带的宽度
    private float offLineWidth;

    private RectF rect=new RectF();

    private OnSwitchChanged mListener;

    public TSSwitchButton(Context context){
        super(context);
    }

    public TSSwitchButton(Context context, AttributeSet attrs){
        super(context,attrs);
        setup(attrs);
    }

    public TSSwitchButton(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs){

        mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               beginSwitch();
            }
        });

        TypedArray typedArray=getContext().obtainStyledAttributes(attrs, R.styleable.TSSwitchButton);
        onColor=typedArray.getColor(R.styleable.TSSwitchButton_onColor,onColor);
        offColor=typedArray.getColor(R.styleable.TSSwitchButton_offColor,offColor);
        offBorderColor=typedArray.getColor(R.styleable.TSSwitchButton_offBorderColor,offBorderColor);
        spotColor=typedArray.getColor(R.styleable.TSSwitchButton_spotColor,spotColor);
        borderWidth=typedArray.getDimensionPixelOffset(R.styleable.TSSwitchButton_switch_border_width,borderWidth);
        typedArray.recycle();
    }

    public void beginSwitch() {
        switchState = !switchState;
        animateCheckedState(switchState);
        if (mListener != null) {
            mListener.onSwitch(switchState);
        }
    }

    public void switchOn(){
        setSwitchOn();
        if(mListener!=null){
          mListener.onSwitch(switchState);
        }
    }

    public void switchOff(){
        setSwitchOff();
        if(mListener!=null){
            mListener.onSwitch(switchState);
        }
    }

    public void setSwitchOn(){
        switchState=true;
        setAnimatorProperty(true);
    }

    public void  setSwitchOff(){
        switchState=false;
        setAnimatorProperty(false);
    }

    @Override
    protected void onLayout(boolean changed,int left,int top,int right,int bottom){
        super.onLayout(changed, left, top, right, bottom);

        final int width=getWidth();
        final int height=getHeight();
        radius=Math.min(width,height)*0.5f;
        centerY=radius;
        startX = radius;
        endX = width - radius;
        spotMinX = startX + borderWidth;
        spotMaxX = endX - borderWidth;
        spotSize = height - 4 * borderWidth;

        setAnimatorProperty(switchState);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        rect.set(0, 0, getWidth(), getHeight());
        mPaint.setColor(borderColor);
        canvas.drawRoundRect(rect, radius, radius, mPaint);

        if (offLineWidth > 0) {
            final float cy = offLineWidth * 0.5f;
            rect.set(spotX - cy, centerY - cy, endX + cy, centerY + cy);
            mPaint.setColor(offColor);
            canvas.drawRoundRect(rect, cy, cy, mPaint);
        }

        rect.set(spotX - 1 - radius, centerY - radius, spotX + 1.1f + radius, centerY + radius);
        mPaint.setColor(borderColor);
        canvas.drawRoundRect(rect, radius, radius, mPaint);

        final float spotR = spotSize * 0.5f;
        rect.set(spotX - spotR, centerY - spotR, spotX + spotR, centerY + spotR);
        mPaint.setColor(spotColor);
        canvas.drawRoundRect(rect, spotR, spotR, mPaint);
    }

    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private static final int ANIMATION_DURATION = 280;
    private ObjectAnimator mAnimator;

    private void animateCheckedState(boolean newCheckedState) {
        AnimatorProperty property = new AnimatorProperty();
        if (newCheckedState) {
            property.color = onColor;
            property.offLineWidth = 10;
            property.spotX = spotMaxX;
        } else {
            property.color = offBorderColor;
            property.offLineWidth = spotSize;
            property.spotX = spotMinX;
        }

        if (mAnimator == null) {
            mAnimator = ObjectAnimator.ofObject(this, ANIM_VALUE, new AnimatorEvaluator(mCurProperty), property);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                mAnimator.setAutoCancel(true);
            mAnimator.setDuration(ANIMATION_DURATION);
            mAnimator.setInterpolator(ANIMATION_INTERPOLATOR);
        } else {
            mAnimator.cancel();
            mAnimator.setObjectValues(property);
        }
        mAnimator.start();
    }

    private AnimatorProperty mCurProperty = new AnimatorProperty();

    private void setAnimatorProperty(AnimatorProperty property) {
        this.spotX = property.spotX;
        this.borderColor = property.color;
        this.offLineWidth = property.offLineWidth;
        invalidate();
    }

    private void setAnimatorProperty(boolean isOn) {
        AnimatorProperty property = mCurProperty;
        if (isOn) {
            property.color = onColor;
            property.offLineWidth = 10;
            property.spotX = spotMaxX;
        } else {
            property.color = offBorderColor;
            property.offLineWidth = spotSize;
            property.spotX = spotMinX;
        }
        setAnimatorProperty(property);
    }

    private final static class AnimatorProperty {
        private int color;
        private float offLineWidth;
        private float spotX;
    }

    private final static class AnimatorEvaluator implements TypeEvaluator<AnimatorProperty> {
        private final AnimatorProperty mProperty;

        public AnimatorEvaluator(AnimatorProperty property) {
            mProperty = property;
        }

        @Override
        public AnimatorProperty evaluate(float fraction, AnimatorProperty startValue, AnimatorProperty endValue) {
            // Values
            mProperty.spotX = (int) (startValue.spotX + (endValue.spotX - startValue.spotX) * fraction);

            mProperty.offLineWidth = (int) (startValue.offLineWidth + (endValue.offLineWidth - startValue.offLineWidth) * (1 - fraction));

            // Color
            int startA = (startValue.color >> 24) & 0xff;
            int startR = (startValue.color >> 16) & 0xff;
            int startG = (startValue.color >> 8) & 0xff;
            int startB = startValue.color & 0xff;

            int endA = (endValue.color >> 24) & 0xff;
            int endR = (endValue.color >> 16) & 0xff;
            int endG = (endValue.color >> 8) & 0xff;
            int endB = endValue.color & 0xff;

            mProperty.color = (startA + (int) (fraction * (endA - startA))) << 24 |
                    (startR + (int) (fraction * (endR - startR))) << 16 |
                    (startG + (int) (fraction * (endG - startG))) << 8 |
                    (startB + (int) (fraction * (endB - startB)));

            return mProperty;
        }
    }

    private final static Property<TSSwitchButton, AnimatorProperty> ANIM_VALUE = new Property<TSSwitchButton, AnimatorProperty>(AnimatorProperty.class, "animValue") {
        @Override
        public AnimatorProperty get(TSSwitchButton object) {
            return object.mCurProperty;
        }

        @Override
        public void set(TSSwitchButton object, AnimatorProperty value) {
            object.setAnimatorProperty(value);
        }
    };

    public interface OnSwitchChanged {

        public void onSwitch(boolean on);

    }


    public void setOnSwitchChanged(OnSwitchChanged onSwitchChanged) {
        mListener = onSwitchChanged;
    }


}
