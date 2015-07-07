/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.adapter;




import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;

import fpg.ftc.si.smart.fragment.DepartUserFragment;
import fpg.ftc.si.smart.fragment.DepartViewFragment;

/**
 * 專給GroupViewActivity使用
 * Created by MarlinJoe on 2014/9/4.
 */
public class DepartTabsPagerAdapter extends FragmentPagerAdapter {

    private final HashMap<Integer, Fragment> mFragments;



    public DepartTabsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragments = new HashMap<Integer, Fragment>(2);
        mFragments.put(0,new DepartViewFragment());
        mFragments.put(1,new DepartUserFragment());
    }

    @Override
    public Fragment getItem(int position) {

//        switch (index) {
//            case 0:
//                // Top Rated fragment activity
//                return new GroupViewFragment();
//            case 1:
//                // Games fragment activity
//                return new GroupUserFragment();
//
//        }
//
//        return null;
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}