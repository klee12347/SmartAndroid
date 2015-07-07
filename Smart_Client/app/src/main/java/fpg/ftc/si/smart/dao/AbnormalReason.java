/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 異常原因
 * Created by MarlinJoe on 2014/9/9.
 */
public class AbnormalReason {

    private String ABID;

    private String STDID;

    private String ABDESC;

    public AbnormalReason(String ABID, String STDID, String ABDESC) {
        this.ABID = ABID;
        this.STDID = STDID;
        this.ABDESC = ABDESC;
    }

    public String getABID() {
        return ABID;
    }

    public String getSTDID() {
        return STDID;
    }

    public String getABDESC() {
        return ABDESC;
    }
}
