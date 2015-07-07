/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;

/**
 * Created by MarlinJoe on 2014/8/18.
 */
public class AppSetting {

    private String SETTING_ID;
    private String PARAMETER;
    private String TXTM;

    public AppSetting(String SETTING_ID, String PARAMETER, String TXTM) {
        this.SETTING_ID = SETTING_ID;
        this.PARAMETER = PARAMETER;
        this.TXTM = TXTM;
    }

    public String getSETTING_ID() {
        return SETTING_ID;
    }

    public String getPARAMETER() {
        return PARAMETER;
    }

    public String getTXTM() {
        return TXTM;
    }
}
