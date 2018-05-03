package com.teacore.teascript.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class StringUtils {

    //email的匹配模式
    private final static Pattern emailPattern=Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    //图片的匹配模式
    private final static Pattern imgUrlPattern=Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)");
    //url的匹配模式
    private final static Pattern urlPattern=Pattern.compile("^(https|http)://.*?$(net|com|.com.cn|org|me|)");

    /*
    判断一个字符串是否是空白串(null "" 空格,制表符，回车符，换行符)
     */
    public static boolean isEmpty(String input){

        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            //只要字符串中有一个字符不是其中的一项，就是非空白串
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }

        return true;
    }

    //判断一个字符串是不是合法的电子邮件地址
    public static boolean isEmail(String stringEmail){

        if(stringEmail==null || stringEmail.trim().length()==0){
            return false;
        }

        return emailPattern.matcher(stringEmail).matches();
    }

    //判断一个字符串是否是图片的url
    public static boolean isImgUrl(String stringImgUrl){

        if(stringImgUrl==null || stringImgUrl.trim().length()==0){
            return false;
        }

        return imgUrlPattern.matcher(stringImgUrl).matches();
    }

    //判断一个字符串是否是一个合法的url
    public static boolean isUrl(String stringUrl){

        if(stringUrl==null || stringUrl.trim().length()==0){
            return false;
        }

        return urlPattern.matcher(stringUrl).matches();
    }

    /*
    字符串转整数
    @param stringInput 输入的字符串
    @param defValue 默认的整数值
     */
    public static int toInt(String stringInput,int defValue){

        try{
            return Integer.parseInt(stringInput);
        }catch (Exception e){
            e.printStackTrace();
        }

        return defValue;
    }

    /*
    对象转整数
    @param object 对象
     */
    public static int toInt(Object object){

        if(object==null)
            return 0;

        return toInt(object.toString(),0);
    }

    //字符串转长整型
    public static  long  toLong(String string){
        try{
            return Long.parseLong(string);
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

    //字符串转布尔值
    public static  boolean toBool(String string){
        try{
            return Boolean.parseBoolean(string);
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    //如果一个字符串为null，返回""
    public static String getString(String stringInput){
        return stringInput==null?"":stringInput;
    }

    /*
    将一个InputStream转换为字符串
    @param inputStream 需要转换为流
     */
    public static String toConvertString(InputStream inputStream){
        StringBuffer stringBuffer=new StringBuffer();

        InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

        try{
            String line=bufferedReader.readLine();

            while(line!=null){
                stringBuffer.append(line+"<br>");
                line=bufferedReader.readLine();
            }


        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(inputStream != null){
                    inputStream.close();
                    inputStream=null;
                }
                if(inputStreamReader != null){
                    inputStreamReader.close();
                    inputStreamReader=null;
                }
                if(bufferedReader != null){
                    bufferedReader.close();
                    bufferedReader=null;
                }
            }catch(Exception e){
            }
        }

        return stringBuffer.toString();
    }

    //截取字符串
    public static String getSubstring(int start,int num,String string){
        if(string == null){
            return "";
        }

        int length =string.length();

        if(start<0){
            start=0;
        }
        if(num <= 0){
            num=1;
        }

        int end=start+num;

        if(start>length){
            start=length;
        }

        if(end>length){
            end=length;
        }

        return string.substring(start, end);

    }

}
