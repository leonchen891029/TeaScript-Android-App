package com.teacore.teascript.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DoubleConverter;
import com.thoughtworks.xstream.converters.basic.FloatConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.converters.basic.LongConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**xml解析工具类
 *@author 陈晓帆
 *@version 1.0
 * Created 2017-2-11.
 */
public class XmlUtils {

    private static final String TAG=XmlUtils.class.getSimpleName();

    //反序列化，将一个xml流转换为bean实体类
    public static <T> T toBean(Class<T> type,InputStream inputStream){

        XStream xStream=new XStream(new DomDriver("UTF-8"));
        //反序列化过程中忽略未知字段
        xStream.ignoreUnknownElements();
        xStream.registerConverter(new MyIntConverter());
        xStream.registerConverter(new MyLongConverter());
        xStream.registerConverter(new MyFloatConverter());
        xStream.registerConverter(new MyDoubleConverter());
        //识别相应类中的注解
        xStream.processAnnotations(type);

        T obj=null;
        try{
            obj=(T) xStream.fromXML(inputStream);
        }catch(Exception e){
            TLog.log(TAG,"解析XML发送异常"+e.getMessage());
        }finally {
            if (inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    TLog.log(TAG, "关闭流出现异常：" + e.getMessage());
                }
            }
        }

        return obj;
    }

    //通过字节数组反序列化
    public static <T> T toBean(Class<T> type,byte[] bytes){
        if(bytes==null)
            return null;
        return toBean(type,new ByteArrayInputStream(bytes));
    }

    private static class MyIntConverter extends IntConverter {

        @Override
        public Object fromString(String str) {
            int value;
            try {
                value = (Integer) super.fromString(str);
            } catch (Exception e) {
                value = 0;
            }
            return value;
        }

        @Override
        public String toString(Object obj) {
            return super.toString(obj);
        }
    }

    private static class MyLongConverter extends LongConverter {
        @Override
        public Object fromString(String str) {
            long value;
            try {
                value = (Long) super.fromString(str);
            } catch (Exception e) {
                value = 0;
            }
            return value;
        }

        @Override
        public String toString(Object obj) {
            return super.toString(obj);
        }
    }

    private static class MyFloatConverter extends FloatConverter {
        @Override
        public Object fromString(String str) {
            float value;
            try {
                value = (Float) super.fromString(str);
            } catch (Exception e) {
                value = 0;
            }
            return value;
        }

        @Override
        public String toString(Object obj) {
            return super.toString(obj);
        }
    }

    private static class MyDoubleConverter extends DoubleConverter {
        @Override
        public Object fromString(String str) {
            double value;
            try {
                value = (Double) super.fromString(str);
            } catch (Exception e) {
                value = 0;
            }
            return value;
        }

        @Override
        public String toString(Object obj) {
            return super.toString(obj);
        }
    }


}
