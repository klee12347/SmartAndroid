/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import fpg.ftc.si.smart.util.NFCUtils;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.LOGE;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;


public class NFCTestMyActivity extends Activity {

    private static final String TAG = makeLogTag(NFCTestMyActivity.class);

    // NFC block
    private NfcAdapter mNfcAdapter;//NFC
    private PendingIntent mPendingIntent;//NFC
    private NdefMessage mNdefMessage;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfctest_my);

        //region  NFC 設定
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);//NFC
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(
                NfcAdapter.ACTION_TECH_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {

            throw new RuntimeException("fail", e);
        }

        IntentFilter[] mFilters = new IntentFilter[] { ndef, };

        // Setup a tech list for all NfcF tags
        String[][] mTechLists = new String[][] { new String[] { MifareClassic.class
                .getName() } };
        //endregion

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nfctest_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        LOGD(TAG,"onResume");
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {

        try
        {
            String uid = NFCUtils.resolveUIDIntent(intent);
            if(!uid.isEmpty())
            {

                uid = uid.toUpperCase();
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(300);

            }


        }catch (Exception ex)
        {
            if(ex.getMessage()!=null)
            {
                LOGE(TAG,"onNewIntent: " + ex.getMessage());
            }
            Toast.makeText(this, getString(R.string.error_invalid_card), Toast.LENGTH_SHORT).show();
        }


    }
}
