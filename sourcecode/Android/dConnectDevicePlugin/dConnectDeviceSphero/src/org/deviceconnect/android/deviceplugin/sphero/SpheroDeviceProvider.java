/*
 SpheroDeviceProvider.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.sphero;

import java.util.logging.Logger;

import android.app.Service;
import android.util.Log;

import org.deviceconnect.android.message.DConnectMessageServiceProvider;

/**
 * Sphero用のService.
 * 
 * @param <T> Service
 * @author NTT DOCOMO, INC.
 */
public class SpheroDeviceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {
    
    /**
     * DebugLog.
     */
    private static final String TAG = "PluginShepro";
    
    /**
     * ロガー.
     */
    private Logger mLogger = Logger.getLogger("dconnect.dplugin.sphero");

    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        Log.i(TAG, "getServiceClass()");
        mLogger.entering(this.getClass().getName(), "getServiceClass");
        Class<? extends Service> clazz = (Class<? extends Service>) SpheroDeviceService.class;

        mLogger.exiting(this.getClass().getName(), "getServiceClass", clazz);
        return (Class<Service>) clazz;
    }

}
