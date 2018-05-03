package com.teacore.teascript.app;

import android.app.Activity;

import java.util.Stack;

/**activity管理者
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-13
 */
public class AppManager {
    private  static Stack<Activity> activityStack;
    private  static AppManager appManager;

    public static AppManager getAppManager(){
        if(appManager==null){
            appManager=new AppManager();
        }

        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }

        return appManager;
    }

    //添加Activity到推栈
    public void addActivity(Activity activity){
        if(activityStack==null){
            activityStack=new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    //获取当前的Activity(堆栈最后一个压入的元素)
    public Activity currentActivity(){
        Activity activity=activityStack.lastElement();
        return activity;
    }

    //获取指定类名的Activity
    public static Activity getActivity(Class<?> cls){
        if(activityStack!=null){
            for(Activity activity:activityStack){
                if(activity.getClass().equals(cls)){
                    return activity;
                }
            }
        }
        return null;
    }

    //结束当前的Activity(堆栈最后一个压入的）
    public void finishActivity(){
        Activity activity=activityStack.lastElement();
        finishActivity(activity);
    }

    //结束指定的Activity
    public void finishActivity(Activity activity){
        if(activity!=null && activityStack.contains(activity)){
            activityStack.remove(activity);
            activity.finish();
        }
    }

    //结束指定类名的Activity
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                return;
            }
        }
    }

    //AppManager中移除Activity
    public void removeActivity(Activity activity){
        if(activity!=null && activityStack.contains(activity)){
            activityStack.remove(activity);
        }
    }

    //结束所有Activity
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                finishActivity(activityStack.get(i));
            }
        }
        activityStack.clear();
    }

    //退出应用程序
    public void AppExit(){
        try{
            finishAllActivity();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
