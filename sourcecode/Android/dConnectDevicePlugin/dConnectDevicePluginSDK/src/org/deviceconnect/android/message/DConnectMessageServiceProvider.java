/*
 DConnectMessageServiceProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.message;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Device Connectメッセージサービスプロバイダー.
 * 
 * <p>
 * Device Connectリクエストメッセージを受信し、Device Connectレスポンスメッセージを送信するサービスである。 本インスタンスで処理をするのではなく、
 * {@link #getServiceClass()} で返却した Service で応答処理を行う。
 * 
 * @param <T> サービスクラス
 * @author NTT DOCOMO, INC.
 */
public abstract class DConnectMessageServiceProvider<T extends Service> extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Intent service = new Intent(intent);
        service.setClass(context, getServiceClass());
        context.startService(service);
    }

    /**
     * サービスクラスを取得する.
     * 
     * @return サービスクラス
     */
    protected abstract Class<T> getServiceClass();

}
