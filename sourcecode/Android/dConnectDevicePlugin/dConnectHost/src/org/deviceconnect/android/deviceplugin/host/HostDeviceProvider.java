/*
 HostDeviceProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.host;

import org.deviceconnect.android.message.DConnectMessageServiceProvider;

import android.app.Service;

/**
 * ホストデバイスプロバイダ.
 * 
 * @param <T> サービスクラス
 * @author NTT DOCOMO, INC.
 */
public class HostDeviceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {

    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) HostDeviceService.class;

        return (Class<Service>) clazz;
    }

}
