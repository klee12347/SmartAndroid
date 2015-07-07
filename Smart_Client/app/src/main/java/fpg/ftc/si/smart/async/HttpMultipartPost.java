/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.smart.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;

import fpg.ftc.si.smart.util.FileUtils;
import fpg.ftc.si.smart.util.FileZipUtils;
import fpg.ftc.si.smart.util.PreferenceUtils;
import fpg.ftc.si.smart.util.consts.SystemConstants;

/**
 * 上傳檔案使用
 * http://blog.csdn.net/u010142437/article/details/14639651
 * Created by MarlinJoe on 2014/5/8.
 */
//public class HttpMultipartPost extends AsyncTask<String, Integer, String> {
//
//    private static final String TAG = makeLogTag(HttpMultipartPost.class);
//    private ExecuteActivity mContext;
//    private String mFilePath;
//    private String mRequestUrl;
//    private ProgressDialog mDialog;
//    private long mTotalSize;
//
//
//    public HttpMultipartPost(ExecuteActivity context, String filePath, String requestUrl) {
//        this.mContext = context;
//        this.mFilePath = filePath;
//        this.mRequestUrl = requestUrl;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        mDialog = new ProgressDialog(mContext);
//        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mDialog.setMessage("檔案上傳中...");
//        mDialog.setCancelable(false);
//        mDialog.show();
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//        String serverResponse = null;
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpContext httpContext = new BasicHttpContext();
//        HttpPost httpPost = new HttpPost(mRequestUrl);
//        try {
//            CustomMultipartEntity multipartContent = new CustomMultipartEntity(
//                    new CustomMultipartEntity.ProgressListener() {
//                        @Override
//                        public void transferred(long num) {
//                            publishProgress((int) ((num / (float) mTotalSize) * 100));
//                        }
//                    }
//            );
//            //
//            multipartContent.addPart("value", new FileBody(new File(mFilePath)));
//            mTotalSize = multipartContent.getContentLength();
//
//            httpPost.setEntity(multipartContent);
//            HttpResponse response = httpClient.execute(httpPost, httpContext);
//            serverResponse = EntityUtils.toString(response.getEntity());
//
//            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
//            {
//                serverResponse = "";
//            }else
//            {
//                //伺服器回傳失敗內容
//                serverResponse = EntityUtils.toString(response.getEntity());
//                LOGE(TAG,"伺服器發生錯誤:" + serverResponse);
//            }
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            LOGE(TAG,"發生錯誤:"+e.getMessage());
//            serverResponse = "與伺服器連接失敗。";
//        }
//        return serverResponse;
//    }
//
//    @Override
//    protected void onProgressUpdate(Integer... progress) {
//        mDialog.setProgress((int) (progress[0]));
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        LOGD(TAG,"上傳結果:"+result);
//        if(TextUtils.isEmpty(result))
//        {
//            Toast.makeText(mContext, "上傳成功", Toast.LENGTH_LONG).show();
//
////            mContext.refreshStep2();
//
//        }else
//        {
//            Toast.makeText(mContext, "上傳失敗",Toast.LENGTH_LONG).show();
//
//        }
//
//        mDialog.dismiss();
//    }
//
//    @Override
//    protected void onCancelled() {
//        LOGD(TAG,"上傳已被取消");
//    }
//}
    //Mo
public class HttpMultipartPost extends AsyncTask<String, Integer, String> {//Modified by cd 2015/06/27

    private static final String TAG = "HttpMultipartPost";
    private Context mContext;
    private String mFilePath;
    private String mRequestUrl;
    private  ProgressDialog mDialog;
    private long mTotalSize;
    private PreferenceUtils mpre;
//    private String marryfileName[];
//    private  String strmsg="";
//    private long myTime;
    //TODO:Upload
    //1.move smart.db to PIC
    //2.zip
    //3.upload
    //4.sucess ->unzip ./Smart.zip And delete Temp
    //   fail  ->move smart.db to origin

    public HttpMultipartPost(Context mContext, String mFilePath, String mRequestUrl) {
        this.mContext = mContext;
        this.mFilePath = mFilePath;
        this.mRequestUrl = mRequestUrl;
    }

//    public HttpMultipartPost(Context mContext, String mFilePath, String mRequestUrl,PreferenceUtils pre) {
//        this.mContext = mContext;
//        this.mFilePath = mFilePath;
//        this.mRequestUrl = mRequestUrl;
//        this.mpre = pre;
//    }
    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(mContext);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setMessage("档案上传中...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        String serverResponse = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(mRequestUrl);
        mpre = new PreferenceUtils(mContext);
        String file_folder_path = mpre.getFilePath() ;//APP存放目錄
       String pic_folder_path = mpre.getPicPath();//图片目录
       String db_in_pic_path = pic_folder_path + File.separator + SystemConstants.SQLITE_SMART;// Smart/pic/smart.db
       String db_origin_path = file_folder_path + File.separator + SystemConstants.SQLITE_SMART;//待移动到Temp的db
      String upload_zip_path = file_folder_path + File.separator + SystemConstants.UPLOAD_FILE_NAME;
      String zip_file_save_path = file_folder_path + File.separator + SystemConstants.DOWNLOAD_FILE_NAME;//zip檔案存放位置
      String temp_path = mpre.getTempPath();
        try {
            FileUtils.moveFile(new File(db_origin_path), new File(db_in_pic_path));//移动db档到Pic路径一起压缩上传
            FileZipUtils.zip(pic_folder_path + File.separator, upload_zip_path);

            CustomMultipartEntity multipartContent = new CustomMultipartEntity(
                    new CustomMultipartEntity.ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) mTotalSize) * 100));
                        }
                    }
            );
            //
            multipartContent.addPart("value", new FileBody(new File(mFilePath)));
            mTotalSize = multipartContent.getContentLength();
            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            serverResponse = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                //  serverResponse = "";
//上传 sucess：删除pic&temp&upload.zip目录，解压原来zip档
                File filepic = new File(pic_folder_path);
                File filetemp = new File(temp_path);
                File fileupload = new File(upload_zip_path);
               if (filepic.isDirectory())
                {
                    FileUtils.deleteFile(filepic);
                }
                if (filetemp.isDirectory()) {
                    FileUtils.deleteFile(filetemp);
                }
                   FileUtils.deleteFile(fileupload);

   //删除后解压原来的压缩档?

//                FileZipUtils.unzip(zip_file_save_path, temp_path + File.separator ,"");
//                File temp_file = new File(temp_path);
//                if(temp_file.exists())
//                {
//                    //TODO COPY 暫存
//                    String target_file = file_folder_path + File.separator + SystemConstants.SQLITE_SMART;
//                    FileUtils.moveFile(temp_file,new File(target_file));
//                }
//                else
//                {
//                    throw new Exception("解壓縮失敗,因找不到解完後的檔案");
//                }


            } else {
                //伺服器回傳失敗內容
                FileUtils.moveFile(new File(db_in_pic_path),new File(db_origin_path) );//上传失败就返回去db档
                serverResponse = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "伺服器發生錯誤:" + serverResponse);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "發生錯誤:" + e.getMessage());
            serverResponse = e.getMessage();
        }
        return serverResponse;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mDialog.setProgress((int) (progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG,"上傳結果:"+result);
        Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        mDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        Log.d(TAG, "上傳已被取消");
    }
}
