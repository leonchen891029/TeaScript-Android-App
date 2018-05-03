package com.teacore.teascript.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * 自定义BadgeView
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-16
 */

public class BadgeView extends TextView {

    public static final int POSITION_TOP_LEFT=1;
    public static final int POSITION_TOP_RIGHT=2;
    public static final int POSITION_BOTTOM_LEFT=3;
    public static final int POSITION_BOTTOM_RIGHT=4;
    public static final int POSITION_CENTER=5;

    private static final int DEFAULT_MARGIN_DP=5;
    private static final int DEFAULT_PADDING_DP=5;
    private static final int DEFAULT_RADIUS_DP=8;
    private static final int DEFAULT_POSITION=POSITION_TOP_RIGHT;
    private static final int DEFAULT_BADGE_COLOR= Color.parseColor("#1E90FF");
    private static final int DEFAULT_TEXT_COLOR=Color.WHITE;

    private static Animation fadeIn;
    private static Animation fadeOut;

    private Context mContext;
    private View targetView;
    private int badgePosition;
    private int badgeMarginH;
    private int badgeMarginV;
    private int badgeColor;
    private boolean isShown;
    private ShapeDrawable badgeBG;
    private int targetTabIndex;

    //构造函数
    public BadgeView(Context context) {
        this(context, (AttributeSet) null, android.R.attr.textViewStyle);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public BadgeView(Context context, View view) {
        this(context, null, android.R.attr.textViewStyle, view, 0);
    }

    public BadgeView(Context context, TabWidget view, int index) {
        this(context, null, android.R.attr.textViewStyle, view, index);
    }

    public BadgeView(Context context,AttributeSet attrs,int defStyle){
        this(context,attrs,defStyle,null,0);
    }

    public BadgeView(Context context, AttributeSet attrs,int defStyle,View view,int tabIndex){
        super(context,attrs,defStyle);
        init(context,view,tabIndex);
    }

    private void init(Context context,View view,int tabIndex){
        this.mContext=context;
        this.targetView=view;
        this.targetTabIndex=tabIndex;

        //初始化相关参数
        badgePosition=DEFAULT_POSITION;
        badgeMarginH=dpToPixels(DEFAULT_MARGIN_DP);
        badgeMarginV=badgeMarginH;
        badgeColor=DEFAULT_BADGE_COLOR;

        setTypeface(Typeface.DEFAULT_BOLD);
        int paddingPixels=dpToPixels(DEFAULT_PADDING_DP);
        setPadding(paddingPixels,0,paddingPixels,0);
        setTextColor(DEFAULT_TEXT_COLOR);

        //进入和退出动画
        fadeIn=new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(200);
        fadeOut=new AlphaAnimation(1,0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(200);

        isShown=false;

        if(this.targetView!=null){
            applyto(this.targetView);
        }else{
            show();
        }

    }

    private void applyto(View view){

        LayoutParams lp=view.getLayoutParams();
        ViewParent parent=view.getParent();
        FrameLayout container=new FrameLayout(mContext);

        if(view instanceof TabWidget){
            view=((TabWidget) view).getChildTabViewAt(targetTabIndex);

            ((ViewGroup) targetView).addView(container,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            this.setVisibility(View.GONE);

            container.addView(this);
        }else{
            ViewGroup viewGroup=(ViewGroup) parent;
            int index=viewGroup.indexOfChild(view);

            viewGroup.removeView(view);
            viewGroup.addView(container,index,lp);

            container.addView(view);
            this.setVisibility(View.GONE);
            container.addView(this);

            viewGroup.invalidate();
        }


    }

    //显示badgeView
    public void show(){
        show(false,null);
    }

    public void show(boolean animate){
        show(animate,fadeIn);
    }

    public void show(Animation anim) {
        show(true, anim);
    }

    private void show(boolean animate,Animation anim){

        if(getBackground()==null){
            if(badgeBG==null){
                badgeBG=getDefaultBackground();
            }
            setBackground(badgeBG);
        }

        applyLayoutParams();

        if(animate){
            this.startAnimation(anim);
        }

        this.setVisibility(View.VISIBLE);

        isShown=true;
    }

    //隐藏badgeView
    public void hide(){
        hide(false,null);
    }

    public void hide(boolean animate){
        hide(animate,fadeOut);
    }

    public void hide(Animation anim){
        hide(true,anim);
    }

    private void hide(boolean animate,Animation anim){
        this.setVisibility(View.GONE);

        if(animate){
            this.startAnimation(anim);
        }

        isShown=false;
    }

    //切换badgeView的可视状态
    public void toggle(){
        toggle(false,null,null);
    }

    public void toggle(boolean animate){
        toggle(animate,fadeIn,fadeOut);
    }

    public void toggle(Animation animIn,Animation animOut){
        toggle(true,animIn,animOut);
    }

    private void toggle(boolean animate,Animation animIn,Animation animOut){
        if (isShown) {
            hide(animate && (animOut != null), animOut);
        } else {
            show(animate && (animIn != null), animIn);
        }
    }

    /*
    增加badge的text值，如果text无法被转换为整数，则设为0
    @param offset 增加的数值
     */
    public int increment(int offset) {
        CharSequence txt = getText();
        int i;
        if (txt != null) {
            try {
                i = Integer.parseInt(txt.toString());
            } catch (NumberFormatException e) {
                i = 0;
            }
        } else {
            i = 0;
        }
        i = i + offset;
        setText(String.valueOf(i));
        return i;
    }

    //减少badge的text数值
    public int decrement(int offset) {
        return increment(-offset);
    }

    //获取默认的背景Drawable
    private ShapeDrawable getDefaultBackground(){

        int r = dpToPixels(DEFAULT_RADIUS_DP);
        float[] outerR = new float[] { r, r, r, r, r, r, r, r };

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(badgeColor);

        return drawable;
    }

    //设置layoutParam参数
    private void applyLayoutParams() {

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        switch (badgePosition) {
            case POSITION_TOP_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.TOP;
                lp.setMargins(badgeMarginH, badgeMarginV, 0, 0);
                break;
            case POSITION_TOP_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.TOP;
                lp.setMargins(0, badgeMarginV, badgeMarginH, 0);
                break;
            case POSITION_BOTTOM_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
                lp.setMargins(badgeMarginH, 0, 0, badgeMarginV);
                break;
            case POSITION_BOTTOM_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                lp.setMargins(0, 0, badgeMarginH, badgeMarginV);
                break;
            case POSITION_CENTER:
                lp.gravity = Gravity.CENTER;
                lp.setMargins(0, 0, 0, 0);
                break;
            default:
                break;
        }

        setLayoutParams(lp);

    }

    //获取targetView
    public View getTarget() {
        return targetView;
    }

    //判断badgeview是否在可见的
    @Override
    public boolean isShown() {
        return isShown;
    }

    //获取badgeposition
    public int getBadgePosition() {
        return badgePosition;
    }

    //设置badgeposition
    public void setBadgePosition(int layoutPosition) {
        this.badgePosition = layoutPosition;
    }

    //获取水平方向的Margin值
    public int getHorizontalBadgeMargin() {
        return badgeMarginH;
    }

    //获取竖直方向的Margin值
    public int getVerticalBadgeMargin() {
        return badgeMarginV;
    }

    //设置Margin值
    public void setBadgeMargin(int badgeMargin) {
        this.badgeMarginH = badgeMargin;
        this.badgeMarginV = badgeMargin;
    }

    public void setBadgeMargin(int horizontal, int vertical) {
        this.badgeMarginH = horizontal;
        this.badgeMarginV = vertical;
    }

    //获取badge的背景色
    public int getBadgeBackgroundColor() {
        return badgeColor;
    }

    //设置badge的背景色
    public void setBadgeBackgroundColor(int badgeColor) {
        this.badgeColor = badgeColor;
        badgeBG = getDefaultBackground();
    }

    //将设置dp数值转换为pixel

    private int dpToPixels(int dp) {
        Resources resources = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics());
        return (int) px;
    }



}
