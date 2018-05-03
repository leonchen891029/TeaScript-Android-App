package com.teacore.teascript.module.general.app;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**App异步线程池类 单例模式
 * @author 陈晓帆
 * @version 1.0
 * Created by apple on 17/10/23.
 */

public final class AppOperator {
    private static ExecutorService instance;

    public static Executor getExecutor(){

        if(instance==null){
            synchronized (AppOperator.class){
                if(instance==null){
                    instance= Executors.newFixedThreadPool(
                            Runtime.getRuntime().availableProcessors()>0?Runtime.getRuntime().availableProcessors():2
                    );
                }
            }
        }
        return instance;
    }

    public static void runOnThread(Runnable runnable){
       getExecutor().execute(runnable);
    }

}
