/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 路線
 * Created by MarlinJoe on 2014/9/9.
 */
public class Route {

    private String WAYID;

    private String WAYNM;

    private String URID;

    private String CLSID;

    private String BEGTM;

    private String ENDTM;

    public Route(String WAYID, String WAYNM, String URID, String CLSID, String BEGTM, String ENDTM) {
        this.WAYID = WAYID;
        this.WAYNM = WAYNM;
        this.URID = URID;
        this.CLSID = CLSID;
        this.BEGTM = BEGTM;
        this.ENDTM = ENDTM;
    }

    public String getWAYID() {
        return WAYID;
    }

    public void setWAYID(String WAYID) {
        this.WAYID = WAYID;
    }

    public String getWAYNM() {
        return WAYNM;
    }

    public void setWAYNM(String WAYNM) {
        this.WAYNM = WAYNM;
    }

    public String getURID() {
        return URID;
    }

    public void setURID(String URID) {
        this.URID = URID;
    }

    public String getCLSID() {
        return CLSID;
    }

    public void setCLSID(String CLSID) {
        this.CLSID = CLSID;
    }

    public String getBEGTM() {
        return BEGTM;
    }

    public void setBEGTM(String BEGTM) {
        this.BEGTM = BEGTM;
    }

    public String getENDTM() {
        return ENDTM;
    }

    public void setENDTM(String ENDTM) {
        this.ENDTM = ENDTM;
    }
}
