/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import fpg.ftc.si.smart.ExecuteCheckItemActivity;
import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.adapter.AbnormalAdapter;
import fpg.ftc.si.smart.adapter.DealAdapter;
import fpg.ftc.si.smart.adapter.GalleryImageAdapter;
import fpg.ftc.si.smart.model.AbnormalItem;
import fpg.ftc.si.smart.model.CheckItem;
import fpg.ftc.si.smart.model.DealMethodItem;
import fpg.ftc.si.smart.util.consts.SystemConstants;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.LOGE;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 檢查項目
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Step4CheckItemFragment} interface
 * to handle interaction events.
 *
 */
public class Step4CheckItemFragment extends Fragment {

    private static final String TAG = makeLogTag(Step4CheckItemFragment.class);

    private View mRootView;
    private ListView mPointListView;
    private ExecuteCheckItemActivity mActivity;

    public CheckItem mCheckItem;//viewModel


    //頁面控制項
    private TextView mPagerInfo;//用來顯示目前總有幾筆資料,目前在第幾筆
    private LinearLayout mFeelContainer;
    private LinearLayout mRecordContainer;
    private TextView mHighValue;
    private TextView mLowValue;
    private TextView mStandarValue;
    private Button mBtnCamera;
    private TextView mStatus;//在頁面顯示 已檢查
    private RadioGroup mRdgFeel;//感官Radio群組
    private EditText mRecord;//抄表數據
    private Spinner mSP_Abnormal;//異常
    private Spinner mSP_Deal;//對策
    private EditText mComment;//備註
    private AbnormalAdapter mAbnormalAdapter;//異常
    private DealAdapter mDealAdapter;//對策
    private Gallery mGallery_View;
    private GalleryImageAdapter mGalleryImageAdapter;
    private int mDealPos = -1;//因為第二層選單DataSource比較晚進來 所以放在記憶體去記目前應該要選第幾個 選完後再調回-1
    public Step4CheckItemFragment() {
        // Required empty public constructor
    }

    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static final String ARG_TOTAL = "total";
    public static final String ARG_CHECK_ITEM = "check_item";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private int mTotalCount;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static Step4CheckItemFragment create(int pageNumber,int totalCount,CheckItem checkItem) {
        Step4CheckItemFragment fragment = new Step4CheckItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putInt(ARG_TOTAL, totalCount);
        args.putSerializable(ARG_CHECK_ITEM, checkItem);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mTotalCount = getArguments().getInt(ARG_TOTAL);
        mCheckItem = (CheckItem)getArguments().getSerializable(ARG_CHECK_ITEM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        LOGD(TAG,"onCreateView ... "+ String.valueOf(mPageNumber+1));
        mActivity = (ExecuteCheckItemActivity) getActivity();
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_check_item, container, false);

        mPagerInfo = (TextView) mRootView.findViewById(R.id.lb_pager_info);
        mFeelContainer = (LinearLayout) mRootView.findViewById(R.id.feel_container);
        mRecordContainer = (LinearLayout) mRootView.findViewById(R.id.record_container);
        mHighValue = (TextView) mRootView.findViewById(R.id.lb_hight_value);
        mLowValue = (TextView) mRootView.findViewById(R.id.lb_low_value);
        mStandarValue = (TextView) mRootView.findViewById(R.id.lb_standar_value);
        mStatus = (TextView) mRootView.findViewById(R.id.lb_status);
        mBtnCamera = (Button) mRootView.findViewById(R.id.btn_camera);
        mRdgFeel = (RadioGroup) mRootView.findViewById(R.id.rdg_feel);
        mRecord = (EditText) mRootView.findViewById(R.id.txt_record);
        mComment = (EditText) mRootView.findViewById(R.id.txt_comment);
        mSP_Abnormal = (Spinner) mRootView.findViewById(R.id.sp_abnornal);
        mSP_Deal = (Spinner) mRootView.findViewById(R.id.sp_deal);
        mGallery_View = (Gallery) mRootView.findViewById(R.id.gallery_view);
        //spinner 預設設未啟用
        mSP_Abnormal.setEnabled(false);
        mSP_Deal.setEnabled(false);






        // 綁定標題指引
        ((TextView) mRootView.findViewById(R.id.lb_check_name)).setText(
                getString(R.string.title_template_step, mPageNumber + 1, mCheckItem.getNAME()));

        mPagerInfo.setText(getString(R.string.title_template_pager,mPageNumber + 1,mTotalCount));

        // 綁定異常原因
        mAbnormalAdapter = new AbnormalAdapter(mActivity,mCheckItem.getAbnormalItems());
        mSP_Abnormal.setAdapter(mAbnormalAdapter);

        // 綁定處理方式
        mDealAdapter = new DealAdapter(mActivity);
        mSP_Deal.setAdapter(mDealAdapter);

        // 異常原因事件
        mSP_Deal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DealMethodItem dealMethodItem = (DealMethodItem) parent.getItemAtPosition(position);
                //將資料經使用者輸入後寫到cache物件
                mCheckItem.setResultDealID(dealMethodItem.getDEALID());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 異常原因事件
        mSP_Abnormal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AbnormalItem abnormalItem = (AbnormalItem) parent.getItemAtPosition(position);
                mDealAdapter.setItemList(abnormalItem.getDealMethodItems());
                //將資料經使用者輸入後寫到cache物件
                mCheckItem.setResultABID(abnormalItem.getABID());
                LOGD(TAG,"mDealAdapter...setting:" + String.valueOf(mPageNumber+1));

                if(mDealPos!=-1)
                {
                    mSP_Deal.setSelection(mDealPos);
                    mDealPos = -1;
                    LOGD(TAG,"mDealAdapter...reset pos:" + String.valueOf(mPageNumber+1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 備註填寫行為 Event
        mComment.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String userTypeValue = s.toString();
                //將Comment欄位寫到快取物件
                mCheckItem.setResultComment(userTypeValue);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        // 切分感官
        if(mCheckItem.getIsFeel())
        {
            //感官
            mFeelContainer.setVisibility(View.VISIBLE);
            mRecordContainer.setVisibility(View.GONE);


            //感官行為 Event
            mRdgFeel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rb_ok:
                            mCheckItem.setResultFeel(true);
                            mCheckItem.setError(false);
                            break;
                        case R.id.rb_error:
                            mCheckItem.setResultFeel(false);
                            mCheckItem.setError(true);
                            break;

                    }

                    //有操作就視為已檢查
                    mCheckItem.setIsChecked(true);

                    LOGD(TAG,"Click! ... "+ String.valueOf(mPageNumber+1));
                    refreshCurrentView();
                }
            });


            Boolean resultFeel = mCheckItem.getResultFeel();
            if(resultFeel!=null)
            {
                if(resultFeel== true)
                {
                    mRdgFeel.check(R.id.rb_ok);

                }
                else
                {
                    mRdgFeel.check(R.id.rb_error);
                }
            }

        }
        else
        {
            //抄表
            mRecordContainer.setVisibility(View.VISIBLE);
            mFeelContainer.setVisibility(View.GONE);
            mStandarValue.setText(String.valueOf(mCheckItem.getCheckStd()));
            mHighValue.setText(mCheckItem.getHight_Val() + " " + mCheckItem.getUnit());
            mLowValue.setText(mCheckItem.getLow_Val() + " " + mCheckItem.getUnit());

            //抄表 Event
            mRecord.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    String userTypeValue = s.toString();
                    LOGD(TAG, "afterTextChanged:" + userTypeValue);
                    if (!userTypeValue.equals("")) {
                        Float resultValue = null;

                        try {

                            resultValue = Float.parseFloat(userTypeValue);
                            mCheckItem.setResultValue(resultValue);
                            mCheckItem.setIsChecked(true);

                        } catch (NumberFormatException nfe) {

                            mRecord.setError("抄表數值不正確");
                            mCheckItem.setResultValue(null);
                            mCheckItem.setIsChecked(false);

                        }
                    }
                    else
                    {
                        mCheckItem.setResultValue(null);
                        mCheckItem.setIsChecked(false);
                    }

                    refreshCurrentView();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });

        }


        // 拍照動作
        mBtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                String date = dateFormat.format(new Date());
                String photoFile = "Picture_" + date + ".jpg";
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = new File(mActivity.mPreferences.getTempPath(), photoFile);
                Uri fileUri = Uri.fromFile(photo);
                mActivity.setCapturedImageURI(fileUri);
                mActivity.setCurrentPhotoPath(fileUri.getPath());
                intent.putExtra(MediaStore.EXTRA_OUTPUT,mActivity.getCapturedImageURI());
                mActivity.startActivityForResult(intent, SystemConstants.REQUEST_TAKE_PHOTO);
            }
        });


        // 設定圖片
        mGallery_View.setSpacing(1);
        mGalleryImageAdapter = new GalleryImageAdapter(mActivity);
        mGallery_View.setAdapter(mGalleryImageAdapter);
        mGalleryImageAdapter.setItemList(mCheckItem.getPhotoItems());

        // click listener for Gallery
        mGallery_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                String image_file_path = mGalleryImageAdapter.getItem(position);
                //使用內建的圖片檢視
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(image_file_path);
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent);
            }
        });

        //TODO 未實作完成
//        mGallery_View.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//
//                //mGalleryImageAdapter.setItemSelected(position,holder.is_checked.isChecked());
//                boolean hasCheckedItems = true;//= mAdapter.getSelectedCount() > 0;
//
//
//
//                if (hasCheckedItems && mActivity.ActionMode == null)
//                {
//                    // there are some selected items, start the actionMode
//                    mActivity.ActionMode  = mActivity.startActionMode(mActionModeCallback);
//
//                }
//                else if (!hasCheckedItems && mActivity.ActionMode  != null)
//                {
//                    // there no selected items, finish the actionMode
//                    mActivity.ActionMode.finish();
//
//                }
//                if (mActivity.ActionMode != null)
//                {
//                    String action_mode_title = String.format(getString(R.string.format_item_choosed), 1);
//
//                    mActivity.ActionMode.setTitle(action_mode_title);
//                    //顯示上傳按鈕
//                }
//
//                return false;
//            }
//        });

        // 回填資料
        if(mCheckItem.getIsChecked())
        {
            refreshCurrentView();
            refillDate();
        }

        return mRootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LOGD(TAG, "onDestroy ... " + String.valueOf(mPageNumber + 1));

    }


    /**
     * 去記住使用者上一次選了哪一個選項
     * @param abId
     * @param dealID
     * @return
     */
    private Vector<Integer> getAbDealPos(String abId,String dealID)
    {
        Vector<Integer> result = null;
        int first_idx = 0;
        int second_idx = 0;
        for(AbnormalItem ab :mCheckItem.getAbnormalItems())
        {
            if(ab.getABID().equals(abId))
            {
                for(DealMethodItem deal: ab.getDealMethodItems())
                {
                    if(deal.getDEALID().equals(dealID))
                    {
                        result = new Vector<Integer>();
                        result.add(first_idx);
                        result.add(second_idx);
                        break;
                    }
                    second_idx++;
                }
            }
            first_idx++;
        }
        return result;
    }

    /**
     * 將cache物件(mCheckItem綁回到畫面)
     */
    private void refillDate()
    {
        if(mCheckItem.getIsChecked())
        {
            //綁備註
            if(mCheckItem.getResultComment()!=null)
            {
                mComment.setText(mCheckItem.getResultComment());
            }

            if(mCheckItem.getIsFeel())
            {
                //感官
                if(mCheckItem.getResultFeel())
                {
                    mRdgFeel.check(R.id.rb_ok);
                }else
                {
                    mRdgFeel.check(R.id.rb_error);
                }
            }
            else {
                //抄表
                Float result_value = mCheckItem.getResultValue();
                mRecord.setText(String.valueOf(result_value));
            }

            //綁回 異常與對策
            Vector<Integer> getAbDealPos = getAbDealPos(mCheckItem.getResultABID(),mCheckItem.getResultDealID());
            if(getAbDealPos!=null)
            {
                int idx = 0;
                for(Integer pos : getAbDealPos)
                {
                    if(idx==0)
                    {
                        //第一個索引 是異常選單
                        mSP_Abnormal.setSelection(pos);
                    }
                    else
                    {
                        mDealPos = pos;
                    }
                    idx++;
                }
            }

        }
    }

    /**
     * 重新更新此畫面資料
     */
    private void refreshCurrentView()
    {
        LOGD(TAG,"refreshCurrentView ... "+ String.valueOf(mPageNumber+1));
        if(mCheckItem.getIsChecked())
        {
            mStatus.setText("已檢查");

            //檢查是否異常
            // 區分感官
            if(mCheckItem.getIsFeel())
            {
                //感官
                Boolean isFeelGood = mCheckItem.getResultFeel();
                if(isFeelGood)
                {
                    isErrorNow(false);

                }
                else
                {
                    isErrorNow(true);
                }

            }
            else
            {
                Float resultValue = mCheckItem.getResultValue();
                if(resultValue!=null)
                {
                    int color_default = mActivity.getResources().getColor(R.color.black);
                    int color_error = mActivity.getResources().getColor(R.color.holo_red_light);

                    if(resultValue >=mCheckItem.getHight_Val())
                    {
                        //超過上限
                        isErrorNow(true);
                        mCheckItem.setError(true);
                        mHighValue.setTextColor(color_error);
                        mLowValue.setTextColor(color_default);
                    }
                    else if(resultValue<=mCheckItem.getLow_Val())
                    {
                        //低於下限
                        isErrorNow(true);
                        mCheckItem.setError(true);
                        mHighValue.setTextColor(color_default);
                        mLowValue.setTextColor(color_error);

                    }
                    else
                    {
                        //正常範圍
                        isErrorNow(false);
                        mCheckItem.setError(false);
                        mHighValue.setTextColor(color_default);
                        mLowValue.setTextColor(color_default);
                    }
                }
            }



        }
        else
        {
            mStatus.setText("未檢查");
        }

        //呼叫主Activity的Method
        mActivity.updateProgress();
    }

    /**
     * 當有異常
     * @param isError True:有異常 False:正常
     */
    private void isErrorNow(boolean isError)
    {
        if(isError)
        {
            //開啟spinner
            mSP_Abnormal.setEnabled(true);
            mSP_Deal.setEnabled(true);
        }
        else
        {
            //關閉spinner
            mSP_Abnormal.setEnabled(false);
            mSP_Deal.setEnabled(false);
        }
    }

    /**
     * 添加照片進去
     * @param photoPath
     */
    public void updatePhotoItem(String photoPath)
    {
        mGalleryImageAdapter.addItemList(photoPath);
    }

    /**
     * 當圖面常按時觸發
     */
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            // assumes that you have "contexual.xml" menu resources
            inflater.inflate(R.menu.execute_check_item, menu);
            return true;
        }

        // called each time the action mode is shown. Always called after
        // onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // called when the user selects a contextual menu item
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    //刪除照片
                    mode.finish();
                    mGalleryImageAdapter.deleteSelectedItem(0);
                    return true;
                default:
                    return false;
            }
        }

        // called when the user exits the action mode
        public void onDestroyActionMode(ActionMode mode) {
            mActivity.ActionMode = null;
        }
    };


}
