package com.teacore.teascript.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.teacore.teascript.app.AppContext;
import com.teacore.teascript.bean.Teatime;

/**
 * 发布Teatime的任务工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-5-25
 */

public class TeatimeTaskUtils {

    public static void pubTeatime(Context context, Teatime Teatime){
        Intent intent=new Intent(TaskService.ACTION_PUB_TEATIME);
        Bundle bundle=new Bundle();
        bundle.putParcelable(TaskService.BUNDLE_PUB_Teatime_TASK, Teatime);
        intent.putExtras(bundle);
        intent.setPackage(AppContext.getInstance().getPackageName());
        context.startService(intent);
    }

    public static void pubSoftwareTeatime(Context context, Teatime Teatime, int softid) {
        Intent intent = new Intent(TaskService.ACTION_PUB_SOFTWARE_TEATIME);
        Bundle bundle = new Bundle();
        bundle.putParcelable(TaskService.BUNDLE_PUB_SOFTWARE_Teatime_TASK,
                Teatime);
        bundle.putInt(TaskService.KEY_SOFTID, softid);
        intent.putExtras(bundle);
        intent.setPackage(AppContext.getInstance().getPackageName());
        context.startService(intent);
    }



}
