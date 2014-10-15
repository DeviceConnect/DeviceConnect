/*
 WarningDialogActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.observer.activity;

import org.deviceconnect.android.observer.fragment.WarningDialogFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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
