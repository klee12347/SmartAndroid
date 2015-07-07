package fpg.ftc.si.smart.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

import fpg.ftc.si.smart.util.consts.SystemConstants;


/**
 * 設定檔操作相關
 * Created by MarlinJoe on 2014/2/10.
 */
public final class PreferenceUtils {

    private static PreferenceUtils sInstance;
    private final SharedPreferences mPreferences;



    /**
     * Constructor for <code>PreferenceUtils</code>
     *
     * @param context The {@link android.content.Context} to use.
     */
    public PreferenceUtils(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @param context The {@link android.content.Context} to use.
     * @return A singleton of this class
     */
    public static final PreferenceUtils getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * 基本API URL路徑
     * http://192.168.1.1/SmartWebAPI/Smart/api/
     * @return
     */
    public final String getBaseUrl() {
//        return SystemConstants.HTTP + mPreferences.getString(SystemConstants.KEY_WEB_IP, "192.168.1.1") + "/" + mPreferences.getString(SystemConstants.KEY_WEB_NAME, "SmartWebAPI") +"/api/";
        return SystemConstants.HTTP + mPreferences.getString(SystemConstants.KEY_WEB_IP, "10.232.227.219") + "/" + mPreferences.getString(SystemConstants.KEY_WEB_NAME, "SmartWebAPI") +"/Smart/api/";
    }

    /**
     * 取得組織樹Hash去確認有無需要更新
     * @return
     */
    public final String getDepartHashUrl() {
        return getBaseUrl() + "DepartHash";
    }

    /**
     * 取得組織樹資料
     * @return
     */
    public final String getDepartUrl() {
        return getBaseUrl() + "Depart";
    }

    /**
     * 取得人員資料
     * @return
     */
    public final String getUserUrl() {
        return getBaseUrl() + "User";
    }


    /**
     * 取得APP檢查更新位置
     * @return
     */
    public final String getAppUpdateUrl() {
        return getBaseUrl() + "Ver";
    }

    public final String getApkUrl() {
        return getBaseUrl() + "Release";
    }

    /**
     * 巡檢資料下載 (下載 SQLITE zip檔案)
     * EX: http://192.168.1.1/SmartWebAPI/Smart/api/Download?urid=b060f9c5-b74e-4822-ae04-aa937265d1fb,436e0140-7a96-4260-a9c1-b61f3c4695ef
     * @return
     */
    public final String getDownloadUrl() { return getBaseUrl() + "Download";}

    public final String getUploadUrl() { return getBaseUrl() + "Upload";}

    /**
     * 取得儲存路徑
     * @return
     */
    public final String getFilePath() {
        String key = SystemConstants.KEY_FILE_PATH;
        //預設放在SD目錄中
        String sd_path = "";
        //確定SD卡可讀寫
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = android.os.Environment.getExternalStorageDirectory();
            sd_path = sdFile.getPath() + File.separator + "Smart";
            File dirFile = new File(sd_path);
            if(!dirFile.exists()){//如果資料夾不存在
                dirFile.mkdir();//建立資料夾
            }
        }
        return mPreferences.getString(key, sd_path);
    }

    /**
     * 設定儲存路徑
     * @param value
     */
    public void setFilePath(final String value) {
        String key = SystemConstants.KEY_FILE_PATH;
        mPreferences.edit().putString(key,value).commit();
    }

    /**
     * 取得此APP的Temp目錄 (暫存目錄)
     * @return
     */
    public final String getTempPath()
    {
        String result = "";
        String sd_path = getFilePath();
        //確定SD卡可讀寫
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = android.os.Environment.getExternalStorageDirectory();
            result = sd_path + File.separator + "Temp";
            File dirFile = new File(result);
            if(!dirFile.exists()){//如果資料夾不存在
                dirFile.mkdir();//建立資料夾
            }
        }
        return  result;
    }

    /**
     * 取得此APP的Temp目錄 (實際存放照片)
     * @return
     */
    public final String getPicPath()
    {
        String result = "";
        String sd_path = getFilePath();
        //確定SD卡可讀寫
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File sdFile = android.os.Environment.getExternalStorageDirectory();
            result = sd_path + File.separator + "Pic";
            File dirFile = new File(result);
            if(!dirFile.exists()){//如果資料夾不存在
                dirFile.mkdir();//建立資料夾
            }
        }
        return  result;
    }

    /**
     * 抄表模式
     * NOTE:目前預設為列表式
     * @return
     */
    public final boolean getModeIfPage()
    {
        String key = SystemConstants.KEY_MODEIFPAGE;
        return mPreferences.getBoolean(key,false);
    }

    public void setModeIfPage(boolean value)
    {
        String key = SystemConstants.KEY_MODEIFPAGE;
        mPreferences.edit().putBoolean(key,value).commit();
    }

}
