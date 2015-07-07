/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 設備
 * Created by MarlinJoe on 2014/9/9.
 */
public class Equipment {

    private String EQID;

    private String CTLPTID;

    private String EQNM;

    public Equipment(String EQID, String CTLPTID, String EQNM) {
        this.EQID = EQID;
        this.CTLPTID = CTLPTID;
        this.EQNM = EQNM;
    }

    public String getEQID() {
        return EQID;
    }

    public String getCTLPTID() {
        return CTLPTID;
    }

    public String getEQNM() {
        return EQNM;
    }
}
