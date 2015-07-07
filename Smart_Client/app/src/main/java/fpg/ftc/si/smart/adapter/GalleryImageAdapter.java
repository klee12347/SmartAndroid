/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.util.ImageUtils;

/**
 * Created by MarlinJoe on 2014/9/22.
 */
public class GalleryImageAdapter extends BaseAdapter {

    private Context mContext;

    private List<String> mDataSource;

    private HashMap<String,Boolean> mSelectedMap;//選取項目 account
    private int mSelectedCount;//選取數

    public GalleryImageAdapter(Context context)
    {
        mContext = context;
        mDataSource = new ArrayList<String>();
        mSelectedMap = new HashMap<String, Boolean>();
    }

    public int getCount() {
        return mDataSource.size();
    }

    public String getItem(int position) {
        return mDataSource.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    // Override this method according to your need
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }

        final String dataHolder = mDataSource.get(position);
        // get screen dimensions
        Bitmap image = ImageUtils.decodeFile(dataHolder, 200,
                200);
        try {
            ExifInterface exif = new ExifInterface(dataHolder);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            if(orientation==6)
            {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }



        imageView.setLayoutParams(new Gallery.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(image);



//        if(mSelectedMap.get(position))
//        {
//            imageView.setBackgroundResource(R.drawable.image_background);
//        }
//        else
//        {
//            imageView.setBackground(null);
//        }


        return imageView;
    }

    public void setItemList(List<String> itemList) {

        this.mDataSource = itemList;
        this.notifyDataSetChanged();

    }

    public void addItemList(String itemList) {

        this.mDataSource.add(itemList);
        this.notifyDataSetChanged();

    }

    public void deleteSelectedItem(int position)
    {
        this.mDataSource.remove(position);
        this.notifyDataSetChanged();
    }


}
