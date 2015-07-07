/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;


import java.io.IOException;

import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.model.AbnormalItem;
import fpg.ftc.si.smart.model.CheckItem;
import fpg.ftc.si.smart.model.DealMethodItem;
import fpg.ftc.si.smart.util.ImageUtils;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 抄表基準 列表式
 * This {@link android.widget.ArrayAdapter} is used to display all of the route on a user's
 * device for {@link }.
 *
 * @author MarlinJoe
 */
public class CheckAdapter extends ArrayAdapter<CheckItem> {

    private static final String TAG = makeLogTag(CheckAdapter.class);
    private LruCache<String, Bitmap> mMemoryCache;
    /**
     * Used to cache the data
     */
    private CheckBridge mCheckBridge;

    private Activity mContext;


    /**
     * Constructor of <code>RouteListAdapter</code>
     *
     * @param context The {@link android.content.Context} to use.

     */
    public CheckAdapter(final Activity context) {
        super(context, 0);
        mCheckBridge = new CheckBridge();
        mContext = context;

        // 獲取到可用內存的最大值，使用內存超出這個值會引起OutOfMemory異常。
        // LruCache通過構造函數傳入緩存值，以KB為單位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用內存值的1/8作為緩存的大小。
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重寫此方法來衡量每張圖片的大小，默認返回圖片數量。
                return bitmap.getByteCount() / 1024;
            }
        };

    }



    @Override
    public int getCount() {
        return mCheckBridge.getCount();
    }

    @Override
    public CheckItem getItem(int location) {
        return mCheckBridge.getmDataSource().get(location);
    }

    @Override
    public long getItemId(int position) {
        if (mCheckBridge.getmDataSource() != null)
            return ((Object)mCheckBridge.getmDataSource().get(position)).hashCode();
        return 0;
    }

    /**
     * 帶入資料
     * @param checkBridge
     */
    public void setmDataSource(CheckBridge checkBridge)
    {
        this.mCheckBridge = checkBridge;
        this.notifyDataSetChanged();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Recycle ViewHolder's items
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row_check, parent, false);
            holder = new ViewHolder();
            holder.error_info = (LinearLayout) convertView.findViewById(R.id.error_info);
            holder.txt_check_name = (TextView) convertView.findViewById(R.id.txt_check_name);
            holder.txt_comment = (TextView) convertView.findViewById(R.id.txt_comment);
            holder.txt_abnornal = (TextView) convertView.findViewById(R.id.txt_abnornal);
            holder.txt_deal = (TextView) convertView.findViewById(R.id.txt_deal);
            holder.image_status = (ImageView) convertView.findViewById(R.id.image_status);
            holder.photo_thumbnail = (ImageView) convertView.findViewById(R.id.photo_thumbnail);
            holder.deal_info = (LinearLayout) convertView.findViewById(R.id.deal_info);
            holder.comment_info = (LinearLayout) convertView.findViewById(R.id.comment_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.position = position;
        // Retrieve the data holder
        final CheckItem dataHolder = mCheckBridge.getmDataSource().get(position);
        holder.txt_check_name.setText(dataHolder.getNAME());
        holder.txt_comment.setText(dataHolder.getResultComment());

        boolean isChecked = dataHolder.getIsChecked();
        boolean isFeel = dataHolder.getIsFeel();

        if(isFeel)
        {

            //感官
            //查看正常還是異常
            if(isChecked)
            {
                //已檢查過了
                if(dataHolder.getResultFeel())
                {
                    isErrorNow(holder,dataHolder,false);
                }
                else
                {
                    isErrorNow(holder,dataHolder,true);
                }

            }
            else
            {
                setToDefault(holder);
            }
        }
        else
        {
            if(isChecked)
            {
                //已檢查過了
                Float resultValue = dataHolder.getResultValue();
                if(resultValue!=null)
                {
                    //超過上限 OR 低於下限
                    if ((resultValue >= dataHolder.getHight_Val()) || (resultValue <= dataHolder.getLow_Val()))
                    {

                        isErrorNow(holder,dataHolder,true);

                    }
                    else
                    {
                        //正常範圍
                        isErrorNow(holder,dataHolder,false);

                    }
                }


            }
            else
            {
                setToDefault(holder);
            }
        }


        //備註欄位有資料才顯示
        if(!TextUtils.isEmpty(dataHolder.getResultComment()))
        {
            holder.comment_info.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.comment_info.setVisibility(View.GONE);
        }

        if(dataHolder.getPhotoItems().size()>0)
        {

            final String image_path = dataHolder.getPhotoItems().get(0);

            final Bitmap bitmap = getBitmapFromMemCache(image_path);
            if (bitmap != null ) {
                holder.photo_thumbnail.setImageBitmap(bitmap);
            } else {
                holder.photo_thumbnail.setImageResource(R.drawable.image_icon_bg);
                BitmapWorkerTask task = new BitmapWorkerTask(holder,position);
                task.execute(image_path);
            }
            holder.photo_thumbnail.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.photo_thumbnail.setVisibility(View.VISIBLE);

        }
        else
        {
            holder.photo_thumbnail.setVisibility(View.GONE);
        }


        return convertView;
    }

    public static class ViewHolder {

        public TextView txt_check_name;
        public TextView txt_comment;
        public TextView txt_abnornal;
        public TextView txt_deal;
        public LinearLayout error_info;
        public ImageView image_status;
        public ImageView photo_thumbnail;
        public LinearLayout deal_info;
        public LinearLayout comment_info;
        public LinearLayout photo_area;
        public int position;//記住位置使用

    }

    /**
     * 當有異常
     * @param isError True:有異常 False:正常
     */
    private void isErrorNow(ViewHolder holder,CheckItem dataHolder,boolean isError)
    {
        if(isError)
        {
            holder.image_status.setImageResource(R.drawable.ic_check_error);
            holder.error_info.setVisibility(View.VISIBLE);
            //顯示 異常與對策
            final String tempABID = dataHolder.getResultABID();
            final String tempDEALID = dataHolder.getResultDealID();
            if(!TextUtils.isEmpty(tempABID))
            {

                Predicate<AbnormalItem> findABID = new Predicate<AbnormalItem>() {
                    @Override public boolean apply(AbnormalItem abnormalItem) {
                        return (abnormalItem.getABID().equals(tempABID));
                    }
                };

                AbnormalItem currentABItem = Iterables.find(dataHolder.getAbnormalItems(),findABID,null);

                Predicate<DealMethodItem> findDEALID = new Predicate<DealMethodItem>() {
                    @Override public boolean apply(DealMethodItem dealMethodItem) {
                        return (dealMethodItem.getDEALID().equals(tempDEALID));
                    }
                };



                if(currentABItem != null)
                {
                    holder.txt_abnornal.setText(currentABItem.getABName());

                    DealMethodItem currentDealItem = Iterables.find(currentABItem.getDealMethodItems(),findDEALID,null);
                    if(currentDealItem != null) {
                        holder.txt_deal.setText(currentDealItem.getDEALNAME());
                    }
                }
            }
            else
            {
                holder.txt_abnornal.setText("");
            }


        }
        else
        {
            holder.image_status.setImageResource(R.drawable.ic_check_ok);
            holder.error_info.setVisibility(View.GONE);
        }
    }

    /**
     * 設成預設畫面
     * @param holder
     */
    private void setToDefault(ViewHolder holder)
    {
        //預設
        holder.image_status.setImageResource(R.drawable.ic_ok_gray);
        holder.error_info.setVisibility(View.GONE);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null ) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private ViewHolder m_viewHolder;
        private int m_position;
        public BitmapWorkerTask(ViewHolder viewHolder, int position) {
            this.m_viewHolder = viewHolder;
            this.m_position = position;
        }

        // 在後台加載圖片。
        @Override
        protected Bitmap doInBackground(String... params) {

            String image_path = params[0];
            LOGD(TAG,"image_path:"+image_path);
            Bitmap bitmap = ImageUtils.decodeFile(image_path, 80, 80);
            try {
                ExifInterface exif = new ExifInterface(image_path);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                if(orientation==6)
                {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                }



            } catch (IOException e) {
                e.printStackTrace();
            }


            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (m_viewHolder.position == m_position) {
                m_viewHolder.photo_thumbnail.setImageBitmap(bitmap);
            }
        }
    }

}
