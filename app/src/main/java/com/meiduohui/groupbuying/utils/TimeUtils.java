package com.meiduohui.groupbuying.utils;

import com.lidroid.xutils.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ALIY on 2018/12/16 0016.
 */

public class TimeUtils {

    // 获取当前时间
    public static String getCurrentTime(String format) {

        SimpleDateFormat df = new SimpleDateFormat(format);
        String time =  df.format(new Date());
        LogUtils.i("TimeUtils: getCurrentTime time " + time);
        return time;
    }

    // Long类型转时间
    public static String LongToString(long timestamp,String format) {

        SimpleDateFormat df = new SimpleDateFormat(format);
        String time =  df.format(new Date(timestamp*1000));
        LogUtils.i("TimeUtils: LongToString time " + time);
        return time;
    }

    // Date类型转时间
    public static String DateToString(Date date,String format) {

        SimpleDateFormat df = new SimpleDateFormat(format);
        String time =  df.format(date);
        LogUtils.i("TimeUtils: DateToString time " + time);
        return time;
    }

}
