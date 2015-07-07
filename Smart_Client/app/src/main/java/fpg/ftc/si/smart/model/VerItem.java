/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.model;

import java.io.Serializable;

/**
 * 版號檢查物件
 * Created by MarlinJoe on 2014/5/12.
 */
public class VerItem implements Serializable {

    private String AppName;
    private String ApkName;
    private String VerName;//給人看得版號
    private String ReleaseNote;
    private Integer VerCode;//程式再判斷的版號
    private String DateReleased;
    private boolean IsForceUpdate;//強制是否一定要更新才能使用APP

    public VerItem(String appName, String apkName, String verName, String releaseNote, Integer verCode, String dateReleased, boolean isForceUpdate) {
        AppName = appName;
        ApkName = apkName;
        VerName = verName;
        ReleaseNote = releaseNote;
        VerCode = verCode;
        DateReleased = dateReleased;
        IsForceUpdate = isForceUpdate;
    }

    public String getAppName() {
        return AppName;
    }

    public String getApkName() {
        return ApkName;
    }

    public String getVerName() {
        return VerName;
    }

    public String getReleaseNote() {
        return ReleaseNote;
    }

    public Integer getVerCode() {
        return VerCode;
    }

    public String getDateReleased() {
        return DateReleased;
    }


}
