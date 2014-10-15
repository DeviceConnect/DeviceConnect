/*
 PackageManageReceiver.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.manager.receiver;

import org.deviceconnect.android.manager.DConnectService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * apkのインストールとアンインストールのブロードキャストを受け取るレシーバー.
 * @author NTT DOCOMO, INC.
 */
public class PackageManageReceiver extends BroadcastReceiver {
    /**
     * DConnectServiceに伝える.
     * @param context コンテキスト
     * @param intent リクエスト
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Intent service = new Intent(intent);
        service.setClass(context, DConnectService.class);
        context.startService(service);
    }
}
