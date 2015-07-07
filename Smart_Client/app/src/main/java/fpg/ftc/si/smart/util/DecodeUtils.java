/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 加解密
 * Created by MarlinJoe on 2014/9/17.
 */
public class DecodeUtils {

    private static final String TAG = makeLogTag(DecodeUtils.class);

    public static String getMD5EncryptedString(String encTarget){
        String result = "";
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
            String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
            while ( md5.length() < 32 ) {
                md5 = "0"+md5;
            }
            result = md5;
        } catch (NoSuchAlgorithmException e) {
            LOGD(TAG,"Exception while encrypting to md5");
            e.printStackTrace();
        }

        return result;
    }

}
