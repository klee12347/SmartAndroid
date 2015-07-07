package fpg.ftc.si.smart.util;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcV;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fpg.ftc.si.smart.util.consts.DeviceConstants;

import static fpg.ftc.si.smart.util.LogUtils.*;


/**
 * NFC Intent 解析
 */
public class NFCUtils {

    public static final String ARG_DEVICE = "ARG_DEVICE";
    private static final String TAG = makeLogTag(NFCUtils.class);

    /**
     * 取得巡檢卡片 UID
     * @param intent
     * @return
     */
    public static String resolveUIDIntent(Intent intent) throws Exception {
        String result = "";
        Log.i("resolveUIDIntent", "start");
        String action = intent.getAction();
        String device = intent.getStringExtra(ARG_DEVICE);
        // 檢查這個intent是不是 Tag DisCovered
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            try {
                ///從 NfcAdapter 取得 Tag instance
                Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                tagFromIntent.getTechList();

                String prefix = "android.nfc.tech.";
                String tagTechString = "";
                for (String tech : tagFromIntent.getTechList()) {

                    tagTechString +=tech.substring(prefix.length());
                }
                LOGD(TAG,"tag type:"+ tagTechString);
                // NfcV類型的需要做處理
                NfcV mTag = NfcV.get(tagFromIntent);
                if(tagTechString.contains("NfcV"))
                {
                    byte[] id = tagFromIntent.getId();
                    if(device.contains(DeviceConstants.UNITECH))
                    {
                        reverseByteArray(id);
                    }
                    result = getHex(id);
                }
                else
                {
                    //不知道的卡片類型
                    mTag.connect();
                    NFCVUtils mNfcVutil = new NFCVUtils(mTag);
                    result = mNfcVutil.getUID();
                }



            } catch (Exception ex) {

                Log.e("resolveUIDIntent 發生錯誤:", ex.getMessage());
                throw ex;

            }
        }
        Log.i("resolveUIDIntent", "end");
        return result;
    }

    private static void reverseByteArray(byte[] bytes)
    {
        if (bytes == null) {
            return;
        }
        int i = 0;
        int j = bytes.length - 1;
        byte tmp;
        while (j > i) {
            tmp = bytes[j];
            bytes[j] = bytes[i];
            bytes[i] = tmp;
            j--;
            i++;
        }

    }

    private static String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append("");
            }
        }
        return sb.toString();
    }

    /**
     * 取得員工卡號
     *
     * @param intent
     * @return 員工識別字串
     */
    public static String resolveNotesIntent(Intent intent) throws Exception {
        String result = "";
        String action = intent.getAction();
        Log.i("resolveNotesIntent", "start");
        // 檢查這個intent是不是 Tag DisCovered
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            ///從 NfcAdapter 取得 Tag instance
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // Get an instance of the Mifare classic card from this TAG intent
            MifareClassic mfc = MifareClassic.get(tagFromIntent);

            // load key
            byte[] keyCode = {
                    (byte) 252,
                    (byte) 250,
                    (byte) 71,
                    (byte) 80,
                    (byte) 252,
                    (byte) 250
            };


            byte[] data;

            try {

                // 連接卡片
                mfc.connect();
                boolean auth = false;
                String cardData = null;
                // 5.2) and get the number of sectors this card has..and loop
                // thru these sectors
                int secCount = mfc.getSectorCount();
                int bCount = 0;
                int bIndex = 0;
                for (int j = 0; j < secCount; j++) {
                    // 讀取認證的 sector
                    auth = mfc.authenticateSectorWithKeyA(j, keyCode);
                    if (auth) {
                        // 6.2) In each sector - get the block count
                        bCount = mfc.getBlockCountInSector(j);
                        bIndex = 0;
                        for (int i = 0; i < bCount; i++) {
                            bIndex = mfc.sectorToBlock(j);
                            // 讀取 block
                            data = mfc.readBlock(bIndex);
                            data = mfc.readBlock(5);
                            if (data != null) {
                                String rawNoteId = NFCStringDecoder.getHexString(data);
                                String tempNoteId = rawNoteId.substring(20, 31);
                                String userNoteId = "";

                                if (tempNoteId.startsWith("22")) {

                                    userNoteId = "N" + tempNoteId.substring(2, 11);
                                    result = userNoteId;

                                }


                            }

                            bIndex++;
                        }
                    } else {

                    }
                }
            } catch (Exception ex) {

                throw ex;
            }

        }
        Log.i("resolveNotesIntent", "end");
        return result;

    }
}
