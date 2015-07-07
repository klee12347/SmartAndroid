package fpg.ftc.si.smart.util.consts;

/**
 * 系統常數
 * Created by MarlinJoe on 2014/2/10.
 */
public final class SystemConstants {

    /**
     * 自動檢查更新 儲存的檔案名稱
     */
    public static final String UPDATE_SAVENAME = "Smart.apk";
    public static final String UPDATE_APK_DOWNLOAD_BASE_URL = "http://10.110.3.41/Android/Release/";



    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";

    public static final String PACKAGE_NAME = "fpg.ftc.si.smart";

    //API基本路徑
    public static final String KEY_API_BASEURL = "/SmartWebAPI/api/";

    // SQLite APP設定 檔案名稱
    public static final String SQLITE_BASIC = "Basic.db";

    // SQLite 基礎資料 檔案名稱
    public static final String SQLITE_SMART = "Smart.db";

    // 從遠端主機下載下來的檔案所存在Andorid 檔案的名稱
    public static final String DOWNLOAD_FILE_NAME = "Smart.zip";

    //上传的檔案所存在Andorid 檔案的名稱 及时删除
    public static final String UPLOAD_FILE_NAME = "Upload.zip";



    //region 系統設定參數 (Preference)

    // 儲存路徑
    public static final String KEY_FILE_PATH = "KEY_FILE_PATH";

    // 計算部門組織hash (為了檢查有無需要自動更新部門組織)
    public static final String KEY_DEPART_HASH = "KEY_DEPART_HASH";

    // WEB Service 位置
    public static final String KEY_WEB_IP = "KEY_WEB_IP";

    // WEB 站台名稱
    public static final String KEY_WEB_NAME = "KEY_WEB_NAME";

    //endregion

    //偏好設定

    //
    public static final String KEY_MODEIFPAGE = "KEY_MODEIFPAGE";

    public static final int REQUEST_TAKE_PHOTO = 2003;


//資料路徑整理


}
