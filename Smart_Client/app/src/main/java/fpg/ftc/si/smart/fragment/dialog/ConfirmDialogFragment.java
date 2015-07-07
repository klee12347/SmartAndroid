/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */

package fpg.ftc.si.smart.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import fpg.ftc.si.smart.R;


/**
 * ref:http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog
 * Created by MarlinJoe on 2014/5/6.
 */
public class ConfirmDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener mListener;//確認


    public static ConfirmDialogFragment newInstance(String message) {
        ConfirmDialogFragment frag = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    public void addListener(DialogInterface.OnClickListener listener){
        mListener = listener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(R.drawable.ic_action_warning);
        alertDialogBuilder.setTitle(getResources().getString(R.string.system_dialog_title));
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.action_ok,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mListener!=null) {
                    mListener.onClick(dialog, which);
                }
                dialog.dismiss();
            }
        });
        setCancelable(false);
        alertDialogBuilder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();



    }

    //TODO not work
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }




}
