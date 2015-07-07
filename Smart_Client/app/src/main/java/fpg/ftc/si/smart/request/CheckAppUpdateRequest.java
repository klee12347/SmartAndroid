/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.request;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import fpg.ftc.si.smart.AppController;
import fpg.ftc.si.smart.fragment.dialog.DialogVersionUpdateFragment;
import fpg.ftc.si.smart.model.VerItem;
import fpg.ftc.si.smart.util.DeviceUtils;
import fpg.ftc.si.smart.util.PreferenceUtils;
import fpg.ftc.si.smart.util.consts.RequestTagConstants;

import static fpg.ftc.si.smart.util.LogUtils.LOGE;
import static fpg.ftc.si.smart.util.LogUtils.LOGI;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * Created by MarlinJoe on 2014/10/21.
 */
public class CheckAppUpdateRequest {

    private static final String TAG = makeLogTag(CheckAppUpdateRequest.class);

    private Context mContext;
    private PreferenceUtils mPreferenceUtils;

    public CheckAppUpdateRequest(Context context) {

        this.mContext = context;
        this.mPreferenceUtils = new PreferenceUtils(context);
    }


    public void getRequest()
    {
        final String url = mPreferenceUtils.getAppUpdateUrl();
        LOGI(TAG,"Server Check Url:" + url);
        final Gson gson = new Gson();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try
                        {
                            VerItem verItem = gson.fromJson(response.toString(),VerItem.class);
                            String current_app_ver_name = DeviceUtils.getVerName(mContext);
                            //比對APP版本與伺服器版本
                            int server_ver_code = verItem.getVerCode();
                            int app_ver_code =  DeviceUtils.getVerCode(mContext);//APP版本
                            if (server_ver_code > app_ver_code) {
                                //提示應該要更新了
                                DialogVersionUpdateFragment dialogFragment = DialogVersionUpdateFragment.newInstance(verItem,current_app_ver_name);
                                dialogFragment.show(((Activity) mContext).getFragmentManager(), "dialog");
                            }


                        } catch (Exception e) {

                            LOGE(TAG,"發生錯誤:"+e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                LOGE(TAG,"與伺服器連線發生錯誤:"+ error.getMessage());

            }
        });


        AppController.getInstance().addToRequestQueue(jsObjRequest, RequestTagConstants.CHECK_APP_UPDATE_REQUEST);

    }

}
