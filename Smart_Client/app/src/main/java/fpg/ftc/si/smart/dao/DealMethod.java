/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 對策
 * Created by MarlinJoe on 2014/9/9.
 */
public class DealMethod {

    private String DEALID;

    private String ABID;

    private String DEALNAME;

    public DealMethod(String DEALID, String ABID, String DEALNAME) {
        this.DEALID = DEALID;
        this.ABID = ABID;
        this.DEALNAME = DEALNAME;
    }

    public String getDEALID() {
        return DEALID;
    }

    public String getABID() {
        return ABID;
    }

    public String getDEALNAME() {
        return DEALNAME;
    }
}
