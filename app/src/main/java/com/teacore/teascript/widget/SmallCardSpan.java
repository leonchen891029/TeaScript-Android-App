package com.teacore.teascript.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.View;

import com.teacore.teascript.R;

import java.lang.ref.WeakReference;

/**自定义的ReplacementSpan(实现小卡片span)
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-1
 */

public abstract class SmallCardSpan extends ReplacementSpan{

    private WeakReference weakReference;
    private String mText;
    private int rectLeft;
    private int rectTop;
    private int width;
    private int smallCardHeight;
    protected Context mContext;
    protected Drawable mDrawable;
    protected int iconWidth;
    protected int iconHeight;
    private int k;
    private Drawable drawables[];
    private int iconMarginLeft;
    private int titleMarginLeft;
    private Paint mPaint;
    private int p;
    private float q;

    public SmallCardSpan(Context context, String s, Drawable drawable) {
        drawables = new Drawable[2];
        mPaint = new Paint(1);
        mContext = context.getApplicationContext();
        mText = s;
        this.mDrawable = drawable;
        initData();
    }

    private void a(Canvas canvas) {
        drawables[k].draw(canvas);
        canvas.save();
        int start = (int) Math.ceil((float) (smallCardHeight - iconHeight) / 2.0F);
        canvas.translate(iconMarginLeft, start);
        mDrawable.draw(canvas);
        canvas.restore();
        canvas.drawText(mText, p, q, mPaint);
    }

    //初始化数据
    private void initData() {
        if (TextUtils.isEmpty(mText))
            mText = "";

        //如果传入的文本长度大于7，截取其中的字符串
        if (mText.length() > 7)
            mText = (new StringBuilder()).append(mText.substring(0, 6)).append("...")
                    .toString();

        Resources resources = mContext.getResources();

        iconMarginLeft = resources
                .getDimensionPixelSize(R.dimen.small_card_icon_margin_left);
        iconWidth = resources
                .getDimensionPixelSize(R.dimen.small_card_icon_width);
        iconHeight = resources
                .getDimensionPixelSize(R.dimen.small_card_icon_height);
        int start = resources
                .getDimensionPixelSize(R.dimen.small_card_start);
        int end = resources
                .getDimensionPixelSize(R.dimen.small_card_end);
        titleMarginLeft = resources
                .getDimensionPixelSize(R.dimen.small_card_title_margin_left);
        int titleTextSize = resources
                .getDimensionPixelSize(R.dimen.small_card_title_text_size);
        smallCardHeight = resources
                .getDimensionPixelSize(R.dimen.small_card_height);

        int titleTextColor = ContextCompat.getColor(mContext,R.color.main_link_text_color);

        int minWidth = resources
                .getDimensionPixelSize(R.dimen.small_card_min_width);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(titleTextSize);
        mPaint.setColor(titleTextColor);

        width = end + (start + ((int) mPaint.measureText(mText, 0, mText.length()) + (iconMarginLeft + iconWidth))) + titleMarginLeft;

        if (width < minWidth) {
            int diff = minWidth - width;
            iconMarginLeft = iconMarginLeft + diff / 2;
            width = minWidth;
        }

        Drawable drawable =ContextCompat
                .getDrawable(mContext,R.drawable.card_small_button);

        drawable.setBounds(0, 0, width, smallCardHeight);
        drawables[0] = drawable;
        Drawable drawable1 = ContextCompat
                .getDrawable(mContext,R.drawable.card_small_button_highlighted);
        drawable1.setBounds(0, 0, width, smallCardHeight);
        drawables[1] = drawable1;
        if (mDrawable == null) {
            mDrawable = ContextCompat
                    .getDrawable(mContext,R.drawable.card_small_placeholder);
        }
        mDrawable.setBounds(0, 0, iconWidth, iconHeight);

        p = iconMarginLeft + iconWidth + titleMarginLeft;

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();

        float x = fontMetrics.bottom - (fontMetrics.top + fontMetrics.ascent)
                / 2.0F;

        q = (float) smallCardHeight - ((float) smallCardHeight - x) / 2.0F - fontMetrics.bottom;
    }

    private void e(View view) {
        if (weakReference != null)
            weakReference.clear();
        k = 1;
        if (view != null)
            view.invalidate();
    }

    private void f(View view) {
        if (weakReference != null)
            weakReference.clear();
        k = 0;
        if (view != null)
            view.invalidate();
    }

    protected int a() {
        return 0;
    }

    public abstract void a(View view);

    public boolean a(int start, int end) {
        Rect rect = getRect();
        boolean flag;
        if (rect != null)
            flag = rect.intersect(start, end, start, end);
        else
            flag = false;
        return flag;
    }

    protected int getSCPaddingRight() {
        return mContext.getResources().getDimensionPixelSize(
                R.dimen.small_card_padding_right);
    }

    public void b(View view) {
        f(view);
        a(view);
    }

    public Rect getRect() {
        Rect rect;
        if (width <= 0 || smallCardHeight <= 0) {
            rect = null;
        } else {
            rect = new Rect();
            rect.left = rectLeft;
            rect.top = rectTop;
            rect.right = rect.left + width;
            rect.bottom = rect.top + smallCardHeight;
        }
        return rect;
    }

    public void c(View view) {
        e(view);
    }

    public void d(View view) {
        f(view);
    }

    public void draw(Canvas canvas, CharSequence charsequence, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        canvas.save();
        int j2 = top + (bottom - top - smallCardHeight) / 2;
        rectLeft = (int) x + a();
        rectTop = j2 - paint.getFontMetricsInt().descent;
        canvas.translate(rectLeft, j2);
        a(canvas);
        canvas.restore();
    }

    public int getSize(Paint paint, CharSequence charsequence, int start, int end,
                       Paint.FontMetricsInt fontmetricsint) {
        if (fontmetricsint != null) {
            fontmetricsint.ascent = -smallCardHeight;
            fontmetricsint.descent = 0;
            fontmetricsint.top = fontmetricsint.ascent;
            fontmetricsint.bottom = 0;
        }
        return width + a() + getSCPaddingRight();
    }

}
