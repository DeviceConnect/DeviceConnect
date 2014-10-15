/*
 DeviceTestServiceProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.test;

import org.deviceconnect.android.message.DConnectMessageServiceProvider;

import android.app.Service;

/**
 * テスト用デバイスプラグインサービスプロバイダ.
 * 
 * @param <T> DeviceTestService
 * @author NTT DOCOMO, INC.
 */
public class DeviceTestServiceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {
    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) DeviceTestService.class;
        return (Class<Service>) clazz;
    }
}
