/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 抄表紀錄 RFID異常
 * Created by MarlinJoe on 2014/9/9.
 */
public class RecordRFID {

    //動態產生的
    private String ID;

    //班別
    private String CLSID;

    //路線
    private String WAYID;

    //管制點
    private String CTLPTID;

    //到位日期
    private String ARRIVE_DATE;

    //檢查人員
    private String CHECK_URID;

    //未刷卡原因
    private String BRKDKIN;

    //異動時間
    private String TXTM;

    public String getID() {

        return CLSID + "_" + WAYID + "_" + CTLPTID + "_" + ARRIVE_DATE + "_" + CHECK_URID;

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public RecordRFID(String CLSID, String WAYID, String CTLPTID, String ARRIVE_DATE, String CHECK_URID, String BRKDKIN, String TXTM) {
        this.CLSID = CLSID;
        this.WAYID = WAYID;
        this.CTLPTID = CTLPTID;
        this.ARRIVE_DATE = ARRIVE_DATE;
        this.CHECK_URID = CHECK_URID;
        this.BRKDKIN = BRKDKIN;
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

    public String getCHECK_URID() {
        return CHECK_URID;
    }

    public String getBRKDKIN() {
        return BRKDKIN;
    }

    public String getTXTM() {
        return TXTM;
    }
}
