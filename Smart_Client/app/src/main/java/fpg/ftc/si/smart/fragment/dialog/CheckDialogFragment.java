/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import fpg.ftc.si.smart.ExecuteCheckItemActivity;
import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.adapter.AbnormalAdapter;
import fpg.ftc.si.smart.adapter.DealAdapter;
import fpg.ftc.si.smart.adapter.GalleryImageAdapter;
import fpg.ftc.si.smart.model.AbnormalItem;
import fpg.ftc.si.smart.model.CheckItem;
import fpg.ftc.si.smart.model.DealMethodItem;
import fpg.ftc.si.smart.model.VerItem;
import fpg.ftc.si.smart.util.PreferenceUtils;
import fpg.ftc.si.smart.util.consts.SystemConstants;
import fpg.ftc.si.smart.widgets.NoEnterEditText;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;


/**
 * 抄表 Dialog
 * 此頁面無法共用 綁定 ExecuteCheckItemActivity
 * ref:http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog
 * Created by MarlinJoe on 2014/5/6.
 */
public class CheckDialogFragment extends DialogFragment {

    private static final String TAG = makeLogTag(CheckDialogFragment.class);
    private static final String ARG_CHECK_ITEM = "ARG_CHECK_ITEM";
    private static final String ARG_CHECK_POS = "ARG_CHECK_POS";
    private ExecuteCheckItemActivity mContext;
    private int mDealPos = -1;//因為第二層選單DataSource比較晚進來 所以放在記憶體去記目前應該要選第幾個 選完後再調回-1
    private GalleryImageAdapter mGalleryImageAdapter;
    private CheckItem mCheckItem;
    private DialogInterface.OnClickListener mListener;
    private Float tempResultValue = null;//暫存的抄表值
    private boolean tempResultValueError = false;//檢測抄表值有無錯誤

    public static CheckDialogFragment newInstance(CheckItem mCheckItem,int position) {
        CheckDialogFragment frag = new CheckDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHECK_ITEM, mCheckItem);
        args.putInt(ARG_CHECK_POS,position);
        frag.setArguments(args);
        return frag;
    }

    /**
     * 增加外部可以添加事件
     * @param listener
     */
    public void addListener(DialogInterface.OnClickListener listener){
        mListener = listener;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View custom_title_view =inflater.inflate(R.layout.dialog_check_title, null);
        View custom_view = inflater.inflate(R.layout.dialog_check, null);
        mCheckItem = (CheckItem)getArguments().getSerializable(ARG_CHECK_ITEM);
        final int position = getArguments().getInt(ARG_CHECK_POS);

        mContext = (ExecuteCheckItemActivity)getActivity();



        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setCustomTitle(custom_title_view);
        builder.setTitle(mCheckItem.getNAME());
        builder.setView(custom_view);
//        builder.setIcon(R.drawable.ic_action_warning);

        //綁定資源
        final LinearLayout feel_container = (LinearLayout) custom_view.findViewById(R.id.feel_container);
        final LinearLayout record_container = (LinearLayout) custom_view.findViewById(R.id.record_container);
        final LinearLayout photo_container = (LinearLayout) custom_view.findViewById(R.id.photo_container);
        final Spinner mSP_Abnormal = (Spinner) custom_view.findViewById(R.id.sp_abnornal);
        final Spinner mSP_Deal = (Spinner) custom_view.findViewById(R.id.sp_deal);
        final LinearLayout deal_info = (LinearLayout) custom_view.findViewById(R.id.deal_info);
        final RadioGroup mRdgFeel = (RadioGroup) custom_view.findViewById(R.id.rdg_feel);
        final RadioButton cb_error = (RadioButton) custom_view.findViewById(R.id.rb_error);
        final NoEnterEditText txt_comment= (NoEnterEditText) custom_view.findViewById(R.id.txt_comment);
        final LinearLayout is_error_info = (LinearLayout) custom_view.findViewById(R.id.is_error_info);
        final Button btn_camera = (Button) custom_view.findViewById(R.id.btn_camera);
        final TextView lb_standar_value = (TextView) custom_view.findViewById(R.id.lb_standar_value);
        final TextView lb_hight_value = (TextView) custom_view.findViewById(R.id.lb_hight_value);
        final TextView lb_low_value = (TextView) custom_view.findViewById(R.id.lb_low_value);
        final EditText txt_record = (EditText)custom_view.findViewById(R.id.txt_record);
        final Gallery gallery_view = (Gallery)custom_view.findViewById(R.id.gallery_view);
        final AlertDialog dialog = builder.create();

        boolean isChecked = mCheckItem.getIsChecked();
        final boolean isFeel = mCheckItem.getIsFeel();


        if(isFeel)
        {
            //感官
            feel_container.setVisibility(View.VISIBLE);
            record_container.setVisibility(View.GONE);


            //感官行為 Event
            mRdgFeel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rb_ok:
                            is_error_info.setVisibility(View.GONE);
                            break;
                        case R.id.rb_error:
                            is_error_info.setVisibility(View.VISIBLE);
                            break;

                    }

                }
            });
        }
        else
        {
            //抄表
            feel_container.setVisibility(View.GONE);
            record_container.setVisibility(View.VISIBLE);
            lb_standar_value.setText(String.valueOf(mCheckItem.getCheckStd()));
            lb_hight_value.setText(mCheckItem.getHight_Val() + " " + mCheckItem.getUnit());
            lb_low_value.setText(mCheckItem.getLow_Val() + " " + mCheckItem.getUnit());

            final int color_default = mContext.getResources().getColor(R.color.black);
            final int color_error = mContext.getResources().getColor(R.color.holo_red_light);

            //抄表 Event
            txt_record.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    String userTypeValue = s.toString();
                    LOGD(TAG, "afterTextChanged:" + userTypeValue);
                    if (!userTypeValue.equals("")) {


                        try {

                            tempResultValue = Float.parseFloat(userTypeValue);
                            mCheckItem.setResultValue(tempResultValue);


                            //UI 即時呈現超過上限還下限
                            if(tempResultValue!=null)
                            {
                                if (tempResultValue >= mCheckItem.getHight_Val()) {
                                    //超過上限
                                    is_error_info.setVisibility(View.VISIBLE);
                                    lb_hight_value.setTextColor(color_error);
                                    lb_low_value.setTextColor(color_default);
                                    tempResultValueError = true;

                                } else if (tempResultValue <= mCheckItem.getLow_Val()) {
                                    //低於下限
                                    is_error_info.setVisibility(View.VISIBLE);
                                    lb_hight_value.setTextColor(color_default);
                                    lb_low_value.setTextColor(color_error);
                                    tempResultValueError = true;

                                } else {
                                    //正常範圍
                                    is_error_info.setVisibility(View.GONE);
                                    lb_hight_value.setTextColor(color_default);
                                    lb_low_value.setTextColor(color_default);
                                    tempResultValueError = false;
                                }
                            }


                        } catch (NumberFormatException nfe) {

                            txt_record.setError("抄表數值不正確");
                            tempResultValue = null;
                            is_error_info.setVisibility(View.GONE);
                            tempResultValueError = false;
                        }
                    }
                    else
                    {
                        tempResultValue = null;
                        tempResultValueError = false;
                        is_error_info.setVisibility(View.GONE);
                    }


                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });
        }



        // 綁定異常原因
        AbnormalAdapter mAbnormalAdapter = new AbnormalAdapter(mContext,mCheckItem.getAbnormalItems());
        mSP_Abnormal.setAdapter(mAbnormalAdapter);

        // 綁定處理方式
        final DealAdapter mDealAdapter = new DealAdapter(mContext);
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
                List<DealMethodItem> dealMethodItemList = abnormalItem.getDealMethodItems();
                if(dealMethodItemList.size() == 0)
                {
                    //NOTE:當對策沒有資料時,則不顯示選單
                    deal_info.setVisibility(View.GONE);
                }
                else
                {
                    mDealAdapter.setItemList(dealMethodItemList);
                    if(mDealPos!=-1)
                    {
                        mSP_Deal.setSelection(mDealPos);
                        mDealPos = -1;

                    }
                    deal_info.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        //照片

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                String date = dateFormat.format(new Date());
                String photoFile = "Picture_" + date + ".jpg";
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

                File photo = new File(mContext.mPreferences.getTempPath(), photoFile);
                Uri fileUri = Uri.fromFile(photo);
                mContext.setCapturedImageURI(fileUri);
                mContext.setCurrentPhotoPath(fileUri.getPath());
                intent.putExtra(MediaStore.EXTRA_OUTPUT,mContext.getCapturedImageURI());
                intent.putExtra("CHECK_POS",position);
                mContext.startActivityForResult(intent, SystemConstants.REQUEST_TAKE_PHOTO);
            }
        });

        //設定圖片
        mGalleryImageAdapter = new GalleryImageAdapter(mContext);
        gallery_view.setAdapter(mGalleryImageAdapter);
//        mGalleryImageAdapter.setItemList(mEquipmentItem.getPhotoItems());

        // click listener for Gallery
        gallery_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        if(mCheckItem.getPhotoItems().size()>0)
        {
            mGalleryImageAdapter.setItemList(mCheckItem.getPhotoItems());
            photo_container.setVisibility(View.VISIBLE);
        }
        else
        {
            photo_container.setVisibility(View.INVISIBLE);
        }



        //確定
        Button btnOK = (Button) custom_view.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isValid = true;
                //驗證
                int checkedId = mRdgFeel.getCheckedRadioButtonId();
                if(isFeel)
                {

                    if(checkedId == -1)
                    {
                        isValid = false;
                        cb_error.setError(getString(R.string.error_field_required));
                    }
                }
                else
                {

                    String record = txt_record.getText().toString();
                    if(TextUtils.isEmpty(record))
                    {
                        txt_record.setError("請輸入抄表數值");
                        isValid = false;
                    }
                }

                if(isFeel)
                {

                    if(R.id.rb_ok != checkedId)
                    {
                        if (mSP_Abnormal.getSelectedItem() == null) ;
                        {
                            Toast.makeText(mContext, "請先至後台建立異常基準", Toast.LENGTH_SHORT).show();
                            isValid = false;
                        }

                        if (mSP_Deal.getSelectedItem() == null) ;
                        {
                            Toast.makeText(mContext, "請先至後台建立對策基準", Toast.LENGTH_SHORT).show();
                            isValid = false;
                        }
                    }

                }

                //資料通過驗證可以儲存
                if (isValid) {
                    //進行資料儲存
                    if(isFeel)
                    {
                        //感官
                        if(R.id.rb_ok == checkedId)
                        {
                            mCheckItem.setResultFeel(true);
                            mCheckItem.setError(false);
                            mCheckItem.setResultABID("");
                            mCheckItem.setResultDealID("");
                        }else
                        {
                            mCheckItem.setResultFeel(false);
                            mCheckItem.setError(true);
                        }
                    }
                    else
                    {
                        //抄表
                        //上面的事件已經有將資料存去進
                        if(tempResultValue!=null)
                        {
                            mCheckItem.setResultValue(tempResultValue);
                            mCheckItem.setError(tempResultValueError);
                        }
                    }




                    String temp_comment = txt_comment.getText().toString();
                    mCheckItem.setResultComment(temp_comment);
                    mCheckItem.setIsChecked(true);

                    if(mCheckItem.isError())
                    {
                        //將資料經使用者輸入後寫到cache物件
                        AbnormalItem tempAbItem = (AbnormalItem)mSP_Abnormal.getSelectedItem();
                        mCheckItem.setResultABID(tempAbItem.getABID());
                        DealMethodItem tempDealItem = (DealMethodItem)mSP_Deal.getSelectedItem();
                        mCheckItem.setResultDealID(tempDealItem.getDEALID());
                    }

                    //照片寫入記憶體

                    mContext.updateCheckDataSource();


                    if(mListener!=null) {
                        mListener.onClick(dialog, 0);
                    }

                    dialog.dismiss();


                } else {
                    // 表示有錯誤,不能進行登入,將焦點聚在第一個 form 欄位
                }


            }
        });

        //回填資料 當針對已經有儲存的物件 把資料帶回
        if(isChecked)
        {
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

            Float resultValue = mCheckItem.getResultValue();
            if(resultValue!=null)
            {
                txt_record.setText(String.valueOf(resultValue));
                tempResultValue = resultValue;
            }

            String result_comment = mCheckItem.getResultComment();
            if(!TextUtils.isEmpty(result_comment))
            {
                txt_comment.setText(result_comment);
            }

            //綁回 異常與對策
            Vector<Integer> getAbDealPos = getAbDealPos(mCheckItem,mCheckItem.getResultABID(),mCheckItem.getResultDealID());
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

        //Create custom dialog
        if (dialog == null)
            super.setShowsDialog (false);
        return dialog;

    }

    //TODO not work
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }


    public void updatePhoto(String photo)
    {
        LOGD(TAG,"asdasadad");
//
//        final TextView btn_camera = (TextView) getDialog().findViewById(R.id.btn_camera);
//        btn_camera.setText("OK!!");

        mCheckItem.getPhotoItems().add(photo);
//        mGalleryImageAdapter.addItemList(photo);
        mGalleryImageAdapter.notifyDataSetChanged();
    }

    /**
     * 去記住使用者上一次選了哪一個選項
     * @param abId
     * @param dealID
     * @return
     */
    private Vector<Integer> getAbDealPos(CheckItem mCheckItem,String abId,String dealID)
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

}
