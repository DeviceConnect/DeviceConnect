package com.nttdocomo.android.dconnect.uiapp.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.preference.Preference;

import com.nttdocomo.android.dconnect.client.fragment.AbstractSettingsFragment;
import com.nttdocomo.android.dconnect.client.fragment.OpenSourceLicenseFragment;
import com.nttdocomo.android.dconnect.client.fragment.OpenSourceLicenseFragment.OpenSourceSoftware;
import com.nttdocomo.android.dconnect.uiapp.R;

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
