package com.teacore.teascript.util;

import java.util.Date;
import java.util.TimeZone;

/**用于判断用户的时区
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-1-5
 */
public class TimeZoneUtils {

    //判断用户是否为东八区
    public static boolean isInEasternEightZones(){
        boolean defaultValue=true;

        if(TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08"))
        defaultValue=true;
        else
        defaultValue=false;

        return defaultValue;
    }

    //根据时区修改日期
    public static Date transformTime(Date date, TimeZone oldZone, TimeZone newZone) {

        Date finalDate = null;

        if (date != null) {
            int timeOffset = oldZone.getOffset(date.getTime())
                    - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }

        return finalDate;
    }

}
