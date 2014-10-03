/*
 DeviceTestServiceProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.wear;

import android.app.Service;

import com.nttdocomo.android.dconnect.message.DConnectMessageServiceProvider;

/**
 * テスト用デバイスプラグインサービスプロバイダ.
 * 
 * @param <T> DeviceTestService
 * @author NTT DOCOMO, INC.
 */
public class WearDeviceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {
    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) WearDeviceService.class;
        return (Class<Service>) clazz;
    }
}
