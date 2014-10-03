package com.nttdocomo.android.dconnect.deviceplugin.sonycamera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nttdocomo.android.dconnect.deviceplugin.sonycamera.SonyCameraDeviceService;

/**
WiFiStateReceiver
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/
public class WiFiStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        intent.setClass(context, SonyCameraDeviceService.class);
        context.startService(intent);
    }
}
