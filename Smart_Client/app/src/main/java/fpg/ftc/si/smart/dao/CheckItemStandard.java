/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 基準
 * Created by MarlinJoe on 2014/9/9.
 */
public class CheckItemStandard {

    private String STDID;

    private String STDNM;

    private String IsFeelItem;

    private Float CHKSTDVAL;

    private Float HIGHVAL;

    private Float LOWVAL;

    private String UNIT;

    private String EQID;

    public CheckItemStandard(String STDID, String STDNM, String isFeelItem, Float CHKSTDVAL, Float HIGHVAL, Float LOWVAL, String UNIT, String EQID) {
        this.STDID = STDID;
        this.STDNM = STDNM;
        IsFeelItem = isFeelItem;
        this.CHKSTDVAL = CHKSTDVAL;
        this.HIGHVAL = HIGHVAL;
        this.LOWVAL = LOWVAL;
        this.UNIT = UNIT;
        this.EQID = EQID;
    }

    public String getSTDID() {
        return STDID;
    }

    public String getSTDNM() {
        return STDNM;
    }

    public String getIsFeelItem() {
        return IsFeelItem;
    }

    public Float getCHKSTDVAL() {
        return CHKSTDVAL;
    }

    public Float getHIGHVAL() {
        return HIGHVAL;
    }

    public Float getLOWVAL() {
        return LOWVAL;
    }

    public String getUNIT() {
        return UNIT;
    }

    public String getEQID() {
        return EQID;
    }
}
