package com.teacore.teascript.widget.emoji;

import android.text.Editable;

/**
 * 表情发送监听器
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-2-22
 */
public interface OnSendClickListener {

    void onClickSendButton(Editable string);

    void onClickFlagButton();
}
