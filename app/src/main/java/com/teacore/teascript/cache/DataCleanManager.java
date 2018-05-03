package com.teacore.teascript.cache;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**数据清理工具类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-20
 */

public class DataCleanManager {

    /*
    清除本应用的内部数据
    1./data/data/<application package>/cache目录
    2./data/data/<application package>/files目录
     */
    public static void cleanInternalCache(Context context){
        deleteFilesByDirectory(context.getCacheDir());
        deleteFilesByDirectory(context.getFilesDir());
    }

    //清除内部文件
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    //清除本应用所有的数据库 (/data/data/<application package>/databases目录)
    public static void cleanDatabases(Context context){
        deleteFilesByDirectory(new File("/data/data/"+context.getPackageName()+"/databases"));
    }

    //按数据库名称清除数据库
    public static void cleanDatabaseByName(Context context,String dbName){
        context.deleteDatabase(dbName);
    }

    //清除本应用的SharedPreference(/data/data/<application package>/shared_prefs)
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    //清除外部cache下的内容(/mnt/sdcard/android/data/<application package>/cache)
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    //清除自定义路径下的文件
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    public static void cleanCustomCache(File file) {
        deleteFilesByDirectory(file);
    }

    //清除本应用所有的数据
    public static void cleanApplicationData(Context context, String... filepaths) {

        cleanInternalCache(context);

        cleanExternalCache(context);

        cleanDatabases(context);

        cleanSharedPreference(context);

        cleanFiles(context);

        for (String filepath : filepaths) {
            cleanCustomCache(filepath);
        }
    }

    //上面所用到的删除方法，删除的是某个文件夹下的文件
    public static void deleteFilesByDirectory(File directory){
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File child : directory.listFiles()) {
                if (child.isDirectory()) {
                    deleteFilesByDirectory(child);
                }
                child.delete();
            }
        }
    }

}
