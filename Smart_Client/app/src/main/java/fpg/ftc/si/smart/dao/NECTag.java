/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;

/**
 * 卡片
 * Created by MarlinJoe on 2014/9/9.
 */
public class NECTag {

    private String TAGID;

    private String CTLPTID;

    private String IS_ENABLE;

    public NECTag(String CTLPTID, String TAGID, String IS_ENABLE) {
        this.CTLPTID = CTLPTID;
        this.TAGID = TAGID;
        this.IS_ENABLE = IS_ENABLE;
    }

    public String getTAGID() {
        return TAGID;
    }

    public String getCTLPTID() {
        return CTLPTID;
    }

    public String getIS_ENABLE() {
        return IS_ENABLE;
    }
}
