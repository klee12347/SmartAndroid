/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.adapter;

/**
 * Created by MarlinJoe on 2014/10/16.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpg.ftc.si.smart.fragment.Step4CheckItemFragment;
import fpg.ftc.si.smart.model.CheckItem;

/**
 * 抄表翻頁的 FragmentStatePagerAdapter
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    /**
     * Used to cache the data
     */
    private CheckBridge mCheckBridge;

    /**
     * 保存目前的 Fragment
     * 為了要讓Activity可以操作
     */
    private Map<Integer, Step4CheckItemFragment> mPageReferenceMap = new HashMap<Integer, Step4CheckItemFragment>();

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
        mCheckBridge = new CheckBridge();
    }

    /**
     * 帶入資料
     * @param checkBridge
     */
    public void setmDataSource(CheckBridge checkBridge)
    {
        this.mCheckBridge = checkBridge;
        this.notifyDataSetChanged();

    }


    @Override
    public Fragment getItem(int position) {
        CheckItem checkItem = mCheckBridge.getmDataSource().get(position);
        Step4CheckItemFragment step4CheckItemFragment =Step4CheckItemFragment.create(position, getCount(), checkItem);
        mPageReferenceMap.put(position,step4CheckItemFragment );
        return step4CheckItemFragment;
    }

    @Override
    public int getCount() {
        return mCheckBridge.getCount();
    }



    /**
     * 取出目前的 Fragment
     * @param key
     * @return
     */
    public Step4CheckItemFragment getFragment(int key) {
        return mPageReferenceMap.get(key);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container,position,object);
        mPageReferenceMap.remove(position);
    }



}