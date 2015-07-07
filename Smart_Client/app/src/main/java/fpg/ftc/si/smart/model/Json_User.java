/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.model;



/**
 * Created by MarlinJoe on 2014/9/5.
 */
public class Json_User {

    private String DEPID;

    private String NAME;

    private String URID;

    private String ACCOUNT;

    public Json_User(String DEPID, String NAME, String URID, String ACCOUNT) {
        this.DEPID = DEPID;
        this.NAME = NAME;
        this.URID = URID;
        this.ACCOUNT = ACCOUNT;
    }

    public String getDEPID() {
        return DEPID;
    }

    public void setDEPID(String DEPID) {
        this.DEPID = DEPID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getURID() {
        return URID;
    }

    public void setURID(String URID) {
        this.URID = URID;
    }

    public String getACCOUNT() {
        return ACCOUNT;
    }

    public void setACCOUNT(String ACCOUNT) {
        this.ACCOUNT = ACCOUNT;
    }
}
