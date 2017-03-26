package com.lcc.oa.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间转换工具
 */
public class DateUtil {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Date StringToDate(String dateString, String formatString) {
        try {
            DateFormat fm = new SimpleDateFormat(formatString);
            Date date = null;
            try {
                date = fm.parse(dateString);
            } catch (java.text.ParseException e) {
                //e.printStackTrace();
                return null;
            } // Thu Jan 18 00:00:00 CST 2007
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public static String DateToString(Date date, String formatString) {
        try {
            DateFormat fm = new SimpleDateFormat(formatString);
            String str = new String();
            str = fm.format(date);
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    public static String penaltyString(int second) {
        int sec = second % 60;
        second = second / 60;
        int min = second % 60;
        int hour = second = second / 60;

        String nowTime = new String();

        if (hour < 10) {
            if (hour == 0) nowTime = "00";
            nowTime = "0" + hour;
        } else {
            nowTime = Integer.toString(hour);
        }

        if (min < 10) {
            nowTime = nowTime + ":0" + min;
        } else {
            nowTime = nowTime + ":" + min;
        }

        if (sec < 10) {
            nowTime = nowTime + ":0" + sec;
        } else {
            nowTime = nowTime + ":" + sec;
        }

        return nowTime;
    }

    public static String secondToString(long second) {

        long sec = second % 60;
        second = second / 60;
        long min = second % 60;
        long hour = second = second / 60;

        String nowTime = new String();

        if (hour < 10) {
            if (hour == 0) nowTime = "00";
            nowTime = "0" + hour;
        } else {
            nowTime = Long.toString(hour);
        }

        if (min < 10) {
            nowTime = nowTime + ":0" + min;
        } else {
            nowTime = nowTime + ":" + min;
        }

        if (sec < 10) {
            nowTime = nowTime + ":0" + sec;
        } else {
            nowTime = nowTime + ":" + sec;
        }

        return nowTime;
    }

    public static String toFriendlyDate(Date time) {
        if (time == null) return "unknown";
        int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);
        if (ct < 3600)
            return Math.max(ct / 60, 1) + " minutes ago";
        // 48小时
        if (ct >= 3600 && ct < 172800)
            return ct / 3600 + " hours ago";
        //86400 * 30
        if (ct >= 172800 && ct < 2592000) {
            int day = ct / 86400;
            return day + " days ago";
        }
        //到24个月
        if (ct >= 2592000 && ct < 62208000)
            return ct / 2592000 + " months ago";
        return ct / 31104000 + " years ago";
    }

    /**
     * 返回年份
     */
    public static int getYear(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.YEAR);
    }

    /**
     * 返回月份
     */
    public static int getMonth(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.MONTH) + 1;
    }

    /**
     * 返回天
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回秒钟
     */
    public static int getSecond(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.SECOND);
    }

    /**
     * 返回小时
     */
    public static int getHour(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回分钟
     */
    public static int getMinute(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.get(java.util.Calendar.MINUTE);
    }

    /**
     * 返回毫秒
     */
    public static long getMillis(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    // 	日期相减
    //	相差天数：long day=difference/(3600*24*1000)
    //	相差小时：long hour=difference/(3600*1000)
    //	相差分钟：long minute=difference/(60*1000)
    //	相差秒： long second=difference/1000
    public static int diffDate(java.util.Date date, java.util.Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (3600 * 1000));
    }

    public static String getTodayString() {
        Date d = new Date();
        return sdf.format(d);
    }
}
