/*
 WearServiceProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.wear;

import java.util.List;

import org.deviceconnect.android.deviceplugin.wear.profile.WearConst;
import org.deviceconnect.android.deviceplugin.wear.profile.WearDeviceOrientationProfile;
import org.deviceconnect.android.deviceplugin.wear.profile.WearNetworkServiceDiscoveryProfile;
import org.deviceconnect.android.deviceplugin.wear.profile.WearNotificationProfile;
import org.deviceconnect.android.deviceplugin.wear.profile.WearSystemProfile;
import org.deviceconnect.android.deviceplugin.wear.profile.WearVibrationProfile;
import org.deviceconnect.android.event.Event;
import org.deviceconnect.android.event.EventManager;
import org.deviceconnect.android.event.cache.db.DBCacheController;
import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.message.DConnectMessageService;
import org.deviceconnect.android.profile.NetworkServiceDiscoveryProfile;
import org.deviceconnect.android.profile.SystemProfile;

import android.content.Intent;

/**
 * Service.
 * 
 * @param <T> DeviceTestService
 * @author NTT DOCOMO, INC.
 */
public class WearDeviceService extends DConnectMessageService {

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize of the EventManager
        EventManager.INSTANCE.setController(new DBCacheController(this));
        LocalOAuth2Main.initialize(getApplicationContext());

        // add supported profiles
        addProfile(new WearNotificationProfile());
        addProfile(new WearVibrationProfile());
        addProfile(new WearDeviceOrientationProfile());

    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent != null) {
            
            String action = intent.getAction();
            if (action.equals(WearConst.DEVICE_TO_WEAR_NOTIFICATION_OPEN)) {
                String deviceId = intent.getStringExtra(WearConst.PARAM_DEVICEID);
                int notificationId = intent.getIntExtra(WearConst.PARAM_NOTIFICATIONID, -1);

                List<Event> events = EventManager.INSTANCE.getEventList(deviceId, WearNotificationProfile.PROFILE_NAME,
                        null, WearNotificationProfile.ATTRIBUTE_ON_CLICK);

                for (int i = 0; i < events.size(); i++) {
                    Event event = events.get(i);
                    Intent mIntent = EventManager.createEventMessage(event);
                    mIntent.putExtra(WearNotificationProfile.PARAM_NOTIFICATION_ID, notificationId);
                    getContext().sendBroadcast(mIntent);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected SystemProfile getSystemProfile() {
        return new WearSystemProfile(this);
    }

    @Override
    protected NetworkServiceDiscoveryProfile getNetworkServiceDiscoveryProfile() {
        return new WearNetworkServiceDiscoveryProfile();
    }
}
