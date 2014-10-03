/*
 DConnectBroadcastReceiver.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * dConnect Managerへのイベント受信.
 * 
 * @author NTT DOCOMO, INC.
 */
public class DConnectBroadcastReceiver extends BroadcastReceiver {
    /**
     * 受信したことをDConnectServiceに通知.
     * 
     * @param context コンテキスト
     * @param intent リクエスト
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Intent targetIntent = new Intent(intent);
        targetIntent.setClass(context, DConnectService.class);
        context.startService(targetIntent);
    }
}
