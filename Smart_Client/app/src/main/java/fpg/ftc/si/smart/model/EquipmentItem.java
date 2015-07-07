package fpg.ftc.si.smart.model;

import java.io.Serializable;

/**
 * Created by MarlinJoe on 2014/8/13.
 */
public class EquipmentItem implements Serializable {


    /**
     * 設備ID
     */
    private String ID;
    private String NAME;

    /**
     * 管制點
     */
    private String CTLPTID;

    /**
     * 路線
     */
    private String WAYID;

    private int Total_Count;
    private int Finish_Count;

    public EquipmentItem(String ID, String NAME, String CTLPTID, String WAYID, int total_Count) {
        this.ID = ID;
        this.NAME = NAME;
        this.CTLPTID = CTLPTID;
        this.WAYID = WAYID;
        Total_Count = total_Count;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public int getTotal_Count() {
        return Total_Count;
    }

    public int getFinish_Count() {
        return Finish_Count;
    }

    public String getCTLPTID() {
        return CTLPTID;
    }

    public void setCTLPTID(String CTLPTID) {
        this.CTLPTID = CTLPTID;
    }

    public String getWAYID() {
        return WAYID;
    }

    public void setWAYID(String WAYID) {
        this.WAYID = WAYID;
    }

    public void setFinish_Count(int finish_Count) {
        Finish_Count = finish_Count;
    }
}
