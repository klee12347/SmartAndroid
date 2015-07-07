/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.provider;

/**
 * 欄位定義
 * Created by MarlinJoe on 2014/10/6.
 */
public class DBColumn {

    /**
     * 異常原因  欄位
     */
    public interface AbnormalReasonColumns {

        /* Table name */
        public static final String TABLE_NAME = "AbnormalReason";

        /* 異常識別  */
        public static final String ABID = "ABID";

        /* 基準識別  */
        public static final String STDID  = "STDID";

        /* 異常描述  */
        public static final String ABDESC = "ABDESC";

    }

    /**
     * 基準  欄位
     */
    public interface CheckItemStandardColumns {

        /* Table name */
        public static final String TABLE_NAME = "CheckItemStandard";

        /* 基準識別  */
        public static final String STDID = "STDID";

        /* 基準名稱  */
        public static final String STDNM  = "STDNM";

        /* 排序用 */
        public static final String FEM_ID  = "FEM_ID";

        /* 是否為感官  */
        public static final String IsFeelItem = "IsFeelItem";

        /* 標準值  */
        public static final String CHKSTDVAL = "CHKSTDVAL";

        /* 上限值  */
        public static final String HIGHVAL = "HIGHVAL";

        /* 下限值  */
        public static final String LOWVAL = "LOWVAL";

        /* 單位  */
        public static final String UNIT = "UNIT";

        /* 設備識別  */
        public static final String EQID = "EQID";
    }

    /**
     * 管制點  欄位
     */
    public interface ControlPointColumns {

        /* Table name */
        public static final String TABLE_NAME = "ControlPoint";

        /* 管制點識別  */
        public static final String CTLPTID = "CTLPTID";

        /* 管制點名稱  */
        public static final String CTLPTNM  = "CTLPTNM";

        /* 路線識別  */
        public static final String WAYID = "WAYID";

        /* 排序  */
        public static final String WEIGHT = "WEIGHT";

        /* 未知  */
        public static final String MIN1 = "MIN1";

        /* 未知  */
        public static final String MIN2 = "MIN2";

    }

    /**
     * 對策  欄位
     */
    public interface DealMethodColumns {

        /* Table name */
        public static final String TABLE_NAME = "DealMethod";

        /* 對策識別  */
        public static final String DEALID = "DEALID";

        /* 異常識別  */
        public static final String ABID  = "ABID";

        /* 對策描述  */
        public static final String DEALNAME = "DEALNAME";

    }

    /**
     * 設備  欄位
     */
    public interface EquipmentColumns {

        /* Table name */
        public static final String TABLE_NAME = "Equipment";

        /* 設備識別  */
        public static final String EQID = "EQID";

        /* 管制點識別  */
        public static final String CTLPTID  = "CTLPTID";

        /* 設備名稱  */
        public static final String EQNM = "EQNM";

    }

    /**
     * 派工  欄位
     */
    public interface JobColumns {

        /* Table name */
        public static final String TABLE_NAME = "Job";

        /* 派工識別  */
        public static final String JOBID = "JOBID";

        /* 路線識別  */
        public static final String WAYID  = "WAYID";

        /* 是否啟用?  */
        public static final String ENABLE = "ENABLE";

        /* 未知  */
        public static final String WeekDay = "WeekDay";

        /* 未知  */
        public static final String URID = "URID";

        /* 派工名稱  */
        public static final String Name = "Name";

        /* 開始時間  */
        public static final String BEGTM = "BEGTM";

        /* 結束時間  */
        public static final String ENDTM = "ENDTM";
    }

    /**
     * 卡片  欄位
     */
    public interface NFCTagColumns {

        /* Table name */
        public static final String TABLE_NAME = "NFCTag";

        /* 卡片識別  */
        public static final String TAGID = "TAGID";

        /* 管制點識別  */
        public static final String CTLPTID  = "CTLPTID";

        /* 啟用與否  */
        public static final String IS_ENABLE = "IS_ENABLE";

    }

    /**
     * 路線  欄位
     */
    public interface RouteColumns {

        /* Table name */
        public static final String TABLE_NAME = "Route";

        /* 路線識別  */
        public static final String WAYID = "WAYID";

        /* 路線名稱  */
        public static final String WAYNM  = "WAYNM";

        /* 使用者識別  */
        public static final String URID = "URID";

        /* 班別識別  */
        public static final String CLSID = "CLSID";

        /* 開始時間  */
        public static final String BEGTM = "BEGTM";

        /* 結束時間  */
        public static final String ENDTM = "ENDTM";
    }

    /**
     * 班別  欄位
     */
    public interface ShiftColumns {

        /* Table name */
        public static final String TABLE_NAME = "Shift";

        /* 班別識別  */
        public static final String CLSID = "CLSID";

        /* 班別名稱  */
        public static final String CLSNM  = "CLSNM";

        /* 未知  */
        public static final String FIRST_TIME = "FIRST_TIME";

    }

    /**
     * 班別  欄位
     */
    public interface UserColumns {

        /* Table name */
        public static final String TABLE_NAME = "User";

        /* 帳號識別  */
        public static final String URID = "URID";

        /* 登入帳號  */
        public static final String ACCOUNT  = "ACCOUNT";

        /* 密碼  */
        public static final String PWD = "PWD";

        /* 未知  */
        public static final String USER_STATUS = "USER_STATUS";

        /* 未知  */
        public static final String MANGER_FLAG = "MANGER_FLAG";

        /* 未知  */
        public static final String NAME = "NAME";

    }

    /**
     * 抄表 到位 資料欄位
     */
    public interface RecordArriveColumns {

        /* Table name */
        public static final String TABLE_NAME = "RecordArrive";

        /* 識別  */
        public static final String ID = "ID";

        /* 班別識別  */
        public static final String CLSID  = "CLSID";

        /* 路線識別  */
        public static final String WAYID  = "WAYID";

        /* 管制點識別  */
        public static final String CTLPTID = "CTLPTID";

        /* 到位日期 */
        public static final String ARRIVE_DATE = "ARRIVE_DATE";

        public static final String ARRIVE_TIME = "ARRIVE_TIME";

        public static final String CHECK_DATE = "CHECK_DATE";

        public static final String CHECK_TIME = "CHECK_TIME";

        public static final String CHECK_URID = "CHECK_URID";

        /* 異動時間 */
        public static final String TXTM = "TXTM";
    }

    /**
     * 抄表 資料欄位
     */
    public interface RecordDataColumns {

        /* Table name */
        public static final String TABLE_NAME = "RecordData";

        /* 作業公司  */
        public static final String ID = "ID";

        /* 作業廠處  */
        public static final String CLSID  = "CLSID";

        public static final String WAYID  = "WAYID";

        /* 作業課別  */
        public static final String CTLPTID = "CTLPTID";

        /* 設備識別 (方便UI處理而加開的欄位)*/
        public static final String EQID = "EQID";

        public static final String STDID = "STDID";
        public static final String ARRIVE_DATE = "ARRIVE_DATE";

        public static final String ARRIVE_TIME = "ARRIVE_TIME";

        public static final String CHECK_DATE = "CHECK_DATE";

        public static final String FINISH_DATE = "FINISH_DATE";

        public static final String CHECK_VAL = "CHECK_VAL";

        public static final String FEEL_FLAG = "FEEL_FLAG";

        public static final String FINISH_TIME = "FINISH_TIME";

        public static final String CHECK_TIME = "CHECK_TIME";

        public static final String CHECK_URID = "CHECK_URID";
        public static final String ABID = "ABID";

        public static final String DEALID = "DEALID";
        public static final String TXTM = "TXTM";
        public static final String COMMENT = "COMMENT";

    }

    /**
     * 抄表 照片欄位
     */
    public interface RecordDataPicColumns {

        /* Table name */
        public static final String TABLE_NAME = "RecordDataPic";

        /* RecordDataID FK  */
        public static final String RECORD_DATA_ID = "RECORD_DATA_ID";

        public static final String FILE_NAME  = "FILE_NAME";

        public static final String FILE_PATH  = "FILE_PATH";

    }


    /**
     *  抄表紀錄 RFID異常 資料欄位
     */
    public interface RecordRFIDColumns {

        /* Table name */
        public static final String TABLE_NAME = "RecordRFID";

        /* 作業公司  */
        public static final String ID = "ID";

        /* 作業廠處  */
        public static final String CLSID  = "CLSID";

        public static final String WAYID  = "WAYID";

        /* 作業課別  */
        public static final String CTLPTID = "CTLPTID";

        public static final String ARRIVE_DATE = "ARRIVE_DATE";

        public static final String CHECK_URID = "CHECK_URID";

        public static final String BRKDKIN = "BRKDKIN";

        public static final String TXTM = "TXTM";

    }
}
