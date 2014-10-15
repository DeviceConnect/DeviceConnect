/*
 SettingsFragment.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.uiapp.fragment;

import java.util.ArrayList;

import org.deviceconnect.android.client.fragment.AbstractSettingsFragment;
import org.deviceconnect.android.client.fragment.OpenSourceLicenseFragment;
import org.deviceconnect.android.client.fragment.OpenSourceLicenseFragment.OpenSourceSoftware;
import org.deviceconnect.android.uiapp.R;

import android.os.Bundle;
import android.preference.Preference;

/**
 * 設定画面フラグメント.
 */
public class SettingsFragment extends AbstractSettingsFragment
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * オープンソースソフトウェア.
     */
    private OpenSourceLicenseFragment.OpenSourceSoftware[] mOssSoftware =
            new OpenSourceLicenseFragment.OpenSourceSoftware[] {
                    OpenSourceLicenseFragment.createOpenSourceSoftware(
                            "android-support-v4.jar", R.raw.andorid_support_v4),
                    OpenSourceLicenseFragment.createOpenSourceSoftware(
                            "apache-mime4j-0.6.jar", R.raw.apache_mime4j),
                    OpenSourceLicenseFragment.createOpenSourceSoftware(
                            "android-support-v4-preferencefragment",
                            R.raw.android_support_v4_preferencefragment),
                    OpenSourceLicenseFragment.createOpenSourceSoftware(
                            "Java WebSocket", R.raw.java_websocket),
            };

    @Override
    protected ArrayList<OpenSourceSoftware> getOpenSourceSoftware() {

        ArrayList<OpenSourceSoftware> software = new ArrayList<OpenSourceSoftware>();
        for (OpenSourceSoftware soft: mOssSoftware) {
            software.add(soft);
        }

        return software;
    }
}
