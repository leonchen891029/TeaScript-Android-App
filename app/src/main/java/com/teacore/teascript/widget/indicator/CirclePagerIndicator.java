package com.teacore.teascript.widget.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.teacore.teascript.R;

/**
 * 自定义圆形PagerIndicator
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-22
 */

public class CirclePagerIndicator extends View implements PagerIndicator{

    private float mRadius;
    private float mIndicatorRadius;

    private final Paint mFillPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mStrokePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mIndicatorPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mCurrentPage;
    private int mFollowPage;
    private float mPageOffset;
    private boolean mCenterHorizontal;
    private boolean mIsFollow;
    private float mIndicatorSpace;

    public CirclePagerIndicator(Context context){
        this(context,null);
    }

    public CirclePagerIndicator(Context context, AttributeSet attrs){
        super(context,attrs);

        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.CirclePagerIndicator);
        mCenterHorizontal=typedArray.getBoolean(R.styleable.CirclePagerIndicator_circle_indicator_centerHorizontal,true);

        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(typedArray.getColor(R.styleable.CirclePagerIndicator_circle_indicator_fill_color,0x0000ff));
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(typedArray.getColor(R.styleable.CirclePagerIndicator_circle_indicator_stroke_color,0x000000));
        mStrokePaint.setStrokeWidth(typedArray.getDimension(R.styleable.CirclePagerIndicator_circle_indicator_stroke_width, 0));
        mIndicatorPaint.setStyle(Paint.Style.FILL);
        mIndicatorPaint.setColor(typedArray.getColor(R.styleable.CirclePagerIndicator_circle_indicator_fill_color, 0x0000ff));
        mRadius = typedArray.getDimension(R.styleable.CirclePagerIndicator_circle_indicator_radius, 10);
        mIndicatorSpace = typedArray.getDimension(R.styleable.CirclePagerIndicator_circle_indicator_space, 20);
        mIndicatorRadius = typedArray.getDimension(R.styleable.CirclePagerIndicator_circle_indicator_indicator_radius, 10);
        mIsFollow = typedArray.getBoolean(R.styleable.CirclePagerIndicator_circle_indicator_follow, true);
        if (mIndicatorRadius < mRadius) mIndicatorRadius = mRadius;
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas){

        super.onDraw(canvas);

        if(mViewPager==null){
            return;
        }

        final int count=mViewPager.getAdapter().getCount();
        if(count==0){
            return;
        }

        if(mCurrentPage>=count){
            setCurrentItem(count-1);
            return;
        }

        int width=getWidth();
        int paddingLeft=getPaddingLeft();
        int paddingRight=getPaddingRight();
        int paddingTop=getPaddingTop();

        //两个圆形之间的距离+直径
        final float circleAndSpace=2*mRadius+mIndicatorSpace;
        //圆心y轴距离
        final float yOffset=paddingTop+mRadius;
        //圆心x轴距离
        float xOffset=paddingLeft+mRadius;

        //如果采用水平居中对齐
        if (mCenterHorizontal) {
            xOffset += ((width - paddingLeft - paddingRight) - (count * circleAndSpace)) / 2.0f;
        }

        float cX;
        float cY;

        float strokeRadius=mRadius;
        //计算外圆的半径
        if (mStrokePaint.getStrokeWidth() > 0) {
            strokeRadius -= mStrokePaint.getStrokeWidth() / 2.0f;
        }

        //绘制所有圆点
        for (int i = 0; i < count; i++) {

            cX = xOffset + (i * circleAndSpace);//计算下个圆绘制起点偏移量
            cY = yOffset;

            //绘制圆
            if (mFillPaint.getAlpha() > 0) {
                canvas.drawCircle(cX, cY, strokeRadius, mFillPaint);
            }

            //绘制外圆
            if (strokeRadius != mRadius) {
                canvas.drawCircle(cX, cY, mRadius, mStrokePaint);
            }
        }

        float cx = (!mIsFollow ? mFollowPage : mCurrentPage) * circleAndSpace;

        //指示器选择缓慢移动
        if (mIsFollow) {
            cx += mPageOffset * circleAndSpace;
        }

        cX = xOffset + cx;
        cY = yOffset;
        canvas.drawCircle(cX, cY, mIndicatorRadius, mIndicatorPaint);

    }

    @Override
    public void bindViewPager(ViewPager view){
        if(mViewPager==view){
            return;
        }
        if(view.getAdapter()==null){
            throw new IllegalStateException(("view中为包含有adapter"));
        }
        mViewPager=view;
        mViewPager.addOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void bindViewPager(ViewPager view,int initialPosition){
        bindViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item){
        if(mViewPager==null){
            throw new IllegalStateException("indicator未绑定ViewPager");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage=item;
        invalidate();
    }

    @Override
    public void notifyDataSetChanged(){
        invalidate();
        requestLayout();
    }

    @Override
    public void onPageScrollStateChanged(int state){
        if(mListener!=null){
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position,float positionOffset,int positionOffsetPixels){
        mCurrentPage = position;
        mPageOffset = positionOffset;
        //如果指示器跟随ViewPager缓慢滑动，那么滚动是时候都绘制界面
        if (mIsFollow) {
            invalidate();
        }
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
        mFollowPage = position;
        invalidate();
        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int width;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
            width = specSize;
        } else {
            final int count = mViewPager.getAdapter().getCount();
            width = (int) (getPaddingLeft() + getPaddingRight()
                    + (count * 2 * mRadius) + (mIndicatorRadius - mRadius) * 2 + (count - 1) * mIndicatorSpace);
            if (specMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, specSize);
            }
        }
        return width;
    }

    private int measureHeight(int measureSpec) {
        int height;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            if (specMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, specSize);
            }
        }
        return height;
    }




}
