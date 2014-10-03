package com.nttdocomo.android.dconnect.deviceplugin.irkit;

import android.app.Service;

import com.nttdocomo.android.dconnect.message.DConnectMessageServiceProvider;

/**
 * IrKitデバイスプラグイン.
 * 
 * @param <T>
 */
public class IRKitDeviceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {

    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) IRKitDeviceService.class;
        return (Class<Service>) clazz;
    }

}
