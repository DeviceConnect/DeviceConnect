/*
 HostDeviceReceiver.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

package org.deviceconnect.android.deviceplugin.host;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 変化を通知するBroadcast Receiver.
 * 
 * @author NTT DOCOMO, INC.
 */
public class HostDeviceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        intent.setClass(context, HostDeviceService.class);
        context.startService(intent);
    }
}
