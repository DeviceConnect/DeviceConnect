package com.nttdocomo.android.dconnect.deviceplugin.sonycamera;

import android.app.Service;

import com.nttdocomo.android.dconnect.message.DConnectMessageServiceProvider;

/**
SonyCameraDeviceProvider
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php

@param <T>
*/
public class SonyCameraDeviceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {
    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) SonyCameraDeviceService.class;
        return (Class<Service>) clazz;
    }
}
