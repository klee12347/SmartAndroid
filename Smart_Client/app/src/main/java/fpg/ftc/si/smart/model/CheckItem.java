/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 檢查項目
 * Created by MarlinJoe on 2014/8/13.
 */
public class CheckItem implements Serializable {


    private String ID;
    private String NAME;
    private Float CheckStd;
    private Float Hight_Val;
    private Float Low_Val;
    private Boolean IsFeel;
    private String Unit;
    private String RecordDataID;//FK

    //紀錄抄表

    //region 紀錄抄表

    private Boolean IsChecked;//是否檢查
    private Boolean ResultFeel;//感官結果 True 正常 False 異常;對應到資料庫 0 正常 1 異常
    private Float ResultValue;//抄表數值結果
    private String ResultComment;//備註 可填可不填
    private String ResultABID;//異常
    private String ResultDealID;//對策
    private boolean IsError;//提供存檔方便去決定 是否要將 異常 對策 資料寫入
    //endregion

    //異常原因
    private List<AbnormalItem> AbnormalItems;

    //照片
    private List<String> PhotoItems;

    public CheckItem(String ID, String NAME, Float checkStd, Float hight_Val, Float low_Val, Boolean isFeel, String unit, String recordDataID) {
        this.ID = ID;
        this.NAME = NAME;
        CheckStd = checkStd;
        Hight_Val = hight_Val;
        Low_Val = low_Val;
        IsFeel = isFeel;
        Unit = unit;
        RecordDataID = recordDataID;
        this.AbnormalItems = new ArrayList<AbnormalItem>();
        this.PhotoItems = new ArrayList<String>();
        this.IsChecked = false;
    }



    public String getID() {
        return ID;
    }

    public String getNAME() {
        return NAME;
    }

    public Float getCheckStd() {
        return CheckStd;
    }

    public Float getHight_Val() {
        return Hight_Val;
    }

    public Float getLow_Val() {
        return Low_Val;
    }

    public Boolean getIsFeel() {
        return IsFeel;
    }

    public String getUnit() {
        return Unit;
    }

    public Boolean getIsChecked() {
        return IsChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        IsChecked = isChecked;
    }

    public Boolean getResultFeel() {
        return ResultFeel;
    }

    public void setResultFeel(Boolean resultFeel) {
        ResultFeel = resultFeel;
    }

    public Float getResultValue() {
        return ResultValue;
    }

    public void setResultValue(Float resultValue) {
        ResultValue = resultValue;
    }

    public List<AbnormalItem> getAbnormalItems() {
        return AbnormalItems;
    }

    public void setAbnormalItems(List<AbnormalItem> abnormalItems) {
        AbnormalItems = abnormalItems;
    }

    public List<String> getPhotoItems() {
        return PhotoItems;
    }

    public void setPhotoItems(List<String> photoItems) {
        PhotoItems = photoItems;
    }

    public String getResultDealID() {
        return ResultDealID;
    }

    public void setResultDealID(String resultDealID) {
        ResultDealID = resultDealID;
    }

    public String getResultABID() {
        return ResultABID;
    }

    public void setResultABID(String resultABID) {
        ResultABID = resultABID;
    }

    public void setResultComment(String resultComment) {
        ResultComment = resultComment;
    }

    public String getResultComment() {
        return ResultComment;
    }

    public boolean isError() {
        return IsError;
    }

    public void setError(boolean isError) {
        IsError = isError;
    }

    public String getRecordDataID() {
        return RecordDataID;
    }

    public void setRecordDataID(String recordDataID) {
        RecordDataID = recordDataID;
    }
}
