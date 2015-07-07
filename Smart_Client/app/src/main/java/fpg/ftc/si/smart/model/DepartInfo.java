package fpg.ftc.si.smart.model;

/**
 * Created by MarlinJoe on 2014/8/8.
 */
public class DepartInfo {

    //ID
    public String DepartID;

    //名稱
    public String DepartName;

    //最後一層
    public boolean IsEnd;

    public boolean IsDir;

    //TODO 未來可以加 部門人數
    public int Count;

    //是否被選取
    public boolean Selected;

    public DepartInfo(String departID, String departName, boolean isEnd) {
        DepartID = departID;
        DepartName = departName;
        IsEnd = isEnd;
    }

    public String getDepartID() {
        return DepartID;
    }

    public String getDepartName() {
        return DepartName;
    }

    public boolean isEnd() {
        return IsEnd;
    }
}
