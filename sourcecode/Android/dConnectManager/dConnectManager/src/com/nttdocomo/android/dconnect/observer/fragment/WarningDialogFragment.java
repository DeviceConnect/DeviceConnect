/*
 WarningDialogFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.observer.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.nttdocomo.android.dconnect.R;
import com.nttdocomo.android.dconnect.observer.DConnectObservationService;
import com.nttdocomo.android.dconnect.observer.receiver.ObserverReceiver;

/**
 * 警告ダイアログフラグメント.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public class WarningDialogFragment extends DialogFragment {
    
    /** 
     * 再監視停止フラグ.
     */
    private boolean mDisableFlg;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.activity_warning_dialog, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Dialog dialog = builder.setTitle(getString(R.string.activity_warning))
                .setMessage(getString(R.string.activity_warning_mess))
                .setView(view)
                .setPositiveButton(R.string.activity_warning_ok, new OnClickListener() {
                    
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        CheckBox box = (CheckBox) view.findViewById(R.id.disable_observer);
                        mDisableFlg = box.isChecked();
                        dismiss();
                    }
                }).create();
        
        dialog.setCanceledOnTouchOutside(false);
        
        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        
        if (!mDisableFlg) {
            Intent i = new Intent();
            i.setAction(DConnectObservationService.ACTION_START);
            i.setClass(getActivity(), ObserverReceiver.class);
            getActivity().sendBroadcast(i);
                        
        }
        getActivity().finish();
    }
}
