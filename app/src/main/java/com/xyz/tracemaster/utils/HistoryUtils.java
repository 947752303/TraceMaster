package com.xyz.tracemaster.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

/**
 * <pre>
 *     author : xyz
 *     time   : 2022/4/22 10:08
 * </pre>
 */
public class HistoryUtils {

    public static String getRecordTime(Long millisecondDate) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return formatter.format(millisecondDate);
    }

    public static String getUseTime(Long startTime, Long endTime) {
        Long diff = endTime - startTime;
        long sec = diff / 1000;
        long min = diff / 60 / 1000;
        long hours = diff / 60 / 60 / 1000;

        return "用时:" + hours + "小时 " + min + "分钟 " + sec + "秒";


    }
}
