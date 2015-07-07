/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.dao;



/**
 * 抄表紀錄 資料照片
 * Created by MarlinJoe on 2014/9/9.
 */
public class RecordDataPic {

    //FK
    private String RECORD_DATA_ID;

    //檔案名稱
    private String FILE_NAME;

    //檔案路徑
    private String FILE_PATH;

    public RecordDataPic(String RECORD_DATA_ID, String FILE_NAME, String FILE_PATH) {
        this.RECORD_DATA_ID = RECORD_DATA_ID;
        this.FILE_NAME = FILE_NAME;
        this.FILE_PATH = FILE_PATH;
    }

    public String getRECORD_DATA_ID() {
        return RECORD_DATA_ID;
    }

    public void setRECORD_DATA_ID(String RECORD_DATA_ID) {
        this.RECORD_DATA_ID = RECORD_DATA_ID;
    }

    public String getFILE_NAME() {
        return FILE_NAME;
    }

    public void setFILE_NAME(String FILE_NAME) {
        this.FILE_NAME = FILE_NAME;
    }

    public String getFILE_PATH() {
        return FILE_PATH;
    }

    public void setFILE_PATH(String FILE_PATH) {
        this.FILE_PATH = FILE_PATH;
    }
}
