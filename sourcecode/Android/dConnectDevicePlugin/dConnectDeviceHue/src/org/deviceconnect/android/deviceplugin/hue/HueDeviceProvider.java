/*
HueDeviceProvider
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue;

import org.deviceconnect.android.deviceplugin.util.DcLoggerHue;
import org.deviceconnect.android.message.DConnectMessageServiceProvider;

import android.app.Service;


/**
 * 
 * プロバイダ.
 *
 * @param <T> Service
 * @author NTT DOCOMO, INC.
 */
public class HueDeviceProvider<T extends Service> extends DConnectMessageServiceProvider<Service> {

    /**
     * ロガー.
     */
    private DcLoggerHue mLogger = new DcLoggerHue();

    /**
     * サービスクラスを取得する.
     * 
     * @return サービスクラス
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Class<Service> getServiceClass() {
        
        mLogger.entering(this, "getServiceClass");
        
        Class<? extends Service> clazz = null;
        
        clazz = (Class<? extends Service>) HueDeviceService.class;

        mLogger.exiting(this, "getServiceClass", clazz);
        
        return (Class<Service>) clazz;
    }

}
