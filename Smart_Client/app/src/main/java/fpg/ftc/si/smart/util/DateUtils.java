/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.smart.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期處理
 * Created by MarlinJoe on 2014/3/3.
 */
public class DateUtils {


    //region 常數

    public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String FORMAT_YYYYMMDD_HHMMSS = "yyyyMMdd HHmmss";
    public static final String FORMAT_HHMMSS = "HHmmss";
    public static final String FORMAT_HHMM = "HHmm";

    //endregion

    /**
     * 取得目前時間
     * @param format
     * @return
     */
    public static String getCurrentDate(String format)
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 將傳入的數字格式化 aDigits 位數的字串，不滿位數則補零
     * @param aNumber
     * @param aDigits
     * @return
     */
    private static String formatNumber(int aNumber, int aDigits) {
        StringBuffer sbFmt = new StringBuffer();
        NumberFormat formatter;

        for (int i = 0; i < aDigits; i++) {
            sbFmt.append("0");
        }

        formatter = new DecimalFormat(sbFmt.toString());
        return formatter.format(aNumber);
    }


    /**
     * 格式化字串
     * @param source
     * @param format
     * @return
     */
    public static String format(Date source,String format)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(source);
    }

    /**
     * hhmmss
     * @param source
     * @return
     */
    public static String converTimeToString(Date source)
    {
        //目前時間
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        return sdf.format(source);

    }

    /**
     * hhmm
     * @param source
     * @return
     */
    public static String converHourMinToString(Date source)
    {
        //目前時間
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        return sdf.format(source);

    }


    /**
     * 加上幾分鐘
     * @param beforeTime 目前時間
     * @param minutes 幾分鐘
     * @return
     */
    public static Date addMinsToDate(Date beforeTime,int minutes){
        final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }
}
