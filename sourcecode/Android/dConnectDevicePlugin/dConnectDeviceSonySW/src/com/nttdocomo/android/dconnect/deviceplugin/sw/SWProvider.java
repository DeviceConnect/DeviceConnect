package com.nttdocomo.android.dconnect.deviceplugin.sw;

import android.app.Service;
import com.nttdocomo.android.dconnect.message.DConnectMessageServiceProvider;

/**
SWProvider
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

public class SWProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {


    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Class<? extends Service> clazz = (Class<? extends Service>) SWService.class;
        return (Class<Service>) clazz;
    }

}
