/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.smart.provider;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;


import fpg.ftc.si.smart.util.PreferenceUtils;

import static fpg.ftc.si.smart.util.LogUtils.*;

/**
 * 為了要將SQLITE DB的存放位置放在SD目錄中,所以繼承 ContextWrapper 自訂 DatabaseContext
 * http://www.cnblogs.com/esrichina/p/3347036.html
 * http://stackoverflow.com/questions/5332328/sqliteopenhelper-problem-with-fully-qualified-db-path-name/9168969#9168969
 * Created by MarlinJoe on 2014/2/22.
 */
public class DatabaseContext extends ContextWrapper {

    private static final String TAG = makeLogTag(DatabaseContext.class);

    private PreferenceUtils mPreferences;
    private String mDatabaseFullPath;



    /**
     * 建構子
     * @param base
     */
    public DatabaseContext(Context base) {
        super(base);
    }

    /**
     * 取得資料庫儲存位置
     * @param name DB名稱
     * @return
     */
    @Override
    public File getDatabasePath(String name) {
        mPreferences = PreferenceUtils.getInstance(this);
        mDatabaseFullPath = mPreferences.getFilePath();
        return new File(mDatabaseFullPath,name);
    };

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name,int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler)
    {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        LOGW(TAG, "openOrCreateDatabase(" + name + ") = " + result.getPath());
        return result;
    }

}
