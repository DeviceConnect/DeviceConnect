/*
 HostDeviceApplication.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.host;

import android.app.Application;
import android.content.Intent;
import com.nttdocomo.android.dconnect.profile.BatteryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import com.nttdocomo.dconnect.message.intent.message.IntentDConnectMessage;

/**
 * Host Device Plugin Application.
 * @author NTT DOCOMO, INC.
 */
public class HostDeviceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // start accept service
        Intent request = new Intent(IntentDConnectMessage.ACTION_PUT);
        request.setClass(this, HostDeviceProvider.class);
        request.putExtra(DConnectMessage.EXTRA_PROFILE, BatteryProfile.PROFILE_NAME);
        sendBroadcast(request);
    }

}
