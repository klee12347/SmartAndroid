/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import fpg.ftc.si.smart.dao.AppSetting;
import fpg.ftc.si.smart.dao.Depart;
import fpg.ftc.si.smart.util.consts.SystemConstants;
import fpg.ftc.si.smart.util.DateUtils;


import static fpg.ftc.si.smart.util.LogUtils.*;

/**
 * Created by MarlinJoe on 2014/10/7.
 */
public class BasicDBHelper extends SQLiteOpenHelper{

    private static final String TAG = makeLogTag(BasicDBHelper.class);
    private final static String DATABASE_NAME = SystemConstants.SQLITE_BASIC;
    private final static int DATABASE_VERSION = 1;

    public BasicDBHelper(Context context) {
        super(new DatabaseContext(context), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_DEPART_TABLE = "CREATE TABLE " + DepartColumns.TABLE_NAME + " (" +
                DepartColumns.DEPID + " NVARCHAR(100), " +
                DepartColumns.PARENT_DEPID + " NVARCHAR(100), " +
                DepartColumns.NAME + " NVARCHAR(50), " +
                DepartColumns.FULLPATH + " NVARCHAR(50), " +
                DepartColumns.FULLPATH_ID + " TEXT, " +
                DepartColumns.ISEND + " Boolean, " +
                DepartColumns.TXTM + " NVARCHAR(15), " +
                "UNIQUE("+ DepartColumns.DEPID +")"+
                ")";
        db.execSQL(CREATE_DEPART_TABLE);

        final String CREATE_DEPART_INDEX = "CREATE INDEX Idx_Depart_FullPathID ON "+DepartColumns.TABLE_NAME+" ("+ DepartColumns.FULLPATH_ID +" ASC);";
        db.execSQL(CREATE_DEPART_INDEX);


        final String CREATE_APPSETTING_TABLE = "CREATE TABLE " + AppSettingColumns.TABLE_NAME + " (" +
                AppSettingColumns.SETTING_ID + " NVARCHAR(20), " +
                AppSettingColumns.PARAMETER + " NVARCHAR(100), " +
                AppSettingColumns.TXTM + " NVARCHAR(15), " +
                "UNIQUE("+ AppSettingColumns.SETTING_ID +")"+
                ")";

        db.execSQL(CREATE_APPSETTING_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    //region 存取 參數
    public boolean setAppSetting(String setting_id,String paramter)
    {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        Cursor cursor = null;
        try {
            //先找有沒有已存在的設定 沒有的話則新增
            //有的話則UPDATE
            final String[] fetch_columns = new String[] {
                    AppSettingColumns.SETTING_ID,
                    AppSettingColumns.PARAMETER ,
                    AppSettingColumns.TXTM
            };

            cursor = wDb.query(AppSettingColumns.TABLE_NAME, fetch_columns , AppSettingColumns.SETTING_ID + "=?",
                    new String[] { setting_id }, null, null, null, null);
            boolean is_exist = false;
            if (cursor.getCount() > 0)
                is_exist = true;

            if(is_exist)
            {
                //UPDATE
                ContentValues values = new ContentValues(2);
                values.put(AppSettingColumns.PARAMETER, paramter);
                values.put(AppSettingColumns.TXTM, DateUtils.getCurrentDate(DateUtils.FORMAT_YYYYMMDD_HHMMSS));
                wDb.update(AppSettingColumns.TABLE_NAME, values, AppSettingColumns.SETTING_ID + "=?", new String[]{setting_id});
            }
            else
            {
                //INSERT
                ContentValues values = new ContentValues(3);
                values.put(AppSettingColumns.SETTING_ID, setting_id);
                values.put(AppSettingColumns.PARAMETER, paramter);
                values.put(AppSettingColumns.TXTM, DateUtils.getCurrentDate(DateUtils.FORMAT_YYYYMMDD_HHMMSS));
                wDb.insert(AppSettingColumns.TABLE_NAME, null, values);
            }

            result = true;

        }
        catch (Exception ex)
        {
            LOGE(TAG,"setAppSetting="+ex.getMessage());
        }
        finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return result;
    }

    public String getAppSetting(String setting_id)
    {
        String result = "";
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    AppSettingColumns.SETTING_ID,
                    AppSettingColumns.PARAMETER ,
                    AppSettingColumns.TXTM
            };
            final String selection = AppSettingColumns.SETTING_ID + "=?";
            final String[] selectionArgs = new String[] {
                    setting_id
            };

            cursor = rDb.query(AppSettingColumns.TABLE_NAME, fetch_columns, selection, selectionArgs,
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                AppSetting item = new AppSetting(
                        cursor.getString(cursor.getColumnIndexOrThrow(AppSettingColumns.SETTING_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AppSettingColumns.PARAMETER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(AppSettingColumns.TXTM))
                );

                result = item.getPARAMETER();
            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getAppSetting="+ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return  result;
    }
    //endregion


    /**
     * 取得 depid 節點底下的 子節點集合
     * @param depid 目前節點
     * @return
     */
    public List<Depart> getDeparts(String depid)
    {
        List<Depart> result = new ArrayList<Depart>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DepartColumns.DEPID,
                    DepartColumns.PARENT_DEPID,
                    DepartColumns.NAME,
                    DepartColumns.ISEND,
                    DepartColumns.FULLPATH,
                    DepartColumns.FULLPATH_ID,
                    DepartColumns.TXTM
            };

            final String selection = DepartColumns.PARENT_DEPID + "=?";
            final String[] selectionArgs = new String[] {
                    depid
            };

            cursor = rDb.query(DepartColumns.TABLE_NAME,fetch_columns,selection,selectionArgs,null,null,null);

            if(cursor.moveToFirst())
            {
                do{
                    Boolean is_end =  cursor.getInt(cursor.getColumnIndexOrThrow(DepartColumns.ISEND)) == 1 ? true : false;
                    result.add(new Depart(
                            cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.DEPID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.PARENT_DEPID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.FULLPATH)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.FULLPATH_ID)),
                            is_end,
                            cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.TXTM))
                    ));

                }while(cursor.moveToNext());

            }
            cursor.close();

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getDeparts="+ex.getMessage());

        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return result;
    }


    /**
     * 取得目前群組
     * @param depid =>空白的話則表示抓取 根節點 ,有值表示抓取改節點
     * @return
     */
    public Depart getDepart(String depid)
    {
        Depart result = null;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DepartColumns.DEPID,
                    DepartColumns.PARENT_DEPID,
                    DepartColumns.NAME,
                    DepartColumns.ISEND,
                    DepartColumns.FULLPATH,
                    DepartColumns.FULLPATH_ID,
                    DepartColumns.TXTM
            };


            if(depid.isEmpty())
            {
                final String selection = DepartColumns.PARENT_DEPID + "=? AND NAME!='晶元光電股份有限公司X'";
                final String[] selectionArgs = new String[] {
                        "*"
                };
                cursor = rDb.query(DepartColumns.TABLE_NAME, fetch_columns, selection, selectionArgs,
                        null, null, null, null);
            }
            else
            {
                final String selection = DepartColumns.DEPID + "=?";
                final String[] selectionArgs = new String[] {
                        depid
                };
                cursor = rDb.query(DepartColumns.TABLE_NAME, fetch_columns, selection, selectionArgs,
                        null, null, null, null);
            }

            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                Boolean is_end =  cursor.getInt(cursor.getColumnIndexOrThrow(DepartColumns.ISEND)) == 1 ? true : false;
                result = new Depart(
                        cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.DEPID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.PARENT_DEPID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.FULLPATH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.FULLPATH_ID)),
                        is_end,
                        cursor.getString(cursor.getColumnIndexOrThrow(DepartColumns.TXTM))
                );
            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getDepart="+ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return  result;

    }

    /**
     * 更新部門資料 使用DELTE INERT方式
     * @param itemList
     */
    public boolean deleteInsertDepartList(List<Depart> itemList) {

        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        wDb.beginTransaction();
        try {

            //Delete
            wDb.delete(DepartColumns.TABLE_NAME, null, null);

            //Insert
            for(Depart item : itemList)
            {
                ContentValues values = new ContentValues(7);
                values.put(DepartColumns.DEPID, item.getDEPID());
                values.put(DepartColumns.PARENT_DEPID, item.getPARENT_DEPID());
                values.put(DepartColumns.NAME, item.getNAME());
                values.put(DepartColumns.FULLPATH, item.getFULLPATH());
                values.put(DepartColumns.FULLPATH_ID, item.getFULLPATH_ID());
                values.put(DepartColumns.ISEND, item.getISEND());
                values.put(DepartColumns.TXTM, item.getTXTM());
                wDb.insert(DepartColumns.TABLE_NAME, null, values);
            }
            wDb.setTransactionSuccessful();
            result = true;
        }
        catch (Exception ex)
        {
            LOGE(TAG,"deleteInsertDepartList="+ex.getMessage());
        }
        finally {
            wDb.endTransaction();
        }
        return result;
    }

    /**
     * 存放一些設定值
     * NOTE: 這裡存放DepartHash =>原本若存在Preference中,若直接砍DB 偏好設定的資料還在 就會無法更新
     * 因此這種資料 還是存放在 DB中,部門基礎資料存在Hash在,若砍掉則消失 以便重新抓取
     */
    public interface AppSettingColumns{

        /* Table name */
        public static final String TABLE_NAME = "AppSetting";

        /* 設定檔的KEY  */
        public static final String SETTING_ID   = "SETTING_ID";

        /* 設定檔值  */
        public static final String PARAMETER   = "PARAMETER";

        /* 異動時間   */
        public static final String TXTM = "TXTM";
    }

    /**
     * 存放部門組織欄位
     * UNIQUE([DEPID])
     *
     */
    public interface DepartColumns {

        /* Table name */
        public static final String TABLE_NAME = "Depart";

        /* 部門代號識別  */
        public static final String DEPID   = "DEPID";

        /* 父節點  */
        public static final String PARENT_DEPID  = "PARENT_DEPID";

        /* 部門名稱    */
        public static final String NAME  = "NAME";

        /* 路徑 完整名稱    */
        public static final String FULLPATH  = "FULLPATH";

        /* 路徑 完整識別   */
        public static final String FULLPATH_ID  = "FULLPATH_ID";

        /* 是否為最底層   */
        public static final String ISEND  = "ISEND";

        /* 異動時間   */
        public static final String TXTM = "TXTM";

    }


}
