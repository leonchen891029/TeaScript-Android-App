package com.teacore.teascript.module.general.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.teacore.teascript.R;

/**
 * 自动换行布局(当子view排满一行,FlowLayout会自动换行)
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-7-13
 */

public class FlowLayout extends ViewGroup{

    //每一行之间的间距
    private float mVerticalSpace;
    //水平方向控件之间的间距
    private float mHorizontalSpace;

    public FlowLayout(Context context){
        super(context);
        init(null,0,0);
    }

    public FlowLayout(Context context, AttributeSet attrs){
        super(context,attrs);
        init(attrs,0,0);
    }

    private void init(AttributeSet attrs,int defStyleAttr,int defStyleRes){
        final Context context=getContext();
        final Resources resource=getResources();
        final float density=resource.getDisplayMetrics().density;

        int vSpace=(int) (4*density);
        int hSpace=vSpace;

        if(attrs!=null){
            final TypedArray typedArray=context.obtainStyledAttributes(
              attrs, R.styleable.FlowLayout,defStyleAttr,defStyleRes
            );
            vSpace=typedArray.getDimensionPixelOffset(R.styleable.FlowLayout_verticalSpace,vSpace);
            hSpace=typedArray.getDimensionPixelOffset(R.styleable.FlowLayout_horizontalSpace,hSpace);
            typedArray.recycle();
        }
        setVerticalSpace(vSpace);
        setHorizontalSpace(hSpace);
    }

    public void setVerticalSpace(float pixelSize){
        mVerticalSpace=pixelSize;
    }

    public void setHorizontalSpace(float pixelSize){
        mHorizontalSpace=pixelSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int selfWidth = resolveSize(0, widthMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;

        //通过计算每一个子控件的高度，得到自己的高度
        for (int i = 0, childCount = getChildCount(); i < childCount; ++i) {
            View childView = getChildAt(i);
            LayoutParams childLayoutParams = childView.getLayoutParams();
            childView.measure(
                    getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight,
                            childLayoutParams.width),
                    getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom,
                            childLayoutParams.height));
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            lineHeight = Math.max(childHeight, lineHeight);

            if (childLeft + childWidth + paddingRight > selfWidth) {
                childLeft = paddingLeft;
                childTop += mVerticalSpace + lineHeight;
                lineHeight = childHeight;
            } else {
                childLeft += childWidth + mHorizontalSpace;
            }
        }

        int wantedHeight = childTop + lineHeight + paddingBottom;
        setMeasuredDimension(selfWidth, resolveSize(wantedHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int myWidth = r - l;

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();

        int childLeft = paddingLeft;
        int childTop = paddingTop;

        int lineHeight = 0;

        //根据子控件的宽高，计算子控件应该出现的位置。
        for (int i = 0, childCount = getChildCount(); i < childCount; ++i) {
            View childView = getChildAt(i);

            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            lineHeight = Math.max(childHeight, lineHeight);

            if (childLeft + childWidth + paddingRight > myWidth) {
                childLeft = paddingLeft;
                childTop += mVerticalSpace + lineHeight;
                lineHeight = childHeight;
            }
            childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + mHorizontalSpace;
        }
    }








































}
