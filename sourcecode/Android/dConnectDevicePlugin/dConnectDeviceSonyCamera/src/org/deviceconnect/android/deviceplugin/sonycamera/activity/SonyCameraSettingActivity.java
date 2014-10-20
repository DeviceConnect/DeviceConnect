/*
SonyCameraSettingActivity
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.sonycamera.activity;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import org.deviceconnect.android.ui.activity.DConnectSettingPageFragmentActivity;

/**
 * Sony Cameraデバイスプラグイン設定画面用Activity.
 * @author NTT DOCOMO, INC.
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
            fragments.add(new SonyCameraPreparationFragment());
            fragments.add(new SonyCameraTurnOnFragment());
            fragments.add(new SonyCameraConnectingFragment());
        }

        SonyCameraBaseFragment fragment = fragments.get(position);
        return fragment;
    }
}
