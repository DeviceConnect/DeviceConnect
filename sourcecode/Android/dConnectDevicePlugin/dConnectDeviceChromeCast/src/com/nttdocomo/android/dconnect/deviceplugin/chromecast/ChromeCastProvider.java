package com.nttdocomo.android.dconnect.deviceplugin.chromecast;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.nttdocomo.android.dconnect.message.DConnectMessageServiceProvider;

/**
 * サービスプロバイダー (Chromecast)
 * <p>
 * リクエストメッセージを受信し、レスポンスメッセージを送信するサービス
 * </p>
 * 
 * @param <T> Service
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
