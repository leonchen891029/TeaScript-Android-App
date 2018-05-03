package com.teacore.teascript.widget;

import android.graphics.Color;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

/**无下划线的Url Span类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-5
 */
public class NoLinkURLSpan extends URLSpan {

    public NoLinkURLSpan(Parcel src) {
        super(src);
    }

    public NoLinkURLSpan(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(Color.BLACK);
        //去掉下划线
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
    }

}