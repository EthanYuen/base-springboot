package org.ethanyuen.springboot.utils;

import java.time.*;
import java.util.Date;

public class DateUtil {
    /**是否闰年
     * @param y
     * @return
     */
    public static boolean IfLeap(int y)
    {
        if(y%400==0)return true;
        if(y%100==0)return false;
        if(y%4==0)return true;
        return false;
    }

    /**获得某年某月的天数
     * @param y
     * @param m
     * @return
     */
    public static int GetDays(int y,int m)
    {
        if(m==4||m==6||m==9||m==11)return 30;
        if(m==2)
        {
            if(IfLeap(y))return 29;
            else return 28 ;
        }
        return 31;
    }
    //LocalDate -> Date
    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    //LocalDateTime -> Date
    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    //Date -> LocalDate
    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    //Date -> LocalDateTime
    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static long asTimestamp(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        return localDate.atStartOfDay(zone).toInstant().toEpochMilli();
    }
    public static long asTimestamp(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }
}
