package com.nttdocomo.android.dconnect.deviceplugin.sphero;

import java.util.logging.Logger;

import android.app.Service;
import android.util.Log;

import com.nttdocomo.android.dconnect.message.DConnectMessageServiceProvider;

/**
 * Sphero用のService.
 * 
 * @param <T> Service
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
