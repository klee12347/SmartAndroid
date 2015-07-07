/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.model.VerItem;
import fpg.ftc.si.smart.util.PreferenceUtils;



import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * ref:http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog
 * Created by MarlinJoe on 2014/5/6.
 */
public class DialogVersionUpdateFragment extends DialogFragment {

    private static final String TAG = makeLogTag(DialogVersionUpdateFragment.class);
    private static final String ARG_VERITEM = "ARG_VERITEM";
    private static final String ARG_CURRENT_VER_NAME = "ARG_CURRENT_VER_NAME";


    private DialogInterface.OnClickListener mListener;
    private ProgressDialog pBar;
    private Handler mHandler = new Handler() ;
    private String mFilePath = "";
    private String mApkName = "";
    private Context mContext;
    private PreferenceUtils mPreferenceUtils;

    public static DialogVersionUpdateFragment newInstance(VerItem verItem,String current_ver_name) {
        DialogVersionUpdateFragment frag = new DialogVersionUpdateFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VERITEM, verItem);
        args.putString(ARG_CURRENT_VER_NAME, current_ver_name);
        frag.setArguments(args);
        return frag;
    }

    public void addListener(DialogInterface.OnClickListener listener){
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View custom_view = inflater.inflate(R.layout.dialog_version_update, null);
        final VerItem verItem = (VerItem)getArguments().getSerializable(ARG_VERITEM);
        final String current_ver_name = getArguments().getString(ARG_CURRENT_VER_NAME);
        mApkName = verItem.getApkName();
        mContext = getActivity();
        mPreferenceUtils = new PreferenceUtils(mContext);
        mFilePath = mPreferenceUtils.getTempPath();//設置APK存放位置

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(custom_view);
        builder.setIcon(R.drawable.ic_action_warning);
        builder.setTitle(getResources().getString(R.string.system_dialog_title_update_app));
        TextView release_note = (TextView) custom_view.findViewById(R.id.release_note);
        release_note.setText(verItem.getReleaseNote());

        TextView top_info = (TextView) custom_view.findViewById(R.id.top_info);
        String top_info_result = String.format(getString(R.string.lb_update_version_info_template), current_ver_name, verItem.getVerName());
        top_info.setText(top_info_result);

        final AlertDialog dialog = builder.create();

        Button btnOK = (Button) custom_view.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pBar = new ProgressDialog(mContext);
                pBar.setTitle(getString(R.string.system_dialog_title_update));
                pBar.setMessage(getString(R.string.system_update_now));
                pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                String apk_download_url = mPreferenceUtils.getApkUrl() + File.separator + verItem.getApkName();
                downFile(apk_download_url);
                dialog.dismiss();
            }
        });
        Button btnCancel = (Button) custom_view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        //Create custom dialog
        if (dialog == null)
            super.setShowsDialog (false);
        return dialog;



    }

    void downFile(final String url) {
        pBar.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                LOGD(TAG,"檔案下載路徑:"+url);

                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    LOGD(TAG,"檔案大小:"+ String.valueOf(length));

                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {

                        File file = new File(mFilePath,mApkName);
                        fileOutputStream = new FileOutputStream(file);

                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                            }
                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();

    }

    /**
     * 進行更新
     */
    void down() {
        mHandler.post(new Runnable() {
            public void run() {
                pBar.cancel();
                update();
            }
        });

    }

    /**
     *
     */
    void update() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        File apk_file = new File(mFilePath,mApkName);
        intent.setDataAndType(Uri.fromFile(apk_file),"application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

}
