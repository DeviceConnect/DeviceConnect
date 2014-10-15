/*
 HostSettingActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host.setting;

import java.util.ArrayList;
import java.util.List;

import org.deviceconnect.android.ui.activity.DConnectSettingPageFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 * 設定用Activity.
 * @author NTT DOCOMO, INC.
 */
public class HostSettingActivity extends DConnectSettingPageFragmentActivity {
    
    /** デバイスID. */
    private String mDeviceId;

    /** フラグメント一覧. */
    private List<HostSettingFragment> fragments = new ArrayList<HostSettingFragment>();

    /** ページ数. */
    private static final int PAGE_COUNT = 1;
    
    @Override
    public Fragment createPage(final int position) {
        Bundle mBundle = new Bundle();
        mBundle.putInt("position", position);
        HostSettingFragment mFragment = new HostSettingFragment();
        mFragment.setArguments(mBundle);

        return mFragment;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 自分の名前
        // SystemProfileでデバイスプラグイン一覧を取得
        // 自分の名前とマッチさせて、PluginをIDを使用する
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
     * @return デバイスID
     */
    public String getDeviceId() {
        return mDeviceId;
    }

    /**
     * デバイスIDを設定する.
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
