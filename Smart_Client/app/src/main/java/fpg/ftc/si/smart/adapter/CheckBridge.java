/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import fpg.ftc.si.smart.model.CheckItem;

/**
 * 抄表模式分成兩種
 * 給 ScreenSlidePagerAdapter 使用 mCheckAdapter
 * Created by MarlinJoe on 2014/12/2.
 */
public class CheckBridge {


    private ArrayList<CheckItem> mDataSource;

    public CheckBridge() {
        mDataSource = new ArrayList<CheckItem>();
    }

    public CheckBridge(ArrayList<CheckItem> mDataSource) {
        this.mDataSource = mDataSource;
    }

    public ArrayList<CheckItem> getmDataSource()
    {
        return mDataSource;
    }

    //取得已檢查的物件
    public List<CheckItem> getCheckSource()
    {
        List<CheckItem> result = new ArrayList<CheckItem>();

        for(CheckItem item : mDataSource)
        {
            if(item.getIsChecked())
            {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 取得總數
     * @return
     */
    public int getCount() {
        return mDataSource.size();
    }

    /**
     * 取得已檢查項目
     * @return
     */
    public int getCheckCount() {

        int result = 0;
        for(CheckItem item : mDataSource)
        {
            if(item.getIsChecked())
            {
                result ++;
            }
        }
        return result;
    }

    /**
     * 取得百分比
     * @return
     */
    public int getProgress()
    {
        return (int)((getCheckCount()*100)/getCount());
    }

    /**
     * 將未檢查的部分(屬於感官的) 設定為正常
     */
    public void setAllFeelIsGood()
    {
        for(CheckItem item : mDataSource)
        {
            if(!item.getIsChecked() && item.getIsFeel())
            {
                item.setIsChecked(true);
                item.setResultFeel(true);
                item.setError(false);
                item.setResultABID("");
                item.setResultDealID("");
            }
        }
    }

    /**
     * 取得目前尚未檢查的基準 給自動切換基準使用
     * TODO 每次都重新計算 未來當有效能考量時 可能要調整方式
     * @return 回傳 position Map
     */
    public LinkedHashSet<Integer> getNotCheckMap()
    {
        LinkedHashSet<Integer> result = new LinkedHashSet<Integer>();
        int position = 0;
        for(CheckItem item : mDataSource)
        {
            if(!item.getIsChecked())
            {
                result.add(position);
            }
            position++;
        }

        return result;
    }
}
