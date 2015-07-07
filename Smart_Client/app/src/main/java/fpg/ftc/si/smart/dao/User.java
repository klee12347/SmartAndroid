/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 使用者帳號
 * Created by MarlinJoe on 2014/9/9.
 */
public class User {

    private String URID;

    private String ACCOUNT;

    private String PWD;

    private String USER_STATUS;

    private String MANGER_FLAG;

    private String NAME;

    public User(String URID, String ACCOUNT, String PWD, String USER_STATUS, String MANGER_FLAG, String NAME) {
        this.URID = URID;
        this.ACCOUNT = ACCOUNT;
        this.PWD = PWD;
        this.USER_STATUS = USER_STATUS;
        this.MANGER_FLAG = MANGER_FLAG;
        this.NAME = NAME;
    }

    public String getURID() {
        return URID;
    }

    public String getACCOUNT() {
        return ACCOUNT;
    }

    public String getPWD() {
        return PWD;
    }

    public String getUSER_STATUS() {
        return USER_STATUS;
    }

    public String getMANGER_FLAG() {
        return MANGER_FLAG;
    }

    public String getNAME() {
        return NAME;
    }
}
