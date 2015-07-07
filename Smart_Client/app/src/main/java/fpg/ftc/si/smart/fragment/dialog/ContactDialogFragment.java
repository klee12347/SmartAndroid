/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fpg.ftc.si.smart.R;

import static fpg.ftc.si.smart.util.LogUtils.makeLogTag;


/**
 * ref:http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog
 * Created by MarlinJoe on 2014/5/6.
 */
public class ContactDialogFragment extends DialogFragment {

    private static final String TAG = makeLogTag(ContactDialogFragment.class);
    private DialogInterface.OnClickListener mListener;
    private Context mContext;

    public static ContactDialogFragment newInstance() {
        ContactDialogFragment frag = new ContactDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public void addListener(DialogInterface.OnClickListener listener){
        mListener = listener;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View custom_view = inflater.inflate(R.layout.dialog_contact, null);
        mContext = getActivity();


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(custom_view);
        builder.setTitle(getResources().getString(R.string.system_dialog_title));

        final AlertDialog dialog = builder.create();


        setCancelable(false);

        TextView id_tel = (TextView) custom_view.findViewById(R.id.id_tel);
        id_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String offical_tel = getString(R.string.lb_offical_tel);
                Intent dial_intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ offical_tel));
                startActivity(dial_intent);
            }
        });

        TextView lb_product_url = (TextView) custom_view.findViewById(R.id.id_product_url);
        lb_product_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String official_url = getString(R.string.lb_official_url);
                Intent url_intent = new Intent(Intent.ACTION_VIEW);
                url_intent.setData(Uri.parse(official_url));
                startActivity(url_intent);
            }
        });


        Button btnOK = (Button) custom_view.findViewById(R.id.btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {

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

    //TODO not work
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }



}
