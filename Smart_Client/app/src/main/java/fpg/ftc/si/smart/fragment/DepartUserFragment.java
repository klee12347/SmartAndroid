/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import fpg.ftc.si.smart.AppController;
import fpg.ftc.si.smart.DepartViewActivity;
import fpg.ftc.si.smart.R;
import fpg.ftc.si.smart.adapter.UserAdapter;
import fpg.ftc.si.smart.async.DownloadFileFromURL;
import fpg.ftc.si.smart.fragment.dialog.ConfirmDialogFragment;
import fpg.ftc.si.smart.model.Json_User;
import fpg.ftc.si.smart.util.consts.FragmentTagConstants;
import fpg.ftc.si.smart.util.consts.RequestTagConstants;

import static fpg.ftc.si.smart.util.LogUtils.LOGD;
import static fpg.ftc.si.smart.util.LogUtils.LOGE;
import static fpg.ftc.si.smart.util.LogUtils.LOGI;
import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DepartUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DepartUserFragment#} factory method to
 * create an instance of this fragment.
 *
 */
public class DepartUserFragment extends Fragment {

    private static final String TAG = makeLogTag(DepartUserFragment.class);
    private DepartViewActivity mActivity;
    private View mRootView;
    private UserAdapter mAdapter;
    private ListView mUserListView;
    private Button mBtnDownload;
    private OnFragmentInteractionListener mListener;
    private EditText mUserSearch;
    public DepartUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LOGI(TAG,"onCreateView");
        // Inflate the layout for this fragment
        mActivity = (DepartViewActivity) getActivity();

        mAdapter = new UserAdapter(mActivity);
        mRootView =inflater.inflate(R.layout.fragment_group_user, container, false);
        mBtnDownload = (Button) mRootView.findViewById(R.id.btn_download);
        mUserListView  = (ListView) mRootView.findViewById(R.id.user_list);
        mUserListView.setAdapter(mAdapter);
        mUserSearch = (EditText) mRootView.findViewById(R.id.txt_user_search);
        mBtnDownload.setEnabled(false);
        mUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserAdapter.ViewHolder holder = (UserAdapter.ViewHolder) view.getTag();
                holder.cbUser.toggle();
                String account = holder.txtAccount.getText().toString();
                mAdapter.setItemSelected(account, holder.cbUser.isChecked());
                if(mAdapter.getmSelectedCount()>0)
                {
                    mBtnDownload.setEnabled(true);
                    mBtnDownload.setText(String.format(getString(R.string.format_download_item_choosed), mAdapter.getmSelectedCount()));

                }
                else
                {
                    mBtnDownload.setEnabled(false);
                    mBtnDownload.setText(getString(R.string.lb_download_default));
                }
            }
        });

        // Action
        // 下載
        mBtnDownload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ConfirmDialogFragment confirmDialog = ConfirmDialogFragment.newInstance(getString(R.string.system_warn_to_download_info));
                confirmDialog.addListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 將使用者選的資料列,串接成參數後,使用非同步的Task進行下載
                        String download_url = mActivity.Preferences.getDownloadUrl() + "?" + mAdapter.getSelectedParam();
                        LOGD(TAG,"download_url:" + download_url);
                        new DownloadFileFromURL(mActivity).execute(download_url);
                    }
                });
                confirmDialog.show(mActivity.getSupportFragmentManager(), FragmentTagConstants.DEPART_USER_FRAGMENT);

            }
        });

        // 過濾篩選條件
        mUserSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mUserSearch.getText().toString().toLowerCase(Locale.getDefault());
                mAdapter.filter(text);
            }
        });

        return mRootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
        LOGI(TAG,"onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        LOGI(TAG,"onDetach");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void RefreshData()
    {
        LOGE(TAG, "RefreshData:" + mActivity.CurrentSelected);
        mAdapter.clear();
        getUserRequest();
        mBtnDownload.setEnabled(false);
        mBtnDownload.setText(getString(R.string.lb_download_default));
    }



    /**
     * 刷新使用者資料
     */
    private void getUserRequest()
    {

        String url = mActivity.Preferences.getUserUrl()+"?DEPID="+ mActivity.CurrentSelected;
        LOGD(TAG,"url:" + url);
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        Json_User[] json_users = gson.fromJson(response.toString(), Json_User[].class);
                        mAdapter.setItemList(new ArrayList(Arrays.asList(json_users)));

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(mActivity, "與Server端連線有問題", Toast.LENGTH_LONG).show();
                error.printStackTrace();
                LOGE(TAG,"onErrorResponse,"+error.toString());

            }
        });

        AppController.getInstance().addToRequestQueue(jsArrayRequest, RequestTagConstants.DEPARTHASH_REQUEST);
    }

}
