/*
 DeviceTestService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.test;

import java.util.Iterator;
import java.util.logging.Logger;

import android.content.Intent;
import android.os.Bundle;

import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestBatteryProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestConnectProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestDeviceOrientationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestFileDescriptorProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestFileProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestMediaPlayerProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestMediaStreamRecordingProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestNetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestNotificationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestPhoneProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestProximityProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestSettingsProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestSystemProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.TestVibrationProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.unique.TestJSONConversionProfile;
import com.nttdocomo.android.dconnect.deviceplugin.test.profile.unique.TestUniqueProfile;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.event.cache.db.DBCacheController;
import com.nttdocomo.android.dconnect.localoauth.LocalOAuth2Main;
import com.nttdocomo.android.dconnect.message.DConnectMessageService;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.android.dconnect.provider.FileManager;

/**
 * テスト用プロファイルを公開するためのサービス.
 * @author NTT DOCOMO, INC.
 */
public class DeviceTestService extends DConnectMessageService {

    /** ロガー. */
    private Logger mLogger = Logger.getLogger("dconnect.dplugin.test");

    @Override
    public void onCreate() {
        super.onCreate();
        EventManager.INSTANCE.setController(new DBCacheController(this));
        LocalOAuth2Main.initialize(getApplicationContext());
        addProfile(new TestNotificationProfile());
        addProfile(new TestFileProfile(new FileManager(getApplicationContext())));
        addProfile(new TestMediaStreamRecordingProfile());
        addProfile(new TestMediaPlayerProfile());
        addProfile(new TestPhoneProfile());
        addProfile(new TestFileDescriptorProfile());
        addProfile(new TestSettingsProfile());
        addProfile(new TestVibrationProfile());
        addProfile(new TestBatteryProfile());
        addProfile(new TestConnectProfile());
        addProfile(new TestProximityProfile());
        addProfile(new TestDeviceOrientationProfile());

        // 独自プロファイル
        addProfile(new TestUniqueProfile());
        addProfile(new TestJSONConversionProfile());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLogger.info("onStartCommand: intent=" + intent);
        if (intent != null) {
            mLogger.info("onStartCommand: extras=" + toString(intent.getExtras()));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private String toString(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Iterator<String> it = bundle.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            sb.append(key + ":" + bundle.get(key));
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new TestSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new TestNetworkServiceDiscoveryProfile();
    }

}
