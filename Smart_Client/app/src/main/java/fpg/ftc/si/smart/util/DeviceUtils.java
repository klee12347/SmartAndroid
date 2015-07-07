/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.SyncStateContract;
import android.util.Log;

import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.util.consts.SystemConstants;

import static fpg.ftc.si.smart.util.LogUtils.LOGE;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 取得設備相關資訊
 * Created by MarlinJoe on 2014/10/17.
 */
public class DeviceUtils {

    private static final String TAG = makeLogTag(DeviceUtils.class);

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(SystemConstants.PACKAGE_NAME, 0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {

            LOGE(TAG,ex.getMessage());
        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(SystemConstants.PACKAGE_NAME, 0).versionName;

        } catch (PackageManager.NameNotFoundException ex) {
            LOGE(TAG,ex.getMessage());
        }
        return verName;

    }

    /**
     * 取得目前此APP的名稱
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        String verName = context.getResources()
                .getText(R.string.app_name).toString();
        return verName;
    }

    /**
     * 取得設備名稱
     * @return
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
