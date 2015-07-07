/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;



/**
 * Session 登入相關
 * Reference:http://www.androidhive.info/2012/08/android-session-management-using-shared-preferences/
 */
public class SessionManager {

	// Shared Preferences
    private SharedPreferences mPreferences;
	
	// Editor for Shared preferences
    private Editor mEditor;
	
	// Context
    private Context mContext;
	
	// Shared Preferences mode
    private int PRIVATE_MODE = 0;
	
	// Shared pref file name
	private static final String PREF_NAME = "SmartPref";
	
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	
	// 登入識別
	public static final String KEY_URID = "URID";

    // 登入帳號
    public static final String KEY_ACCOUNT = "Account";

    // 登入名稱(顯示)
    public static final String KEY_NAME = "Name";

	// 目前登入的班別
	public static final String KEY_CLSID = "CLSID";

    public static final String KEY_CLSNM = "CLSNM";

    // 目前登入的班別
    public static final String KEY_FIRST_TIME = "First_Time";

    public static String mReturnActivity ;



    // Constructor
	public SessionManager(Context context){
		mContext = context;
		mPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		mEditor = mPreferences.edit();
	}

    /**
     * 寫入登入 Session
     * @param urid 使用者帳號
     * @param clsid 路線
     */
	public void createLoginSession(String urid,String account,String name, String clsid,String clsnm,String first_time){
		// Storing login value as TRUE
		mEditor.putBoolean(IS_LOGIN, true);
		mEditor.putString(KEY_URID, urid);
        mEditor.putString(KEY_ACCOUNT, account);
        mEditor.putString(KEY_NAME, name);
        mEditor.putString(KEY_CLSID, clsid);
        mEditor.putString(KEY_CLSNM, clsnm);
        mEditor.putString(KEY_FIRST_TIME, first_time);
		// commit changes
		mEditor.commit();
	}


    /**
     * 判斷使用者登入狀態
     * 未登入 => 導回登入頁面
     */
	public void checkLogin(int activity_code){
		// 檢查登入狀態
		if(!this.isLoggedIn()){


            if( mContext instanceof Activity)
            {
                ((Activity) mContext).finish();
            }
		}
		
	}



    /**
     * 取得使用者登入資訊
     * @return
     */
	public UserSession getUserSession(){
        String userId = mPreferences.getString(KEY_URID, null);
        String account = mPreferences.getString(KEY_ACCOUNT, null);
        String name = mPreferences.getString(KEY_NAME, null);
        String clsid = mPreferences.getString(KEY_CLSID, null);
        String clsnm = mPreferences.getString(KEY_CLSNM, null);
        String first_time = mPreferences.getString(KEY_FIRST_TIME, null);
        UserSession user = new UserSession(userId,account,name,clsid,clsnm,first_time);
		return user;
	}


    /**
     * 登出使用者
     */
	public void logoutUser(){
		// Clearing all data from Shared Preferences
		mEditor.clear();
		mEditor.commit();
		
//		// After logout redirect user to Loing Activity
//		Intent i = new Intent(mContext, ProcessLoginActivity.class);
//		// Closing all the Activities
//		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		// Add new Flag to start new Activity
//		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		// Staring Login Activity
//		mContext.startActivity(i);
	}


    /**
     * 檢查使用者是否登入
     * @return
     */
	public boolean isLoggedIn(){
		return mPreferences.getBoolean(IS_LOGIN, false);
	}


    /**
     * 取得登入後 要導回的頁面
     * @return
     */
    public static String getmReturnActivity() {
        return mReturnActivity;

    }
}
