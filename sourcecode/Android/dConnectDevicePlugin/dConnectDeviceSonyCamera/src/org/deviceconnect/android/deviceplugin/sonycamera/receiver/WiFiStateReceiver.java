/*
WiFiStateReceiver
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.sonycamera.receiver;

import org.deviceconnect.android.deviceplugin.sonycamera.SonyCameraDeviceService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Wi-Fiイベント受信用レシーバー.
 * @author NTT DOCOMO, INC.
 */
public class WiFiStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        intent.setClass(context, SonyCameraDeviceService.class);
        context.startService(intent);
    }
}
