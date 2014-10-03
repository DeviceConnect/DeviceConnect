package com.nttdocomo.android.dconnect.deviceplugin.sw;

import com.nttdocomo.android.dconnect.deviceplugin.sw.profile.SWDeviceOrientationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sw.profile.SWFileProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sw.profile.SWNetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sw.profile.SWNotificationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sw.profile.SWSystemProfile;
import com.nttdocomo.android.dconnect.deviceplugin.sw.profile.SWVibrationProfile;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.event.cache.db.DBCacheController;
import com.nttdocomo.android.dconnect.message.DConnectMessageService;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.android.dconnect.provider.FileManager;

/**
SWService
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

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
