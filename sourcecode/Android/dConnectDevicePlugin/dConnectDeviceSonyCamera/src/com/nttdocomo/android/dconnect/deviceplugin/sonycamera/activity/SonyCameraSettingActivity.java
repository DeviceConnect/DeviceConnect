package com.nttdocomo.android.dconnect.deviceplugin.sonycamera.activity;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.nttdocomo.android.dconnect.ui.activity.DConnectSettingPageFragmentActivity;

/**
SonyCameraSettingActivity
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/
public class SonyCameraSettingActivity extends DConnectSettingPageFragmentActivity {
    /** QX10のデバイスID. */
    private String mDeviceId;
    /** 全Fragmentページ数. */
    private static final int PAGE_COUNTER = 3;

    /** フラグメント一覧. */
    private List<SonyCameraBaseFragment> fragments = new ArrayList<SonyCameraBaseFragment>();

    @Override
    protected void onResume() {
        super.onResume();

        ViewPager vp = getViewPager();
        vp.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(final int state) {
            }

            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(final int position) {
                SonyCameraBaseFragment fragment = fragments.get(position);
                if (fragment != null) {
                    fragment.onShowFragment();
                }
            }
        });
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
        return PAGE_COUNTER;
    }

    @Override
    public Fragment createPage(final int position) {
        if (fragments.size() == 0) {
            fragments.add(new SonyCameraIndexFragment());
            fragments.add(new SonyCameraTurnOnFragment());
            fragments.add(new SonyCameraSettingFragment());
        }

        SonyCameraBaseFragment fragment = fragments.get(position);
        return fragment;
    }
}
