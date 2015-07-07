/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 班別
 * Created by MarlinJoe on 2014/9/9.
 */
public class Shift {

    private String CLSID;

    private String CLSNM;

    private String FIRST_TIME;

    public Shift(String CLSID, String CLSNM, String FIRST_TIME) {
        this.CLSID = CLSID;
        this.CLSNM = CLSNM;
        this.FIRST_TIME = FIRST_TIME;
    }

    public String getCLSID() {
        return CLSID;
    }

    public String getCLSNM() {
        return CLSNM;
    }

    public String getFIRST_TIME() {
        return FIRST_TIME;
    }
}
