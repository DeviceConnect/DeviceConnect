/*
 HostDeviceProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.host;

import android.app.Service;
import com.nttdocomo.android.dconnect.message.DConnectMessageServiceProvider;

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
