/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.async;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import fpg.ftc.si.smart.DepartViewActivity;
import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.fragment.dialog.AlertDialogFragment;
import fpg.ftc.si.smart.fragment.dialog.ProgressDialogFragment;
import fpg.ftc.si.smart.model.enumtype.DownloadStatus;
import fpg.ftc.si.smart.util.FileUtils;
import fpg.ftc.si.smart.util.FileZipUtils;
import fpg.ftc.si.smart.util.consts.FragmentTagConstants;
import fpg.ftc.si.smart.util.consts.SystemConstants;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.LOGE;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * 非同步 從遠端主機下載資料
 * 相依 DepartViewActivity DepartUserFragment
 * Created by MarlinJoe on 2014/10/17.
 */
public class DownloadFileFromURL extends AsyncTask<String, Void, DownloadStatus> {

    private static final String TAG = makeLogTag(DownloadFileFromURL.class);
    private DepartViewActivity mContext;
    private ProgressDialogFragment mDialog;
    private String mErrorMessage = "";
    public DownloadFileFromURL(FragmentActivity mContext) {
        this.mContext = (DepartViewActivity) mContext;
        this.mDialog = ProgressDialogFragment.newInstance("下載中...", true);

    }

    /**
     * 顯示進度條
     */
    private void showProgress() {
        mDialog.show(mContext.getSupportFragmentManager(), FragmentTagConstants.DEPART_USER_FRAGMENT);
    }

    @Override
    protected void onPreExecute() {
        showProgress();
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected DownloadStatus doInBackground(String... download_url) {
        int count;
        DownloadStatus downloadStatus = DownloadStatus.Fail;

        try {
            URL url = new URL(download_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            //檔案總長度
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),8192);
            String file_folder = mContext.Preferences.getFilePath() ;//APP存放目錄
            String temp_folder = mContext.Preferences.getTempPath() ;//暫存目錄
            String zip_file_save_path = file_folder + File.separator + SystemConstants.DOWNLOAD_FILE_NAME;//zip檔案存放位置
            String temp_file_path =  temp_folder + File.separator + SystemConstants.SQLITE_SMART;//暫存db完整路徑
            LOGD(TAG,"save_full_path:" + zip_file_save_path);
            // Output stream
            OutputStream output = new FileOutputStream(zip_file_save_path);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                int percent = (int) ((total * 100) / lenghtOfFile);
                mDialog.updateProgress(percent);
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // 解完壓縮後 在暫存會有一個DB
            // 將原本實際運行的 抄表資料 copy 到 暫存的DB
            // 完成後進行切換 將暫存切換成正式的

            // 進行解壓縮

            FileZipUtils.unzip(zip_file_save_path, temp_folder + File.separator ,"");
            File temp_file = new File(temp_file_path);
            if(temp_file.exists())
            {
//                mContext.SmartDBHelper.cloneRecordToTemp(temp_file_path);
                //TODO COPY 暫存
                String target_file = file_folder + File.separator + SystemConstants.SQLITE_SMART;
                FileUtils.moveFile(temp_file,new File(target_file));
            }
            else
            {
                throw new Exception("解壓縮失敗,因找不到解完後的檔案");
            }


            // closing streams
            output.close();
            input.close();

            downloadStatus = DownloadStatus.Success;
        } catch (Exception ex) {
            LOGE(TAG, "DownloadFileFromURL 下載發生錯誤:" + ex.getMessage());
        }

        return downloadStatus;
    }



    @Override
    protected void onPostExecute(DownloadStatus status) {
        // dismiss the dialog after the file was downloaded
          if(status == DownloadStatus.Success)
          {
              AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(mContext.getString(R.string.system_download_finish));
              alertDialogFragment.show(mContext.getSupportFragmentManager(),FragmentTagConstants.DEPART_USER_FRAGMENT);

              mContext.finish();
          }
          else
          {
              AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(mContext.getString(R.string.error_download_crash));
              alertDialogFragment.show(mContext.getSupportFragmentManager(),FragmentTagConstants.DEPART_USER_FRAGMENT);
          }

        LOGD(TAG,mErrorMessage);
        mDialog.dismiss();
    }



}
