/*
 KeywordDialogAcitivty.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.setting;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * キーワード表示用Activity.
 * @author NTT DOCOMO, INC.
 */
public class KeywordDialogAcitivty extends FragmentActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeywordDialogFragment fragment = new KeywordDialogFragment();
        fragment.show(getSupportFragmentManager(), "keyword_dialog");
    }
}
