package com.teacore.teascript.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.teacore.teascript.app.AppContext;

/**字体图标设置工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-18
 */

public class TypefaceUtils {

    private static Typeface getTypeface(){
        Context context= AppContext.getInstance();

        Typeface font=Typeface.createFromAsset(context.getAssets(),"fontawesome-webfont.ttf");

        return font;
    }

    public static void setTypeface(TextView textView){
        textView.setTypeface(getTypeface());
    }

    public static void setTypeface(TextView textView,String string){
        if(StringUtils.isEmpty(string)){
            return;
        }
        textView.setText(string);
        textView.setTypeface(getTypeface());
    }

    public static void setTypeface(TextView textView, int textId) {
        setTypeface(textView, AppContext.getInstance().getResources().getString(textId));
    }

    public static void setTypefaceWithText(TextView textView, int textId, String text) {
        String lastText = AppContext.getInstance().getResources().getString(textId) + " " + text;
        setTypeface(textView, lastText);
    }


    public static void setTypefaceWithColor(TextView textView, int textId,
                                            String colorStr) {
        try {
            int color = Color.parseColor(colorStr);
            textView.setTextColor(color);
        } catch (Exception e) {
        }
        setTypeface(textView, textId);
    }

    public static void setTypefaceWithColor(TextView textView, int textId, int colorId) {
        textView.setTextColor(ContextCompat.getColor(AppContext.getInstance(),colorId));
        setTypeface(textView, textId);
    }

    public static void setTypefaceWithColor(TextView textView, int colorId) {
        textView.setTextColor(ContextCompat.getColor(AppContext.getInstance(),colorId));
        setTypeface(textView);
    }

    public static void setTypefaceWithColor(TextView textView, String colorStr) {
        try {
            int color = Color.parseColor(colorStr);
            textView.setTextColor(color);
        } catch (Exception e) {
        }

        setTypeface(textView);
    }



}
