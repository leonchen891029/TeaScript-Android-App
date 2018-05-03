package com.teacore.teascript.widget.dialog;

import android.app.ProgressDialog;

/**对话框的控制接口
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-15
 */
public interface DialogControl {

    void hideWaitDialog();

    ProgressDialog showWaitDialog();

    ProgressDialog showWaitDialog(int resid);

    ProgressDialog showWaitDialog(String text);

}
