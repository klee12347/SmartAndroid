package fpg.ftc.si.smart.dao;


/**
 * Created by MarlinJoe on 2014/8/18.
 */
public class Depart {

//    DEPID: "b1f8a371-4121-4976-a766-8a664cb6b608,5612eed9-934e-4824-91e9-4c0d6b697933",
//    PARENT_DEPID: "*",
//    NAME: "中纖集團"

    //region Member
    private String DEPID;

    private String PARENT_DEPID;

    private String NAME;

    private String FULLPATH;

    private String FULLPATH_ID;

    private Boolean ISEND;

    private String TXTM;
    //endregion

    public Depart(String DEPID, String PARENT_DEPID, String NAME, String FULLPATH, String FULLPATH_ID, Boolean ISEND, String TXTM) {
        this.DEPID = DEPID;
        this.PARENT_DEPID = PARENT_DEPID;
        this.NAME = NAME;
        this.FULLPATH = FULLPATH;
        this.FULLPATH_ID = FULLPATH_ID;
        this.ISEND = ISEND;
        this.TXTM = TXTM;
    }

    public String getDEPID() {
        return DEPID;
    }

    public void setDEPID(String DEPID) {
        this.DEPID = DEPID;
    }

    public String getPARENT_DEPID() {
        return PARENT_DEPID;
    }

    public void setPARENT_DEPID(String PARENT_DEPID) {
        this.PARENT_DEPID = PARENT_DEPID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getFULLPATH() {
        return FULLPATH;
    }

    public void setFULLPATH(String FULLPATH) {
        this.FULLPATH = FULLPATH;
    }

    public String getFULLPATH_ID() {
        return FULLPATH_ID;
    }

    public void setFULLPATH_ID(String FULLPATH_ID) {
        this.FULLPATH_ID = FULLPATH_ID;
    }

    public Boolean getISEND() {
        return ISEND;
    }

    public void setISEND(Boolean ISEND) {
        this.ISEND = ISEND;
    }

    public String getTXTM() {
        return TXTM;
    }

    public void setTXTM(String TXTM) {
        this.TXTM = TXTM;
    }
}
