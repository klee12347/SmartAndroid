package fpg.ftc.si.smart.model;

import java.util.Date;

/**
 * Created by MarlinJoe on 2014/8/13.
 */
public class PointItem {


    private String CTLPTID;
    private String RFID;
    private String NAME;
    private String WAYID;

    private String SETARRTIME;//基準時間

    //開始時間
    private Date TIME_START;

    //結束時間
    private Date TIME_END;

    //應做設備數
    private int Total_Count;

    //實作設備數
    private int Finish_Count;


    public PointItem(String CTLPTID, String RFID, String NAME, String WAYID, Date TIME_START, Date TIME_END) {
        this.CTLPTID = CTLPTID;
        this.RFID = RFID;
        this.NAME = NAME;
        this.WAYID = WAYID;
        this.TIME_START = TIME_START;
        this.TIME_END = TIME_END;
    }

    public String getCTLPTID() {
        return CTLPTID;
    }

    public void setCTLPTID(String CTLPTID) {
        this.CTLPTID = CTLPTID;
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getSETARRTIME() {
        return SETARRTIME;
    }

    public void setSETARRTIME(String SETARRTIME) {
        this.SETARRTIME = SETARRTIME;
    }

    public String getWAYID() {
        return WAYID;
    }

    public void setWAYID(String WAYID) {
        this.WAYID = WAYID;
    }

    public Date getTIME_START() {
        return TIME_START;
    }

    public void setTIME_START(Date TIME_START) {
        this.TIME_START = TIME_START;
    }

    public Date getTIME_END() {
        return TIME_END;
    }

    public void setTIME_END(Date TIME_END) {
        this.TIME_END = TIME_END;
    }

    public int getTotal_Count() {
        return Total_Count;
    }

    public void setTotal_Count(int total_Count) {
        Total_Count = total_Count;
    }

    public int getFinish_Count() {
        return Finish_Count;
    }

    public void setFinish_Count(int finish_Count) {
        Finish_Count = finish_Count;
    }
}
