/*
 DeviceTestService.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.test;

import java.util.Iterator;
import java.util.logging.Logger;

import org.deviceconnect.android.deviceplugin.test.profile.TestBatteryProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestConnectProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestDeviceOrientationProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestFileDescriptorProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestFileProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestMediaPlayerProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestMediaStreamRecordingProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestNetworkServiceDiscoveryProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestNotificationProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestPhoneProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestProximityProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestSettingsProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestSystemProfile;
import org.deviceconnect.android.deviceplugin.test.profile.TestVibrationProfile;
import org.deviceconnect.android.deviceplugin.test.profile.unique.TestJSONConversionProfile;
import org.deviceconnect.android.deviceplugin.test.profile.unique.TestUniqueProfile;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.event.cache.db.DBCacheController;
import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;
import org.deviceconnect.android.provider.FileManager;

import android.content.Intent;
import android.os.Bundle;


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
