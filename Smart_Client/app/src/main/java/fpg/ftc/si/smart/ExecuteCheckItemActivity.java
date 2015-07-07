/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart;

import android.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import fpg.ftc.si.smart.adapter.CheckAdapter;
import fpg.ftc.si.smart.adapter.CheckBridge;
import fpg.ftc.si.smart.adapter.ScreenSlidePagerAdapter;
import fpg.ftc.si.smart.dao.AbnormalReason;
import fpg.ftc.si.smart.dao.DealMethod;
import fpg.ftc.si.smart.dao.RecordData;
import fpg.ftc.si.smart.dao.RecordDataPic;
import fpg.ftc.si.smart.fragment.Step4CheckItemFragment;
import fpg.ftc.si.smart.fragment.dialog.CheckDialogFragment;
import fpg.ftc.si.smart.fragment.dialog.ConfirmDialogFragment;
import fpg.ftc.si.smart.model.AbnormalItem;
import fpg.ftc.si.smart.model.CheckItem;
import fpg.ftc.si.smart.model.DealMethodItem;
import fpg.ftc.si.smart.model.EquipmentItem;
import fpg.ftc.si.smart.provider.SmartDBHelper;
import fpg.ftc.si.smart.util.consts.FragmentTagConstants;
import fpg.ftc.si.smart.util.consts.SystemConstants;
import fpg.ftc.si.smart.util.DateUtils;
import fpg.ftc.si.smart.util.FileUtils;
import fpg.ftc.si.smart.util.PreferenceUtils;
import fpg.ftc.si.smart.util.SessionManager;
import fpg.ftc.si.smart.util.UserSession;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 設備底下的基準
 */
public class ExecuteCheckItemActivity extends FragmentActivity {

    private static final String TAG = makeLogTag(ExecuteCheckItemActivity.class);

    //資料庫
    private SmartDBHelper mSmartDBHelper ;

    public PreferenceUtils mPreferences;
    private EquipmentItem mEquipmentItem;
    public SessionManager mSession;
    public ActionMode ActionMode;

    private FragmentManager mFragmentManager;
    private TextView mEquipName;
    private Button mBtnSave;//儲存
    private Button mBtnRest;//備機
    private Button mBtnChangeMode;//變更抄表方式
    private Button mBtnAllFeelIsGood;
    private ProgressBar mProgressBar;

    //分頁元件 (使用左右滑動)
    private ViewPager mPager;
    public ScreenSlidePagerAdapter mPagerAdapter;

    //清單 (使用下拉滑動)
    private ListView mCheckListView;
    private CheckAdapter mCheckAdapter;
    private FrameLayout mListContainer;
    //資料集
    private CheckBridge mCheckBridge;
    //照片
    private final static int CAMERA_RESULT = 0;
    private Uri capturedImageURI;
    private String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_execute_cp);
        //actionbar
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.drawable.ic_action_go_to_today);

        mPreferences = new PreferenceUtils(this);
        mSession = new SessionManager(this);
        mSmartDBHelper = new SmartDBHelper(this);
        mFragmentManager = this.getSupportFragmentManager();
        //取回物件
        mEquipmentItem = (EquipmentItem)getIntent().getSerializableExtra("eqid");
        mEquipName = (TextView) findViewById(R.id.lb_equip_name);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEquipName.setText(mEquipmentItem.getNAME());
        mBtnSave = (Button) findViewById(R.id.btn_save);
        mBtnRest = (Button) findViewById(R.id.btn_rest);
        mBtnChangeMode= (Button) findViewById(R.id.btn_change_mode);
        mBtnAllFeelIsGood = (Button) findViewById(R.id.btn_all_feel_is_good);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mCheckListView = (ListView)findViewById(R.id.check_list);
        mCheckAdapter = new CheckAdapter(this);
        mCheckListView.setAdapter(mCheckAdapter);
        mListContainer = (FrameLayout) findViewById(R.id.list_container);
        //區域變數
        final UserSession userSession = mSession.getUserSession();
        final Date currentDate = new Date();
        final String currentDateStr = DateUtils.format(currentDate,DateUtils.FORMAT_YYYYMMDD);
        final String currentDateTimeStr = DateUtils.format(currentDate,DateUtils.FORMAT_YYYYMMDD_HHMMSS);
        final String clsid = userSession.getmCLSID();
        final String wayid = mEquipmentItem.getWAYID();
        final String ctlptid = mEquipmentItem.getCTLPTID();
        final String eqid = mEquipmentItem.getID();
        final String urid = userSession.getmUserId();
        final String pic_folder = mPreferences.getPicPath();
        final String temp_folder = mPreferences.getTempPath();


        // 綁定事件 listview抄表點選 單擊
        mCheckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                final CheckItem dataHolder = mCheckAdapter.getItem(position);

                //提示應該要更新了
                CheckDialogFragment dialogFragment = CheckDialogFragment.newInstance(dataHolder,position);
                dialogFragment.show(getSupportFragmentManager(),FragmentTagConstants.CHECK_DIALOG_FRAGMENT);

                //自動導入下一筆
                LinkedHashSet<Integer> notCheckMap =  mCheckBridge.getNotCheckMap();

                if(notCheckMap.size() > 1)
                {
                    int next_position = -1;
                    int fist_candidate = 0;//候選
                    boolean is_first_time_in = true;
                    for(Integer queue_pos : notCheckMap)
                    {
                        if(is_first_time_in)
                        {
                            fist_candidate = queue_pos;
                            is_first_time_in = false;
                        }

                        if(queue_pos>position)
                        {
                            next_position = queue_pos;
                            break;
                        }
                    }

                    if(next_position == -1)
                    {
                        //抓第一個
                        next_position = fist_candidate;
                    }

                    final int finalNext_position = next_position;
                    dialogFragment.addListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mCheckListView.performItemClick(view,finalNext_position,0);
                            LOGD(TAG,"目前所在第"+String.valueOf(position)+"個,下一個為:第" + String.valueOf(finalNext_position)+"個");
                        }
                    });

                }



            }
        });


        // 綁定事件 更換模式
        mBtnChangeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = mPreferences.getModeIfPage();
                if(flag)
                    mPreferences.setModeIfPage(false);
                else
                    mPreferences.setModeIfPage(true);
                setmDisplayChangeMode();
            }
        });

        // 綁定事件 儲存
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //從UI物件取出已檢查的
                List<CheckItem> check_source = mCheckBridge.getCheckSource();

                if (check_source.size() == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.system_message_no_data_can_save), Toast.LENGTH_SHORT).show();
                    return;
                }

                //轉換物件 CheckItem => RecordData
                List<RecordData> recordDataList = new ArrayList<RecordData>();


                for (CheckItem item : check_source) {

                    String result_feel = null;
                    if (item.getResultFeel() != null) {
                        result_feel = item.getResultFeel() == true ? "0" : "1";
                    }
                    String abID = "";
                    String dealID = "";
                    if (item.isError()) {
                        abID = item.getResultABID();
                        dealID = item.getResultDealID();
                    }
                    RecordData record_data_new_item = new RecordData(
                            clsid,
                            wayid,
                            ctlptid,
                            eqid,
                            item.getID(),
                            currentDateStr,
                            "000000",
                            currentDateStr,
                            "000000",
                            currentDateStr,
                            "000000",
                            urid,
                            item.getResultValue(),
                            result_feel,
                            abID,
                            dealID,
                            currentDateTimeStr,
                            item.getResultComment()
                    );



                    List<RecordDataPic> recordDataPicList = new ArrayList<RecordDataPic>();
                    //如果照片裡面有路徑是TEMP 就準備搬到 PIC裡面
                    for(String photo_item : item.getPhotoItems())
                    {
                        String photo_file_path = photo_item;
                        String photo_file_name = "";
                        if(photo_item.contains(temp_folder))
                        {
                            // 將TEMP檔案複製過去PIC
                            File temp_file = new File(photo_item);
                            String file_name = temp_file.getName();
                            photo_file_name = file_name;
                            photo_file_path = pic_folder + File.separator + file_name;
                            FileUtils.copyFile(temp_file,new File(photo_file_path));

                        }
                        else
                        {
                            File photo_file = new File(photo_item);
                            photo_file_name = photo_file.getName();
                        }

                        RecordDataPic record_data_pic_new_item = new RecordDataPic(
                                record_data_new_item.getID(),
                                photo_file_name,
                                photo_file_path
                        );

                        recordDataPicList.add(record_data_pic_new_item);
                    }

                    record_data_new_item.setRecordDataPics(recordDataPicList);
                    recordDataList.add(record_data_new_item);
                }

                // Delete Insert
                boolean result_flag = mSmartDBHelper.deleteInsertRecordDataList(recordDataList);
                if (result_flag) {
                    //儲存成功
                    Toast.makeText(getApplicationContext(), getString(R.string.system_message_save_success), Toast.LENGTH_SHORT).show();

                    //如果全部做完 返回原本頁面
                    int count = mCheckBridge.getCount();
                    int checkCount = mCheckBridge.getCheckCount();
                    if (count == checkCount) {
                        onBackPressed();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.system_message_save_fail), Toast.LENGTH_SHORT).show();
                }

            }
        });

        //抓取設備資料
        ArrayList<CheckItem> checkItemArrayList = mSmartDBHelper.getCheckItems(mEquipmentItem.getID(),clsid,wayid,ctlptid,currentDateStr,urid);


        //loop data
        for(CheckItem item : checkItemArrayList)
        {
            //異常原因物件 UI
            List<AbnormalItem> abnormalItemList = new ArrayList<AbnormalItem>();

            //異常原因 DB
            List<AbnormalReason> abnormalReasonList = mSmartDBHelper.getAbnormalReasons(item.getID());

            //處理方式 DB
            for (AbnormalReason abnormalReason : abnormalReasonList)
            {
                AbnormalItem abnormalItem = new AbnormalItem(abnormalReason.getABID(),abnormalReason.getABDESC());//UI
                List<DealMethodItem> dealMethodItemList = new ArrayList<DealMethodItem>();//UI
                List<DealMethod> dealMethodList = mSmartDBHelper.getDealMethods(abnormalReason.getABID());

                for(DealMethod dealMethod : dealMethodList)
                {
                    dealMethodItemList.add(new DealMethodItem(dealMethod.getDEALID(),dealMethod.getABID(),dealMethod.getDEALNAME()));
                }

                abnormalItem.setDealMethodItems(dealMethodItemList);
                abnormalItemList.add(abnormalItem);
            }

            //如果原本已經有填寫異常原因表示上一次有異常 需要註記異常
            if(!TextUtils.isEmpty(item.getResultABID()))
            {
                item.setError(true);
            }
            item.setAbnormalItems(abnormalItemList);
        }

        //mDataSource = checkItemArrayList;
        mCheckBridge = new CheckBridge(checkItemArrayList);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                //invalidateOptionsMenu();
                if(ActionMode!=null)
                {
                    ActionMode.finish();
                    ActionMode = null;

                }
            }
        });

        mBtnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.system_message_can_not_turn_off), Toast.LENGTH_SHORT).show();
            }
        });

        // 綁定事件 感官正常
        mBtnAllFeelIsGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialogFragment confirmDialog = ConfirmDialogFragment.newInstance(getString(R.string.system_message_set_all_feel_is_good));
                confirmDialog.addListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mCheckBridge.setAllFeelIsGood();
                        mCheckAdapter.notifyDataSetChanged();
                        updateProgress();//呼叫進度條
                        return;
                    }
                });
                confirmDialog.show(ExecuteCheckItemActivity.this.getSupportFragmentManager(), FragmentTagConstants.CONFIRM_DIALOG_FRAGMENT);
            }
        });

        setmDisplayChangeMode();
    }

    /**
     * 切換頁面顯示方式
     */
    private void setmDisplayChangeMode()
    {
        if(mPreferences.getModeIfPage())
        {
            //左右抄表
            mPager.setVisibility(View.VISIBLE);

            mPagerAdapter.setmDataSource(mCheckBridge);
            mListContainer.setVisibility(View.GONE);

        }
        else
        {
            //上下抄表
            mListContainer.setVisibility(View.VISIBLE);
            mCheckAdapter.setmDataSource(mCheckBridge);
            mPager.setVisibility(View.GONE);
        }
        updateProgress();//呼叫進度條
    }


    @Override
    public void onPause() {
        super.onPause();
        mSmartDBHelper.close();
    }

    /*
 * 離開
 * 提醒使用者會登出
 *
 * */
    @Override
    public void onBackPressed() {
        int count = mCheckBridge.getCount();
        int checkCount = mCheckBridge.getCheckCount();
        if( count != checkCount)
        {
            String message = "總共需完成:"+ count + "項基準,目前只完成:" + checkCount + "項,確定要離開嗎?";

            ConfirmDialogFragment confirmDialog = ConfirmDialogFragment.newInstance(message);
            confirmDialog.addListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
                    return;
                }
            });
            confirmDialog.show(this.getSupportFragmentManager(), FragmentTagConstants.CONFIRM_DIALOG_FRAGMENT);
        }
        else
        {
            DeleteTempImageTask deleteTempImageTask = new DeleteTempImageTask();
            deleteTempImageTask.execute((Void) null);

            finish();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            return;
        }
    }

    /**
     * 隱藏小鍵盤
     */
    public void HideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public void updateCheckDataSource()
    {
        mCheckAdapter.notifyDataSetChanged();
        updateProgress();
    }


    /**
     * 更新進度條
     */
    public void updateProgress()
    {
        mProgressBar.setProgress(mCheckBridge.getProgress());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SystemConstants.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            String currentPhotoPath = getCurrentPhotoPath();
             // Show the full sized image.
             // setFullImageFromFilePath(activity.getCurrentPhotoPath(), mImageView); setFullImageFromFilePath(activity.getCurrentPhotoPath(), mThumbnailImageView); } else { Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT) .show();
            if(mPreferences.getModeIfPage())
            {
                //分頁式
                // 呼叫 Fragment method 更新這張照片和基準綁定
                int position = mPager.getCurrentItem();
                Step4CheckItemFragment step4CheckItemFragment =  (Step4CheckItemFragment) mPagerAdapter.getFragment(position);
                step4CheckItemFragment.updatePhotoItem(currentPhotoPath);

            }
            else
            {
                //抄表式
                if(currentPhotoPath!=null)
                {

                    CheckDialogFragment checkDialogFragment = (CheckDialogFragment)getSupportFragmentManager().findFragmentByTag(FragmentTagConstants.CHECK_DIALOG_FRAGMENT);
                    if(checkDialogFragment != null)
                    {
                        if(checkDialogFragment.getActivity() == this)
                        {

                            LOGD(TAG,"UPDATE");
                            checkDialogFragment.updatePhoto(currentPhotoPath);
                        }
                    }


                }

            }



         }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.execute_check_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                //NavUtils.navigateUpTo(this, new Intent(this, ExecuteActivity.class));
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }





    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCapturedImageURI(Uri capturedImageURI) {
        this.capturedImageURI = capturedImageURI;
    }

    public Uri getCapturedImageURI() {
        return capturedImageURI;
    }




    /**
     * 非同步 刪除掉存放在暫存區的照片
     * 離開頁面時觸發
     */
    private class DeleteTempImageTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            String temp_folder = mPreferences.getTempPath();
            File directory = new File(temp_folder);
            File[] tempFileList = directory.listFiles();
            LOGD(TAG,"刪除暫存照片中..資料筆數:" + tempFileList.length);
            for(File temp_file : tempFileList)
            {
                boolean deleted_flag = temp_file.delete();
            }
            LOGD(TAG,"刪除暫存照片完成!");

            return true;
        }

    }

}
