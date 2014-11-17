/*
 AccessTokenListActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.localoauth.activity;

import org.deviceconnect.android.R;
import org.deviceconnect.android.localoauth.fragment.AccessTokenListFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * アクセストークン一覧Activity.
 * @author NTT DOCOMO, INC.
 */
public class AccessTokenListActivity extends Activity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.access_token);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            AccessTokenListFragment f = new AccessTokenListFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            t.add(android.R.id.content, f, "continar");
            t.commit();
        }
    }
}
