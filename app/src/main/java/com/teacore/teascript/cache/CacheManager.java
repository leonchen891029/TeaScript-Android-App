package com.teacore.teascript.cache;

import android.content.Context;

import com.teacore.teascript.util.TDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**缓存管理者
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-3-16
 */

public class CacheManager {

    //wifi缓存时间为5分钟，其他的网络的缓存为1小时(单位为毫秒)
    private static long wifi_cache_time=5*60*1000;
    private static long other_cache_time=60*60*1000;

    //保存对象
    public static boolean saveObject(Context context, Serializable seri,String file){
        FileOutputStream fileOutputStream=null;
        ObjectOutputStream objectOutputStream=null;

        try{
            //创建FileOutputStream
            fileOutputStream=context.openFileOutput(file,Context.MODE_PRIVATE);
            //创建ObjectOutput
            objectOutputStream=new ObjectOutputStream(fileOutputStream);
            //写对象
            objectOutputStream.writeObject(seri);
            objectOutputStream.flush();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }finally {

            //关闭流
            try{objectOutputStream.close();}catch (Exception e){}

            try{fileOutputStream.close();}catch (Exception e){}
        }

    }

    //读取对象
    public static Serializable readObject(Context context,String file){
        //判断缓存是否存在
        if(!isCacheExist(context,file))
            return null;

        FileInputStream fileInputStream=null;
        ObjectInputStream objectInputStream=null;
        try{
            fileInputStream=context.openFileInput(file);
            objectInputStream=new ObjectInputStream(fileInputStream);
            return (Serializable) objectInputStream.readObject();
        }catch (FileNotFoundException e){
        }catch(Exception e){
            e.printStackTrace();
            //反序列化失败-删除缓存文件
            if(e instanceof InvalidClassException){
                File data=context.getFileStreamPath(file);
                data.delete();
            }
        }finally {

            //关闭流
            try {
                objectInputStream.close();
            } catch (Exception e) {
            }
            try {
                fileInputStream.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    //判断缓存是否存在
    public static boolean isCacheExist(Context context,String file){
        if(context==null)
            return false;
        boolean exist=false;
        File data=context.getFileStreamPath(file);
        if(data.exists())
            exist=true;
        return exist;
    }

    //判断缓存是否失效
    public static boolean isCacheInvalid(Context context,String file){
        File data=context.getFileStreamPath(file);
        if(!data.exists())
            return true;
        long existTime = System.currentTimeMillis() - data.lastModified();
        boolean invalid=false;
        if (TDevice.getNetworkType() == TDevice.NETTYPE_WIFI) {
            invalid = existTime > wifi_cache_time ? true : false;
        } else {
            invalid = existTime > other_cache_time ? true : false;
        }
        return invalid;
    }

}
