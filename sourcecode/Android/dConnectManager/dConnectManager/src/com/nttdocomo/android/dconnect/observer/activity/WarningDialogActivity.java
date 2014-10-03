/*
 WarningDialogActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.observer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.nttdocomo.android.dconnect.observer.fragment.WarningDialogFragment;

/**
 * 警告用ダイアログアクティビティ.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public class WarningDialogActivity extends FragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WarningDialogFragment fragment = new WarningDialogFragment();
        fragment.show(getSupportFragmentManager(), "warning_dialog");
    }

}
