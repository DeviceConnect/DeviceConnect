/*
 WearSettingActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear.setting;

import org.deviceconnect.android.ui.activity.DConnectSettingPageFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 設定用Activity.
 * 
 * @author NTT DOCOMO, INC.
 */
public class WearSettingActivity extends DConnectSettingPageFragmentActivity {

    /** デバイスID. */
    private String mDeviceId;

    /** ページ数. */
    private static final int PAGE_COUNT = 1;

    @Override
    public Fragment createPage(final int position) {
        Bundle mBundle = new Bundle();
        mBundle.putInt("position", position);
        WearSettingFragment mFragment = new WearSettingFragment();
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    /**
     * デバイスIDを取得する.
     * 
     * @return デバイスID
     */
    public String getDeviceId() {
        return mDeviceId;
    }

    /**
     * デバイスIDを設定する.
     * 
     * @param deviceId デバイスID
     */
    public void setDeviceId(final String deviceId) {
        mDeviceId = deviceId;
    }

    @Override
    public int getPageCount() {
        return PAGE_COUNT;
    }
}
