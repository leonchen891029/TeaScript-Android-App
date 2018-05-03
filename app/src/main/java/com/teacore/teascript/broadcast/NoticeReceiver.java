package com.teacore.teascript.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.teacore.teascript.service.NoticeUtils;

/**
 * 广播接收器类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-15
 */

public class NoticeReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        NoticeUtils.requestNotice(context);
    }
}
