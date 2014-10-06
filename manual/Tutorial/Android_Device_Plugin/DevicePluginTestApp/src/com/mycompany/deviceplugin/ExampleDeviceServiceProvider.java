package com.mycompany.deviceplugin;

import com.nttdocomo.android.dconnect.message.DConnectMessageServiceProvider;

import android.app.Service;

public class ExampleDeviceServiceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {
    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) ExampleDeviceService.class;
        return (Class<Service>) clazz;
    }

}
