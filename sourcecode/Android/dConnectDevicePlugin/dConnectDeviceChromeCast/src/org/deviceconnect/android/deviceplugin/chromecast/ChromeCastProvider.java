/*
 ChromeCastProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.chromecast;

import org.deviceconnect.android.message.DConnectMessageServiceProvider;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

/**
 * サービスプロバイダー (Chromecast)
 * <p>
 * リクエストメッセージを受信し、レスポンスメッセージを送信するサービス
 * </p>
 * 
 * @param <T> Service
 * @author NTT DOCOMO, INC.
 */
public class ChromeCastProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) ChromeCastService.class;
        return (Class<Service>) clazz;
    }

}
