package com.teacore.teascript.util;

import com.teacore.teascript.app.AppContext;

/**
 * webview使用的字体大小工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-8
 */

public class FontSizeUtils {

    public static final String KEY_WEBVIEW_FONT_SIZE="KEY_WEBVIEW_FONT_SIZE";

    public static int getSaveFontSizeIndex(){
        return AppContext.getPreferences().getInt(KEY_WEBVIEW_FONT_SIZE,3);
    }

    public static String getSaveFontSize() {
        return getFontSize(getSaveFontSizeIndex());
    }

    public static String getFontSize(int fontSizeIndex) {
        String fontSize = "";
        switch (fontSizeIndex) {
            case 0:
                fontSize = "javascript:showSuperBigSize()";
                break;
            case 1:
                fontSize = "javascript:showBigSize()";
                break;
            case 2:
                fontSize = "javascript:showMidSize()";
                break;
            default:
                fontSize = "javascript:showSmallSize()";
                break;
        }
        return fontSize;
    }

    public static void saveFontSize(int fontSizeIndex) {
        AppContext.set(KEY_WEBVIEW_FONT_SIZE, fontSizeIndex);
    }

}
