package fpg.ftc.si.smart;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import fpg.ftc.si.smart.adapter.NavDrawerListAdapter;
import fpg.ftc.si.smart.async.HttpMultipartPost;
import fpg.ftc.si.smart.fragment.IOnFragmentInteractionListener;
import fpg.ftc.si.smart.fragment.Step1RouteItemFragment;
import fpg.ftc.si.smart.fragment.Step2PointFragment;
import fpg.ftc.si.smart.fragment.Step3EquipFragment;
import fpg.ftc.si.smart.model.EquipmentItem;
import fpg.ftc.si.smart.model.NavDrawerItem;
import fpg.ftc.si.smart.model.PointItem;
import fpg.ftc.si.smart.model.RouteItem;
import fpg.ftc.si.smart.provider.SmartDBHelper;
import fpg.ftc.si.smart.util.DeviceUtils;
import fpg.ftc.si.smart.util.NFCUtils;
import fpg.ftc.si.smart.util.PreferenceUtils;
import fpg.ftc.si.smart.util.SessionManager;
import fpg.ftc.si.smart.util.consts.FragmentTagConstants;
import fpg.ftc.si.smart.util.consts.SystemConstants;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.LOGE;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * http://www.yrom.net/blog/2013/03/10/fragment-switch-not-restart/
 */
public class ExecuteActivity extends FragmentActivity implements IOnFragmentInteractionListener {

    private static final String TAG = makeLogTag(ExecuteActivity.class);
    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private String mDevice;
    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter mNavDrawerListAdapter;

    //資料庫
    public SmartDBHelper mSmartDBHelper ;//TODO 和DB共用不知有無副作用
    public PreferenceUtils mPreferences;//TODO 和Fragment共用不知有無副作用
    public SessionManager mSession;//TODO 和Fragment共用不知有無副作用


    // NFC block
    private NfcAdapter mNfcAdapter;//NFC
    private PendingIntent mPendingIntent;//NFC

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute);
        if (savedInstanceState == null) {
            mFragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            Fragment fragment = new Step1RouteItemFragment();
            ft.add(R.id.container, fragment , FragmentTagConstants.STEP1_ROUTE_FRAGMENT);
            ft.commit();
        }
        else
        {
            mFragmentManager = getSupportFragmentManager();
        }

        mPreferences = new PreferenceUtils(this);
        mSmartDBHelper = new SmartDBHelper(this);
        mSession = new SessionManager(this);
        mDevice = DeviceUtils.getDeviceName();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();
        //設定左邊選單項目
        navDrawerItems.add(new NavDrawerItem(R.id.slide_menu_upload,getString(R.string.navdrawer_item_upload),R.drawable.slide_upload));
        navDrawerItems.add(new NavDrawerItem(R.id.slide_menu_setting,getString(R.string.navdrawer_item_settings),R.drawable.slide_setting));
        navDrawerItems.add(new NavDrawerItem(R.id.slide_menu_patrol_data,getString(R.string.navdrawer_item_patrol_data),R.drawable.slide_patrol_data));
        navDrawerItems.add(new NavDrawerItem(R.id.slide_menu_sign_out,getString(R.string.navdrawer_item_sign_out),R.drawable.slide_sign_out));
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list mNavDrawerListAdapter
        mNavDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItems);
        mDrawerList.setAdapter(mNavDrawerListAdapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        //region  NFC 設定
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);//NFC
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ((Object) this).getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        //endregion

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.execute, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 路線 => 管制點
     * @param routeItem
     */
    @Override
    public void onRouteFragmentInteraction(RouteItem routeItem) {
        LOGD(TAG,"wayid=" + routeItem.getID());
        switchContent(new Step2PointFragment(routeItem),FragmentTagConstants.STEP2_POINT_FRAGMENT);

    }

    @Override
    public void onPointFragmentInteraction(PointItem pointItem) {
        LOGD(TAG,"ctlptid=" + pointItem.getCTLPTID());
        switchContent(new Step3EquipFragment(pointItem), FragmentTagConstants.STEP3_EQUIP_FRAGMENT);
    }

    @Override
    public void onEquipFragmentInteraction(EquipmentItem equipmentItem) {
        LOGD(TAG,"eqid =" + equipmentItem.getID());
        Intent intent = new Intent(getApplicationContext(),
                ExecuteCheckItemActivity.class);
        intent.putExtra("eqid", equipmentItem);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        startActivity(intent);
    }


    /**
     * 切換 Fragment 使用
     * @param fragment
     * @param tag tag 識別
     */
    public void switchContent(Fragment fragment,String tag) {
        if( mFragment != fragment) {
            mFragment = fragment;
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left,R.anim.pull_in_left,R.anim.push_out_right);
            ft.replace(R.id.container, fragment,tag);
            ft.addToBackStack(tag);
            ft.commit();
        }

    }

    /**
     * 登出
     */
    private void signOut()
    {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.system_dialog_title))
                .setMessage(getString(R.string.system_message_sure_to_leave))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                }).show();
    }

    @Override
    public void onBackPressed() {

        String currentFragmentTag = getCurrentFragmentTag();
        LOGD(TAG,"CurrentFragmentTag:" + currentFragmentTag);
        if(currentFragmentTag.equals(FragmentTagConstants.STEP1_ROUTE_FRAGMENT))
        {
            signOut();
        }
        else if(currentFragmentTag.equals(FragmentTagConstants.STEP3_EQUIP_FRAGMENT))
        {
            //目前已經在設備層 但是還沒做完 提示使用者確定要返回嗎!?
            Step3EquipFragment step3EquipFragment  = (Step3EquipFragment)mFragmentManager.findFragmentByTag(FragmentTagConstants.STEP3_EQUIP_FRAGMENT);
            if (step3EquipFragment != null && step3EquipFragment.isVisible())
            {
                if(step3EquipFragment.getCurrentProgress()!=100)
                {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.system_dialog_title))
                            .setMessage(getString(R.string.system_message_not_done_sure_to_leave_cp))
                            .setNegativeButton(getString(R.string.action_cancel), null)
                            .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getSupportFragmentManager().popBackStack();
                                }

                            }).show();
                }
                else
                {
                    super.onBackPressed();
                }
            }
            else
            {
                super.onBackPressed();
            }

        }
        else
        {
            super.onBackPressed();
        }
    }


    /**
    * Slide menu item click listener
    * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
            NavDrawerItem navDrawerItem = mNavDrawerListAdapter.getItem(position);
            if(navDrawerItem!=null)
            {
                int current_id = navDrawerItem.getId();

                switch (current_id) {
                    case R.id.slide_menu_upload:

                      try {
                          SessionManager sm =new SessionManager(ExecuteActivity.this);
                         String account =sm.getUserSession().getmAccount();
                        String url = mPreferences.getUploadUrl();
                          //Smart/api/Upload?account={account}&PDAID={PDAID}&PDAVersion={PDAVersion}
                         //TODO;url
//                        String full_url =  url + "?account="+"03045"+"&PDAID="+"ss"+"&PDAVersion=" +"v1.0.0";
                          mDevice =mDevice.replace(" ", "_");
                          account = account.replace(" ","");
                        String full_url = url + "?account="+account+"&PDAID="+mDevice+"&PDAVersion=" +"v1.0.0";
                        Log.i(TAG,"full_url:"+full_url);
                        String file_folder_path = mPreferences.getFilePath() ;//APP存放目錄
                        String upload_zip_path = file_folder_path + File.separator + SystemConstants.UPLOAD_FILE_NAME;
                        HttpMultipartPost post = new HttpMultipartPost(ExecuteActivity.this, upload_zip_path, full_url);
                        post.execute();

                      }
                      catch (Exception ex)
                      {
                          Log.i(TAG,ex.toString());
                      }
//                        ContactDialogFragment contactDialogFragment = ContactDialogFragment.newInstance();
//                        contactDialogFragment.show(getSupportFragmentManager(), FragmentTagConstants.CONTACT_DIALOG_FRAGMENT);
                        break;
                    case R.id.slide_menu_setting:
                        Intent setting_intent = new Intent(getApplicationContext(),
                                SettingsActivity.class);
                        startActivity(setting_intent);
                        break;
                    case R.id.slide_menu_patrol_data:
                        Intent patrol_intent = new Intent(getApplicationContext(),
                                PatrolDataActivity.class);
                        startActivity(patrol_intent);
                        break;
                    case R.id.slide_menu_sign_out:
                        signOut();
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume()
    {
        //TODO 拿掉 isCanReadTag() 因方法判斷有問題
        super.onResume();
        LOGD(TAG,"onResume");
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,null,null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null && isCanReadTag())
            mNfcAdapter.disableForegroundDispatch(this);
        mSmartDBHelper.close();
    }


    /**
     * 取得目前 FragmentTag
     * 取到0 識別為第一個 Fragment
     * @return
     */
    private String getCurrentFragmentTag(){
        String result = FragmentTagConstants.STEP1_ROUTE_FRAGMENT;
        if(mFragmentManager.getBackStackEntryCount()!=0)
        {
            result = mFragmentManager.getBackStackEntryAt(mFragmentManager.getBackStackEntryCount() - 1).getName();
        }
        return result;
    }

    /**
     * 判斷目前是否可以進行靠卡
     * true:表示目前在管制點可以開始靠卡 false:在其他畫面
     * @return
     */
    private boolean isCanReadTag()
    {
        boolean result = false;
        Fragment fragment  = mFragmentManager.findFragmentByTag(FragmentTagConstants.STEP2_POINT_FRAGMENT);
        if(fragment!=null)
        {
            if(((Object)fragment).getClass() == Step2PointFragment.class)
            {
                if (fragment.isVisible()) {
                    result = true;
                }
            }
        }
        return result;
    }

    @Override
    protected void onNewIntent(Intent intent) {

        try
        {
            //TODO 先應急 目前不知道為什麼不能只要在 OnResume 和 OnPause用就好
            if(!isCanReadTag())
                return;
            intent.putExtra(NFCUtils.ARG_DEVICE,mDevice);
            String uid = NFCUtils.resolveUIDIntent(intent);
            if(!uid.isEmpty())
            {

                uid = uid.toUpperCase();
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(300);

                //比對當前資料有無此tagID
                LOGD(TAG,"uid:"+ uid );
                Step2PointFragment step2PointFragment  = (Step2PointFragment)mFragmentManager.findFragmentByTag("Step2PointFragment");
                if(step2PointFragment!=null&& step2PointFragment.isVisible())
                {
                    step2PointFragment.getRFIDItem(uid);
                }

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
