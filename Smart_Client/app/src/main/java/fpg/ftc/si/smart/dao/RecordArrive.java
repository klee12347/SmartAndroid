/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 抄表紀錄 到位
 * Created by MarlinJoe on 2014/9/9.
 */
public class RecordArrive {

    private String ID;

    //班別
    private String CLSID;

    //路線
    private String WAYID;

    //管制點
    private String CTLPTID;

    //到位日期 (實際寫入的)
    private String ARRIVE_DATE;

    //到位時間 (實際寫入的)
    private String ARRIVE_TIME;

    //檢查日期 (畫面自動帶 應該檢查時間)
    private String CHECK_DATE;

    //檢查時間 (畫面自動帶 應該檢查時間)
    private String CHECK_TIME;

    //檢查人員
    private String CHECK_URID;

    public String getID() {

        return CLSID + "_" + WAYID + "_" + CTLPTID + "_" + ARRIVE_DATE + "_" + CHECK_URID;

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    //異動時間
    private String TXTM;

    public RecordArrive(String ID, String CLSID, String WAYID, String CTLPTID, String ARRIVE_DATE, String ARRIVE_TIME, String CHECK_DATE, String CHECK_TIME, String CHECK_URID, String TXTM) {
        this.ID = ID;
        this.CLSID = CLSID;
        this.WAYID = WAYID;
        this.CTLPTID = CTLPTID;
        this.ARRIVE_DATE = ARRIVE_DATE;
        this.ARRIVE_TIME = ARRIVE_TIME;
        this.CHECK_DATE = CHECK_DATE;
        this.CHECK_TIME = CHECK_TIME;
        this.CHECK_URID = CHECK_URID;
        this.TXTM = TXTM;
    }

    /**
     * 新增 操表紀錄時使用
     * @param CLSID
     * @param WAYID
     * @param CTLPTID
     * @param ARRIVE_DATE
     * @param ARRIVE_TIME
     * @param CHECK_URID
     * @param TXTM
     */
    public RecordArrive(String CLSID, String WAYID, String CTLPTID, String ARRIVE_DATE, String ARRIVE_TIME,  String CHECK_URID, String TXTM) {
        this.CLSID = CLSID;
        this.WAYID = WAYID;
        this.CTLPTID = CTLPTID;
        this.ARRIVE_DATE = ARRIVE_DATE;
        this.ARRIVE_TIME = ARRIVE_TIME;
        this.CHECK_URID = CHECK_URID;
        this.TXTM = TXTM;
    }

    public String getCLSID() {
        return CLSID;
    }

    public String getWAYID() {
        return WAYID;
    }

    public String getCTLPTID() {
        return CTLPTID;
    }

    public String getARRIVE_DATE() {
        return ARRIVE_DATE;
    }

    public String getARRIVE_TIME() {
        return ARRIVE_TIME;
    }

    public String getCHECK_DATE() {
        return CHECK_DATE;
    }

    public String getCHECK_TIME() {
        return CHECK_TIME;
    }

    public String getCHECK_URID() {
        return CHECK_URID;
    }

    public String getTXTM() {
        return TXTM;
    }
}
