/*
 SWService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sw;

import org.deviceconnect.android.deviceplugin.sw.profile.SWDeviceOrientationProfile;
import org.deviceconnect.android.deviceplugin.sw.profile.SWFileProfile;
import org.deviceconnect.android.deviceplugin.sw.profile.SWNetworkServiceDiscoveryProfile;
import org.deviceconnect.android.deviceplugin.sw.profile.SWNotificationProfile;
import org.deviceconnect.android.deviceplugin.sw.profile.SWSystemProfile;
import org.deviceconnect.android.deviceplugin.sw.profile.SWVibrationProfile;

import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.event.cache.db.DBCacheController;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.android.provider.FileManager;

/**
 * 本デバイスプラグインのプロファイルをDeviceConnectに登録するサービス.
 */
public class SWService extends DConnectMessageService {

    @Override
    public void onCreate() {
        super.onCreate();

        EventManager.INSTANCE.setController(new DBCacheController(this));
        // ファイル管理クラスの作成
        FileManager fileMgr = new FileManager(this);

        addProfile(new SWDeviceOrientationProfile());
        addProfile(new SWNotificationProfile());
        addProfile(new SWVibrationProfile());
        addProfile(new SWFileProfile(fileMgr));
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new SWSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new SWNetworkServiceDiscoveryProfile();
    }
}
