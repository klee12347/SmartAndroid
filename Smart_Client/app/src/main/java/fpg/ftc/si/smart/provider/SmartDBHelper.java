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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import fpg.ftc.si.smart.dao.AbnormalReason;
import fpg.ftc.si.smart.dao.ControlPoint;
import fpg.ftc.si.smart.dao.DealMethod;
import fpg.ftc.si.smart.dao.Equipment;
import fpg.ftc.si.smart.dao.RecordArrive;
import fpg.ftc.si.smart.dao.RecordData;
import fpg.ftc.si.smart.dao.RecordDataPic;
import fpg.ftc.si.smart.dao.RecordRFID;
import fpg.ftc.si.smart.dao.Route;
import fpg.ftc.si.smart.dao.Shift;
import fpg.ftc.si.smart.dao.User;
import fpg.ftc.si.smart.model.CheckItem;
import fpg.ftc.si.smart.util.consts.SystemConstants;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.LOGE;
import static fpg.ftc.si.smart.util.LogUtils.LOGI;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 基礎資料
 * Created by MarlinJoe on 2014/7/22.
 */
public class SmartDBHelper extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(SmartDBHelper.class);
    private static final String DATABASENAME = SystemConstants.SQLITE_SMART;
    private static final int DB_VERSION = 1;

    public SmartDBHelper(Context context) {

        super(new DatabaseContext(context), DATABASENAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void checkTest(String eqid,String record_data_clsid,String record_data_wayid,String record_data_ctlptid,String record_data_arrive_date,String record_data_check_urid)
    {
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        final String[] selectionArgs = new String[] {
                record_data_clsid,
                record_data_wayid,
                record_data_ctlptid,
                record_data_arrive_date,
                record_data_check_urid,
        };

        String sql = "SELECT * FROM RecordData WHERE "
            + DBColumn.RecordDataColumns.CLSID +" =? AND "
            + DBColumn.RecordDataColumns.WAYID +" =? AND "
            + DBColumn.RecordDataColumns.CTLPTID +" =? AND "
            + DBColumn.RecordDataColumns.ARRIVE_DATE +" =? AND "
            + DBColumn.RecordDataColumns.CHECK_URID +" =?";
        cursor = rDb.rawQuery(sql,selectionArgs);
        if(cursor.moveToFirst())
        {
            do{


                String recordDataID = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataColumns.ID));
                String check_val = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataColumns.CHECK_VAL));


            }while(cursor.moveToNext());

        }
    }

    /**
     * 取得該管制點下 設備的完成數量
     * @param ctlptid 管制點
     * @return
     */
    public HashMap<String,Integer> getEquipRecordDoneMap(String ctlptid,String record_data_clsid,String record_data_wayid,String record_data_ctlptid,String record_data_arrive_date,String record_data_check_urid)
    {
        final SQLiteDatabase rDb = getReadableDatabase();
        HashMap<String,Integer> result = new HashMap<String, Integer>();
        Cursor cursor = null;
        try {

            final String[] selectionArgs = new String[] {
                    record_data_clsid,
                    record_data_wayid,
                    record_data_ctlptid,
                    record_data_arrive_date,
                    record_data_check_urid,
                    ctlptid
            };

//            SELECT E.[EQID], count(*) FROM Equipment as E
//            JOIN CheckItemStandard as C on E.[EQID] = C.EQID
//            JOIN RecordData as rd on
//            WHERE E.CTLPTID='031b861e-155e-4eed-8959-c70558b08230,BD9FDC2C-9FC2-430E-8938-3C108DF8EAE1'

            String query = "SELECT e.EQID EQID,count(*) COUNT"
                    +" FROM Equipment e"
                    +" JOIN "+ DBColumn.CheckItemStandardColumns.TABLE_NAME +" s ON e.EQID = s.EQID"
                    +" JOIN (SELECT * FROM RecordData WHERE "
                    + DBColumn.RecordDataColumns.CLSID +" =? AND "
                    + DBColumn.RecordDataColumns.WAYID +" =? AND "
                    + DBColumn.RecordDataColumns.CTLPTID +" =? AND "
                    + DBColumn.RecordDataColumns.ARRIVE_DATE +" =? AND "
                    + DBColumn.RecordDataColumns.CHECK_URID +" =?"
                    +" ) rd ON"
                    +" s.STDID = rd.STDID"
                    +" WHERE e.CTLPTID=? "
                    +" GROUP BY e.[EQID]";


            cursor = rDb.rawQuery(query,selectionArgs);

            if(cursor.moveToFirst())
            {

                do{

                    String key = cursor.getString(cursor.getColumnIndexOrThrow("EQID"));
                    int count = cursor.getInt(cursor.getColumnIndexOrThrow("COUNT"));
                    result.put(key, count);

                }while(cursor.moveToNext());

            }
            cursor.close();

        }
        catch (Exception ex)
        {
            LOGE(TAG,"JobCount="+ex.getMessage());

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
     * 取得 基準
     * 基準與抄表join
     * @return CheckItem UI物件
     */
    public ArrayList<CheckItem> getCheckItems(String eqid,String record_data_clsid,String record_data_wayid,String record_data_ctlptid,String record_data_arrive_date,String record_data_check_urid)
    {

        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<CheckItem> resultList = new ArrayList<CheckItem>();
        Cursor cursor = null;
        try {
            HashSet<String> recordDataIDMap = new HashSet<String>();//存放所有的 recordData
            HashMap<String,ArrayList<String>> recordDataMap = new HashMap<String, ArrayList<String>>();

            final String[] selectionArgs = new String[] {
                    record_data_clsid,
                    record_data_wayid,
                    record_data_ctlptid,
                    record_data_arrive_date,
                    record_data_check_urid,
                    eqid
            };

            String query = "SELECT ci.*,"
                    +" rd." + DBColumn.RecordDataColumns.ID + ","
                    +" rd." + DBColumn.RecordDataColumns.CHECK_VAL + ","
                    +" rd." + DBColumn.RecordDataColumns.FEEL_FLAG + ","
                    +" rd." + DBColumn.RecordDataColumns.ABID + ","
                    +" rd." + DBColumn.RecordDataColumns.DEALID + ","
                    +" rd." + DBColumn.RecordDataColumns.COMMENT
                    +" FROM "+ DBColumn.CheckItemStandardColumns.TABLE_NAME +" ci LEFT JOIN "
                    +" (SELECT * FROM RecordData WHERE "
                    + DBColumn.RecordDataColumns.CLSID +" =? AND "
                    + DBColumn.RecordDataColumns.WAYID +" =? AND "
                    + DBColumn.RecordDataColumns.CTLPTID +" =? AND "
                    + DBColumn.RecordDataColumns.ARRIVE_DATE +" =? AND "
                    + DBColumn.RecordDataColumns.CHECK_URID +" =?"
                    +" ) rd ON"
                    +" ci.STDID = rd.STDID"
                    +" WHERE ci.EQID=? "
                    +" ORDER BY ci." + DBColumn.CheckItemStandardColumns.FEM_ID ;

            cursor = rDb.rawQuery(query,selectionArgs);
            if(cursor.moveToFirst())
            {
                do{
                    Boolean tempIsFeel = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.CheckItemStandardColumns.IsFeelItem)).equals("1")  ? true : false;

                    CheckItem checkItem = new CheckItem  (
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.CheckItemStandardColumns.STDID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.CheckItemStandardColumns.STDNM)),
                            cursor.getFloat(cursor.getColumnIndexOrThrow(DBColumn.CheckItemStandardColumns.CHKSTDVAL)),
                            cursor.getFloat(cursor.getColumnIndexOrThrow(DBColumn.CheckItemStandardColumns.HIGHVAL)),
                            cursor.getFloat(cursor.getColumnIndexOrThrow(DBColumn.CheckItemStandardColumns.LOWVAL)),
                            tempIsFeel,
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.CheckItemStandardColumns.STDNM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataColumns.ID))
                    );

                    String recordDataID = checkItem.getRecordDataID();
                    if(recordDataID!=null)
                    {
                        recordDataIDMap.add(recordDataID);
                        //回填資料
                        //有抄表資料
                        if(tempIsFeel)
                        {
                            //感官
                            String feel_flag = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataColumns.FEEL_FLAG));
                            boolean result_feel = false;
                            if(feel_flag.equals("0"))
                            {
                                //正常
                                result_feel = true;
                            }
                            checkItem.setResultFeel(result_feel);
                        }
                        else
                        {
                            //抄表
                            Float check_val = cursor.getFloat(cursor.getColumnIndexOrThrow(DBColumn.RecordDataColumns.CHECK_VAL));
                            checkItem.setResultValue(check_val);
                        }


                        //備註
                        String comment = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataColumns.COMMENT));
                        checkItem.setResultComment(comment);

                        String abid = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataColumns.ABID));
                        checkItem.setResultABID(abid);
                        String dealid = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataColumns.DEALID));
                        checkItem.setResultDealID(dealid);

                        //註記這個物件已經檢查過
                        checkItem.setIsChecked(true);
                    }


                    resultList.add(checkItem);

                }while(cursor.moveToNext());


                if(recordDataIDMap.size()>0)
                {
                    //將照片的資訊給撈出來 第二段query
                    final String[] fetch_pic_columns = new String[] {
                            DBColumn.RecordDataPicColumns.RECORD_DATA_ID,
                            DBColumn.RecordDataPicColumns.FILE_NAME,
                            DBColumn.RecordDataPicColumns.FILE_PATH
                    };

                    final String pic_selection =
                            DBColumn.RecordDataPicColumns.RECORD_DATA_ID + " IN ("+makePlaceholders(recordDataIDMap.size())+")";

                    final List<String> pic_selectionArgs = new ArrayList<String>();
                    for(String recordDataID : recordDataIDMap)
                    {
                        pic_selectionArgs.add(recordDataID);
                    }

                    cursor = rDb.query(DBColumn.RecordDataPicColumns.TABLE_NAME,fetch_pic_columns,pic_selection,pic_selectionArgs.toArray(new String[pic_selectionArgs.size()]),null,null,null);

                    if(cursor.moveToFirst())
                    {
                        do{
                            String record_data_id = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataPicColumns.RECORD_DATA_ID));
                            String file_name = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataPicColumns.FILE_NAME));
                            String file_path = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordDataPicColumns.FILE_PATH));
                            if(recordDataMap.containsKey(record_data_id))
                            {
                                recordDataMap.get(record_data_id).add(file_path);
                            }
                            else
                            {
                                ArrayList<String> temp_pic_list = new ArrayList<String>();
                                temp_pic_list.add(file_path);
                                recordDataMap.put(record_data_id,temp_pic_list);
                            }

                        }while(cursor.moveToNext());
                    }

                    //綁回資料
                    for(CheckItem item : resultList)
                    {
                        String record_data_id = item.getRecordDataID();
                        if(recordDataMap.containsKey(record_data_id))
                        {
                            item.setPhotoItems(recordDataMap.get(record_data_id));
                        }
                    }
                }



            }
        }
        catch (Exception ex)
        {
            LOGE(TAG,"發生錯誤:"+ex.getMessage());
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return resultList;
    }


    /**
     * 取得 基準的異常
     * @param stdid 基準識別
     * @return
     */
    public ArrayList<AbnormalReason> getAbnormalReasons(String stdid)
    {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<AbnormalReason> result = new ArrayList<AbnormalReason>();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DBColumn.AbnormalReasonColumns.ABID,
                    DBColumn.AbnormalReasonColumns.STDID,
                    DBColumn.AbnormalReasonColumns.ABDESC
            };

            final String selection = DBColumn.AbnormalReasonColumns.STDID + "=?";
            final String[] selectionArgs = new String[] {
                    stdid
            };

            cursor = rDb.query(DBColumn.AbnormalReasonColumns.TABLE_NAME,fetch_columns,selection,selectionArgs,null,null,null);

            if(cursor.moveToFirst())
            {
                do{

                    result.add(new AbnormalReason(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.AbnormalReasonColumns.ABID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.AbnormalReasonColumns.STDID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.AbnormalReasonColumns.ABDESC))
                    ));

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"發生錯誤:"+ex.getMessage());
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
     * 取得 異常相關的對策
     * @param abid 異常識別
     * @return
     */
    public ArrayList<DealMethod> getDealMethods(String abid)
    {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<DealMethod> result = new ArrayList<DealMethod>();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DBColumn.DealMethodColumns.DEALID,
                    DBColumn.DealMethodColumns.ABID,
                    DBColumn.DealMethodColumns.DEALNAME
            };

            final String selection = DBColumn.AbnormalReasonColumns.ABID + "=?";
            final String[] selectionArgs = new String[] {
                    abid
            };

            cursor = rDb.query(DBColumn.DealMethodColumns.TABLE_NAME,fetch_columns,selection,selectionArgs,null,null,null);

            if(cursor.moveToFirst())
            {
                do{

                    result.add(new DealMethod(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.DealMethodColumns.DEALID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.DealMethodColumns.ABID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.DealMethodColumns.DEALNAME))
                    ));

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"發生錯誤:"+ex.getMessage());
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
     * 取得所有班別
     * @return
     */
    public ArrayList<Shift> getShifts() {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<Shift> result = new ArrayList<Shift>();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DBColumn.ShiftColumns.CLSID,
                    DBColumn.ShiftColumns.CLSNM,
                    DBColumn.ShiftColumns.FIRST_TIME
            };

            cursor = rDb.query(DBColumn.ShiftColumns.TABLE_NAME,fetch_columns,null,null,null,null,null);

            if(cursor.moveToFirst())
            {
                do{

                    result.add(new Shift(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.ShiftColumns.CLSID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.ShiftColumns.CLSNM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.ShiftColumns.FIRST_TIME))
                    ));

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"發生錯誤:"+ex.getMessage());
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
     * 取得使用者 為了要忽略大小寫 改用text query
     * @param account => 登入帳號
     * @return
     */
    public User getUser(String account)
    {
        User result = null;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] selectionArgs = new String[] {
                    account
            };

            String query = "SELECT * FROM " + DBColumn.UserColumns.TABLE_NAME + " " +
                    "WHERE " + DBColumn.UserColumns.ACCOUNT + " = ? "+
                    "COLLATE NOCASE " ;

            cursor = rDb.rawQuery(query,selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                result = new User(
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UserColumns.URID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UserColumns.ACCOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UserColumns.PWD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UserColumns.USER_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UserColumns.MANGER_FLAG)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.UserColumns.NAME))
                );
            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getUser="+ex.getMessage());
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
     * 取得管制點下的設備
     * @param ctlptid 管制點
     * @return
     */
    public ArrayList<Equipment> getEquipments(String ctlptid) {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<Equipment> result = new ArrayList<Equipment>();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DBColumn.EquipmentColumns.EQID,
                    DBColumn.EquipmentColumns.CTLPTID,
                    DBColumn.EquipmentColumns.EQNM

            };

            final String selection = DBColumn.EquipmentColumns.CTLPTID + "=?";
            final String[] selectionArgs = new String[] {
                    ctlptid
            };

            cursor = rDb.query(DBColumn.EquipmentColumns.TABLE_NAME,fetch_columns,selection,selectionArgs,null,null,null);

            if(cursor.moveToFirst())
            {
                do{

                    result.add(new Equipment(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.EquipmentColumns.EQID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.EquipmentColumns.CTLPTID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.EquipmentColumns.EQNM))
                    ));

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getEquipments 發生錯誤:"+ex.getMessage());
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
     * 取得設備 應該要做的基準數量
     * @param ctlptid 管制點
     * @return
     */
    public HashMap<String, Integer> getEquipmentTotalMap(String ctlptid) {
        final SQLiteDatabase rDb = getReadableDatabase();
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        Cursor cursor = null;
        try {

            final String[] selectionArgs = new String[] {
                    ctlptid
            };

            String query = "SELECT E." + DBColumn.EquipmentColumns.EQID + ", count(*) Count FROM " + DBColumn.EquipmentColumns.TABLE_NAME + " as E " +
                    "JOIN "+ DBColumn.CheckItemStandardColumns.TABLE_NAME +" as C on E.[EQID] = C.EQID " +
                    "WHERE E.CTLPTID = ? "+
                    "GROUP BY E.[EQID] " ;

            cursor = rDb.rawQuery(query,selectionArgs);
            if(cursor.moveToFirst())
            {
                do{

                    result.put(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.EquipmentColumns.EQID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow("Count"))
                    );

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getEquipmentTotalMap 發生錯誤:"+ex.getMessage());
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
     * 取得設備 已完成的基準數量
     * TODO:未知這個規則是否夠用
     * @param ctlptid
     * @return
     */
    public HashMap<String, Integer> getEquipmentFinishMapX(String clsid, String wayid, String ctlptid, String arrive_date, String urid)
    {
        //CLSID + "_" + WAYID + "_" + CTLPTID + "_" + ARRIVE_DATE + "_" + CHECK_URID + "_" + STDID;
        final SQLiteDatabase rDb = getReadableDatabase();
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        Cursor cursor = null;
        try {

            final String[] selectionArgs = new String[] {
                    ctlptid
            };

            String query = "SELECT E." + DBColumn.RecordDataColumns.EQID + ", count(*) Count FROM " + DBColumn.RecordDataColumns.TABLE_NAME + " as E " +
                    "WHERE E."+ DBColumn.RecordDataColumns.CLSID +" = ? "+
                    "AND E." + DBColumn.RecordDataColumns.WAYID +" = ? "+
                    "AND E." + DBColumn.RecordDataColumns.CTLPTID +" = ? "+
                    "AND E." + DBColumn.RecordDataColumns.ARRIVE_DATE +" = ? "+
                    "AND E." + DBColumn.RecordDataColumns.CHECK_URID +" = ? "+
                    "GROUP BY E.[EQID] " ;

            cursor = rDb.rawQuery(query,selectionArgs);
            if(cursor.moveToFirst())
            {
                do{

                    result.put(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.EquipmentColumns.EQID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow("Count"))
                    );

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getEquipmentFinishMap 發生錯誤:"+ex.getMessage());
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
     * 取得到位紀錄
     * @param id => 動態組成的key
     * @return
     */
    public RecordArrive getRecordArrive(String id)
    {
        RecordArrive result = null;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DBColumn.RecordArriveColumns.ID,
                    DBColumn.RecordArriveColumns.CLSID,
                    DBColumn.RecordArriveColumns.WAYID,
                    DBColumn.RecordArriveColumns.CTLPTID,
                    DBColumn.RecordArriveColumns.ARRIVE_DATE,
                    DBColumn.RecordArriveColumns.ARRIVE_TIME,
                    DBColumn.RecordArriveColumns.CHECK_DATE,
                    DBColumn.RecordArriveColumns.CHECK_TIME,
                    DBColumn.RecordArriveColumns.CHECK_URID,
                    DBColumn.RecordArriveColumns.TXTM
            };

            final String selection = DBColumn.RecordArriveColumns.ID + "=?";
            final String[] selectionArgs = new String[] {
                    id
            };

            cursor = rDb.query(DBColumn.RecordArriveColumns.TABLE_NAME, fetch_columns, selection, selectionArgs,
                    null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                cursor.moveToFirst();
                result = new RecordArrive(
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.CLSID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.WAYID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.CTLPTID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.ARRIVE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.ARRIVE_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.CHECK_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.CHECK_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.CHECK_URID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RecordArriveColumns.TXTM))
                );
            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getRecordArrive="+ex.getMessage());
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
     * 取得路線下的管制點
     * @param wayid 路線
     * @return
     */
    public ArrayList<ControlPoint> getControlPoints(String wayid) {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<ControlPoint> result = new ArrayList<ControlPoint>();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DBColumn.ControlPointColumns.CTLPTID,
                    DBColumn.ControlPointColumns.CTLPTNM,
                    DBColumn.ControlPointColumns.WAYID,
                    DBColumn.ControlPointColumns.WEIGHT,
                    DBColumn.ControlPointColumns.MIN1,
                    DBColumn.ControlPointColumns.MIN2
            };

            final String selection = DBColumn.ControlPointColumns.WAYID + "=?";
            final String[] selectionArgs = new String[] {
                    wayid
            };

            cursor = rDb.query(DBColumn.ControlPointColumns.TABLE_NAME,fetch_columns,selection,selectionArgs,null,null,DBColumn.ControlPointColumns.WEIGHT);

            if(cursor.moveToFirst())
            {
                do{

                    result.add(new ControlPoint(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.ControlPointColumns.CTLPTID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.ControlPointColumns.CTLPTNM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.ControlPointColumns.WAYID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.ControlPointColumns.WEIGHT)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.ControlPointColumns.MIN1)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.ControlPointColumns.MIN2))
                    ));

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getControlPoints 發生錯誤:"+ex.getMessage());
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
     * 取得路線下的管制點
     * @param ctlptid_list 管制點集合
     * @return HashMap<String,String> key=> 卡片識別, value=> 管制點識別
     */
    public HashMap<String,String> getNFCTags(ArrayList<String> ctlptid_list) {
        final SQLiteDatabase rDb = getReadableDatabase();
        HashMap<String,String> result = new HashMap<String,String>();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DBColumn.NFCTagColumns.TAGID,
                    DBColumn.NFCTagColumns.CTLPTID,
            };

            final String selection = DBColumn.NFCTagColumns.IS_ENABLE + "=1 AND " +
                    DBColumn.NFCTagColumns.CTLPTID + " IN ("+makePlaceholders(ctlptid_list.size())+")";

            final List<String> selectionArgs = new ArrayList<String>();
            for(String ctlptid : ctlptid_list)
            {
                selectionArgs.add(ctlptid);
            }

            cursor = rDb.query(DBColumn.NFCTagColumns.TABLE_NAME,fetch_columns,selection,selectionArgs.toArray(new String[selectionArgs.size()]),null,null,null);

            if(cursor.moveToFirst())
            {
                do{

                    result.put(cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.NFCTagColumns.TAGID)),cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.NFCTagColumns.CTLPTID)));

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getNFCTags 發生錯誤:"+ex.getMessage());
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
     * 取得路線
     * @param urid 目前登入的使用者
     * @param clsid 班別
     * @param current_date 目前時間
     * @return
     */
    public ArrayList<Route> getRoutes(String urid,String clsid,String current_date) {
        final SQLiteDatabase rDb = getReadableDatabase();
        ArrayList<Route> result = new ArrayList<Route>();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[] {
                    DBColumn.RouteColumns.WAYID,
                    DBColumn.RouteColumns.WAYNM,
                    DBColumn.RouteColumns.URID,
                    DBColumn.RouteColumns.CLSID,
                    DBColumn.RouteColumns.BEGTM,
                    DBColumn.RouteColumns.ENDTM
            };

            final String selection =
                    DBColumn.RouteColumns.URID + " =? AND " +
                    DBColumn.RouteColumns.CLSID + " =? AND " +
                    DBColumn.RouteColumns.BEGTM + " >=? AND " +
                    DBColumn.RouteColumns.ENDTM + " <=? ";

            final String[] selectionArgs = new String[] {
                    urid,
                    clsid,
                    current_date,
                    current_date
            };

            cursor = rDb.query(DBColumn.RouteColumns.TABLE_NAME, fetch_columns, selection, selectionArgs, null, null, null);

            if(cursor.moveToFirst())
            {
                do{

                    result.add(new Route(
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RouteColumns.WAYID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RouteColumns.WAYNM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RouteColumns.URID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RouteColumns.CLSID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RouteColumns.BEGTM)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.RouteColumns.ENDTM))
                    ));

                }while(cursor.moveToNext());

            }

        }
        catch (Exception ex)
        {
            LOGE(TAG,"getRoutes 發生錯誤:"+ex.getMessage());
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
     * 刪除,新增靠卡失敗
     * 若無異常註記 只會進行刪除的動作
     * @param recordRFID
     * @param hasError
     */
    public void deleteInsertRecordRFID(RecordRFID recordRFID,boolean hasError) {
        final SQLiteDatabase wDb = getWritableDatabase();

        try
        {
            wDb.beginTransaction();
            int delete_rows = wDb.delete(DBColumn.RecordRFIDColumns.TABLE_NAME, DBColumn.RecordRFIDColumns.ID + "=?",new String[]{ recordRFID.getID() });
            LOGD(TAG,"移除 RFID資料 筆數:="  + delete_rows);

            if(hasError)
            {
                final ContentValues values = new ContentValues(7);
                values.put(DBColumn.RecordRFIDColumns.ID, recordRFID.getID());
                values.put(DBColumn.RecordRFIDColumns.CLSID, recordRFID.getCLSID());
                values.put(DBColumn.RecordRFIDColumns.WAYID, recordRFID.getWAYID());
                values.put(DBColumn.RecordRFIDColumns.ARRIVE_DATE, recordRFID.getARRIVE_DATE());
                values.put(DBColumn.RecordRFIDColumns.CHECK_URID, recordRFID.getCHECK_URID());
                values.put(DBColumn.RecordRFIDColumns.BRKDKIN, recordRFID.getBRKDKIN());
                values.put(DBColumn.RecordRFIDColumns.TXTM, recordRFID.getTXTM());
                long insert_row_id = wDb.insert(DBColumn.RecordRFIDColumns.TABLE_NAME, null, values);
                LOGD(TAG,"新增 RFID異常資料 成功 insert_row_id:"  + String.valueOf(insert_row_id)) ;
            }

            wDb.setTransactionSuccessful();
        }catch (Exception ex)
        {
            LOGE(TAG,"deleteInsertRecordRFID="+ex.getMessage());
        }
        finally {
            wDb.endTransaction();
        }
    }


    /**
     * 刪除,新增 抄表紀錄 到位
     * @param recordArrive
     */
    public void deleteInsertRecordArrive(RecordArrive recordArrive) {
        final SQLiteDatabase wDb = getWritableDatabase();
        try
        {
            wDb.beginTransaction();
            int delete_rows = wDb.delete(DBColumn.RecordArriveColumns.TABLE_NAME, DBColumn.RecordArriveColumns.ID + "=?",new String[]{ recordArrive.getID() });
            LOGD(TAG, "移除 抄表紀錄-到位 筆數:=" + delete_rows);
            final ContentValues values = new ContentValues(10);
            values.put(DBColumn.RecordArriveColumns.ID, recordArrive.getID());
            values.put(DBColumn.RecordArriveColumns.CLSID, recordArrive.getCLSID());
            values.put(DBColumn.RecordArriveColumns.WAYID, recordArrive.getWAYID());
            values.put(DBColumn.RecordArriveColumns.CTLPTID, recordArrive.getCTLPTID());
            values.put(DBColumn.RecordArriveColumns.ARRIVE_DATE, recordArrive.getARRIVE_DATE());
            values.put(DBColumn.RecordArriveColumns.ARRIVE_TIME, recordArrive.getARRIVE_TIME());
            values.put(DBColumn.RecordArriveColumns.CHECK_DATE, recordArrive.getCHECK_DATE());
            values.put(DBColumn.RecordArriveColumns.CHECK_TIME, recordArrive.getCHECK_TIME());
            values.put(DBColumn.RecordArriveColumns.CHECK_URID, recordArrive.getCHECK_URID());
            values.put(DBColumn.RecordArriveColumns.TXTM, recordArrive.getTXTM());
            long insert_row_id = wDb.insert(DBColumn.RecordArriveColumns.TABLE_NAME, null, values);
            LOGD(TAG,"新增 抄表紀錄-到位 成功 insert_row_id:"  + String.valueOf(insert_row_id)) ;
            wDb.setTransactionSuccessful();
        }catch (Exception ex)
        {
            LOGE(TAG,"deleteInsertRecordArrive="+ex.getMessage());
        }
        finally {
            wDb.endTransaction();
        }
    }


    /**
     * 刪除,新增 抄表紀錄 包含照片
     * @param recordDataList
     * @return True:成功 False:失敗
     */
    public boolean deleteInsertRecordDataList(List<RecordData> recordDataList) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try
        {
            wDb.beginTransaction();

            for(RecordData recordData : recordDataList)
            {
                //TODO 應該用WHERE IN STDID

                int delete_rows = wDb.delete(DBColumn.RecordDataColumns.TABLE_NAME, DBColumn.RecordDataColumns.ID + "=?",new String[]{ recordData.getID() });
                LOGD(TAG, "移除 抄表紀錄 筆數:=" + delete_rows);
                int delete_pic_rows = wDb.delete(DBColumn.RecordDataPicColumns.TABLE_NAME, DBColumn.RecordDataPicColumns.RECORD_DATA_ID + "=?",new String[]{ recordData.getID() });
                LOGD(TAG, "移除 抄表紀錄(照片) 筆數:=" + delete_pic_rows);

                final ContentValues values = new ContentValues(18);
                values.put(DBColumn.RecordDataColumns.ID, recordData.getID());
                values.put(DBColumn.RecordDataColumns.CLSID, recordData.getCLSID());
                values.put(DBColumn.RecordDataColumns.WAYID, recordData.getWAYID());
                values.put(DBColumn.RecordDataColumns.CTLPTID, recordData.getCTLPTID());
                values.put(DBColumn.RecordDataColumns.STDID, recordData.getSTDID());
                values.put(DBColumn.RecordDataColumns.EQID, recordData.getEQID());
                values.put(DBColumn.RecordDataColumns.ARRIVE_DATE, recordData.getARRIVE_DATE());
                values.put(DBColumn.RecordDataColumns.ARRIVE_TIME, recordData.getARRIVE_TIME());
                values.put(DBColumn.RecordDataColumns.CHECK_DATE, recordData.getCHECK_DATE());
                values.put(DBColumn.RecordDataColumns.CHECK_TIME, recordData.getCHECK_TIME());
                values.put(DBColumn.RecordDataColumns.FINISH_DATE, recordData.getFINISH_DATE());
                values.put(DBColumn.RecordDataColumns.FINISH_TIME, recordData.getFINISH_TIME());
                values.put(DBColumn.RecordDataColumns.CHECK_URID, recordData.getCHECK_URID());
                values.put(DBColumn.RecordDataColumns.CHECK_VAL, recordData.getCHECK_VAL());
                values.put(DBColumn.RecordDataColumns.FEEL_FLAG, recordData.getFEEL_FLAG());
                values.put(DBColumn.RecordDataColumns.ABID, recordData.getABID());
                values.put(DBColumn.RecordDataColumns.DEALID, recordData.getDEALID());
                values.put(DBColumn.RecordDataColumns.TXTM, recordData.getTXTM());
                values.put(DBColumn.RecordDataColumns.COMMENT, recordData.getCOMMENT());
                long insert_row_id = wDb.insert(DBColumn.RecordDataColumns.TABLE_NAME, null, values);
                LOGD(TAG,"新增 抄表紀錄 成功 insert_row_id:"  + String.valueOf(insert_row_id)) ;

                for(RecordDataPic recordDataPic : recordData.getRecordDataPics())
                {
                    final ContentValues pic_values = new ContentValues(3);
                    pic_values.put(DBColumn.RecordDataPicColumns.RECORD_DATA_ID, recordDataPic.getRECORD_DATA_ID());
                    pic_values.put(DBColumn.RecordDataPicColumns.FILE_NAME, recordDataPic.getFILE_NAME());
                    pic_values.put(DBColumn.RecordDataPicColumns.FILE_PATH, recordDataPic.getFILE_PATH());
                    long insert_pic_row_id = wDb.insert(DBColumn.RecordDataPicColumns.TABLE_NAME, null, pic_values);
                    LOGD(TAG,"新增 抄表紀錄(照片) 成功 insert_row_id:"  + String.valueOf(insert_row_id)) ;
                }

            }
            wDb.setTransactionSuccessful();
            result = true;
        }catch (Exception ex)
        {
            LOGE(TAG,"deleteInsertRecordDataList="+ex.getMessage());
        }
        finally {
            wDb.endTransaction();
        }
        return result;
    }

    public void cloneRecordToTemp(String temp_db_path)
    {
        final SQLiteDatabase wDb = getWritableDatabase();
        int delete_rows = 0;

        delete_rows = wDb.delete(DBColumn.RecordArriveColumns.TABLE_NAME, null,null);
        delete_rows = wDb.delete(DBColumn.RecordDataColumns.TABLE_NAME, null,null);
        delete_rows = wDb.delete(DBColumn.RecordDataPicColumns.TABLE_NAME, null,null);
        delete_rows = wDb.delete(DBColumn.RecordRFIDColumns.TABLE_NAME, null,null);

        wDb.beginTransaction();
        wDb.setTransactionSuccessful();
        wDb.endTransaction();

    }

    /**
     * where in 串接方法
     * @param len
     * @return
     */
    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}
