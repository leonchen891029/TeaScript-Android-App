package com.teacore.teascript.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

    //三种不同的DateFormat类型，这里使用到了ThreadLocal，所以后面的dateFormat需要使用.get()来获得
    private final static ThreadLocal<SimpleDateFormat> dateFormat1=new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormat2=new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormat3=new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };

    //将日期字符串转换为日期对象(根据指定的DateFormat)
    public static Date toDate(String dateString,SimpleDateFormat simpleDateFormat){
        try{
            return simpleDateFormat.parse(dateString);
        }catch(Exception e){
            return null;
        }
    }

    //toDate重载函数(默认使用dateFormat1)
    public static Date toDate(String dateString){
        return toDate(dateString,dateFormat1.get());
    }

    //将Date对象转换为日期字符串
    public static String getDateString(Date date,SimpleDateFormat simpleDateFormat){
        return simpleDateFormat.format(date);
    }

    //getDateString重载函数(默认使用dateFormat1)
    public static String getDateString(Date date){
        return dateFormat2.get().format(date);
    }

    //以友好的方式显示时间
    public static String friendly_time(String dateString){
        Date time=null;
        String ftime="";

        if(TimeZoneUtils.isInEasternEightZones()){
            time=toDate(dateString);
        }else{
            time=TimeZoneUtils.transformTime(toDate(dateString), TimeZone.getTimeZone("GMT+08"),TimeZone.getDefault());
        }

        if(time==null){
            return "你的日期格式有错误";
        }

        Calendar calendar=Calendar.getInstance();
        //判断是否是同一天(通过dateFormat2格式相比较),如果是同一天则比较是在几个小时前
        String curDate=dateFormat2.get().format(calendar.getTime());
        String paramDate=dateFormat2.get().format(time);
        //确定是同一天
        if(curDate.equals(paramDate)){
            //计算相差时间足不足一个小时
            int hour=(int) ((calendar.getTimeInMillis()-time.getTime())/3600000);

            //如果hour等于0，表示相差的事件不足一个小时
            if(hour==0){
                ftime=Math.max((calendar.getTimeInMillis()-time.getTime())/60000,1)+"分钟前";
            }else{
                ftime=hour+"小时前";
            }

            return ftime;
        }

        //已经确定不是同一天 86400000一天的毫秒数
        long lt=time.getTime()/86400000;
        long ct=calendar.getTimeInMillis()/86400000;
        //相差的天数
        int days=(int) (ct-lt);

        if (days == 0) {
            int hour = (int) ((calendar.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (calendar.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天 ";
        } else if (days > 2 && days < 31) {
            ftime = days + "天前";
        } else if (days >= 31 && days <= 2 * 31) {
            ftime = "一个月前";
        } else if (days > 2 * 31 && days <= 3 * 31) {
            ftime = "2个月前";
        } else if (days > 3 * 31 && days <= 4 * 31) {
            ftime = "3个月前";
        } else {
            ftime = dateFormat2.get().format(time);
        }
        return ftime;

    }

    //返回日期字符串的星期几信息
    public static String friendly_time2(String dateString){

        String ftime="";

        if(StringUtils.isEmpty(dateString)){
            return "";
        }

        String[] weekDays={"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};

        //获取相应格式的系统时间
        String systemTime=getSystemTime("MM-dd");
        //获取系统时间的日期
        int currentDay=StringUtils.toInt(systemTime.substring(3));
        //获取系统时间的月份
        int currentMonth=StringUtils.toInt(systemTime.substring(0,2));
        //获取指定字符串的年份
        int stringYear=StringUtils.toInt(dateString.substring(0,4));
        //获取指定字符串的月份
        int stringMonth=StringUtils.toInt(dateString.substring(5,7));
        //获取指定字符串的日期
        int stringDay=StringUtils.toInt(dateString.substring(8,10));

        Date date=new Date(stringYear,stringMonth-1,stringDay-1);

        if(stringDay==currentDay && stringMonth==currentMonth){
            ftime="今天/"+weekDays[getWeekOfDate(new Date())];
        }else if(stringDay==currentDay-1 && stringMonth==currentMonth){
            ftime="昨天/"+weekDays[(getWeekOfDate(new Date()) + 6) % 7];
        }else{
            if (stringMonth < 10) {
                ftime = "0";
            }
            ftime += stringMonth + "/";
            if (stringDay < 10) {
                ftime += "0";
            }
            ftime += stringDay + " / " + weekDays[getWeekOfDate(date)];
        }

        return ftime;
    }

    //获取日期字符串的具体的上下午时间
    public static String frendly_time3(String stringDate){
        String ftime="";

        if(StringUtils.isEmpty(stringDate)){
            return "";
        }

        Date date=toDate(stringDate);

        if(date==null)
            return stringDate;

        SimpleDateFormat format =dateFormat2.get();

        if(isToday(date.getTime())){
            format.applyPattern(isMorning(date.getTime())?"上午 hh:mm":"下午 hh:mm");
            ftime=format.format(date);
        }else if (isYesterday(date.getTime())) {
            format.applyPattern(isMorning(date.getTime()) ? "昨天 上午 hh:mm" : "昨天 下午 hh:mm");
            ftime = format.format(date);
        } else if (isCurrentYear(date.getTime())) {
            format.applyPattern(isMorning(date.getTime()) ? "MM-dd 上午 hh:mm" : "MM-dd 下午 hh:mm");
            ftime = format.format(date);
        } else {
            format.applyPattern(isMorning(date.getTime()) ? "yyyy-MM-dd 上午 hh:mm" : "yyyy-MM-dd 下午 hh:mm");
            ftime = format.format(date);
        }

        return ftime;
    }

    /*
    判断一个时间是不是上午
    @param when 时间的毫秒数
     */
    public static boolean isMorning(long when){
        Calendar calendar=Calendar.getInstance();

        calendar.setTimeInMillis(when);

        int hour=calendar.get(Calendar.HOUR_OF_DAY);

        return (hour>=0) && (hour<12);
    }

    /*
    判断一个时间是不是今天
    @param when 时间的毫秒数
     */
    public static boolean isToday(long when) {

        Calendar calendar = Calendar.getInstance();

        //获取指定时间值的年月日信息 注意:calendar的月份范围是从0-11
        calendar.setTimeInMillis(when);
        int whenYear = calendar.get(Calendar.YEAR);
        int whenMonth = calendar.get(Calendar.MONTH);
        int whenDay = calendar.get(Calendar.DAY_OF_MONTH);

        //获取当前系统时间的年月日信息
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        return (whenYear==currentYear) && (whenMonth==currentMonth) && (whenDay==currentDay);
    }

    /*
    判断一个时间字符串是否是今天
    @param stringDate 字符串
     */
    public static boolean isToday(String stringDate){
        boolean b=false;

        Date time=toDate(stringDate);
        Date today=new Date();

        if(time!=null){
            String nowDate=dateFormat2.get().format(today);
            String timeDate=dateFormat2.get().format(time);

            if(nowDate.equals(timeDate)){
                b=true;
            }
        }

        return b;
    }

    /*
    判断一个时间是不是昨天
    @param when 时间的毫秒数
     */
    public static boolean isYesterday(long when){
        Calendar calendar=Calendar.getInstance();

        calendar.setTimeInMillis(when);
        int whenYear=calendar.get(Calendar.YEAR);
        int whenMonth=calendar.get(Calendar.MONTH);
        int whenDay=calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear=calendar.get(Calendar.YEAR);
        int currentMonth=calendar.get(Calendar.MONTH);
        int currentDay=calendar.get(Calendar.DAY_OF_MONTH);

        return (whenYear==currentYear) && (whenMonth==currentMonth) &&(whenDay==currentDay-1);
    }

    /*
    判断一个时间是不是今年
    @param when 时间的毫秒数
     */
    public static  boolean isCurrentYear(long when){

        Calendar calendar=Calendar.getInstance();

        calendar.setTimeInMillis(when);
        int whenYear=calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear=calendar.get(Calendar.YEAR);

        return (whenYear==currentYear);
    }

    /*
    获取指定日期是一周的第几天
    @param  date 指定的Date对象
    @return 整数值
     */
    public static int getWeekOfDate(Date date){

        Calendar calendar=Calendar.getInstance();

        //设置Date对象为参数，使用setTime函数，设置毫秒数为参数，使用setTimeInMillis函数
        calendar.setTime(date);
        //sunday大部分地区默认返回值为1，所以与字符串数组配合使用时需要-1
        int w=calendar.get(Calendar.DAY_OF_WEEK)-1;

        if(w<0)
            w=0;

        return w;
    }

    /*
    判断两个日期字符串是否是同一天
    @param stringDate1 日期字符串
    @param stringDate2 日期字符串
     */
    public static boolean isSameDay(String stringDate1,String stringDate2){

        if(StringUtils.isEmpty(stringDate1) || StringUtils.isEmpty(stringDate2)){
            return false;
        }

        boolean b = false;

        Date date1 = toDate(stringDate1);

        Date date2 = toDate(stringDate2);

        if(date1!= null && date2 != null){

            String d1 = dateFormat2.get().format(date1);

            String d2 = dateFormat2.get().format(date2);

            if (d1.equals(d2)) {
                b = true;
            }
        }
        return b;
    }

    /*
    返回long类型的今天的日期
    @return 返回的是20180601这样的数据，返回的并不是毫秒数
     */
    public static long getToday(){
        Calendar calendar=Calendar.getInstance();

        String curDate=dateFormat2.get().format(calendar.getTime());

        curDate=curDate.replace("-","");

        return Long.parseLong(curDate);
    }

    //返回当前时间的字符串
    public static String getCurrentTimeStr(){
        Calendar calendar=Calendar.getInstance();

        String curDate=dateFormat1.get().format(calendar.getTime());

        return curDate;
    }

    /*
    计算两个时间字符串的时间差
    @return 注意这里返回的是秒而不是毫秒
     */
    public static long dateDifferent(String stringDate1,String stringDate2){
        long diff=0;

        try{
            Date date1=dateFormat1.get().parse(stringDate1);
            Date date2=dateFormat1.get().parse(stringDate2);

            diff=date1.getTime()-date2.getTime();
        }catch(Exception e){
            e.printStackTrace();;
        }

        return diff;
    }

    //获取当前的时间是每年的第几周
    public static int getWeekOfYear() {
        return getWeekOfYear(new Date());
    }

    //获取指定的时间是每年的第几周
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        int week = c.get(Calendar.WEEK_OF_YEAR) - 1;
        week = week == 0 ? 52 : week;
        return week > 0 ? week : 1;
    }

    /*
    根据指定的format返回当前的系统时间
    @param stringFormat 格式字符串
     */
    public static  String getSystemTime(String format){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);

        return simpleDateFormat.format(new Date());
    }


    //获取一个当前日期的整数数组
    public static int[] getCurrentDate(){
        int[] dateArray=new int[3];

        String[] temp=getSystemTime("yyyy-MM-dd").split("-");

        for(int i=0;i<3;i++){
            try{
                dateArray[i]=Integer.parseInt(temp[i]);
            }catch (Exception e){
                dateArray[i]=0;
            }
        }

        return dateArray;
    }

}
