/*
 * Copyright (c) 2014 FTC Inc. All rights reserved.
 */
package fpg.ftc.si.smart.fragment.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import fpg.ftc.si.smart.R;


/**
 * ref:
 * 1.http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog
 * 2.https://code.google.com/p/sifeup-mobile/source/browse/SiFEUPMobile/src/pt/up/beta/mobile/ui/dialogs/ProgressDialogFragment.java?r=b6ed776d1392f4e48b13969bcccef56d0962eca9
 * 3.https://gist.github.com/daichan4649/6421407
 * Created by MarlinJoe on 2014/5/6.
 */
public class ProgressDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "TITLE";
    private static final String ARG_MESSAGE = "MESSAGE";
    private static final String ARG_CANCELABLE = "CANCELABLE";

    private ProgressDialog mProgressDialog;

    public static ProgressDialogFragment newInstance(String message,boolean cancelable) {

        return newInstance("",message,cancelable);
    }

    public static ProgressDialogFragment newInstance(String title,String message,boolean cancelable) {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putBoolean(ARG_CANCELABLE, cancelable);
        frag.setArguments(args);
        return frag;
    }

    public interface ProgressDialogFragmentListener {
        void onProgressCancelled();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getArguments().getString(ARG_TITLE);
        final String message = getArguments().getString(ARG_MESSAGE);
        final boolean cancelable = getArguments().getBoolean(ARG_CANCELABLE);

        if(title.isEmpty())
        {
            title = getString(R.string.system_dialog_title);
        }

        // ProgressDialog

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(false);

        setCancelable(false);

        // 進度條
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);


        return mProgressDialog;

    }

//    //TODO not work 忘記效果
//    @Override
//    public void onDestroyView() {
//        if (getDialog() != null && getRetainInstance())
//            getDialog().setDismissMessage(null);
//        super.onDestroyView();
//    }

    public void updateProgress(int value) {
        ProgressDialog dialog = (ProgressDialog) getDialog();
        if (dialog != null) {
            dialog.setProgress(value);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (getProgressDialogFragmentListener() != null) {
            getProgressDialogFragmentListener().onProgressCancelled();
        }
    }

    private ProgressDialogFragmentListener getProgressDialogFragmentListener() {
        if (getActivity() == null) {
            return null;
        }

        if (getActivity() instanceof ProgressDialogFragmentListener) {
            return (ProgressDialogFragmentListener) getActivity();
        }
        return null;
    }

}
