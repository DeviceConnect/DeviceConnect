/*
 PebbleDeviceProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.pebble;

import android.app.Service;

import org.deviceconnect.android.message.DConnectMessageServiceProvider;

/**
 * Pebble デバイスプロバイダークラス.
 *
 * @param <T> service.
 * @author NTT DOCOMO, INC.
 */
public class PebbleDeviceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {
    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) PebbleDeviceService.class;
        return (Class<Service>) clazz;
    }
}
