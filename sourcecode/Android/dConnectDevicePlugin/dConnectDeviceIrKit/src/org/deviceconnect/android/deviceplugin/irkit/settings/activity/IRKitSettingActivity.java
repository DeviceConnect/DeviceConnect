/*
 IRKitSettingActivity.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.settings.activity;

import java.util.List;

import org.deviceconnect.android.deviceplugin.irkit.BuildConfig;
import org.deviceconnect.android.deviceplugin.irkit.IRKitManager;
import org.deviceconnect.android.deviceplugin.irkit.IRKitManager.WiFiSecurityType;
import org.deviceconnect.android.deviceplugin.irkit.settings.fragment.IRKitAccessPointSettingFragment;
import org.deviceconnect.android.deviceplugin.irkit.settings.fragment.IRKitBaseFragment;
import org.deviceconnect.android.deviceplugin.irkit.settings.fragment.IRKitEndingFragment;
import org.deviceconnect.android.deviceplugin.irkit.settings.fragment.IRKitPowerOnFragment;
import org.deviceconnect.android.deviceplugin.irkit.settings.fragment.IRKitWiFiSelectionFragment;
import org.deviceconnect.android.deviceplugin.irkit.settings.widget.HoldableViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 * 設定用Activity.
 * @author NTT DOCOMO, INC.
 */
public class IRKitSettingActivity extends IRKitAbstractSettingActivity {

    /** ページ数. */
    private static final int PAGE_COUNT = 4;

    /**
     * SharedPreference 名前.
     */
    public static final String SP_NAME = "irkit";

    /**
     * SharedPreference キー clientkey.
     */
    public static final String SP_KEY_CLIENT_KEY = "client_key";

    /**
     * クライアントキー.
     */
    private String mClientKey;

    /**
     * デバイスID.
     */
    private String mDeviceId;

    /**
     * デバイスキー.
     */
    private String mDeviceKey;

    /**
     * 前のページ.
     */
    private int mPrePage;
    
    /** 
     * SSID.
     */
    private String mSSID;
    
    /** 
     * パスワード.
     */
    private String mPassword;
    
    /** 
     * セキュリティタイプ.
     */
    private WiFiSecurityType mSecType;

    /**
     * ページのクラス一覧.
     */
    @SuppressWarnings({ "rawtypes" })
    private static final Class[] PAGES = { 
        IRKitPowerOnFragment.class, 
        IRKitAccessPointSettingFragment.class,
        IRKitWiFiSelectionFragment.class, 
        IRKitEndingFragment.class, 
        };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrePage = 0;
        mSecType = WiFiSecurityType.WPA2;
        
        getViewPager().setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(final int position) {
                
                FragmentManager fm = getSupportFragmentManager();
                List<Fragment> list = fm.getFragments();
                
                if (list == null) {
                    return;
                }
                
                Fragment next = list.get(position);
                Fragment pre = list.get(mPrePage);
                
                if (next != null && next instanceof IRKitBaseFragment) {
                    ((IRKitBaseFragment) next).onAppear();
                }
                
                if (pre != null && pre instanceof IRKitBaseFragment) {
                    ((IRKitBaseFragment) pre).onDisapper();
                }
                
                mPrePage = position;
            }

            @Override
            public void onPageScrolled(final int position, final float positionOffset,
                    final int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });

        IRKitManager.INSTANCE.init(this);
        final SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        mClientKey = sp.getString(SP_KEY_CLIENT_KEY, null);
    }

    @Override
    public Fragment createPage(final int position) {

        Fragment page;
        try {
            page = (Fragment) PAGES[position].newInstance();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            page = null;
        }

        return page;
    }

    @Override
    public int getPageCount() {
        return PAGE_COUNT;
    }

    /**
     * クライアントキーを取得する.
     * 
     * @return クライアントキー。無い場合はnullを返す。
     */
    public String getClientKey() {
        return mClientKey;
    }

    /**
     * クライアントキーを保存する.
     * 
     * @param clientKey クライアントキー
     */
    public void saveClientKey(final String clientKey) {
        SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.edit().putString(SP_KEY_CLIENT_KEY, clientKey).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> list = fm.getFragments();

        if (list == null) {
            return;
        }

        Fragment f = list.get(getViewPager().getCurrentItem());

        if (f != null && f instanceof IRKitBaseFragment) {
            ((IRKitBaseFragment) f).onEnterForeground();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> list = fm.getFragments();

        if (list == null) {
            return;
        }

        getViewPager().setEnabled(false);
        Fragment f = list.get(getViewPager().getCurrentItem());

        if (f != null && f instanceof IRKitBaseFragment) {
            ((IRKitBaseFragment) f).onEnterBackground();
        }
    }

    /**
     * デバイスIDを設定する.
     * 
     * @param deviceId デバイスID
     */
    public void setDeviceId(final String deviceId) {
        mDeviceId = deviceId;
    }

    /**
     * デバイスキーを設定する.
     * 
     * @param deviceKey デバイスキー
     */
    public void setDeviceKey(final String deviceKey) {
        mDeviceKey = deviceKey;
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
     * デバイスキーを取得する.
     * 
     * @return デバイスキー
     */
    public String getDeviceKey() {
        return mDeviceKey;
    }

    /**
     * タッチの可不可を設定する.
     * 
     * @param touchEnable 可不可
     */
    public void setTouchEnable(final boolean touchEnable) {
        ((HoldableViewPager) getViewPager()).setScrollable(touchEnable);
    }

    /**
     * SSIDを設定する.
     * 
     * @param ssid SSID
     */
    public void setSSID(final String ssid) {
        mSSID = ssid;
    }
    
    /**
     * パスワードを設定する.
     * 
     * @param password パスワード
     */
    public void setPassword(final String password) {
        mPassword = password;
    }
    
    /**
     * セキュリティタイプを設定する.
     * 
     * @param type タイプ
     */
    public void setSecType(final WiFiSecurityType type) {
        mSecType = type;
    }
    
    /**
     * SSIDを取得する.
     * 
     * @return SSID
     */
    public String getSSID() {
        return mSSID;
    }
    
    /**
     * パスワードを取得する.
     * 
     * @return パスワード
     */
    public String getPassword() {
        return mPassword;
    }
    
    /**
     * セキュリティタイプを取得する.
     * 
     * @return セキュリティタイプ
     */
    public WiFiSecurityType getSecType() {
        return mSecType;
    }
}
