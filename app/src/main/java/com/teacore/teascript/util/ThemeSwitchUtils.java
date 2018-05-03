package com.teacore.teascript.util;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.R;

/**主题切换工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-22
 */

public class ThemeSwitchUtils {

    public static int getTitleReadedColor() {
        if (AppContext.getNightModeSwitch()) {
            return R.color.night_infoTextColor;
        } else {
            return R.color.day_infoTextColor;
        }
    }

    public static int getTitleUnReadedColor() {
        if (AppContext.getNightModeSwitch()) {
            return R.color.night_textColor;
        } else {
            return R.color.day_textColor;
        }
    }

    public static String getWebViewBodyString() {
        if (AppContext.getNightModeSwitch()) {
            return "<body class='night'><div class='contentstyle' id='article_body'>";
        } else {
            return "<body style='background-color: #FFF'><div class='contentstyle' id='article_body' >";
        }
    }

}
