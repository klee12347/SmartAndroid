/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.model;

import java.io.Serializable;

/**
 * 對策
 * Created by MarlinJoe on 2014/9/17.
 */
public class DealMethodItem implements Serializable {

    public String DEALID;
    public String ABID;
    public String DEALNAME;

    public DealMethodItem(String DEALID, String ABID, String DEALNAME) {
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
