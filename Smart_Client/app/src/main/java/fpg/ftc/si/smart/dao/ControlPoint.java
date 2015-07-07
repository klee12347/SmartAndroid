/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 管制點
 * Created by MarlinJoe on 2014/9/9.
 */
public class ControlPoint {

    private String CTLPTID;

    private String CTLPTNM;

    private String WAYID;

    private Integer ORDER;

    private Integer MIN1;

    private Integer MIN2;

    public ControlPoint(String CTLPTID, String CTLPTNM, String WAYID, Integer ORDER, Integer MIN1, Integer MIN2) {
        this.CTLPTID = CTLPTID;
        this.CTLPTNM = CTLPTNM;
        this.WAYID = WAYID;
        this.ORDER = ORDER;
        this.MIN1 = MIN1;
        this.MIN2 = MIN2;
    }

    public String getCTLPTID() {
        return CTLPTID;
    }

    public String getCTLPTNM() {
        return CTLPTNM;
    }

    public String getWAYID() {
        return WAYID;
    }

    public Integer getORDER() {
        return ORDER;
    }

    public Integer getMIN1() {
        return MIN1;
    }

    public Integer getMIN2() {
        return MIN2;
    }
}
