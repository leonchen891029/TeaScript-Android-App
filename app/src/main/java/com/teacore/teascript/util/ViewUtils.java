package com.teacore.teascript.util;

import android.widget.TextView;

/**View工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-5
 */

public class ViewUtils {

    //设置view的划线状态
    public static void setTextViewLineFlag(TextView tv, int flags) {
        tv.getPaint().setFlags(flags);
        tv.invalidate();
    }

}
