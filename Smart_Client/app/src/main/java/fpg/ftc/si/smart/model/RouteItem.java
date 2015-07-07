package fpg.ftc.si.smart.model;

/**
 * Created by MarlinJoe on 2014/8/13.
 */
public class RouteItem {


    private String ID;
    private String NAME;
    private String ARRTIME;

    //UI 使用 紀錄目前巡檢時間 yyyyMMdd
    private String CURRENTDATE;

    public RouteItem(String ID, String NAME, String ARRTIME, String CURRENTDATE) {
        this.ID = ID;
        this.NAME = NAME;
        this.ARRTIME = ARRTIME;
        this.CURRENTDATE = CURRENTDATE;
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

    public String getARRTIME() {
        return ARRTIME;
    }

    public void setARRTIME(String ARRTIME) {
        this.ARRTIME = ARRTIME;
    }

    public String getCURRENTDATE() {
        return CURRENTDATE;
    }

    public void setCURRENTDATE(String CURRENTDATE) {
        this.CURRENTDATE = CURRENTDATE;
    }
}
