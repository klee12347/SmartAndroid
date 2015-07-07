/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;


import java.util.List;

/**
 * 抄表紀錄 資料
 * Created by MarlinJoe on 2014/9/9.
 */
public class RecordData {

    private String ID;

    private String CLSID;

    private String WAYID;

    //管制點
    private String CTLPTID;

    //設備 (參考用)
    private String EQID;

    //基準
    private String STDID;

    //到位日期
    private String ARRIVE_DATE;

    //到位時間
    private String ARRIVE_TIME;

    //檢查日期 (畫面自動帶 應該檢查時間)
    private String CHECK_DATE;

    //檢查時間
    private String CHECK_TIME;

    //完成日期 CheckItem UI存放的
    private String FINISH_DATE;

    //完成時間
    private String FINISH_TIME;

    //檢查人員
    private String CHECK_URID;

    //抄表值
    private Float CHECK_VAL;

    //感官
    private String FEEL_FLAG;

    //異常
    private String ABID;

    //處理方式
    private String DEALID;

    //異動時間
    private String TXTM;

    //備註
    private String COMMENT;

    //異常照片
    private List<RecordDataPic> RecordDataPics;

    /**
     * 新增時使用
     * @param CLSID
     * @param WAYID
     * @param CTLPTID
     * @param EQID
     * @param STDID
     * @param ARRIVE_DATE
     * @param ARRIVE_TIME
     * @param CHECK_DATE
     * @param CHECK_TIME
     * @param FINISH_DATE
     * @param FINISH_TIME
     * @param CHECK_URID
     * @param CHECK_VAL
     * @param FEEL_FLAG
     * @param ABID
     * @param DEALID
     * @param TXTM
     * @param COMMENT
     */
    public RecordData(String CLSID, String WAYID, String CTLPTID, String EQID, String STDID, String ARRIVE_DATE, String ARRIVE_TIME, String CHECK_DATE, String CHECK_TIME, String FINISH_DATE, String FINISH_TIME, String CHECK_URID, Float CHECK_VAL, String FEEL_FLAG, String ABID, String DEALID, String TXTM, String COMMENT) {
        this.CLSID = CLSID;
        this.WAYID = WAYID;
        this.CTLPTID = CTLPTID;
        this.EQID = EQID;
        this.STDID = STDID;
        this.ARRIVE_DATE = ARRIVE_DATE;
        this.ARRIVE_TIME = ARRIVE_TIME;
        this.CHECK_DATE = CHECK_DATE;
        this.CHECK_TIME = CHECK_TIME;
        this.FINISH_DATE = FINISH_DATE;
        this.FINISH_TIME = FINISH_TIME;
        this.CHECK_URID = CHECK_URID;
        this.CHECK_VAL = CHECK_VAL;
        this.FEEL_FLAG = FEEL_FLAG;
        this.ABID = ABID;
        this.DEALID = DEALID;
        this.TXTM = TXTM;
        this.COMMENT = COMMENT;
    }

    public String getID() {

        return CLSID + "_" + WAYID + "_" + CTLPTID + "_" + ARRIVE_DATE + "_" + CHECK_URID + "_" + STDID;

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEQID() {return EQID;}

    public String getCLSID() {
        return CLSID;
    }

    public String getWAYID() {
        return WAYID;
    }

    public String getCTLPTID() {
        return CTLPTID;
    }

    public String getSTDID() {
        return STDID;
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

    public String getFINISH_DATE() {
        return FINISH_DATE;
    }

    public String getFINISH_TIME() {
        return FINISH_TIME;
    }

    public String getCHECK_URID() {
        return CHECK_URID;
    }

    public Float getCHECK_VAL() {
        return CHECK_VAL;
    }

    public String getFEEL_FLAG() {
        return FEEL_FLAG;
    }

    public String getABID() {
        return ABID;
    }

    public String getDEALID() {
        return DEALID;
    }

    public String getTXTM() {
        return TXTM;
    }

    public String getCOMMENT() {
        return COMMENT;
    }

    public List<RecordDataPic> getRecordDataPics() {
        return RecordDataPics;
    }

    public void setRecordDataPics(List<RecordDataPic> recordDataPics) {
        RecordDataPics = recordDataPics;
    }
}
