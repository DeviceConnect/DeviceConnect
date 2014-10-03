package com.nttdocomo.android.dconnect.deviceplugin.pebble;

import android.app.Service;

import com.nttdocomo.android.dconnect.message.DConnectMessageServiceProvider;

/**
 * Pebble デバイスプロバイダークラス.
 * @author dconnect04
 *
 * @param <T> service.
 */
public class PebbleDeviceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {
    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) PebbleDeviceService.class;
        return (Class<Service>) clazz;
    }
}
