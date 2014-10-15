/*
 ObserverReceiver.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.observer.receiver;

import org.deviceconnect.android.observer.DConnectObservationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 監視用のブロードキャストを受けるレシーバー.
 * 
 *
 * @author NTT DOCOMO, INC.
 */
public class ObserverReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Intent service = new Intent(intent);
        service.setClass(context, DConnectObservationService.class);
        context.startService(service);
    }

}
