/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.util;

/**
 * 使用者登入後暫存的登入資訊
 * Created by MarlinJoe on 2014/2/13.
 */
public class UserSession {

    private String mUserId;
    private String mAccount;
    private String mName;
    private String mCLSID;
    private String mCLSNM;
    private String mFirstTime;

    public UserSession(String mUserId, String mAccount, String mName, String mCLSID, String mCLSNM, String mFirstTime) {
        this.mUserId = mUserId;
        this.mAccount = mAccount;
        this.mName = mName;
        this.mCLSID = mCLSID;
        this.mCLSNM = mCLSNM;
        this.mFirstTime = mFirstTime;
    }


    public String getmUserId() {
        return mUserId;
    }

    public String getmAccount() {
        return mAccount;
    }

    public String getmName() {
        return mName;
    }

    public String getmCLSID() {
        return mCLSID;
    }

    public String getmCLSNM() {
        return mCLSNM;
    }

    public String getmFirstTime() {
        return mFirstTime;
    }
}
